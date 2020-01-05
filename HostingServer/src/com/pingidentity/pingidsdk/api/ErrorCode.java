package com.pingidentity.pingidsdk.api;

/**
 * ErrorCode
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import javax.ws.rs.core.Response.Status;

public enum ErrorCode {
	INVALID_DATA(Status.BAD_REQUEST),
	REQUEST_FAILED(Status.BAD_REQUEST),
	INVALID_REQUEST(Status.BAD_REQUEST),
	NOT_FOUND(Status.NOT_FOUND),
	UNAUTHORIZED_REQ(Status.UNAUTHORIZED),
	UNEXPECTED_ERROR(Status.INTERNAL_SERVER_ERROR);

	private Status httpStatusCode;

	public Status getHttpStatusCode() {
		return httpStatusCode;
	}

	private ErrorCode(Status httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

}

