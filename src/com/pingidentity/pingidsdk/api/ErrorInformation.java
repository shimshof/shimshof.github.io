package com.pingidentity.pingidsdk.api;

/**
 * ErrorInformation
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS,
include=JsonTypeInfo.As.PROPERTY,
property="@type")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public abstract class ErrorInformation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String trackingIdentifier;
	
	public ErrorInformation(){
	}

	public ErrorInformation(String trackingIdentifier) {
		super();
		this.trackingIdentifier = trackingIdentifier;
	}

	public String getTrackingIdentifier() {
		return trackingIdentifier;
	}

	public void setTrackingIdentifier(String trackingIdentifier) {
		this.trackingIdentifier = trackingIdentifier;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ErrorInformation that = (ErrorInformation) o;

		return trackingIdentifier != null ? trackingIdentifier.equals(that.trackingIdentifier) : that.trackingIdentifier == null;
	}

	@Override
	public int hashCode() {
		return trackingIdentifier != null ? trackingIdentifier.hashCode() : 0;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ErrorInformation{");
		sb.append("trackingIdentifier='").append(trackingIdentifier).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
