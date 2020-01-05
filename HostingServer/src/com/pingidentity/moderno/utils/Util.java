package com.pingidentity.moderno.utils;

/**
 * Util
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Util {

	private static ServletContext servletContext = null;

	public static ServletContext getServletContext() {
		return servletContext;
	}

	public static void setServletContext(ServletContext servletContext) {
		Util.servletContext = servletContext;
	}

	public static String getCookieValue(HttpServletRequest request, String cookieName) {
		Cookie cookies[] = request.getCookies();
		Cookie myCookie = null;
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(cookieName)) {
					myCookie = cookies[i];
					break;
				}
			}
		}
		if (myCookie != null) {
			return myCookie.getValue();
		}
		return "";
	}

	public static void eraseCookie(HttpServletRequest request, HttpServletResponse response) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null)
	        for (int i = 0; i < cookies.length; i++) {
	            cookies[i].setValue("");
	            cookies[i].setPath("/");
	            cookies[i].setMaxAge(0);
	            response.addCookie(cookies[i]);
	        }
	}
}
