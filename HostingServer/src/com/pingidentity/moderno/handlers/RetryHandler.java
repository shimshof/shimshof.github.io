package com.pingidentity.moderno.handlers;

import java.text.DecimalFormat;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pingidentity.moderno.api.PingIDSdkConfiguration;
import com.pingidentity.moderno.exceptions.CommonException;
import com.pingidentity.moderno.handlers.requestdata.JsonRequestData;
import com.pingidentity.moderno.model.api.AuthenticationResponse;
import com.pingidentity.moderno.model.api.ClientContext;
import com.pingidentity.moderno.utils.PingIdAuthenticationUtils;
import com.pingidentity.moderno.utils.SessionData;
import com.pingidentity.moderno.utils.SessionUtils;
import com.pingidentity.pingidsdk.PingIDSdkException;
import com.pingidentity.pingidsdk.PingIDSdkHelper;
import com.pingidentity.pingidsdk.api.Authentication;
import com.pingidentity.pingidsdk.api.AuthenticationStatus;

public class RetryHandler extends Handler<AuthenticationResponse>{

	private static final Logger logger = LoggerFactory.getLogger(RetryHandler.class);

	/**
	 * Mobile "payload" which is received from the mobile application. Exists only for mobile application requests.
	 * When calls are not from the mobile application, this member is null.
	 */
	private String pingIdPayloadMobile = null;
	
	/**
	 * The user name (taken from the authentication request)
	 */
	private String username;
	
	/**
	 * Optional: the authentication can be done with a specific device ID.
	 */
	private String deviceId;
	
	/**
	 * The final response to the authentication request
	 */
	private AuthenticationResponse authenticationResponse;
	
	private PingIDSdkHelper pingIDSdkHelper;
	
	private SessionData sessionData;
	
	public RetryHandler(JsonRequestData requestData, HttpServletResponse response) {

		super(requestData, response);
		
		// initialize the "PingID SDK" helper
		pingIDSdkHelper = new PingIDSdkHelper(PingIDSdkConfiguration.instance().getUrl(), PingIDSdkConfiguration.instance().getAccountId(), PingIDSdkConfiguration.instance().getApiKey(),
						PingIDSdkConfiguration.instance().getToken(), PingIDSdkConfiguration.instance().getAppId());
		
		// pingIdPayloadMobile != null if the call is from the mobile application. pingIdPayloadMobile == null, otherwise.
		pingIdPayloadMobile = requestData.getValue(PARAMETER_PING_ID_PAYLOAD);
		
		// optional: the request may contain the device ID
		deviceId = requestData.getValue(PARAMETER_DEVICE_ID);
		
		// initialize the final response
		authenticationResponse = new AuthenticationResponse();
	}
	
	/**
	 * when making a retry authentication there are two recommended steps to do from the hosting server
	 * 1. delete the "old" authentication.
	 * 2. extract the deviceId that the user started to authenticate against it 
	 *    and make the retry against this device
	 * those steps are not mandatory. 
	 * */
	@Override
	protected Status processRequest() throws CommonException, PingIDSdkException {
		//retry authentication is always after an authentication starts (and push failed) there for there
		//must be existing sessionId
		Status status = Status.FAILED;
		
		sessionData = SessionUtils.getSessionData(requestData);
		if (sessionData == null) {
			return status;
		}
		
		//extract userName from sessionData
		//TODO: for CR, can it be null??
		username = sessionData.getUserName();
		//get the old authentication
		Authentication authentication = sessionData.getPingidAuthentication();
		if (authentication == null) {
			//weird behavior, but does not affect flow  
			logger.error("PingID SDK authentication wasn't started or saved in current session");
			//in that case the developer can decide if to continue and re-authenticate or to fail the transaction.
			status = authenticate();
		}
		//before deletion - extract the deviceId that the user started to authenticate against it
		deviceId = authentication.getDeviceId();
		
		//delete the "old" authentication.
		AuthenticationStatus lastAuthStatus = authentication.getStatus();
		if (shouldCancelAuthentication(lastAuthStatus)){
			pingIDSdkHelper.deleteAuthentication(username, authentication.getId());
		}
		
		//make a new authentication
		status = authenticate();
		
		if (Status.OK == status) {
			if (sessionData != null && sessionData.getSubAccountChecking() != null) {
				String sum = new DecimalFormat("#,###.00").format(sessionData.getSubAccountChecking().getValue());
				authenticationResponse.setSum(sum);
			}
		}
		
		//case - need OTP
		if (Status.OTP == status){
			//TODO: add explenation
			sessionData.setPingidAuthentication(authenticationResponse.getPingIDSdkAuthentication());
		}
		
		return status;
	}
	
	/**
	 * check if the authentication is "alive" and therefore should be canceled 
	 * authentication is "alive" if its status is "OTP", "IN_PROGRSS" or "SELECT DEVICE"
	 * */
	private boolean shouldCancelAuthentication(AuthenticationStatus lastAuthStatus) {
		if (lastAuthStatus.equals(AuthenticationStatus.IN_PROGRESS) || 
			lastAuthStatus.equals(AuthenticationStatus.OTP) ||
			lastAuthStatus.equals(AuthenticationStatus.SELECT_DEVICE)){
			return true;
		}
		return false;
	}

	private Status authenticate() throws PingIDSdkException{		
		ClientContext cc = new ClientContext(username, ClientContext.TrxType.AUTHENTICATION);
		Authentication newAuthentication = pingIDSdkHelper.authenticate(username, pingIdPayloadMobile, deviceId, null, null, cc.toString());
		
		if (newAuthentication.getStatus().equals(AuthenticationStatus.OTP)) {
			handleOTPStatus(newAuthentication);
		}

		// The response must contains the server payload [if exists] (so that the mobile application SDK handle it)
		// Any hosting server implementation must return in the response the server payload (if exists)
		authenticationResponse.setPingIdPayload(newAuthentication.getPayload());

		// The next steps are for demonstration. Each hosting server can handle the authentication results in a different way.

		// Add the user device which was used for this authentication
		authenticationResponse.setCurrentAuthenticatingDeviceData(newAuthentication.getDevice());

		// Let the caller know if the "PingID SDK" authentication approved
		if (newAuthentication.getStatus() == AuthenticationStatus.APPROVED) {
			authenticationResponse.setPingIdAuthenticated(true);
		}

		// Add the "PingID SDK" authentication to the response
		authenticationResponse.setPingIDSdkAuthentication(newAuthentication);

		// convert the authentication status to the hosting server status.
		return PingIdAuthenticationUtils.convertPingIdAuthenticationStatusToHostingServerStatus(newAuthentication.getStatus());
		
	}
	
	@Override
	protected AuthenticationResponse createResponse(Status status) {
		authenticationResponse.setStatus(status.getCode());
		authenticationResponse.setDescription(status.getMessage());
		return authenticationResponse;
	}	

	/**
	 * "OTP" authentication status means that, for the current authentication, it is impossible to authenticate the user online and there is an OTP fallback
	 * configuration (configured per application - can be configured in the Web-Portal). 
	 * In order to continue with the "PingID SDK" authentication, the user must enterOTP (One-Time Passcode) 
	 * 
	 * @param authentication the current authentication
	 */
	private void handleOTPStatus(Authentication authentication) {
		// Add the session to the response
		authenticationResponse.setAuthSessionID(sessionData.getSessionId());
		
		//  Note: the session is stored, so that when the user tries to authenticate offline,
		//  the same authentication will be used
	}


}
