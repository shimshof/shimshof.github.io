package com.pingidentity.pingidsdk.api;

/**
 * Device
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Device extends BaseResource {

	private static final long serialVersionUID = -876289734107782969L;

	private String deviceType;
	private String id;
	private String deviceName;
	private String deviceFingerprint;
	private String deviceRole;
	private Date enrollmentTime;
	private String applicationId;
	private Date bypassExpiration;
	private boolean bypassed;
	private boolean rooted;

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceFingerprint() {
		return deviceFingerprint;
	}

	public void setDeviceFingerprint(String deviceFingerprint) {
		this.deviceFingerprint = deviceFingerprint;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceRole() {
		return deviceRole;
	}

	public void setDeviceRole(String deviceRole) {
		this.deviceRole = deviceRole;
	}

	public Date getEnrollmentTime() {
		return enrollmentTime;
	}

	public void setEnrollmentTime(Date enrollmentTime) {
		this.enrollmentTime = enrollmentTime;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}	

	public Date getBypassExpiration() {
		return bypassExpiration;
	}

	public void setBypassExpiration(Date bypassExpiration) {
		this.bypassExpiration = bypassExpiration;
	}	
	
	public boolean isBypassed() {
		return bypassed;
	}

	public void setBypassed(boolean bypassed) {
		this.bypassed = bypassed;
	}

	public boolean isRooted() {
		return rooted;
	}

	public void setRooted(boolean rooted) {
		this.rooted = rooted;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Device{");
		sb.append("deviceType='").append(deviceType).append('\'');
		sb.append(", id='").append(id).append('\'');
		sb.append(", applicationId='").append(applicationId).append('\'');
		sb.append(", deviceName='").append(deviceName).append('\'');
		sb.append(", deviceRole='").append(deviceRole).append('\'');
		sb.append(", enrollmentTime='").append(enrollmentTime).append('\'');
		sb.append(", bypassExpiration='").append(bypassExpiration).append('\'');
		sb.append(", rooted='").append(rooted).append('\'');
		sb.append('}');
		return sb.toString();
	}
}

