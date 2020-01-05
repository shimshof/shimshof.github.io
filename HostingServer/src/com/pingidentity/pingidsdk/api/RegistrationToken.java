package com.pingidentity.pingidsdk.api;

/**
 * RegistrationToken
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RegistrationToken  extends PayloadContainer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private String pairingKey;
	
	public RegistrationToken() {
	}

	public RegistrationToken(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPairingKey() {
		return pairingKey;
	}

	public void setPairingKey(String pairingKey) {
		this.pairingKey = pairingKey;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("RegistrationToken{");
		sb.append(", id='").append(id).append('\'');
		sb.append(", pairingKey='").append(pairingKey).append('\'');
		sb.append('}');
		return sb.toString();
	}

}
