package com.pingidentity.pingidsdk.api;

/**
 * RestAPIError
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public class RestAPIError extends ComplexError {

	private static final long serialVersionUID = 517955706694002405L;

	public RestAPIError() {
	}

	public RestAPIError(ErrorCode code, String message) {
		super(code, message, null);
	}

	public RestAPIError(ErrorCode code, String message, String target) {
		super(code, message, target, null);
	}

	public RestAPIError(ErrorCode code, String message, String target, ErrorInformation errorInformation) {
		super(code, message, target, errorInformation);
	}
}
