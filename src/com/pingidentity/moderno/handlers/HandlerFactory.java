package com.pingidentity.moderno.handlers;

import javax.servlet.http.HttpServletResponse;

import com.pingidentity.moderno.handlers.requestdata.JsonRequestData;
import com.pingidentity.moderno.qrcode.GetAuthenticationTokenStatusHandler;
import com.pingidentity.moderno.qrcode.UpdateAuthenticationTokenUserNameHandler;

/**
 * HandlerFactory
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public class HandlerFactory {

	private HandlerFactory() {
	};

	private static class HandlerFactoryHolder {
		private static final HandlerFactory instance = new HandlerFactory();
	}

	public static HandlerFactory instance() {
		return HandlerFactoryHolder.instance;
	}

	/**
	 * Returns the handler to handle the request by the request type
	 * 
	 * @param requestType
	 *            the request type
	 * @param requestData
	 *            the request data
	 * @param response
	 *            the response
	 * @return the handler to handle the request by the request type
	 */
	public Handler<?> getHandler(RequestType requestType, JsonRequestData requestData, HttpServletResponse response) {
		if (requestType == null) {
			return null;
		}

		switch (requestType) {
		case AUTHENTICATE_USER:
			return new AuthenticateOnlineWithPingIDSdk(requestData, response);
		case AUTHENTICATE_OFFLINE_USER:
			return new AuthenticateOfflineWithPingIDSdk(requestData, response);
		case TRANSFER:
			return new TransferHandler(requestData, response);
		case RETRY:
			return new RetryHandler(requestData, response);
		case GET_AUTHENTICATION_TOKEN_STATUS:
			return new GetAuthenticationTokenStatusHandler(requestData, response);
		case UPDATE_AUTHENTICATION_TOKEN_USERNAME:
			return new UpdateAuthenticationTokenUserNameHandler(requestData, response);
		default:
			break;
		}

		return null;
	}

}
