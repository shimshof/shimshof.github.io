package com.pingidentity.moderno.handlers;

/**
 * RequestType
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public enum RequestType {

	AUTHENTICATE_USER("auth_user"), TRANSFER("transfer"), AUTHENTICATE_OFFLINE_USER("auth_offline_user"), RETRY("retry"),GET_AUTHENTICATION_TOKEN_STATUS("get_authentication_token_status"),
	UPDATE_AUTHENTICATION_TOKEN_USERNAME("update_authentication_token_username");


	private String name;
	
	private RequestType(String name){
		this.name = name;
	}
	
	/**
	 * Returns the request type by name
	 * @param name name
	 * @return the request type by name
	 */
	public static RequestType getRequestType(String name) {
		if (name == null || name.trim().isEmpty()) {
			return null;
		}
		for (RequestType type : values()) {
			if (type.name.equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
	
}
