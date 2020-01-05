package com.pingidentity.pingidsdk.api;

/**
 * UserWithDevices
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class UserWithDevices extends User{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Device> devices;

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}	
}

