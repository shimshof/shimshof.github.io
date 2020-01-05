package com.pingidentity.moderno.handlers.requestdata;

import java.io.IOException;

import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * JsonRequestData
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public class JsonRequestData {

	private static final Logger logger = LoggerFactory.getLogger(JsonRequestData.class);

	private HttpServletRequest request;
	private JsonObject requestJsonObj;

	public JsonRequestData(HttpServletRequest request) {
		this.request = request;
		initRequest();
	}

	private void initRequest() {
		try {
			String jsonString = IOUtils.toString(new ServletRequestWrapper(request).getInputStream());
			requestJsonObj = new JsonParser().parse(jsonString).getAsJsonObject();
		} catch (IOException e) {
			logger.error("failed to initialize request...");
		}
	}


	public String getValue(String parameterName) {
		if (requestJsonObj == null) {
			return null;
		}
		return getStringFromJson(requestJsonObj, parameterName);
	}
	

	public Cookie[] getCookies() {
		return request.getCookies();
	}

	private static String getStringFromJson(JsonObject jsonObject, String parameterName) {
		try {
			return getFromJson(jsonObject, parameterName).getAsString();
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	private static JsonElement getFromJson(JsonObject jsonObject, String parameterName) {
		return jsonObject.get(parameterName);
	}

}
