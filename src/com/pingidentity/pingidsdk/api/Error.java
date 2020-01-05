package com.pingidentity.pingidsdk.api;

/**
 * Error
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Error implements Serializable {
	
	private static final long serialVersionUID = 4249098641965159183L;

	String message;
	String target;
	ErrorInformation errorInformation;
	
	public Error(){
		
	}

	public Error(String message) {
		super();
		this.message = message;
	}

	public Error(String message, String target) {
		super();
		this.message = message;
		this.target = target;
	}

	public Error(String message, String target, ErrorInformation errorInformation) {
		this.message = message;
		this.target = target;
		this.errorInformation = errorInformation;
	}

	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}
	
	@JsonProperty("innerError")
	public ErrorInformation getErrorInformation() {
		return errorInformation;
	}

	public void setErrorInformation(ErrorInformation errorInformation) {
		this.errorInformation = errorInformation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Error error = (Error) o;

		if (message != null ? !message.equals(error.message) : error.message != null) return false;
		if (target != null ? !target.equals(error.target) : error.target != null) return false;
		return errorInformation != null ? errorInformation.equals(error.errorInformation) : error.errorInformation == null;
	}

	@Override
	public int hashCode() {
		int result = message != null ? message.hashCode() : 0;
		result = 31 * result + (target != null ? target.hashCode() : 0);
		result = 31 * result + (errorInformation != null ? errorInformation.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Error{");
		sb.append("message='").append(message).append('\'');
		sb.append(", target='").append(target).append('\'');
		sb.append(", errorInformation=").append(errorInformation);
		sb.append('}');
		return sb.toString();
	}
}
