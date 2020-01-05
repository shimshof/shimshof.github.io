package com.pingidentity.moderno.model.api;

public class AuthenticationTokenResponse extends BaseResponse{

	private String id;
	private String tokenSchemeUri;

	public String getTokenSchemeUri() {
		return tokenSchemeUri;
	}

	public void setTokenSchemeUri(String tokenSchemeUri) {
		this.tokenSchemeUri = tokenSchemeUri;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
