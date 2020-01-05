package com.pingidentity.pingfed;

import java.util.Properties;

/**
 * This class holds the dynamic data which is sent whenever redirecting to PingFederate.
 * This data is used by the PingID SDK Adapter
 * Created by Ping Identity on 11/06/18.
 * Copyright © 2018 Ping Identity. All rights reserved.
 */
public class PingFedData {
	/*
	 * PingFederate Authorization end point
	 */
	private String pingFedBaseEndPoint;
	
	/*
	 * Whether user approval is required once the user successfully scanned the QR code
	 */
	private Boolean qrCodeUserApprovalRequired;
	
	/*
	 * In case of QR code - if the device which successfully scanned the QR code is paired with more than one user,
	 * whether the mobile application or the web should select the user
	 */
	private Boolean webUserSelection;
	
	/*
	 * Any client context that the customer server wishes to pass the mobile application that successfully scanned the QR code
	 */
	private String qrCodeClientContext;
	
	/*
	 * The Push message title. Relevant only when webUserSelection = true
	 */
	private String qrCodePushMessageTitle;
	
	/*
	 * The Push message body. Relevant only when webUserSelection = true
	 */
	private String qrCodePushMessageBody;
	
	/*
	 * Any client context that the customer server wishes to pass the mobile application (during an authentication which is not a QR code authentication)
	 */
	private String clientContext;
	
	/*
	 * The Push message title. (for QR code authentication, use: qrCodePushMessageTitle)
	 */
	private String pushMessageTitle; 
	
	/*
	 * The Push message title. (for QR code authentication, use: qrCodePushMessageBody)
	 */
	private String pushMessageBody;
	
	/*
	 * Set a value here if you wish to override the existing application ID which is configured in the PingFederate PingID SDK Adapter
	 */
	private String applicationId;
	
	/*
	 * It is recommended to sign the dynamic data sent to PingFederate
	 */
	private String pingFedRequestPrivateKey;
	
	/*
	 * The signing algorithm
	 */
	private String signAlgorithm;
	
	/*
	 * Whether to encode (base64) the dynamic data sent to PingFederate
	 */
	private boolean sendDynamicDataWithoutEncoding;
	
	/*
	 * If true - each of the dynamic data attribute will be sent as a request claim (instead of combining all the attributes into one Json)
	 */
	private boolean sendDynamicDataAsRequestClaims;
	
	/*
	 * The adapter context. Optionally - a json. This parameter is passed to the adapter UI as a velocity parameter.
	 * One can use it to customize the adapter UI according to the context
	 */
	private String adapterContext;
	
	/*
	 * Whether the adapter should skip the success screens (this parameter, if set, overrides the adapter configuration)
	 */
	private Boolean skipSuccessScreens;
	
	/*
	 * Whether the adapter should skip the error screens (this parameter, if set, overrides the adapter configuration)
	 */
	private Boolean skipErrorScreens;

	/*
	 * Whether the adapter should skip the timeout screens (this parameter, if set, overrides the adapter configuration)
	 */
	private Boolean skipTimeoutScreens;
	
	public Boolean isQrCodeUserApprovalRequired() {
		return qrCodeUserApprovalRequired;
	}
	
	public Boolean isWebUserSelection() {
		return webUserSelection;
	}

	public String getQrCodeClientContext() {
		return qrCodeClientContext;
	}

	public String getQrCodePushMessageTitle() {
		return qrCodePushMessageTitle;
	}

	public String getQrCodePushMessageBody() {
		return qrCodePushMessageBody;
	}
	
	public String getClientContext() {
		return clientContext;
	}

	public String getPushMessageTitle() {
		return pushMessageTitle;
	}

	public String getPushMessageBody() {
		return pushMessageBody;
	}

	public String getPingFedRequestPrivateKey() {
		return pingFedRequestPrivateKey;
	}

	public String getSignAlgorithm() {
		return signAlgorithm;
	}
	
	public String getPingFedBaseEndPoint() {
		return pingFedBaseEndPoint;
	}
	
	public String getApplicationId() {
		return applicationId;
	}
	
	public Boolean getSkipSuccessScreens() {
		return skipSuccessScreens;
	}

	public void setSkipSuccessScreens(Boolean skipSuccessScreens) {
		this.skipSuccessScreens = skipSuccessScreens;
	}

	public Boolean getSkipErrorScreens() {
		return skipErrorScreens;
	}

	public void setSkipErrorScreens(Boolean skipErrorScreens) {
		this.skipErrorScreens = skipErrorScreens;
	}

	public Boolean getSkipTimeoutScreens() {
		return skipTimeoutScreens;
	}

	public void setSkipTimeoutScreens(Boolean skipTimeoutScreens) {
		this.skipTimeoutScreens = skipTimeoutScreens;
	}

