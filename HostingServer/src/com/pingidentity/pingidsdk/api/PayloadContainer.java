package com.pingidentity.pingidsdk.api;

/**
 * PayloadContainer
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public abstract class PayloadContainer extends BaseResource {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String payload;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
