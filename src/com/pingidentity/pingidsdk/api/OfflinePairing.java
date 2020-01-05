package com.pingidentity.pingidsdk.api;
/**
 * Authentication
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class OfflinePairing extends BaseResource{

	private static final long serialVersionUID = -2213100491287817446L;
	
	private String phoneNumber;
	
	private String message;
	
	private String sender;
	
	private boolean automaticPairing;
	
	private String id;
	
	private DeviceType deviceType;


	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public boolean isAutomaticPairing() {
		return automaticPairing;
	}

	public void setAutomaticPairing(boolean automaticPairing) {
		this.automaticPairing = automaticPairing;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	@Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Pairing [");
        addToStringIfNeed(sb, "phoneNumber", phoneNumber, true);
        addToStringIfNeed(sb, "message", message);
        addToStringIfNeed(sb, "sender", sender);
        addToStringIfNeed(sb, "automaticPairing", automaticPairing);
        addToStringIfNeed(sb, "id", id);
        addToStringIfNeed(sb, "deviceType", deviceType);
        sb.append(']');
        return sb.toString();
    }

}
