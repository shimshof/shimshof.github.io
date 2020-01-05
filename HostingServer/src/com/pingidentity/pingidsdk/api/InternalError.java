package com.pingidentity.pingidsdk.api;

/**
 * InternalError
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InternalError extends Error {

	private static final long serialVersionUID = 231384609275595969L;

	InternalErrorCode code;
	
	public InternalError(){
	}
	
	public InternalError(InternalErrorCode code, String message) {
		this(code, message, null);
	}

	public InternalError(InternalErrorCode code, String message, String target) {
		super(message, target);
		this.code = code;
	}

	public InternalErrorCode getCode() {
		return code;
	}

	public void setCode(InternalErrorCode code) {
		this.code = code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		InternalError other = (InternalError) obj;
		if (code != other.code)
			return false;
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("InternalError{");
		sb.append("code=").append(code);
		sb.append(", message=").append(message);
		sb.append(", target=").append(target);
		sb.append('}');
		return sb.toString();
	}
}