	public boolean getSendDynamicDataWithoutEncoding() {
		return sendDynamicDataWithoutEncoding;
	}

	public boolean getSendDynamicDataAsRequestClaims() {
		return sendDynamicDataAsRequestClaims;
	}
	
	public void setPingFedBaseEndPoint(String pingFedBaseEndPoint) {
		this.pingFedBaseEndPoint = pingFedBaseEndPoint;
	}

	public void setQrCodeUserApprovalRequired(Boolean qrCodeUserApprovalRequired) {
		this.qrCodeUserApprovalRequired = qrCodeUserApprovalRequired;
	}

	public void setWebUserSelection(Boolean webUserSelection) {
		this.webUserSelection = webUserSelection;
	}

	public void setQrCodeClientContext(String qrCodeClientContext) {
		this.qrCodeClientContext = qrCodeClientContext;
	}

	public void setQrCodePushMessageTitle(String qrCodePushMessageTitle) {
		this.qrCodePushMessageTitle = qrCodePushMessageTitle;
	}

	public void setQrCodePushMessageBody(String qrCodePushMessageBody) {
		this.qrCodePushMessageBody = qrCodePushMessageBody;
	}

	public void setClientContext(String clientContext) {
		this.clientContext = clientContext;
	}

	public void setPushMessageTitle(String pushMessageTitle) {
		this.pushMessageTitle = pushMessageTitle;
	}

