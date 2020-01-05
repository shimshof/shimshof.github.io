package com.pingidentity.moderno.servlets;

/**
 * HostingServerServlet
 *
 * The Hosting Server Servlet which serves requests:
 * a. From the web
 * b. From the mobile application
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pingidentity.moderno.handlers.Handler;
import com.pingidentity.moderno.handlers.HandlerFactory;
import com.pingidentity.moderno.handlers.RequestType;
import com.pingidentity.moderno.handlers.Status;
import com.pingidentity.moderno.handlers.requestdata.JsonRequestData;
import com.pingidentity.moderno.model.api.BaseResponse;
@WebServlet(name = "HostingServerServlet", urlPatterns = { "/pidc", "/pingidsdk" })
public class HostingServerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;


	private static final Logger logger = LoggerFactory.getLogger(HostingServerServlet.class);

	/**
	 * The request data - 
	 * Requests come in Json format
	 */
	private JsonRequestData requestData;

	/**
	 * The returned response
	 */
	protected HttpServletResponse response;

	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		handleRequest(request, response);
	}

	/**
	 * Handles the request ("get" or "post")
	 * @param request the request
	 * @param response the response
	 * @throws ServletException 
	 */
	private void handleRequest(HttpServletRequest request, HttpServletResponse response){
		PrintWriter out = null;
		
		// creates request data from the request.
		this.requestData = new JsonRequestData(request);
		this.response = response;

		try {
			out = response.getWriter();

			String responseBody = null;
			
			// get the operation from the request data.
			String requestType = requestData.getValue("operation");
			
			if (!isValidRequest(requestType)) {
				String errorMessage = String.format("invalid request type: %s", requestType);
				handleInvalidRequest(errorMessage);
				return;
			}
			
			// get the relevant handler to deal with the request
			Handler<?> handler = HandlerFactory.instance().getHandler(RequestType.getRequestType(requestType), requestData, response);
			if (handler == null) {
				String errorMessage = "Handler not found";
				handleInvalidRequest(errorMessage);
				return;
			}
			
			// handle the request
			responseBody = handler.handle();
			
			// write the response
			out.print(responseBody);
		} catch (IOException e) {
			logger.error("Can not receieve writer of response", e);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
	
	/**
	 * Checks if the request is valid.
	 * Invalid request is a request with unsupported request type
	 * @param requestType the request type (taken from the request)
	 * @return true iff the request is vald
	 */
	protected boolean isValidRequest(String requestType) {
		RequestType type = RequestType.getRequestType(requestType);
		return type != null;
	}

	/**
	 * Handles the scenario in which the request is invalid
	 * @param errorMessage the error message
	 * @throws IOException IO Exception
	 */
	private void handleInvalidRequest(String errorMessage) throws IOException {
		PrintWriter out = response.getWriter();
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setStatus(Status.FAILED.getCode());
		baseResponse.setDescription(errorMessage);
		String responseBody = Handler.response2String(baseResponse);
		out.print(responseBody);
	}

}
