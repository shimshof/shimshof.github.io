package com.pingidentity.moderno.handlers;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;

import com.pingidentity.moderno.model.api.ClientContext;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pingidentity.moderno.api.PingIDSdkConfiguration;
import com.pingidentity.moderno.exceptions.CommonException;
import com.pingidentity.moderno.handlers.requestdata.JsonRequestData;
import com.pingidentity.moderno.model.api.AuthenticationResponse;
import com.pingidentity.moderno.utils.PingIdAuthenticationUtils;
import com.pingidentity.moderno.utils.SessionData;
import com.pingidentity.moderno.utils.SessionUtils;
import com.pingidentity.pingidsdk.PingIDSdkException;
import com.pingidentity.pingidsdk.PingIDSdkAPI;
import com.pingidentity.pingidsdk.PingIDSdkHelper;
import com.pingidentity.pingidsdk.api.Authentication;
import com.pingidentity.pingidsdk.api.AuthenticationStatus;
import com.pingidentity.pingidsdk.api.Device;
import com.pingidentity.pingidsdk.api.RegistrationToken;
import com.pingidentity.pingidsdk.api.User;


/**
 * AuthenticateWithPingIDSdk
 *
 * This handler demonstrates how to authenticate with "PingID SDK".
 * 
 * 1. The authentication request can come from 2 types of callers:
 * 
 *    a. The mobile application. In this case, the request contains a payload. Once the "PingID SDK" is integrated into the mobile application,
 *    each mobile application request should contain this payload (the payload is generated in the SDK)
 *    
 *    b. The Web. In this case, the request does not contain a payload
 *    
 * 2. In this handler, if the user is not active and the caller is the mobile application, the user is paired on the fly (the caller device will be paired). 
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public class AuthenticateOnlineWithPingIDSdk extends Handler<AuthenticationResponse>{

	private static final Logger logger = LoggerFactory.getLogger(AuthenticateOnlineWithPingIDSdk.class);
	
	private static Random random = new Random(System.currentTimeMillis());

	public static class AccountDetails {
		private Pair<String, Float> subAccountChecking;
		private Pair<String, Float> subAccountSavings;

		public Pair<String, Float> getSubAccountChecking() {
			return subAccountChecking;
		}

		public void setSubAccountChecking(Pair<String, Float> subAccountChecking) {
			this.subAccountChecking = subAccountChecking;
		}

		public Pair<String, Float> getSubAccountSavings() {
			return subAccountSavings;
		}

		public void setSubAccountSavings(Pair<String, Float> subAccountSavings) {
			this.subAccountSavings = subAccountSavings;
		}

	}
	
	private static final Map<String, AccountDetails> accountDetailsDB = new ConcurrentHashMap<>();
	
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
	

	public AuthenticateOnlineWithPingIDSdk(JsonRequestData requestData, HttpServletResponse response) {

		super(requestData, response);
		
		// initialize the "PingID SDK" helper
		pingIDSdkHelper = new PingIDSdkHelper(PingIDSdkConfiguration.instance().getUrl(), PingIDSdkConfiguration.instance().getAccountId(), PingIDSdkConfiguration.instance().getApiKey(),
						PingIDSdkConfiguration.instance().getToken(), PingIDSdkConfiguration.instance().getAppId());
		
		// take the user name from the request
		username = requestData.getValue(PARAMETER_USERNAME);
		
		// pingIdPayloadMobile != null if the call is from the mobile application. pingIdPayloadMobile == null, otherwise.
		pingIdPayloadMobile = requestData.getValue(PARAMETER_PING_ID_PAYLOAD);
		
		// optional: the request may contain the device ID
		deviceId = requestData.getValue(PARAMETER_DEVICE_ID);
		
		// initialize the final response
		authenticationResponse = new AuthenticationResponse();
	}

	@Override
	protected Status processRequest() throws CommonException, PingIDSdkException {
		// Creating the hosting server session
		createSessionData();
		

		Status status = authenticate();

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
	
	protected Status authenticate() throws CommonException, PingIDSdkException {
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// This section is hosting server logic authentication ( a very specific one ):                                        	//
		// First factor authentication (verifying the user credentials for accessing the hosting server.) 						//
		// This is done before verifying the user with "PingID SDK".                                                 	//
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Authenticate with the "Hosting Server" (1st factor). this is for the demonstration.
		// Usually, authentication with "PingID SDK" is a second factor authentication 
		// (that is, the user is first authenticated with the user credentials and then , as a second factor, authenticated with "PingID SDK").
		Status status = firstFactorAuthentication();
		if (status != Status.OK) {
			return status;
		}

		logger.info(String.format("User passed 1st factor authenticatication. username: %s", username));

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// This section demonstrates how this hosting server integrate its logic with  the "PingID SDK" solution //
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		try {
			// At this stage, the user passed the Hosting Server authentication

			// Step (1): Check if the user exists in PingID server.
			// If the user does not exist, create the user in "PingID SDK"
			User user = pingIDSdkHelper.getUserOrCreateIfUserNotExist(username);

			// At this stage, the user exists in "PingID SDK" Server DB

			// Step (2): Check if the user is active. The user is active, if the user has at least one paired device.
			boolean isUserActive = pingIDSdkHelper.isUserActive(user);

			// If the user is not active -> Create a RegToken and return it to the caller.
			if (!isUserActive) {

				// case 1: call is from the mobile application. (pingIdPayloadMobile is not null or empty)
				if (pingIdPayloadMobile != null && !pingIdPayloadMobile.trim().isEmpty()) {
					RegistrationToken regToken = pingIDSdkHelper.createRegistrationTokenForNonActiveUser(user, pingIdPayloadMobile);

					// The response must contain the RegToken itself & the server payload.
					// The mobile application will pair the user device using this data.
					authenticationResponse.setRegistrationToken(regToken.getId());
					authenticationResponse.setPingIdPayload(regToken.getPayload());
					return status;
				}

				// case 2: call is not from the mobile application.
				// In our implementation it is impossible to create a RegToken if the caller is not the mobile application.
				// In this case, the user must authenticate using the mobile application.
				logger.info(String.format("User %s is not active. The user should autheticate via the mobile application ", username));

				return Status.USER_NOT_ACTIVE;
			}

			// isUserActive == true. The user is active (if we reach this line).

			// Step (3) authenticate the user with "PingID SDK"
			ClientContext cc = new ClientContext(username, ClientContext.TrxType.AUTHENTICATION);
			Authentication authentication = pingIDSdkHelper.authenticate(username, pingIdPayloadMobile, deviceId, null, null, cc.toString());

			// "AuthenticationStatus.SELECT_DEVICE" means that the "PingID SDK" server was not able
			// to determine which user device to use for the authentication. For example, when the user does not have any primary device
			// and has more than one trusted device. In this case, the hosting server should decide which device to use.
			// In this sample, the Hosting server returns ,in the response, the user devices list and the user should select which device to use
			if (authentication.getStatus().equals(AuthenticationStatus.SELECT_DEVICE)) {
				List<Device> availableDevices = pingIDSdkHelper.getUserDevices(username);
				authenticationResponse.setAvailableDevicesForAuthentication(availableDevices);
			}

			// "AuthenticationStatus.OTP" means that the user should authenticate offline.
			// For example, if the user device is pushless. It means that in order to authenticate, the user must enter an OTP (One time Passcode)
			else if (authentication.getStatus().equals(AuthenticationStatus.OTP)) {
				handleOTPStatus(authentication);
			}

			// The response must contains the server payload [if exists] (so that the mobile application SDK handle it)
			// Any hosting server implementation must return in the response the server payload (if exists)
			authenticationResponse.setPingIdPayload(authentication.getPayload());

			// The next steps are for demonstration. Each hosting server can handle the authentication results in a different way.

			// Add the user device which was used for this authentication
			authenticationResponse.setCurrentAuthenticatingDeviceData(authentication.getDevice());

			// Let the caller know if the "PingID SDK" authentication approved
			if (authentication.getStatus() == AuthenticationStatus.APPROVED) {
				authenticationResponse.setPingIdAuthenticated(true);
			}

			// Add the "PingID SDK" authentication to the response
			authenticationResponse.setPingIDSdkAuthentication(authentication);

			// convert the authentication status to the hosting server status.
			status = PingIdAuthenticationUtils.convertPingIdAuthenticationStatusToHostingServerStatus(authentication.getStatus(), authentication.getReason());
		} catch (PingIDSdkException e) {
			// Logic of hosting server expects that when application is disabled means that the server should work without PingID SDK.
			if (PingIDSdkAPI.isApplicationDisabled(e)) {
				logger.info("Your application is disabled");
				return status;
			}

			throw e;
		}

		return status;
	}
	
	/**
	 * First Factor authentication, Doing nothing in this sample.
	 * @return Status.OK (always. Just for simplicity)
	 */
	private Status firstFactorAuthentication() {
		return Status.OK;
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

	/**
	 * Creating a session for the user
	 * @return the session data
	 */
	private SessionData createSessionData() {
		// 1. Create a session which stores the current authentication and user name
		// (very simple implementation for the simplicity)
		sessionData = new SessionData(username);

		AccountDetails accountDetails = accountDetailsDB.get(username);
		if (!accountDetailsDB.containsKey(username)) {
			accountDetails = new AccountDetails();
			accountDetails.setSubAccountChecking(generateSubAccount());
			accountDetails.setSubAccountSavings(generateSubAccount());
			accountDetailsDB.put(username, accountDetails);
		}
		
		if (accountDetailsDB.containsKey(username)) {
			sessionData.setSubAccountChecking(accountDetails.getSubAccountChecking());
			sessionData.setSubAccountSavings(accountDetails.getSubAccountSavings());
		}
		
		// 2. Store the session
		// (very simple implementation for the simplicity)
		SessionUtils.setSessionData(response, sessionData);
		
		return sessionData;
	}

	/**
	 * creating a sub account for this user
	 * (Note: this this hosting server specific logic.)
	 * @return a pair of sub account and amount
	 */
	private Pair<String, Float> generateSubAccount() {
		StringBuilder builder = new StringBuilder("*****-***");
		int subAccount = generateRandom(100);
		if (subAccount < 10) {
			builder.append('0');
		}
		builder.append(subAccount);
		String subAccountId = builder.toString();
		Float amount = new Float(generateRandom(20000));

		return MutablePair.of(subAccountId, amount);
	}
	

	/**
	 * Generates a random
	 * @param limit the random limit
	 * @return a random
	 */
	private synchronized int generateRandom(int limit) {
		return random.nextInt(limit);
	}

	@Override
	protected AuthenticationResponse createResponse(Status status) {
		authenticationResponse.setStatus(status.getCode());
		authenticationResponse.setDescription(status.getMessage());
		return authenticationResponse;
	}	

}
