package com.pingidentity.pingidsdk.api;

public class FinalizePairing {
	
	private String otp;
	
	private String deviceNickname;

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getDeviceNickname() {
		return deviceNickname;
	}

	public void setDeviceNickname(String deviceNickname) {
		this.deviceNickname = deviceNickname;
	}

}
