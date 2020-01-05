package com.pingidentity.pingfed;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.jose4j.jws.JsonWebSignature;

import com.google.gson.JsonObject;
import com.pingidentity.moderno.utils.SessionData;
import com.pingidentity.moderno.utils.SessionUtils;

/**
 * The PingFederateRequestGenerator is responsible for generating requests to PingFederate.
 * 
 * A request to PingFederate may contain a "request" query parameter (optional).
 * Read here: https://openid.net/specs/openid-connect-core-1_0.html#rfc.section.6.1 for further details
 * 
 * The "request" parameter can be a JSON:
 * 
 * The JSON structure (example) -
 * 
 * {
 *    "pingIdSdkUserApprovalRequired": true,
 *    "pingIdSdkQrCodeClientContext": "Some data",
 *    "pingIdSdkQrCodePushTitle": "Some data",
 *    "pingIdSdkQrCodePushBody": "Some data",
 *    "pingIdSdkClientContext": "Some Data",
 *    "pingIdSdkPushTitle": "Some data",
 *    "pingIdSdkPushBody": "Some data",
 *    "pingIdSdkApplicationId": "123456"
 * }
 * 
 * Each of the JSON attributes is optional .
 * 
 * Optionally, the JSON can be Base64 encoded.
 * 
 * This JSON (encoded or not) will be added with "pingIdSdkData" attribute to the "request" object.
 * 
 * For example, the request object with the  "pingIdSdkData" attribute:
 * 
 * {
   "iss": "s6BhdRkqt3",
   "aud": "https://server.example.com",
   "response_type": "code id_token",
   "client_id": "s6BhdRkqt3",
   "redirect_uri": "https://client.example.org/cb",
   "scope": "openid",
   "state": "af0ifjsldkj",
   "nonce": "n-0S6_WzA2Mj",
   "max_age": 86400,
  "pingIdSdkData":
   {
    "pingIdSdkUserApprovalRequired": true,
    "pingIdSdkQrCodeClientContext": "Some data",
    "pingIdSdkQrCodePushTitle": "Some data",
    "pingIdSdkQrCodePushBody": "some data",
    "pingIdSdkClientContext": "Some Data",
    "pingIdSdkPushTitle": "Some data",
    "pingIdSdkPushBody": "Some data",
    "pingIdSdkApplicationId": "123456"
   }  
   "claims":
    {
     "userinfo":
      {
       "given_name": {"essential": true},
       "nickname": null,
       "email": {"essential": true},
       "email_verified": {"essential": true},
       "picture": null
      },
     "id_token":
      {
       "gender": null,
       "birthdate": {"essential": true},
       "acr": {"values": ["urn:mace:incommon:iap:silver"]}
      }
    }
  }
 * 
 * 
 * Alternatively, each attribute can be sent separately.
 * 
 * For example, the request object with separated attributes:
 * {
   "iss": "s6BhdRkqt3",
   "aud": "https://server.example.com",
   "response_type": "code id_token",
   "client_id": "s6BhdRkqt3",
   "redirect_uri": "https://client.example.org/cb",
   "scope": "openid",
   "state": "af0ifjsldkj",
   "nonce": "n-0S6_WzA2Mj",
   "max_age": 86400,
  "pingIdSdkUserApprovalRequired": true,
  "pingIdSdkClientContext": "Some Data",
   "claims":
    {
     "userinfo":
      {
       "given_name": {"essential": true},
       "nickname": null,
       "email": {"essential": true},
       "email_verified": {"essential": true},
       "picture": null
      },
     "id_token":
      {
       "gender": null,
       "birthdate": {"essential": true},
       "acr": {"values": ["urn:mace:incommon:iap:silver"]}
      }
    }
  }
 * 
 * copyright © 2018 Ping Identity. All rights reserved.
 *
 */
public class PingFederateRequestGenerator {

