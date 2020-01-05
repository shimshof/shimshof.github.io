package com.pingidentity.pingidsdk.api;

/**
 * Authentication
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Authentication extends PayloadContainer {

	private static final long serialVersionUID = 4229092326325039325L;

    private String id;

    private String deviceId;

    private AuthenticationType authenticationType;

    // TODO: client should transfer us ip address or we should get it from http request
    // TODO: add validation
    private String ipAddress;
    
    // TODO: client should transfer us userAgent or we should get it from http request
    // TODO: add validation
    private String userAgent;

 // TODO: add validation
    private String offlineOTP;
    
    
    private AuthenticationStatus status;

    //a user "trusted" device - the "authenticating" device  
    private Device device;

    //the device that the user try to access from 
    private Device accessingDevice;

    private AuthenticationLevel requiredLevel;

    private AuthenticationLevel level;

    private String pushMessageTitle;

    private String pushMessageBody;

    
    private String clientContext;

    private Reason reason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getOfflineOTP() {
        return offlineOTP;
    }

    public void setOfflineOTP(String offlineOTP) {
        this.offlineOTP = offlineOTP;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    public AuthenticationStatus getStatus() {
        return status;
    }

    public void setStatus(AuthenticationStatus status) {
        this.status = status;
    }

    public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Device getAccessingDevice() {
		return accessingDevice;
	}

	public void setAccessingDevice(Device accessingDevice) {
		this.accessingDevice = accessingDevice;
	}

	public AuthenticationLevel getRequiredLevel() {
		return requiredLevel;
	}

	public void setRequiredLevel(AuthenticationLevel requiredLevel) {
		this.requiredLevel = requiredLevel;
	}

	public AuthenticationLevel getLevel() {
		return level;
	}

	public void setLevel(AuthenticationLevel level) {
		this.level = level;
	}

	public String getPushMessageTitle() {
		return pushMessageTitle;
	}

	public void setPushMessageTitle(String pushMessageTitle) {
		this.pushMessageTitle = pushMessageTitle;
	}

	public String getPushMessageBody() {
		return pushMessageBody;
	}

	public void setPushMessageBody(String pushMessageBody) {
		this.pushMessageBody = pushMessageBody;
	}

	public String getClientContext() {
		return clientContext;
	}

	public void setClientContext(String clientContext) {
		this.clientContext = clientContext;
	}

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Authentication [");
        addToStringIfNeed(sb, "id", id, true);
        addToStringIfNeed(sb, "deviceId", deviceId);
        addToStringIfNeed(sb, "ipAddress", ipAddress);
        addToStringIfNeed(sb, "userAgent", userAgent);
        addToStringIfNeed(sb, "authenticationType", authenticationType);
        addToStringIfNeed(sb, "status", status);
        addToStringIfNeed(sb, "accessingDevice", accessingDevice == null? "n/a" :accessingDevice.toString());
        addToStringIfNeed(sb, "authenticationDevice", device == null? "n/a" : device.toString());
        addToStringIfNeed(sb, "requiredLevel", requiredLevel);
        addToStringIfNeed(sb, "level", level);
        addToStringIfNeed(sb, "reason", reason);
        sb.append(']');
        return sb.toString();
    }

}

