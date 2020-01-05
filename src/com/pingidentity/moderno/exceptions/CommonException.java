package com.pingidentity.moderno.exceptions;

/**
 * CommonException
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import com.pingidentity.moderno.handlers.Status;

public class CommonException extends Exception {

	private static final long serialVersionUID = 3541270031053076226L;

	private Status status;

	public CommonException(Status status) {
		super();
		this.status = status;
	}

	public CommonException(Status status, String message) {
		super(message);
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

}
