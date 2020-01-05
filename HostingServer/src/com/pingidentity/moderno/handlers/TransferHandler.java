package com.pingidentity.moderno.handlers;

import java.text.DecimalFormat;
/**
 * TransferHandler
 * This class demonstrates how to send a context to the PingID SDK authentication end-point.
 * The context is passed to the mobile app. Only the customer logic is able to handle this context.
 * 
 * In this sample, money is transferred.
 * 
 * Moderno sever sends the following additional parameters in the authentication request:
 * 
 * 1. client context - In this sample, the client context is: {"msg":"<<<formatted money sum>>>","transactionType":"STEP_UP"}.
 *    The client context is passed as it is to the Moderno app. Note: the client context can be any string. It is up to the customer logic to handle it.
 *    
 * 2. push message title - In this sample, the push message title is: "Transfer Between Accounts"
 * 
 * 3. push message body -  In this sample, the push message body is: "Checking -> Savings\n<<<formatted money sum>>>"
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */

import java.text.ParseException;

import javax.servlet.http.HttpServletResponse;

import com.pingidentity.moderno.model.api.ClientContext;

import org.apache.commons.lang.StringUtils;
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

public class TransferHandler extends Handler<AuthenticationResponse> {

	private static final Logger logger = LoggerFactory.getLogger(TransferHandler.class);

	private SessionData sessionData;

	private String pingIdPayloadMobile = null;

	private String sum = null;

	private AuthenticationResponse authenticationResponse;

	private PingIDSdkHelper pingIDSDKHelper;

	public TransferHandler(JsonRequestData requestData, HttpServletResponse response) {
		super(requestData, response);

		// initialize the "PingID SDK" helper
		pingIDSDKHelper = new PingIDSdkHelper(PingIDSdkConfiguration.instance().getUrl(),
				PingIDSdkConfiguration.instance().getAccountId(), PingIDSdkConfiguration.instance().getApiKey(),
				PingIDSdkConfiguration.instance().getToken(), PingIDSdkConfiguration.instance().getAppId());

		// pingIdPayloadMobile != null if the call is from the mobile
		// application. pingIdPayloadMobile == null, otherwise.
		pingIdPayloadMobile = requestData.getValue(PARAMETER_PING_ID_PAYLOAD);

		sum = requestData.getValue(PARAMETER_SUM);

		// initialize the final response
		authenticationResponse = new AuthenticationResponse();
	}

