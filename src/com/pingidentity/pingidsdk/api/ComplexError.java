package com.pingidentity.pingidsdk.api;

/**
 * ComplexError
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import java.util.ArrayList;
import java.util.List;

public class ComplexError extends Error {
	private static final long serialVersionUID = 1L;
	private String id;
	private List<InternalError> details;
	private ErrorCode code;

	public ComplexError() {
		this(null, null, null, null);
	}

	public ComplexError(ErrorCode code, String message) {
		this(code, message, null, null);
	}

	public ComplexError(ErrorCode code, String message, String target) {
		this(code, message, target, null);
	}

	public ComplexError(ErrorCode code, String message, String target, ErrorInformation errorInformation) {
		super(message, target, errorInformation);
		this.code = code;
		details = new ArrayList<>();
	}

	public List<InternalError> getDetails() {
		return details;
	}

	public void setDetails(List<InternalError> details) {
		this.details = details;
	}

	public void addInternalError(InternalError internalError) {
		details.add(internalError);
	}

	public ErrorCode getCode() {
		return code;
	}

	public void setCode(ErrorCode code) {
		this.code = code;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		ComplexError that = (ComplexError) o;

		if (id != null ? !id.equals(that.id) : that.id != null)
			return false;
		
		if (details != null ? !details.equals(that.details) : that.details != null)
			return false;
		return code == that.code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result + (details != null ? details.hashCode() : 0);
		result = prime * result + (code != null ? code.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("RestAPIError{");
		sb.append("id=").append(id);
		sb.append("details=").append(details);
		sb.append(", code=").append(code);
		sb.append('}');
		return sb.toString();
	}

}
