package com.pingidentity.pingfed;

import org.apache.commons.codec.binary.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AccessTokenParser {

	private static final Logger logger = LoggerFactory.getLogger(AccessTokenParser.class);

	private static final String PINGID_SDK_STATUS_ATTRIBUTE_NAME = "pingid.sdk.status";

	/**
	 * This method demonstrate how to get "PingID SDK" authentication status
	 * from the access token. This status can be used, for example, to reduce
	 * the user permissions. There are some cases in which the user hasn't
	 * passed MFA but PingFederate may still return the access token (depends on
	 * the PingID SDK configuration). Read
	 * "PingID SDK - PingFederate Integration" Documentation for
	 * pingid.sdk.status property possible values (which contains the
	 * authentication level)
	 * 
	 * NOTE: This method does not validate the access token. However, it is
	 * recommended to validate the access token JWT
	 * 
	 * @param accessToken
	 */
	public static String getPingIDSdkAuthenticationStatusFromTheAccessToken(String accessToken) {
		try {
			String[] split_string = accessToken.split("\\.");
			String base64EncodedBody = split_string[1];

			String body = new String(Base64.decodeBase64(base64EncodedBody));

			JsonObject jsonObj = new JsonParser().parse(body).getAsJsonObject();
			// NOTE: the attribute: pingid.sdk.status is defined in the "PingID
			// SDK" Adapter core contract.
			// In order to make the attribute value to appear in the access
			// token, the adapter attribute "pingid.sdk.status" should be mapped
			// to an attribute within the access token. In this example, the
			// access token contains an attribute:pingid.sdk.status which was
			// mapped to the "pingid.sdk.status" adapter attribute
			if (!jsonObj.has(PINGID_SDK_STATUS_ATTRIBUTE_NAME)) {
				logger.info("The access token does not contain pingid.sdk.status property");
				return "";
			}

			JsonElement pingIdSdkStatusAttributeJson = jsonObj.get(PINGID_SDK_STATUS_ATTRIBUTE_NAME);
			String pingIdSdkStatusAttribute = pingIdSdkStatusAttributeJson.getAsString();

			return pingIdSdkStatusAttribute;
		} catch (Exception e) {
			logger.error("failed to get PingID SDK authentication status from the access token");
			return "";
		}
	}
}
