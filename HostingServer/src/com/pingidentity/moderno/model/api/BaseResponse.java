package com.pingidentity.moderno.model.api;

import com.pingidentity.moderno.handlers.Status;

/**
 * BaseResponse
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public class BaseResponse {

	/**
	 * Status code
	 */
	private int status;

	/**
	 * Response description
	 */
	private String description;

	public BaseResponse() {
		super();
	}

	public BaseResponse(Status status) {
		super();
		this.status = status.getCode();
		this.description = status.getMessage();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
