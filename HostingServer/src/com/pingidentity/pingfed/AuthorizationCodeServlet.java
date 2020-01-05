package com.pingidentity.pingfed;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pingidentity.moderno.handlers.AuthenticateOnlineWithPingIDSdk.AccountDetails;
import com.pingidentity.moderno.utils.SessionData;
import com.pingidentity.moderno.utils.SessionUtils;

@WebServlet(name = "AuthorizationCodeServlet", urlPatterns = { "/authCallback" })
public class AuthorizationCodeServlet extends HttpServlet{

	private static final Logger logger = LoggerFactory.getLogger(AuthorizationCodeServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Random random = new Random(System.currentTimeMillis());
	
	private static final Map<String, AccountDetails> accountDetailsDB = new ConcurrentHashMap<>();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			handleRequest(request, response);
		} catch (Exception e) {
			logger.error("failed to handle post request", e);
			throw new ServletException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			handleRequest(request, response);
		} catch (Exception e) {
			logger.error("failed to handle get request", e);
			throw new ServletException(e);
		}
	}
	
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String code = request.getParameter("code");
		String transactionApprovalSessionId = request.getParameter("state");
		if(code == null){
			
			handleDenial(response, transactionApprovalSessionId);
			return;
		}
		
		String redirectUri = new StringBuilder(request.getRequestURL()).toString();
		
		
		
		AccessToken accessToken = PingfedAPI.getAccessToken(code, redirectUri);
		/*
		 * !!!NOTE: The returned access token should be validated (not demonstrated in this sample).
		 * Please refer to PingFederate Documentation for further details.
		 * 
		 * The ID token should be validated as well
		 */
		
		logger.debug(String.format("accessToken = %s", accessToken));
		if(accessToken != null){
			
			/*
			 * The returned "PingID SDK" authentication status can be used, for example, to reduce the user permissions.
			 * There are some cases in which the user hasn't passed MFA but PingFederate may still return the access token (depends on the PingID SDK configuration).
			 * Read "PingID SDK - PingFederate Integration" Documentation for pingid.sdk.status property possible values (which contains the authentication level)
			 */
			String pingIDSdkAuthenticationstatus = AccessTokenParser.getPingIDSdkAuthenticationStatusFromTheAccessToken(accessToken.getAccess_token());

		    logger.debug(String.format("PingID SDK authentication status = %s", pingIDSdkAuthenticationstatus));
			
			UserInfo userInfo = UserInfoHandler.getUserInfo(accessToken.getAccess_token(), accessToken.getId_token());
			logger.info(String.format("userInfo = %s", userInfo));
			if(userInfo != null){
				if(transactionApprovalSessionId != null){
					updateSessionDataWithTransferredSum(transactionApprovalSessionId);
				}else{
					createSessionData(response, userInfo);
				}
				
				response.sendRedirect("jsp/Transactions.jsp");
			}else{
				handleDenial(response, transactionApprovalSessionId);
			}
		}else{
			handleDenial(response, transactionApprovalSessionId);
		}
	}
	
	private void handleDenial(HttpServletResponse response, String transactionApprovalSessionId) throws IOException{
		if(transactionApprovalSessionId != null){
			response.sendRedirect("jsp/Transactions.jsp");
		} else{
			response.sendRedirect("denied.html");
		}
	}
	
	
	private void updateSessionDataWithTransferredSum(String transactionApprovalSessionId){
		SessionData sessionData = SessionUtils.getSessionData(transactionApprovalSessionId);
		Pair<String, Float> subAccountChecking = sessionData.getSubAccountChecking();
		float transferSumAsNumber = getTranferFloatValue(sessionData.getTransferSum());
		if (subAccountChecking.getValue() - transferSumAsNumber >= 0) {
			subAccountChecking.setValue(subAccountChecking.getValue() - transferSumAsNumber);
			
			Pair<String, Float> subAccountSavings = sessionData.getSubAccountSavings();
			subAccountSavings.setValue(subAccountSavings.getValue() + transferSumAsNumber);
		}
		
		sessionData.setTransactionApproved(true);
	}

	private void createSessionData(HttpServletResponse response, UserInfo userInfo) {
		String username = userInfo.getSub();
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
		sessionData.setPingFederateSession(true);
		SessionUtils.setSessionData(response, sessionData);
	}
	
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
	
	private float getTranferFloatValue(String sum) {
		Number transferSumAsNumber;
		try {
			transferSumAsNumber = new DecimalFormat("#,###.00").parse(sum);
		} catch (ParseException e) {
			try {
				transferSumAsNumber = Float.parseFloat(sum);
			} catch (NumberFormatException e1) {
				return 0;
			}
		}
		float transferSum = transferSumAsNumber.floatValue();
		return transferSum;
	}
	
	 
	private synchronized int generateRandom(int limit) {
		return random.nextInt(limit);
	}
}
