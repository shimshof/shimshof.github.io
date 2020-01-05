package com.pingidentity.pingidsdk;

/**
 * PingIDSdkException
 *
 * Exception which is thrown from the layer which sends requests to the "PingID SDK" server
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import com.pingidentity.pingidsdk.api.RestAPIError;

public class PingIDSdkException extends Exception{

	private static final long serialVersionUID = 1L;
	
	/**
	 * The returned status from "PingID SDK" server (if exists)
	 */
	private int pingIDSdkResponseStatus;
	
	/**
	 * The error from "PingID SDK" server (if exists)
	 */
	private RestAPIError pingIDSdkError;

	public PingIDSdkException(String msg) {
		super(String.format("PingID SDK Error: %s", msg));
	}
	
	public PingIDSdkException(int status, String msg) {
		this(String.format("PIngID SDK Error. HTTP status: %d. error: %s", status, msg));
		pingIDSdkResponseStatus = status;
	}
	
	public PingIDSdkException(int status, RestAPIError pingIDSdkError) {
		this(pingIDSdkError == null ? "PIngID SDK error is null"
				: String.format("PIngID SDK Error. HTTP status: %d. error: %s", status, pingIDSdkError.toString()));
		pingIDSdkResponseStatus = status;
		setPingIDSdkError(pingIDSdkError);
	}
	
	public int getPingIDSdkResponseStatus() {
		return pingIDSdkResponseStatus;
	}

	public void setPingIDSdkResponseStatus(int pingIDSdkResponseStatus) {
		this.pingIDSdkResponseStatus = pingIDSdkResponseStatus;
	}

	public RestAPIError getPingIDSdkError() {
		return pingIDSdkError;
	}

	public void setPingIDSdkError(RestAPIError pingIDSdkError) {
		this.pingIDSdkError = pingIDSdkError;
	}
}