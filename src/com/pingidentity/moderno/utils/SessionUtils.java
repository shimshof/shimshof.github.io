package com.pingidentity.moderno.utils;

/**
 * SessionUtils
 * .
 * Implement a simple in-memory session handling
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pingidentity.moderno.handlers.Handler;
import com.pingidentity.moderno.handlers.requestdata.JsonRequestData;

public class SessionUtils {
	
	private static Map<String,SessionData> sessionIdToSessionData = new HashMap<>();
	public static final String SESSION_NAME = "auth_session";
	
	private static final Logger logger = LoggerFactory.getLogger(SessionUtils.class);
	
	/**
	 * Get the session data.
	 * @param requestData the request data
	 * @return the session data.
	 */
	public static synchronized SessionData getSessionData(JsonRequestData requestData){
		
		String sessionId = requestData.getValue(Handler.PARAMETER_AUTH_SESSION_ID);
		if(sessionId != null && !sessionId.trim().isEmpty()){
			return sessionIdToSessionData.get(sessionId);
		}
		
		Optional<Cookie> cookieExists = 
	            Arrays.stream(requestData.getCookies())
	            .filter(cookie -> cookie.getName().equals(SESSION_NAME))
	            .findAny();
		if(cookieExists.isPresent() && sessionIdToSessionData.containsKey(cookieExists.get().getValue())){
			return sessionIdToSessionData.get(cookieExists.get().getValue());
		}
		return null;	
	}
	
	public static synchronized SessionData getSessionData(String sessionId) {
		if (sessionId != null && !sessionId.trim().isEmpty()) {
			return sessionIdToSessionData.get(sessionId);
		}

		return null;
	}

	/**
	 * Sets the session data
	 * @param response the response
	 * @param sessionData the session data
	 */
	public static synchronized void setSessionData(HttpServletResponse response,SessionData sessionData){
		logger.info(String.format("create session: %s. Session Data: %s", sessionData.getSessionId(), sessionData));
		sessionIdToSessionData.put(sessionData.getSessionId(), sessionData);
		Cookie myCookie = new Cookie(SESSION_NAME, sessionData.getSessionId());
		myCookie.setMaxAge(-1);
		response.addCookie(myCookie);
	}
}
