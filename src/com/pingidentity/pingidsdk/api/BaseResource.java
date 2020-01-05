package com.pingidentity.pingidsdk.api;

/**
 * BaseResource
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import java.io.Serializable;

public abstract class BaseResource implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void addToStringIfNeed(StringBuilder builder, String key, Object value) {
		addToStringIfNeed(builder, key, value, false);
	}

	protected void addToStringIfNeed(StringBuilder builder, String key, Object value, boolean isFirst) {
		if (value == null) {
			return;
		}

		if (isFirst) {
			builder.append(", ");
		}

		builder.append(key).append("=\"").append(value).append("\"");
	}
}