	/**
	 * This method demonstrates how to construct request to PingFederate which contains PingID SDK data.
	 * Observe generateRequestQueryParameter method which demonstrates the specific way of adding PingID SDK data
	 * to the "request" object.
	 * @param moerdnoUri - the base uri. For example: "http://demo-server.pingidsdk.ping-eng.com/hosting-server".
	 * This parameter is used to construct the "redirect_uri".
	 * @return PingFederate authorization request
	 * @throws Exception
	 */
	public static String generateRequest(PingFedData pingFedRequestData, String moerdnoUri, String sessionId) throws Exception {
		
		// constructing the redirect URI.
		// PingFederate redirect the authorization result to this endpoint.
	    // Observe: AuthorizationCodeServlet class (which handles the authorization result returned from PingFederate)
		String redirectUri = (new StringBuilder(moerdnoUri)).append("/authCallback").toString();
		
		// The endpoint is defined in the properties file (located under: /env/moderno-props/)
		StringBuilder pingFedAuthorizationEndpoint = new StringBuilder(pingFedRequestData.getPingFedBaseEndPoint());
		pingFedAuthorizationEndpoint.append("/as/authorization.oauth2?client_id=moderno_client&response_type=code&scope=openid&redirect_uri=");
		pingFedAuthorizationEndpoint.append(redirectUri);
		
		if(sessionId != null){
			SessionData sessionData = SessionUtils.getSessionData(sessionId);
			if(sessionData != null){
				pingFedAuthorizationEndpoint.append("&state=");
				pingFedAuthorizationEndpoint.append(sessionId);
				if(sessionData.getUserName() != null){
					pingFedAuthorizationEndpoint.append("&login_hint=");
					pingFedAuthorizationEndpoint.append(sessionData.getUserName());
				}
			}
		}
		
		// If there is no dynamic data at all, return the end point without any query parameter
		if (pingFedRequestData == null || pingFedRequestData.isEmpty()) {
			return pingFedAuthorizationEndpoint.toString();
		}

		// Generate the query parameter which contains all the dynamic data
		String encodedData = generateRequestQueryParameter(pingFedRequestData, redirectUri, sessionId);
		pingFedAuthorizationEndpoint.append("&request=");
		pingFedAuthorizationEndpoint.append(encodedData);
		
		return pingFedAuthorizationEndpoint.toString();

	}
	
	
	/**
	 * This method demonstrates how to prepare the signed request object.
	 * Read here: https://openid.net/specs/openid-connect-core-1_0.html#rfc.section.6.1 for further details
	 * @param redirectUri the redirect uri
	 * @param pingFedRequestData - PingFederate data
	 * @return the signed object parameter
	 * @throws Exception
	 */
	private static String generateRequestQueryParameter(PingFedData pingFedRequestData, String redirectUri, String sessionId) throws Exception{
		
		// STEP 1: create the "request" object parameter
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("scope", "openid");
		jsonObject.addProperty("client_id", "moderno_client");
		jsonObject.addProperty("response_type", "code");
		jsonObject.addProperty("redirect_uri", redirectUri);
		
		if(sessionId != null){
			SessionData sessionData = SessionUtils.getSessionData(sessionId);
			if(sessionData != null){
				jsonObject.addProperty("state", sessionId);
				if(sessionData.getUserName() != null){
					jsonObject.addProperty("login_hint", sessionData.getUserName());
				}
			}
		}

		// STEP 2: add the "pingidSdkData" information
		// The data can be added directly to the request object claims 
		// or as an one JSON in one claim ("pingIdSdkData")
		if(pingFedRequestData.getSendDynamicDataAsRequestClaims()){
			addDynamicDataToJson(pingFedRequestData, jsonObject);
		}else{
			String dynamicData = generatePingIdSdkDataAsJson(pingFedRequestData);
			jsonObject.addProperty("pingIdSdkData", dynamicData);
		}
		
		// STEP 3: sign the data
		String pingFedRequestPrivateKey = pingFedRequestData.getPingFedRequestPrivateKey();
		String signAlg = pingFedRequestData.getSignAlgorithm();
		String requestObject = jsonObject.toString();
		if (pingFedRequestPrivateKey != null && signAlg != null) {
			requestObject = signData(requestObject, pingFedRequestPrivateKey, signAlg);
		}
		
		return requestObject;
	}

	public static String generatePingIdSdkDataAsJson(PingFedData pingFedRequestData) {
		// Step 1: create a JSON object and set all the relevant attribute. Each one of the attributes is optional.
		JsonObject jsonObject = new JsonObject();
		
		addDynamicDataToJson(pingFedRequestData, jsonObject);
		
		String dynamicData = jsonObject.toString();
		
		if(pingFedRequestData.getSendDynamicDataWithoutEncoding()){
			return dynamicData;
		}
		String encodedData = new String(Base64.getEncoder().encode(dynamicData.getBytes(StandardCharsets.UTF_8)));
		return encodedData;
	}


