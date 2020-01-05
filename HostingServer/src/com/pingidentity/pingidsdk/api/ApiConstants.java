package com.pingidentity.pingidsdk.api;

/**
 * ApiConstants
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public class ApiConstants {
	
	public static final String ACCOUNT_ID_HEADER = "account_id";
	public static final String TOKEN_HEADER = "token";
	public static final String TYPE_HEADER = "typ";
	public static final String API_TYPE_JWT = "JWT";
	public static final String JWT_VERSION_HEADER = "jwt_version";
	public static final String JWT_EXPIRATION_HEADER = "expires";
	public static final String REQUEST_ID_HEADER = "X-Request-ID";
	public static final String JWT_VERSTION_4 ="v4";

	
	public static class Signature {
		public static final String HEADERS_SEPARATOR = ",";
		public static final String RESPONSE_TOKEN_HEADER = "X-PINGID-Singature";
		public static final String CANONICAL_STR_SEPARATOR = ":";
		public static final String JWT_BODY_FIELD = "data";
	}
}
