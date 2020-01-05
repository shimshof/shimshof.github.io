package com.pingidentity.moderno.handlers;

import java.text.DecimalFormat;

import javax.servlet.http.HttpServletResponse;

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
import com.pingidentity.pingidsdk.PingIDSdkHelper;
import com.pingidentity.pingidsdk.api.Authentication;
import com.pingidentity.pingidsdk.api.AuthenticationStatus;

/**
 * AuthenticateWithPingIDSdk
 *
 * This handler demonstrates how to authenticate with "PingID SDK" in offline mode 
 * offline mode refers to two main scenarios:
 * 
 * 1. the user has a mobile application (with "PingID SDK") but the mobile is unreachable or in a push-less mode. 
 * 2. the second factor authentication is done by sending the user a passcode via sms message or email. 
 * 
 * The authentication request can come from 2 types of callers:
 * 
 *    a. The mobile application. In this case, the request contains a payload. Once the "PingID SDK" is integrated into the mobile application,
 *    each mobile application request should contain this payload (the payload is generated in the SDK)
 *    
 *    b. The Web. In this case, the request does not contain a payload
 *    
 *  *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public class AuthenticateOfflineWithPingIDSdk extends Handler<AuthenticationResponse> {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticateOfflineWithPingIDSdk.class);
	
	/**
	 * The final response to the authentication request
	 */
	private AuthenticationResponse authenticationResponse;
	
	/**
	 * object that works against the PingID Sdk server
	 * */
	private PingIDSdkHelper pingIDSdkHelper;
	
	/**
	 * object that passes data that connect to the authentication request (see inside)
	 * and additional data that connect to the request context
	 * (in our sample the data of the user financial balance)   
	 * */
	private SessionData sessionData;
	
	private String pingIdPayloadMobile = null;
	
	/**
	 * this is the One Time Passcode the user has typed (got by sms/email/App) in order to finish the authentication
	 * */
	private String otp = null;
	
	public AuthenticateOfflineWithPingIDSdk(JsonRequestData requestData, HttpServletResponse response) {
		super(requestData, response);
		
		// initialize the "PingID SDK" helper
		pingIDSdkHelper = new PingIDSdkHelper(PingIDSdkConfiguration.instance().getUrl(), PingIDSdkConfiguration.instance().getAccountId(), PingIDSdkConfiguration.instance().getApiKey(), PingIDSdkConfiguration.instance().getToken(),
				PingIDSdkConfiguration.instance().getAppId());

		// pingIdPayloadMobile != null if the call is from the mobile application. pingIdPayloadMobile == null, otherwise.
		pingIdPayloadMobile = requestData.getValue(PARAMETER_PING_ID_PAYLOAD);
		
		otp = requestData.getValue(PARAMETER_OTP);
		
		// initialize the final response
		authenticationResponse = new AuthenticationResponse();
	}

	@Override
	protected Status processRequest() throws CommonException, PingIDSdkException {
		//offline authentication must be part of a session that already started. therefore must have sessionData  
		sessionData = SessionUtils.getSessionData(requestData);
		if (sessionData == null) {
			return Status.FAILED;
		}
		
		//and the session data must have authentication object
		Authentication authentication = sessionData.getPingidAuthentication();
		if (authentication == null) {
			logger.error("PingID SDK authentication wasn't started or saved in current session");
			return Status.FAILED;
		}
		
		Status status = Status.FAILED;
		try {
			authentication = pingIDSdkHelper.authenticateOffline(sessionData.getUserName(), pingIdPayloadMobile, authentication.getId(), otp);
			
			// The response must contains the server payload [if exists] (so
			// that the mobile application SDK handle it)
			// if server payload exist then any hosting server implementation must return it in the response  
			authenticationResponse.setPingIdPayload(authentication.getPayload());

			// The next steps are for demonstration. Each hosting server can
			// handle the authentication results in a different way.

			// Add the user device which was used for this authentication
			authenticationResponse.setCurrentAuthenticatingDeviceData(authentication.getDevice());

			// Let the caller know if the "PingID SDK" authentication approved
			if (authentication.getStatus() == AuthenticationStatus.APPROVED) {
				authenticationResponse.setPingIdAuthenticated(true);
			}

			// Add the "PingID SDK" authentication to the response
			authenticationResponse.setPingIDSdkAuthentication(authentication);

			// convert the authentication status to the hosting server status.
			status = PingIdAuthenticationUtils.convertPingIdAuthenticationStatusToHostingServerStatus(authentication.getStatus());
		} catch (PingIDSdkException e) {
			logger.error("Json problems", e.getMessage());
			throw e;
		}
		
		//this part is not connect to the authentication flow . our sample is banking - 
		//so the server side send to the client side the user balance. 
		if (Status.OK == status) {
			if (sessionData != null && sessionData.getSubAccountChecking() != null) {
				String sum = new DecimalFormat("#,###.00").format(sessionData.getSubAccountChecking().getValue());
				authenticationResponse.setSum(sum);
			}
		}
		return status;
	}

	@Override
	protected AuthenticationResponse createResponse(Status status) {
		authenticationResponse.setStatus(status.getCode());
		authenticationResponse.setDescription(status.getMessage());
		return authenticationResponse;
	}

}
