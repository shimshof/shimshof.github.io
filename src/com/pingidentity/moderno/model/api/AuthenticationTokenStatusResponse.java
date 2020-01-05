package com.pingidentity.moderno.model.api;

import java.util.List;

import com.pingidentity.pingidsdk.api.User;

public class AuthenticationTokenStatusResponse extends BaseResponse{

	private TokenStatus tokenStatus;
	
	List<User> users;
	
	public TokenStatus getTokenStatus() {
		return tokenStatus;
	}

	public void setTokenStatus(TokenStatus tokenStatus) {
		this.tokenStatus = tokenStatus;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public enum TokenStatus {
		NOT_CLAIMED, CLAIMED, NOT_EXIST, CANCELED, IN_PROGRESS, EXPIRED, DENIED, PENDING_WEB_USER_SELECTION;
		
		public static TokenStatus getValue(String name) {
			for (TokenStatus it : values()) {
				if (it.name().equalsIgnoreCase(name)) {
					return it;
				}
			}
			return NOT_CLAIMED;
		}
	}
}
