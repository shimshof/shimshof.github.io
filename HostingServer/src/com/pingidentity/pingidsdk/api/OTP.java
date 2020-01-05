package com.pingidentity.pingidsdk.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OTP extends PayloadContainer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String otp;

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
	
}