	@Override
	protected Status processRequest() throws CommonException, PingIDSdkException {

		// Get the session data. In this sample, the user must be already
		// authenticated.
		sessionData = SessionUtils.getSessionData(requestData);
		if (sessionData == null) {
			return Status.FAILED;
		}

		// The browser sends the amount of money the user wishes to transfer.
		// The following code converts the amount of money into a specific
		// format
		Number transferSumAsNumber;
		try {
			transferSumAsNumber = new DecimalFormat("#,###.00").parse(sum);
		} catch (ParseException e) {
			try {
				transferSumAsNumber = Float.parseFloat(sum);
			} catch (NumberFormatException e1) {
				throw new CommonException(Status.FAILED, e.getMessage());
			}
		}
		float transferSum = transferSumAsNumber.floatValue();
		String formattedSum = new DecimalFormat("#,###.00").format(transferSum);
		Status status = Status.FAILED;

		// The following code demonstrates how to set the context in the
		// authentication request
		/*
		 * * Moderno sever sends the following additional parameters in the
		 * authentication request:
		 * 
		 * 1. client context - In this sample, the client context is: {"msg":
		 * "<<<formatted money sum>>>","transactionType":"STEP_UP"}. The client
		 * context is passed as it is to the Moderno app. Note: the client
		 * context can be any string. It is up to the customer logic to handle
		 * it.
		 * 
		 * 2. push message title - In this sample, the push message title is:
		 * "Transfer Between Accounts"
		 * 
		 * 3. push message body - In this sample, the push message body is:
		 * "Checking -> Savings\n<<<formatted money sum>>>"
		 *
		 */

		try {
			
			// create a client context. In this sample, the client context has the following format:
			//  {"msg":"<<<formatted money sum>>>","transactionType":"STEP_UP"}
			ClientContext cc = new ClientContext(formattedSum, ClientContext.TrxType.STEP_UP);
			
			// OTP - one time passcode. If the mobile is unreachable, the server returns an "OTP" status.
			// Then, the browser asks the user to type the OTP from the mobile app
			// Once user types the OTP, this OTP is used here and passed in the authentication request

			String otp = requestData.getValue(PARAMETER_OTP);
			
			// The authentication request is set to contain clientContext, pushMessageTitle, pushMessageBody.
			// All of these are useful for the mobile to get the needed information about the context of the authentication request
			Authentication authentication = approveTransaction(sessionData.getUserName(), "Transfer Between Accounts",
					String.format("Checking -> Savings\n%s", formattedSum), cc.toString(), otp);

			
			// The authentication response handling:
			
			// The server may returned a payload in the response if the request was originated from the mobile
			// This payload eventually should be passed to the mobile app (and the mobile app should pass the returned payload 
			// to the mobile PingID SDK)
			authenticationResponse.setPingIdPayload(authentication.getPayload());
			
			// if the authentication is approved, it means the user approves the money transferral.

			if (AuthenticationStatus.APPROVED == authentication.getStatus()) {
				// transfer the money between the accounts
				authenticationResponse.setPingIdAuthenticated(true);

				Pair<String, Float> subAccountChecking = sessionData.getSubAccountChecking();
				if (subAccountChecking.getValue() - transferSum >= 0) {
					subAccountChecking.setValue(subAccountChecking.getValue() - transferSum);

					Pair<String, Float> subAccountSavings = sessionData.getSubAccountSavings();
					subAccountSavings.setValue(subAccountSavings.getValue() + transferSum);
				}
			}

			// If the device was unreachable, the PingID Server returns an "OTP" (one-time passcode) status.
			// The browser should ask the user to type the OTP from the mobile.
			if (AuthenticationStatus.OTP == authentication.getStatus()) {
				sessionData.setPingidAuthentication(authentication);
			}

			status = PingIdAuthenticationUtils
					.convertPingIdAuthenticationStatusToHostingServerStatus(authentication.getStatus());
		} catch (PingIDSdkException e) {
			// Logic of hosting server expects that when application is disabled
			// means that the server should work without PingID SDK.
			if (PingIDSdkAPI.isApplicationDisabled(e)) {
				logger.info("Your application is disabled");
				return Status.OK;
			}

			throw e;
		}

		return status;
	}

	@Override
	protected AuthenticationResponse createResponse(Status status) {
		authenticationResponse.setStatus(status.getCode());
		authenticationResponse.setDescription(status.getMessage());
		return authenticationResponse;
	}

	private Authentication approveTransaction(String username, String notificationTitle, String notificationMessage,
			String clientContext, String otp) throws PingIDSdkException {
		logger.debug("Approve transaction for user: %s", username);
		Authentication createdAuthentication;
		// If the authentication is an online authentication (otp is blank)
		if (StringUtils.isBlank(otp)) {
			createdAuthentication = pingIDSDKHelper.authenticate(username, pingIdPayloadMobile, null, notificationTitle,
					notificationMessage, clientContext);
		} else {
			// the authentication is an offline authentication
			Authentication activeAuth = sessionData.getPingidAuthentication();
			if (activeAuth == null) {
				logger.error("PingID SDK authentication wasn't started or saved in current session");
				activeAuth = new Authentication();
				activeAuth.setStatus(AuthenticationStatus.REJECTED);
				return activeAuth;
			}
			createdAuthentication = pingIDSDKHelper.authenticateOffline(username, pingIdPayloadMobile,
					activeAuth.getId(), otp);
		}
		logger.info(String.format("Transaction Approval Status: %s for user: %s", createdAuthentication.getStatus(),
				username));
		return createdAuthentication;
	}

}
