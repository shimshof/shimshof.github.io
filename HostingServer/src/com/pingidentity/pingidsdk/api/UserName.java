package com.pingidentity.pingidsdk.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class UserName extends BaseResource {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String toString() {
		final StringBuilder sb = new StringBuilder("UserName [");
		addToStringIfNeed(sb, "username", getUsername(), true);
		sb.append(']');
		return sb.toString();
    }
}

