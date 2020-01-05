package com.pingidentity.pingidsdk.api;

/**
 * UserStatus
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public enum UserStatus {
	ACTIVE("active"), NOT_ACTIVE("not_active"), SUSPENDED("suspended"), BYPASS("bypass"),UNKNOWN("unknown");

	private String name;

	private UserStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	public static UserStatus get(String role) {
		for (UserStatus status : UserStatus.values()) {
			if (status.getName().equals(role.trim())) {
				return status;
			}
		}
		return UserStatus.NOT_ACTIVE;
	}
}
