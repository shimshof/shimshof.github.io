package com.pingidentity.pingfed;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pingidentity.moderno.api.PingIDSdkConfiguration;

/**
 * 
 * The PingFederateServlet is responsible for sending requests to PingFederate.
 * It creates a request which contains the relevant PingID SDK information
 * (such as client context, whether user approval is required once QR code is scanned etc...)
 * The data is retrieved from the properties (located under: "/env/moderno-props/").
 *  
 * copyright © 2018 Ping Identity. All rights reserved.
 *
 */
@WebServlet(name = "PingFederateServlet", urlPatterns = { "/pingfederate" })
public class PingFederateServlet extends HttpServlet{

	private static final Logger logger = LoggerFactory.getLogger(PingFederateServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		handleRequest(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		handleRequest(request, response);
	}
	
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			String modernoUri = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("/pingfederate"));
			String pingFederateRequest = PingFederateRequestGenerator.generateRequest(PingIDSdkConfiguration.instance().getPingFedRequestData(), modernoUri, null);
			response.sendRedirect(pingFederateRequest);
		} catch (Exception e) {
			logger.error(String.format("An error occured. Pingfederate request was not created successfully. Error: %s", e));
		}
	}	
}
