package com.pingidentity.pingidsdk;

/**
 * PingIDSdkAPI
 *
 * This class job is to send requests to the "PingID SDK" server. This
 * layer also creates the authorization header (see
 * {@link #createAuthorizationHeader(HttpRequestBase, Object) }
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 *
 */
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Base64;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import com.pingidentity.pingidsdk.api.APISignatureImpl;
import com.pingidentity.pingidsdk.api.ApiConstants;
import com.pingidentity.pingidsdk.api.InternalError;
import com.pingidentity.pingidsdk.api.InternalErrorCode;
import com.pingidentity.pingidsdk.api.PatchOperations;
import com.pingidentity.pingidsdk.api.RestAPIError;

public class PingIDSdkAPI {

	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PingIDSdkAPI.class);

	/**
	 * The base path of the end points
	 */
	private static final String BASE_PATH = "/pingid/v1"; 
	
	/**
	 * A separator. used for creating a canonical string which is used to create the authorization header (see
	 * {@link #createAuthorizationHeader(HttpRequestBase, Object) }
	 */
	private static final String CANONICAL_STR_SEPARATOR = ":";
	
	/**
	 * The authorization header prefix
	 */
	private static final String AUTHORIZATION_HEADER_PREFIX = "PINGID-HMAC=";
	

	/**
	 * The URL of the end points
	 */
	private String url;

	/**
	 * The API signature class
	 */
	private APISignatureImpl signImpl;
	

	/**
	 * PingID API constructor
	 * @param accountId the account ID
	 * @param apiKey the account API Key. The API key is specific for the account. Can be download from the admin portal
	 * @param token the account token. The token is specific for the account. Can be download from the admin portal
	 */
	public PingIDSdkAPI(String url, String accountId, String apiKey, String token) {
		this.url = url;

		// In order to send any request to the "PingID SDK" server - 
		// the request must be signed and must contain an authorization header
		SecretKeySpec signingKey = new SecretKeySpec(Base64.getDecoder().decode(apiKey.getBytes()), "HS256");
		signImpl = new APISignatureImpl(token, signingKey, accountId);
	}

	/**
	 * Sends "GET" request to the "PingID SDK" server
	 * @param responseType the returned resource class (USER, AUTHENTICATION etc)
	 * @param path the path for the resource. For example: /accounts/7BDAC822-8DD5-4961-8112-0D6FFC06C7FA/users/james
	 * @return the resource or null if the resource does not exist
	 * @throws PingIDSdkException PingID SDK Exception
	 */
	public <T> T get(Class<T> responseType, String path) throws PingIDSdkException {
		return get(responseType, path, null);
	}

	/**
	 * Sends "GET" request to the "PingID SDK" server
	 * @param responseType the returned resource class (USER, AUTHENTICATION etc)
	 * @param path the path for the resource. For example: /accounts/7BDAC822-8DD5-4961-8112-0D6FFC06C7FA/users/james
	 * @param expandParamValue a query parameter which is used to get an extended information about the the resource
	 * For example, in order to get the user devices, set this parameter to "devices"
	 * @return the resource or null if the resource does not exist
	 * @throws PingIDSdkException
	 */
	public <T> T get(Class<T> responseType, String path, String expandParamValue) throws PingIDSdkException {
		logger.info(String.format("Making 'GET' REST API call to %s", url + path));
		CloseableHttpClient httpclient = HttpClients.createDefault();

		try {
			URIBuilder uriBuilder = new URIBuilder(url);
			uriBuilder.setPath(getPath(path));
			
			if (expandParamValue != null) {
				uriBuilder.setParameter("expand", expandParamValue);
			}

			URI uri = uriBuilder.build();
			HttpGet request = new HttpGet(uri);
			buildHeaders(request, null);

			CloseableHttpResponse response = httpclient.execute(request);
			try {
				String responseEntity = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
				verifyResponse(response, responseEntity);
				verifyStatus(responseEntity, response.getStatusLine().getStatusCode(), org.apache.http.HttpStatus.SC_OK);
				ObjectMapper objectMapper = new ObjectMapper();
				T entity = objectMapper.readValue(responseEntity, responseType);
				return entity;
			} finally {
				response.close();
			}
		} catch (PingIDSdkException pingIDSdkEx) {
			if (isEntityNotFoundError(responseType.getSimpleName(), pingIDSdkEx)) {
				return null;
			}
			throw pingIDSdkEx;
		} catch (Exception e) {
			logger.error("failed to execute PingID SDK action", e);
			throw new PingIDSdkException(e.getMessage());
		}
	}

	/**
	 * Returns true if the exception indicates that the resource was not found
	 * @param resourceName the resource class name: USER, AUTHENTICATION etc.
	 * @param pingIDSdkEx the PingID SDK exception
	 * @return true if the exception indicates that the resource was not found
	 */
	private boolean isEntityNotFoundError(String resourceName, PingIDSdkException pingIDSdkEx) {
		if (pingIDSdkEx.getPingIDSdkResponseStatus() != org.apache.http.HttpStatus.SC_NOT_FOUND) {
			return false;
		}

		RestAPIError error = pingIDSdkEx.getPingIDSdkError();

		if (error == null || error.getTarget() == null || !error.getTarget().equalsIgnoreCase(resourceName)) {
			return false;
		}

		return true;
	}

	/**
	 * Returns true if the exception indicates that the application was disabled
	 * @param e the PingID SDK exception
	 * @return true if the exception indicates that the application was disabled
	 */
	public static boolean isApplicationDisabled(PingIDSdkException e) {
		if (e.getPingIDSdkResponseStatus() != HttpStatus.SC_BAD_REQUEST) {
			return false;
		}
		
		RestAPIError error = e.getPingIDSdkError();

		if (error == null || error.getTarget() == null || !error.getTarget().equalsIgnoreCase("application")) {
			return false;
		}
		
		List<InternalError> internalErrors = error.getDetails();
		if (internalErrors == null || internalErrors.isEmpty()) {
			return false;
		}
		
		for (InternalError internalError : internalErrors) {
			if (internalError != null && InternalErrorCode.APPLICATION_DISABLED == internalError.getCode()) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Sends "POST" request to the "PingID SDK" server
	 * @param responseType the returned resource class (USER, AUTHENTICATION etc)
	 * @param path the path. For example: /accounts/7BDAC822-8DD5-4961-8112-0D6FFC06C7FA/users
	 * @param payload the resource to create
	 * @return the created resource
	 * @throws PingIDSdkException
	 */
	public <T> T post(Class<T> responseType, String path, Object payload) throws PingIDSdkException {
		logger.info(String.format("Making 'POST' REST API call to %s", url + path));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			URI uri = new URIBuilder(url).setPath(getPath(path)).build();
			HttpPost request = new HttpPost(uri);
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(payload);
			logger.info(String.format("post payload = %s", json));
			StringEntity se = new StringEntity(json.toString(), StandardCharsets.UTF_8);
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON));
			request.setEntity(se);

			buildHeaders(request, payload);

			CloseableHttpResponse response = httpclient.execute(request);
			try {
				String responseEntity = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
				logger.info(String.format("response entity = %s", responseEntity));
				verifyResponse(response, responseEntity);
				verifyStatus(responseEntity, response.getStatusLine().getStatusCode(), org.apache.http.HttpStatus.SC_CREATED);
				T entity = mapper.readValue(responseEntity, responseType);
				return entity;
			} finally {
				response.close();
			}

		} catch (Exception e) {
			logger.error("failed to execute PingID SDK action", e);
			throw new PingIDSdkException(e.getMessage());
		}
	}

	/**
	 * Sends "PATCH" request to the "PingID SDK" server
	 * @param responseType the returned resource class (USER, AUTHENTICATION etc)
	 * @param path the path. For example: /accounts/7BDAC822-8DD5-4961-8112-0D6FFC06C7FA/users
	 * @param patchOperations the object which contains the patch itself
	 * @return the patched resource
	 * @throws PingIDSdkException
	 */
	public <T> T patch(Class<T> responseType, String path, PatchOperations patchOperations) throws PingIDSdkException {
		logger.info(String.format("Making 'PATCH' REST API call to %s", url + path));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			URI uri = new URIBuilder(url).setPath(getPath(path)).build();
			HttpPatch request = new HttpPatch(uri);
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(patchOperations);
			StringEntity se = new StringEntity(json.toString(), StandardCharsets.UTF_8);
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON));
			request.setEntity(se);

			buildHeaders(request, patchOperations);

			CloseableHttpResponse response = httpclient.execute(request);
			try {
				String responseEntity = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
				verifyResponse(response, responseEntity);
				verifyStatus(responseEntity, response.getStatusLine().getStatusCode(), org.apache.http.HttpStatus.SC_OK);
				T entity = mapper.readValue(responseEntity, responseType);
				return entity;
			} finally {
				response.close();
			}

		} catch (Exception e) {
			logger.error("failed to execute PingID SDK action", e);
			throw new PingIDSdkException(e.getMessage());
		}
	}

	/**
	 * Sends "PUT" request to the "PingID SDK" server
	 * @param responseType the returned resource class (USER, AUTHENTICATION etc)
	 * @param path the path. For example: /accounts/7BDAC822-8DD5-4961-8112-0D6FFC06C7FA/users
	 * @param payload the resource to update
	 * @return the updated resource
	 * @throws PingIDSdkException
	 */
	public <T> T put(Class<T> responseType, String path, Object payload) throws PingIDSdkException {
		logger.info(String.format("Making 'PUT' REST API call to %s", url + path));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			URI uri = new URIBuilder(url).setPath(getPath(path)).build();
			HttpPut request = new HttpPut(uri);
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(payload);
			StringEntity se = new StringEntity(json.toString(), StandardCharsets.UTF_8);
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON));
			request.setEntity(se);

			buildHeaders(request, payload);

			CloseableHttpResponse response = httpclient.execute(request);
			try {
				String responseEntity = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
				verifyResponse(response, responseEntity);
				verifyStatus(responseEntity, response.getStatusLine().getStatusCode(), org.apache.http.HttpStatus.SC_OK);
				T entity = mapper.readValue(responseEntity, responseType);
				return entity;
			} finally {
				response.close();
			}

		} catch (Exception e) {
			logger.error("failed to execute PingID SDK action", e);
			throw new PingIDSdkException(e.getMessage());
		}
	}

	/**
	 * Sends "PUT" request to the "PingID SDK" server
	 * @param responseType the returned resource class (USER, AUTHENTICATION etc)
	 * @param path the path. For example: /accounts/7BDAC822-8DD5-4961-8112-0D6FFC06C7FA/users
	 * @param payload the resource to update
	 * @throws PingIDSdkException
	 */
	public void put(String path, Object payload) throws PingIDSdkException {
		logger.info(String.format("Making 'PUT' REST API call to %s", url + path));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			URI uri = new URIBuilder(url).setPath(getPath(path)).build();
			HttpPut request = new HttpPut(uri);
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(payload);
			StringEntity se = new StringEntity(json.toString(), StandardCharsets.UTF_8);
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON));
			request.setEntity(se);

			buildHeaders(request, payload);
			
			CloseableHttpResponse response = httpclient.execute(request);
			try {
				String responseEntity = response.getEntity() != null ? IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8) : "";
				verifyResponse(response, responseEntity);
				verifyStatus(responseEntity, response.getStatusLine().getStatusCode(), org.apache.http.HttpStatus.SC_NO_CONTENT);
			} finally {
				response.close();
			}

		} catch (Exception e) {
			logger.error("failed to execute PingID SDK action", e);
			throw new PingIDSdkException(e.getMessage());
		}
	}
	
	/**
	 * Sends "DELETE" request to the "PingID SDK" server
	 * @param path the path. For example: /accounts/7BDAC822-8DD5-4961-8112-0D6FFC06C7FA/users
	 * @throws PingIDSdkException
	 */
	public void delete(String path) throws PingIDSdkException {
		logger.info(String.format("Making 'DELETE' REST API call to %s", url + path));

		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			URI uri = new URIBuilder(url).setPath(getPath(path)).build();
			HttpDelete request = new HttpDelete(uri);
			buildHeaders(request, null);
			CloseableHttpResponse response = httpclient.execute(request);
			try {
				String responseEntity = response.getEntity() != null ? IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8) : "";
				verifyResponse(response, responseEntity);
				verifyStatus(responseEntity, response.getStatusLine().getStatusCode(), org.apache.http.HttpStatus.SC_NO_CONTENT);
			} finally {
				response.close();
			}

		} catch (Exception e) {
			logger.error("failed to execute PingID SDK action", e);
			throw new PingIDSdkException(e.getMessage());
		}
	}

	/**
	 * Verify the response status. If the actual status is not equal to the expected status - an exception is thrown
	 * @param responseEntity the response entity
	 * @param actualStatus the actual status
	 * @param expectedStatus the expected status
	 * @throws PingIDSdkException
	 */
	private void verifyStatus(String responseEntity, int actualStatus, int expectedStatus) throws PingIDSdkException {

		if (actualStatus == expectedStatus) {
			return;
		}

		if (responseEntity == null) {
			throw new PingIDSdkException(actualStatus, "PingID SDK failed to handle the request");
		}

		RestAPIError error = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			error = objectMapper.readValue(responseEntity, RestAPIError.class);
		} catch (Exception e) {
			throw new PingIDSdkException(actualStatus, "PingID SDK not properly configured");
		}

		if (error != null) {
			throw new PingIDSdkException(actualStatus, error);
		}

		throw new PingIDSdkException(actualStatus, "PingID SDK not properly configured. null error");
	}

	/**
	 * Builds the request headers
	 * @param request the request
	 * @param entity the entity (the request payload)
	 * @throws Exception
	 */
	private void buildHeaders(HttpRequestBase request, Object entity) throws Exception {		
		byte[] authHeader = createAuthorizationHeader(request, entity);
		StringBuilder authorizationHedaerValue = new StringBuilder();
		authorizationHedaerValue.append(AUTHORIZATION_HEADER_PREFIX);
		authorizationHedaerValue.append(new String(authHeader));
		request.addHeader(HttpHeaders.AUTHORIZATION, authorizationHedaerValue.toString());
	}

	/**
	 * Create the authorization header
	 * @param request the request
	 * @param entity the entity (the request payload)
	 * @return the authorization header
	 * @throws Exception
	 */
	private byte[] createAuthorizationHeader(HttpRequestBase request, Object entity) throws Exception {
		String canonicalStr = getCanonicalStr(request, entity);
		logger.debug(String.format("authorization_header=\"%s\"", canonicalStr));
		String authorization = signImpl.calculateSHA256(canonicalStr);
		logger.debug(String.format("hashed authorization_header \"%s\"", authorization));

		return signImpl.jwtSign(authorization);
	}

	/**
	 * Creates the canonical string which is used for signing and verification
	 * @param request the request
	 * @param entity the entity (payload)
	 * @return canonical string which used for signing and verification
	 * @throws SignatureException
	 * @throws NoSuchAlgorithmException
	 */
	private String getCanonicalStr(HttpRequestBase request, Object entity) throws SignatureException, NoSuchAlgorithmException {

		String path = request.getURI().getPath();
		String method = request.getMethod();
		String host = request.getURI().getHost();

		String queryParams = request.getURI().getQuery() != null ? request.getURI().getQuery() : "";
		String hashedPayload = signImpl.calculateSHA256(entity != null ? entity : "");
		StringBuilder sb = new StringBuilder("");
		sb.append(method).append(CANONICAL_STR_SEPARATOR);
		sb.append(host).append(CANONICAL_STR_SEPARATOR);
		sb.append(path).append(CANONICAL_STR_SEPARATOR);
		sb.append(queryParams).append(queryParams.equals("") ? "" : CANONICAL_STR_SEPARATOR);
		sb.append(hashedPayload).append(CANONICAL_STR_SEPARATOR);
		return sb.toString();
	}

	/**
	 * Verifies the response
	 * @param clientResponse the client response
	 * @param responseBody the response body
	 * @throws SignatureException
	 */
	private void verifyResponse(HttpResponse clientResponse, String responseBody) throws SignatureException {
		logger.info("start verifying response");
		
		if(clientResponse.getFirstHeader(ApiConstants.Signature.RESPONSE_TOKEN_HEADER) == null){
			logger.equals("invalid server response");
			throw new SignatureException();
		}
		
		String tokenHeader = clientResponse.getFirstHeader(ApiConstants.Signature.RESPONSE_TOKEN_HEADER).getValue();
		String jwtPayload = verifyJwt(tokenHeader);

		String responseSignedPayload = "";
		try {
			responseSignedPayload = signImpl.calculateSHA256(responseBody);
		} catch (Exception e) {
			logger.error("Failed create response payload", e);
			throw new SignatureException();
		}

		signImpl.comparePayloads(jwtPayload, responseSignedPayload);
		logger.info("Response verified");
	}
	
	/**
	 * Verifies the authorization header JWT
	 * @param authorizationHeader the authorization header
	 * @return
	 * @throws SignatureException
	 */
	private String verifyJwt(String authorizationHeader) throws SignatureException {
		logger.debug("Verify Jwt");
		String jwtStr = "";

		if (authorizationHeader == null || authorizationHeader.equalsIgnoreCase("null")) {
			logger.error("Failed token header cannot be null");
			throw new SignatureException("Header cannot be null");
		}

		try {
			jwtStr = signImpl.jwtVerify(authorizationHeader.getBytes()).getPayload();
		} catch (SignatureException e) {
			logger.error("Failed verify signature", e);
			throw new SignatureException("Failed verify signature", e);
		} catch (JoseException e) {
			logger.error("Failed getting payload", e);
			throw new SignatureException("Failed getting payload", e);
		}

		return new JsonParser().parse(jwtStr).getAsJsonObject().get("data").getAsString();
	}
	
	private String getPath(String path) {
		return BASE_PATH + path;
	}
}