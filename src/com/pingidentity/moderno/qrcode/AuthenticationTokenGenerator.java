package com.pingidentity.moderno.qrcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pingidentity.moderno.api.PingIDSdkConfiguration;
import com.pingidentity.moderno.handlers.Status;
import com.pingidentity.moderno.model.api.AuthenticationTokenResponse;
import com.pingidentity.moderno.model.api.ClientContext;
import com.pingidentity.pingidsdk.PingIDSdkException;
import com.pingidentity.pingidsdk.PingIDSdkHelper;
import com.pingidentity.pingidsdk.api.AuthenticationToken;

/**
 * AuthenticationTokenGenerator class generates an authentication token.
 * 
 * Created by Ping Identity on 3/23/18.
 * Copyright © 2017 Ping Identity. All rights reserved.
 *
 */
public class AuthenticationTokenGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationTokenGenerator.class);
	
	/**
	 * object that works against the PingID SDK server
	 * */
	private PingIDSdkHelper pingIDSdkHelper;
	
	/**
	 * The generated authentication token
	 */
	private AuthenticationToken authenticationToken;

	public AuthenticationTokenGenerator() {
		
		// initialize the "PingID SDK" helper
		pingIDSdkHelper = new PingIDSdkHelper(PingIDSdkConfiguration.instance().getUrl(), PingIDSdkConfiguration.instance().getAccountId(), PingIDSdkConfiguration.instance().getApiKey(),
								PingIDSdkConfiguration.instance().getToken(), PingIDSdkConfiguration.instance().getAppId());
	}

	/**
	 * Creates a new authentication token
	 * @return the created authentication token
	 * @throws PingIDSdkException
	 */
	 public AuthenticationTokenResponse createAuthenticationToken() throws PingIDSdkException{
		// creates the authentication token in PingID SDK Server
		 // (Observe: pingIDSdkHelper.createAuthenticationToken code to see how the request body is built)
		ClientContext cc = new ClientContext("approve sign on for Moderno?", ClientContext.TrxType.QRCODE_AUTHENTICATION);
		
		// If the device that scans the QR code is shared - user selection must be performed.
		// By default - the selection is done in the mobile application.
		// However, it can also be performed in the web.
		// "Moderno" Sample support both.
		boolean webUserSelection = PingIDSdkConfiguration.instance().isQrCodeWebUserSelection();
		
		authenticationToken = pingIDSdkHelper.createAuthenticationToken(webUserSelection, cc.toString(), "", "");
		logger.info(String.format("New authentication token created. id: %s", authenticationToken.getId()));
		
		// Create the client response. This response is a specific "Moderno" sample implementation
		AuthenticationTokenResponse authenticationTokenResponse = new AuthenticationTokenResponse();
		authenticationTokenResponse.setStatus(Status.OK.getCode());
		authenticationTokenResponse.setTokenSchemeUri(authenticationToken.getTokenSchemeUri());
		authenticationTokenResponse.setId(authenticationToken.getId());
		return authenticationTokenResponse;
	}
}