	private static void addDynamicDataToJson(PingFedData pingFedRequestData, JsonObject jsonObject) {
		/*
		 * Whether user approval is required once the user successfully scanned the QR code
		 */
		if (pingFedRequestData.isQrCodeUserApprovalRequired() != null) {
			jsonObject.addProperty("pingIdSdkUserApprovalRequired", pingFedRequestData.isQrCodeUserApprovalRequired());
		}
		
		/*
		 * In case of QR code - if the device which successfully scanned the QR code is paired with more than one user,
		 * whether the mobile application or the web should select the user
		 */
		if (pingFedRequestData.isWebUserSelection() != null) {
			jsonObject.addProperty("pingIdSdkWebUserSelection", pingFedRequestData.isWebUserSelection());
		}

		/*
		 * Any client context that the customer server wishes to pass the mobile application that successfully scanned the QR code
		 */
		if (pingFedRequestData.getQrCodeClientContext() != null) {
			jsonObject.addProperty("pingIdSdkQrCodeClientContext", pingFedRequestData.getQrCodeClientContext());
		}

		/*
		 * The Push message title. Relevant only when webUserSelection = true
		 */
		if (pingFedRequestData.getQrCodePushMessageTitle() != null) {
			jsonObject.addProperty("pingIdSdkQrCodePushTitle", pingFedRequestData.getQrCodePushMessageTitle());
		}

		/*
		 * The Push message body. Relevant only when webUserSelection = true
		 */
		if (pingFedRequestData.getQrCodePushMessageBody() != null) {
			jsonObject.addProperty("pingIdSdkQrCodePushBody", pingFedRequestData.getQrCodePushMessageBody());
		}

		/*
		 * Any client context that the customer server wishes to pass the mobile application (during an authentication which is not a QR code authentication)
		 */
		if (pingFedRequestData.getClientContext() != null) {
			jsonObject.addProperty("pingIdSdkClientContext", pingFedRequestData.getClientContext());
		}

		/*
		 * The Push message title. (for QR code authentication, use: qrCodePushMessageTitle)
		 */
		if (pingFedRequestData.getPushMessageTitle() != null) {
			jsonObject.addProperty("pingIdSdkPushTitle", pingFedRequestData.getPushMessageTitle());
		}

		/*
		 * The Push message title. (for QR code authentication, use: qrCodePushMessageBody)
		 */
		if (pingFedRequestData.getPushMessageBody() != null) {
			jsonObject.addProperty("pingIdSdkPushBody", pingFedRequestData.getPushMessageBody());
		}
		
		/*
		 * Set a value here if you wish to override the existing application ID which is configured in the PingFederate PingID SDK Adapter
		 */
		if (pingFedRequestData.getApplicationId() != null) {
			jsonObject.addProperty("pingIdSdkApplicationId", pingFedRequestData.getApplicationId());
		}
		
		/*
		 * Set a value here if you wish to pass a context to the adapter UI.
		 * (optionally, this context can be a json)
		 */
		if (pingFedRequestData.getAdapterContext() != null) {
			jsonObject.addProperty("pingIdSdkAdapterContext", pingFedRequestData.getAdapterContext());
		}
		
		/*
		 * Set a value here if you wish to override the adapter show/skip success screens.
		 * (optionally, this context can be a json)
		 */
		if (pingFedRequestData.getSkipSuccessScreens() != null) {
			jsonObject.addProperty("pingIdSdkSkipSuccessScreens", pingFedRequestData.getSkipSuccessScreens());
		}
		
		/*
		 * Set a value here if you wish to override the adapter show/skip error screens.
		 * (optionally, this context can be a json)
		 */
		if (pingFedRequestData.getSkipErrorScreens() != null) {
			jsonObject.addProperty("pingIdSdkSkipErrorScreens", pingFedRequestData.getSkipErrorScreens());
		}
		
		/*
		 * Set a value here if you wish to override the adapter show/skip timeout screens.
		 * (optionally, this context can be a json)
		 */
		if (pingFedRequestData.getSkipTimeoutScreens() != null) {
			jsonObject.addProperty("pingIdSdkSkipTimeoutScreens", pingFedRequestData.getSkipTimeoutScreens());
		}
	}

	private static String signData(String dynamicData, String pingFedRequestPrivateKey, String signAlg) throws Exception {
		PrivateKey privateKey = getPrivateKey(pingFedRequestPrivateKey, signAlg);
		JsonWebSignature jws = new JsonWebSignature();

		// The payload of the JWS is JSON content of the JWT Claims
		jws.setPayload(dynamicData);

		// The JWT is signed using the private key
		jws.setKey(privateKey);

		// Set the signature algorithm on the JWT/JWS that will integrity
		// protect the claims
		jws.setAlgorithmHeaderValue(signAlg);

		String jwt = jws.getCompactSerialization();
		return jwt;
	}

	private static PrivateKey getPrivateKey(String pingFedRequestPrivateKey, String JwtAlg) throws Exception {
		byte[] decoded = Base64.getDecoder().decode(pingFedRequestPrivateKey);

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
		String keyFactoryAlg = getKeyFactoryAlgorithmFromJwtAlgorithm(JwtAlg);
		KeyFactory kf = KeyFactory.getInstance(keyFactoryAlg);
		return kf.generatePrivate(spec);
	}
	
	private static String getKeyFactoryAlgorithmFromJwtAlgorithm(String JwtAlg) {
		if(JwtAlg.startsWith("ES")) {
			return "EC";
		}
		
		return "RSA";
	}
}
