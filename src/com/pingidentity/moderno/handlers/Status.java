package com.pingidentity.moderno.handlers;

/**
 * Status
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public enum Status {
	
	OK(0, "SUCCESS"),
	FAILED(1000, "FAILED"), //GENERAL failure
	USER_NOT_AUTHENTICATED(1010, "user is not authenticated"),
	AUTHENTICATION_IN_PROGRESS(1012,"Authentication in progress"),
	AUTHENTICATION_DENIED(1014,"Authentication is denied"),
	OTP(1015,"OTP required"),
	TIMEDOUT(1016,"Timedout"),
	SELECT_DEVICE(1018,"second factor authenticator must be approved by one of the user trusted devices"),
	INVALID_INPUT_DATA(1020,"Invalid input data"),
	AUTHENTICATION_IS_IN_NOT_OFFLINE_MODE(1021,"PingID Authentication is not in offline mode"),
	IGNORED_DEVICE(1023,"Ignored Device"),
	USER_NOT_ACTIVE(1024,"User is not active. Please install the mobile application..."),
	INVALID_OTP(1025,"Invalid OTP"),
	BYPASSED_DEVICE(1026,"Bypassed Device"), 
	BLOCKED(1027, "user is blocked"),
    ROOTED_DEVICE(1028,"Rooted Device");

	private int code;
	private String message;

	private Status(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
