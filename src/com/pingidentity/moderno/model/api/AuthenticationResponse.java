package com.pingidentity.moderno.model.api;

import java.util.List;


import com.pingidentity.moderno.handlers.Status;
import com.pingidentity.pingidsdk.api.Authentication;
import com.pingidentity.pingidsdk.api.Device;

/**
 * AuthenticationResponse
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public class AuthenticationResponse extends BaseResponse {

	/**
	 * The "PingID for Customer" server payload.
	 * Null if the request is from the web. No null if the request is from the mobile application.
	 * This payload will be used by the "PingID SDK" integrated into the mobile application
	 */
	private String pingIdPayload;

	/**
	 * The registration token.
	 * Null if the user is active in "PingID SDK" (has at least one paired device)/
	 * Otherwise, the user should pair his/her device using this token
	 */
	private String registrationToken;

	/**
	 * The session ID. returned only if the hosting server creates session for the authetication.
	 * For example, when the user authenticate and OTP (One-Time Passcode) is required to complete the authentication
	 */
	private String authSessionID;
	
	/**
	 * True iff the authentication with "PingID for Customer" is successful
	 */
	private boolean isPingIdAuthenticated;
	
	/**
	 * The authenticating device data
	 */
	private Device currentAuthenticatingDeviceData;
	
	/**
	 * The available devices for authentication.
	 * Not empty iff "PingID for Customer" server cannot choose which device to use for the authentication.
	 * For example, if the user has no primary device and more than one trusted devices
	 */
	private List<Device> availableDevicesForAuthentication;
	
	/**
	 * The created "PingID SDK" authentication
	 */
	private Authentication pingIDSdkAuthentication;

	private String sum;

	public AuthenticationResponse() {
	}

	public AuthenticationResponse(Status status) {
		super(status);
	}

	public String getPingIdPayload() {
		return pingIdPayload;
	}

	public void setPingIdPayload(String pingIdPayload) {
		this.pingIdPayload = pingIdPayload;
	}

	public String getRegistrationToken() {
		return registrationToken;
	}

	public void setRegistrationToken(String registrationToken) {
		this.registrationToken = registrationToken;
	}

	public boolean isPingIdAuthenticated() {
		return isPingIdAuthenticated;
	}

	public void setPingIdAuthenticated(boolean isPingIdAuthenticated) {
		this.isPingIdAuthenticated = isPingIdAuthenticated;
	}

	public String getAuthSessionID() {
		return authSessionID;
	}

	public void setAuthSessionID(String authSessionID) {
		this.authSessionID = authSessionID;
	}
	
	public Device getCurrentAuthenticatingDeviceData() {
		return currentAuthenticatingDeviceData;
	}

	public void setCurrentAuthenticatingDeviceData(Device currentAuthenticatingDeviceData) {
		this.currentAuthenticatingDeviceData = currentAuthenticatingDeviceData;
	}

	public List<Device> getAvailableDevicesForAuthentication() {
		return availableDevicesForAuthentication;
	}

	public void setAvailableDevicesForAuthentication(List<Device> availableDevicesForAuthentication) {
		this.availableDevicesForAuthentication = availableDevicesForAuthentication;
	}

	public Authentication getPingIDSdkAuthentication() {
		return pingIDSdkAuthentication;
	}

	public void setPingIDSdkAuthentication(Authentication pingIDSdkAuthentication) {
		this.pingIDSdkAuthentication = pingIDSdkAuthentication;
	}

	public String getSum() {
		return sum;
	}

	public void setSum(String sum) {
		this.sum = sum;
	}

}
