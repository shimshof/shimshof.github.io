package com.pingidentity.moderno.qrcode;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.pingidentity.moderno.api.PingIDSdkConfiguration;
import com.pingidentity.moderno.exceptions.CommonException;
import com.pingidentity.moderno.handlers.Handler;
import com.pingidentity.moderno.handlers.Status;
import com.pingidentity.moderno.handlers.AuthenticateOnlineWithPingIDSdk.AccountDetails;
import com.pingidentity.moderno.handlers.requestdata.JsonRequestData;
import com.pingidentity.moderno.model.api.AuthenticationTokenStatusResponse;
import com.pingidentity.moderno.model.api.AuthenticationTokenStatusResponse.TokenStatus;
import com.pingidentity.moderno.utils.SessionData;
import com.pingidentity.moderno.utils.SessionUtils;
import com.pingidentity.pingidsdk.PingIDSdkException;
import com.pingidentity.pingidsdk.PingIDSdkHelper;
import com.pingidentity.pingidsdk.api.AuthenticationToken;

/**
 * UpdateAuthenticationTokenUserNameHandler class is responsible for updating the selected user name.
 * It is called once the WEB displays the user list (in case the device that scanned the QR code is paired with more than one user).
 * 
 * This token can be used as QR code
 * Created by Ping Identity on 3/23/18.
 * Copyright © 2017 Ping Identity. All rights reserved.
 *
 */
public class UpdateAuthenticationTokenUserNameHandler extends Handler<AuthenticationTokenStatusResponse>{

	/**
	 * The authentication token id (this id should be specified in the request)
	 */
	private String id;
	
	/**
	 * The returned authentication token
	 */
	private AuthenticationToken authenticationToken;
	
	/**
	 * object that works against the PingID Sdk server
	 * */
	private PingIDSdkHelper pingIDSdkHelper;
	
	
	/**
	 * Mock - mocks the user account details
	 */
	private static final Map<String, AccountDetails> accountDetailsDB = new ConcurrentHashMap<>();
	
	/**
	 * used to mock the user account savings
	 */
	private static Random random = new Random(System.currentTimeMillis());

	private String username;
	
	public UpdateAuthenticationTokenUserNameHandler(JsonRequestData requestData, HttpServletResponse response) {
		super(requestData, response);
		
		// the authentication token id (which was already created). see: AuthenticationTokenGenerator class
		id = requestData.getValue(PARAMETER_AUTHENTICATION_TOKEN_ID);
		
		username = requestData.getValue(PARAMETER_USERNAME);
		// initialize the "PingID SDK" helper
		pingIDSdkHelper = new PingIDSdkHelper(PingIDSdkConfiguration.instance().getUrl(),
				PingIDSdkConfiguration.instance().getAccountId(), PingIDSdkConfiguration.instance().getApiKey(),
				PingIDSdkConfiguration.instance().getToken(), PingIDSdkConfiguration.instance().getAppId());
		
	}
	
	@Override
	protected Status processRequest() throws CommonException, PingIDSdkException {
		
		authenticationToken = pingIDSdkHelper.updateAuthenticationTokenUsername(id, username);
		
		// If the authentication token has "CLAIMED" status, it means that the user was authenticated successfully using this token.
		// the returned token also contains the user name.
		// In this sample, if the user is authenticated successfully, the mock session is filled with the user account savings data
		if(authenticationToken != null && TokenStatus.CLAIMED.name().equalsIgnoreCase(authenticationToken.getStatus())){
			createSessionData(response, authenticationToken.getUsername());
		}
		
		return Status.OK;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////              THE FOLLOWING METHODS ARE RELATED TO A SPECIFIC "MODERNO" SAMPLE DEMO                                        /////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	/**
	 * This code is a specific "Moderno" sample implementation. It creates a user account (if the authentication is successful)
	 * @param response
	 * @param username
	 */
	@Override
	protected AuthenticationTokenStatusResponse createResponse(Status status) {
		AuthenticationTokenStatusResponse tokenResponse = new AuthenticationTokenStatusResponse();
		tokenResponse.setStatus(status.getCode());
		
		// The token does not exist. The token has 30 minutes TTL
		if(authenticationToken == null) {
			tokenResponse.setTokenStatus(TokenStatus.NOT_EXIST);
			return tokenResponse;
		}
		
		// convert the string to token status
		TokenStatus tokenStatus = TokenStatus.getValue(authenticationToken.getStatus());
		tokenResponse.setTokenStatus(tokenStatus);
		return tokenResponse;
	}

	/**
	 * This code is a specific "Moderno" sample implementation. It creates a user account (if the authentication is successful)
	 * @return Mock sub-account
	 */
	private void createSessionData(HttpServletResponse response, String username) {
		SessionData sessionData = new SessionData(username);
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
		SessionUtils.setSessionData(response, sessionData);
	}

	/**
	 * This code is a specific "Moderno" sample implementation
	 * @return Mock sub-account
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
	 * This code is a specific "Moderno" sample implementation
	 * @return Mock sub-account
	 */	 
	private synchronized int generateRandom(int limit) {
		return random.nextInt(limit);
	}
}
