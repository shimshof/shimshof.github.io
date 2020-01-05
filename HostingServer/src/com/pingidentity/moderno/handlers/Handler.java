package com.pingidentity.moderno.handlers;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.pingidentity.moderno.exceptions.CommonException;
import com.pingidentity.moderno.handlers.requestdata.JsonRequestData;
import com.pingidentity.moderno.model.api.BaseResponse;
import com.pingidentity.pingidsdk.PingIDSdkException;


/**
 * Handler
 *
 * Base class for handlers.
 * Each handler handles a specific request
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 *
 * @param <T> the response
 */
public abstract class Handler<T extends BaseResponse> {

	private static final Logger logger = LoggerFactory.getLogger(Handler.class);
	public static final String PARAMETER_USERNAME = "user";
	public static final String PARAMETER_PING_ID_PAYLOAD = "pingIdPayload";
	public static final String PARAMETER_AUTH_SESSION_ID = "sessionId";
	public static final String PARAMETER_DEVICE_ID = "deviceId";
	public static final String PARAMETER_SUM = "sum";
	public static final String PARAMETER_COUNTRY_CODE = "countryCode";
	public static final String PARAMETER_PHONE_NUMBER = "phoneNumber";
	public static final String PARAMETER_OTP = "otp";
	public static final String PARAMETER_DEVICE_NICK_NAME = "deviceNickName";
	public static final String PARAMETER_AUTHENTICATION_TOKEN_ID = "authenticationTokenId";
	

	/**
	 * The request data
	 */
	protected JsonRequestData requestData;
	
	/**
	 * The response
	 */
	protected HttpServletResponse response;

	/**
	 * Handler constructor
	 * @param requestData the request data
	 * @param response the response
	 */
	public Handler(JsonRequestData requestData, HttpServletResponse response) {
		this.requestData = requestData;
		this.response = response;
	}

	/**
	 * Handles the request
	 * @return the response as string
	 */
	public String handle() {
		Status status = null;
		PingIDSdkException pingIDSdkex = null;
		try {
			status = processRequest();
		}catch(PingIDSdkException e){
			pingIDSdkex = e;
			logger.error("Failed to handle a request. PingID SDK error", e);
			status = Status.FAILED;
		} catch (CommonException e) {
			logger.error("Failed to handle a request.", e);
			status = e.getStatus();
		} catch (Exception e) {
			logger.error("Failed to handle a request.", e);
			status = Status.FAILED;
		}
		T response = createResponse(status);
		if(pingIDSdkex != null &&pingIDSdkex.getPingIDSdkError() != null){
			response.setDescription(pingIDSdkex.getPingIDSdkError().toString());
		}
		return response2String(response);
	}

	/**
	 * Returns the response as string
	 * @param response the response
	 * @return the response as string
	 */
	public static String response2String(BaseResponse response) {
		return new Gson().toJson(response);
	}
	
	/**
	 * Processes the request
	 * @return the request processing status
	 * @throws CommonException
	 */
	protected abstract Status processRequest() throws CommonException, PingIDSdkException;

	/**
	 * Creates the response
	 * @param status the response status
	 * @return the response
	 */
	protected abstract T createResponse(Status status);
	

}