	public void setPushMessageBody(String pushMessageBody) {
		this.pushMessageBody = pushMessageBody;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public void setPingFedRequestPrivateKey(String pingFedRequestPrivateKey) {
		this.pingFedRequestPrivateKey = pingFedRequestPrivateKey;
	}

	public void setSignAlgorithm(String signAlgorithm) {
		this.signAlgorithm = signAlgorithm;
	}

	public void setSendDynamicDataWithoutEncoding(boolean sendDynamicDataWithoutEncoding) {
		this.sendDynamicDataWithoutEncoding = sendDynamicDataWithoutEncoding;
	}

	public void setSendDynamicDataAsRequestClaims(boolean sendDynamicDataAsRequestClaims) {
		this.sendDynamicDataAsRequestClaims = sendDynamicDataAsRequestClaims;
	}
	
	public String getAdapterContext() {
		return adapterContext;
	}

	public void setAdapterContext(String adapterContext) {
		this.adapterContext = adapterContext;
	}

	public boolean isEmpty(){
		return qrCodeUserApprovalRequired == null && webUserSelection == null && qrCodeClientContext == null && qrCodePushMessageTitle == null &&
				qrCodePushMessageBody == null && clientContext == null && pushMessageBody == null && pushMessageTitle == null && applicationId == null
				&& skipSuccessScreens == null && skipErrorScreens == null && skipTimeoutScreens == null && adapterContext == null;
	}

	public static class Builder {
		
		private Boolean qrCodeUserApprovalRequired;
		
		private Boolean webUserSelection;
		
		private String qrCodeClientContext;
		
		private String qrCodePushMessageTitle;
		
		private String qrCodePushMessageBody;
		
		private String clientContext;
		
		private String pushMessageTitle; 
		
		private String pushMessageBody;
		
		private String applicationId;
		
		private String pingFedRequestPrivateKey;
		
		private String signAlgorithm;
		
		private String pingFedBaseEndPoint;
		
		private boolean sendDynamicDataWithoutEncoding;
		
		private boolean sendDynamicDataAsRequestClaims;
		
		private String adapterContext;
		
		private Boolean skipSuccessScreens;

		private Boolean skipErrorScreens;

		private Boolean skipTimeoutScreens;
		
		public PingFedData build(PingFedData pingFedData){
			
			if(pingFedData == null){
				return build();
			}
			
			qrCodeUserApprovalRequired = pingFedData.isQrCodeUserApprovalRequired();
			webUserSelection = pingFedData.isWebUserSelection();
			qrCodeClientContext = pingFedData.getQrCodeClientContext();
			qrCodePushMessageTitle = pingFedData.getQrCodePushMessageTitle();
			qrCodePushMessageBody = pingFedData.getQrCodePushMessageBody();
			clientContext = pingFedData.getClientContext();
			pushMessageTitle = pingFedData.getPushMessageTitle();
			pushMessageBody = pingFedData.getPushMessageBody();
			pingFedRequestPrivateKey = pingFedData.getPingFedRequestPrivateKey();
			signAlgorithm =  pingFedData.getSignAlgorithm();
			pingFedBaseEndPoint = pingFedData.getPingFedBaseEndPoint();
			applicationId =  pingFedData.getApplicationId();
			sendDynamicDataWithoutEncoding = pingFedData.getSendDynamicDataWithoutEncoding();
			sendDynamicDataAsRequestClaims = pingFedData.getSendDynamicDataAsRequestClaims();
			adapterContext = pingFedData.getAdapterContext();
			skipSuccessScreens = pingFedData.getSkipSuccessScreens();
			skipErrorScreens = pingFedData.getSkipErrorScreens();
			skipTimeoutScreens = pingFedData.getSkipTimeoutScreens();

			return build();
		}

		public PingFedData build(Properties prop){
			String userApprovalStr = prop.getProperty("pingfed_qr_code_user_approval_required");
			if(userApprovalStr != null){
				qrCodeUserApprovalRequired = Boolean.parseBoolean(userApprovalStr);
			}
			
			String webUserSelectionStr = prop.getProperty("pingfed_qr_code_web_user_selection");
			if(webUserSelectionStr != null){
				webUserSelection = Boolean.parseBoolean(webUserSelectionStr);
			}
			
			qrCodeClientContext = prop.getProperty("pingfed_qr_code_client_context");
			qrCodePushMessageTitle = prop.getProperty("pingfed_qr_code_push_message_title");
			qrCodePushMessageBody = prop.getProperty("pingfed_qr_code_push_message_body");
			clientContext = prop.getProperty("pingfed_client_context");
			pushMessageTitle = prop.getProperty("pingfed_push_message_title");
			pushMessageBody = prop.getProperty("pingfed_push_message_body");
			pingFedRequestPrivateKey = prop.getProperty("pingfed_base64_private_key");
			signAlgorithm =  prop.getProperty("pingfed_sign_alg");
			pingFedBaseEndPoint = prop.getProperty("pingfed_base_endpoint");
			applicationId =  prop.getProperty("pingfed_application_id");
			adapterContext = prop.getProperty("pingfed_adapter_context");
			
			String sendDynamicDataWithoutEncodingStr = prop.getProperty("pingfed_send_dynamic_data_without_encoding");
			if(sendDynamicDataWithoutEncodingStr != null){
				sendDynamicDataWithoutEncoding = Boolean.parseBoolean(sendDynamicDataWithoutEncodingStr);
			}
			
			String sendDynamicDataAsRequestClaimsStr = prop.getProperty("pingfed_send_dynamic_data_as_request_claims");
			if(sendDynamicDataAsRequestClaimsStr != null){
				sendDynamicDataAsRequestClaims = Boolean.parseBoolean(sendDynamicDataAsRequestClaimsStr);
			}
			
			String skipSuccessScreensStr = prop.getProperty("pingfed_skip_success_screens");
			if(skipSuccessScreensStr != null){
				skipSuccessScreens = Boolean.parseBoolean(skipSuccessScreensStr);
			}
			
			String skipErrorScreensStr = prop.getProperty("pingfed_skip_error_screens");
			if(skipErrorScreensStr != null){
				skipErrorScreens = Boolean.parseBoolean(skipErrorScreensStr);
			}
			
			String skipTimeoutScreensStr = prop.getProperty("pingfed_skip_timeout_screens");
			if(skipTimeoutScreensStr != null){
				skipTimeoutScreens = Boolean.parseBoolean(skipTimeoutScreensStr);
			}
			
			return build();
		}
		
		private PingFedData build(){
			PingFedData pingFedData = new PingFedData();
			pingFedData.qrCodeUserApprovalRequired = qrCodeUserApprovalRequired;
			pingFedData.qrCodeClientContext = qrCodeClientContext;
			pingFedData.qrCodePushMessageTitle = qrCodePushMessageTitle;
			pingFedData.qrCodePushMessageBody = qrCodePushMessageBody;
			pingFedData.clientContext = clientContext;
			pingFedData.pushMessageTitle = pushMessageTitle;
			pingFedData.pushMessageBody = pushMessageBody;
			pingFedData.pingFedRequestPrivateKey = pingFedRequestPrivateKey != null ? pingFedRequestPrivateKey.trim() : null;
			pingFedData.signAlgorithm = signAlgorithm != null ? signAlgorithm.trim() : null;
			pingFedData.pingFedBaseEndPoint = pingFedBaseEndPoint != null ? pingFedBaseEndPoint.trim() : null;
			pingFedData.applicationId = applicationId != null ? applicationId.trim() : null;
			pingFedData.webUserSelection = webUserSelection;
			pingFedData.adapterContext = adapterContext;
			
			pingFedData.sendDynamicDataWithoutEncoding = sendDynamicDataWithoutEncoding;
			pingFedData.sendDynamicDataAsRequestClaims = sendDynamicDataAsRequestClaims;
			
			pingFedData.skipSuccessScreens = skipSuccessScreens;
			pingFedData.skipErrorScreens = skipErrorScreens;
			pingFedData.skipTimeoutScreens = skipTimeoutScreens;
			return pingFedData;
		}
	}
}
