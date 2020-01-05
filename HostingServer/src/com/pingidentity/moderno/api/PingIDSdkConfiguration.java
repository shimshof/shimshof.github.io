package com.pingidentity.moderno.api;

import com.pingidentity.pingfed.PingFedData;

/**
 * PingIDSdkConfiguration
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public class PingIDSdkConfiguration {
	
	private static class ConfigurationHolder{
		private static final PingIDSdkConfiguration instance = new PingIDSdkConfiguration();
	}
	
	public static PingIDSdkConfiguration instance() {
		return ConfigurationHolder.instance;
	}
	
	private PingIDSdkConfiguration(){}
	
	protected void initialize(String url, String accountId, String appId, String token,  String apiKey, boolean qrCodeWebUserSelection, PingFedData pingFedRequestData){
		this.url = url;
		this.accountId = accountId;
		this.appId = appId;
		this.token = token;
		this.apiKey = apiKey;
		this.qrCodeWebUserSelection = qrCodeWebUserSelection;
		this.pingFedRequestData = pingFedRequestData;
	}

	private String url;
	
	/**
	 * The account ID
	 */
	private String accountId;
	
	/**
	 * The application ID
	 */
	private String appId;
	
	/**
	 * The account token
	 */
	private String token;	
	
	/**
	 * The account api key
	 */
	private String apiKey;
	
	/**
	 * QR code web user selection
	 */
	private boolean qrCodeWebUserSelection;
	
	/**
	 * PingFederate request data
	 */
	private PingFedData pingFedRequestData;
	
	public String getUrl() {
		return url;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getAppId() {
		return appId;
	}

	public String getToken() {
		return token;
	}

	public String getApiKey() {
		return apiKey;
	}

	public boolean isQrCodeWebUserSelection() {
		return qrCodeWebUserSelection;
	}

	public PingFedData getPingFedRequestData() {
		return pingFedRequestData;
	}
}
