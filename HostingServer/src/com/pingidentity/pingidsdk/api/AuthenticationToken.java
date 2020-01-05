package com.pingidentity.pingidsdk.api;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AuthenticationToken extends BaseResource {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String tokenSchemeUri;
	
	private String status;
	
	private String statusReason;
	
	private String username;
	
	private List<User> users;
	
	private String deviceId;
	
	private String clientContext;
	
	private boolean userApprovalRequired;
	
	private String pushMessageTitle;
	
	private String pushMessageBody;
	
	private boolean webUserSelection;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTokenSchemeUri() {
		return tokenSchemeUri;
	}

	public void setTokenSchemeUri(String tokenSchemeUri) {
		this.tokenSchemeUri = tokenSchemeUri;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
		
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}	

	public String getClientContext() {
		return clientContext;
	}

	public void setClientContext(String clientContext) {
		this.clientContext = clientContext;
	}	

	public boolean isUserApprovalRequired() {
		return userApprovalRequired;
	}

	public void setUserApprovalRequired(boolean userApprovalRequired) {
		this.userApprovalRequired = userApprovalRequired;
	}
	
	public String getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
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
	
	public boolean isWebUserSelection() {
		return webUserSelection;
	}

	public void setWebUserSelection(boolean webUserSelection) {
		this.webUserSelection = webUserSelection;
	}

	public String getUsersToString(){
		if(users == null || users.isEmpty()){
			return "";
		}
		
		return String.join(",", users.stream().map(user -> user.toString()).collect(Collectors.toList()));
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("AuthenticationToken[");
		addToStringIfNeed(sb, "id", id, true);
		addToStringIfNeed(sb, "username", username);
		addToStringIfNeed(sb, "status", status);
		addToStringIfNeed(sb, "statusReason", statusReason);
		addToStringIfNeed(sb, "deviceId", deviceId);
		addToStringIfNeed(sb, "webUserSelection", webUserSelection);
		addToStringIfNeed(sb, "userApprovalRequired", userApprovalRequired);
		addToStringIfNeed(sb, "users", getUsersToString());
		addToStringIfNeed(sb, "hasClientContext", StringUtils.isNotBlank(clientContext));
		sb.append(']');
		return sb.toString();
	}
}
