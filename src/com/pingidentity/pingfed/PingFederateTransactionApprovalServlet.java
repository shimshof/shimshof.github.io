package com.pingidentity.pingfed;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.pingidentity.moderno.api.PingIDSdkConfiguration;
import com.pingidentity.moderno.exceptions.CommonException;
import com.pingidentity.moderno.handlers.Status;
import com.pingidentity.moderno.model.api.ClientContext;
import com.pingidentity.moderno.utils.SessionData;
import com.pingidentity.moderno.utils.SessionUtils;
import com.pingidentity.moderno.utils.Util;


@WebServlet(name = "PingFederateTransferApprovalServlet", urlPatterns = { "/pingfederate/transaction" })
public class PingFederateTransactionApprovalServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(PingFederateTransactionApprovalServlet.class);
	
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			handleRequest(request, response);
		} catch (Exception e) {
			logger.error("failed to handle get request", e);
			throw new ServletException(e);
		}
	}
	
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sum = request.getParameter("sum");
		String modernoUri = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("/pingfederate"));
		PingFedData transactionApprovalData = createTransactionApprovalData(getFormattedSum(sum));
		String sessionId = Util.getCookieValue(request, SessionUtils.SESSION_NAME);
		SessionData sessionData = SessionUtils.getSessionData(sessionId);
		sessionData.setTransferSum(sum);
		String transferRequest = PingFederateRequestGenerator.generateRequest(transactionApprovalData, modernoUri, sessionId);
		response.sendRedirect(transferRequest);
	}
	
	private static String getFormattedSum(String sum) throws CommonException {
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
		String formattedSum =new DecimalFormat("#,###.00").format(transferSum);
		return formattedSum;
	}

	
	
	private PingFedData createTransactionApprovalData(String formattedSum){
		PingFedData transactionApprovalData = (new PingFedData.Builder()).build(PingIDSdkConfiguration.instance().getPingFedRequestData());
		ClientContext cc = new ClientContext(formattedSum, ClientContext.TrxType.STEP_UP);
		
		transactionApprovalData.setClientContext(cc.toString());
		transactionApprovalData.setPushMessageTitle("Transfer Between Accounts");
		transactionApprovalData.setPushMessageBody(String.format("Checking -> Savings\n%s", formattedSum));
		
		transactionApprovalData.setQrCodeClientContext(cc.toString());
		transactionApprovalData.setQrCodePushMessageTitle("Transfer Between Accounts");
		transactionApprovalData.setQrCodePushMessageBody(String.format("Checking -> Savings\n%s", formattedSum));
		transactionApprovalData.setSkipSuccessScreens(true);
		
		JsonObject adapterContext = new JsonObject();
		adapterContext.addProperty("transactionApproval", true);
		transactionApprovalData.setAdapterContext(adapterContext.toString());
		
		return transactionApprovalData;
	}
	
	


}
