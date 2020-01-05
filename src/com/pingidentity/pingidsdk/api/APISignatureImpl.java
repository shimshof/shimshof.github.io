package com.pingidentity.pingidsdk.api;

/**
 * ApiSignatureImpl
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.binary.Hex;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class APISignatureImpl {
	  private static Logger logger = LoggerFactory.getLogger(APISignatureImpl.class);

	    String token;
	    SecretKey signingKey;
	    String accountId;

	    public APISignatureImpl() {
	    }

	    public APISignatureImpl(String token, SecretKey signingKey, String accountId) {
	        this.token = token;
	        this.signingKey = signingKey;
	        this.accountId = accountId;
	    }

	    public String calculateSHA256(Object data) throws SignatureException, NoSuchAlgorithmException {
	        if (data == null) {
	            data = "";
	        }

	        String objStr = getObjStr(data);

	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(objStr.getBytes(StandardCharsets.UTF_8));

	        return Hex.encodeHexString(hash);
	    }

	    private String getObjStr(Object data) throws SignatureException {
	        String objStr = "";
	        if (data instanceof String) {
	            return (String) data;
	        }    

	        ObjectMapper objectMapper = new ObjectMapper();
	        try {
	            objStr = objectMapper.writeValueAsString(data);
	        } catch (JsonProcessingException e) {
	            logger.error("Error processing object mapper", e);
	            throw new SignatureException();
	        }

	        return objStr;
	    }

	    @SuppressWarnings("unchecked")
		public byte[] jwtSign(String data) throws Exception {
	        logger.debug("Sign Jwt");
	        JsonWebSignature jws = new JsonWebSignature();
	        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
	        jws.setKey(signingKey);
	        jws.setHeader(ApiConstants.TYPE_HEADER, ApiConstants.API_TYPE_JWT);
	        jws.setHeader(ApiConstants.ACCOUNT_ID_HEADER, accountId);
	        jws.setHeader(ApiConstants.TOKEN_HEADER, token);
	        String expiresAsISO = getExpirationDate();
	        jws.setHeader(ApiConstants.JWT_EXPIRATION_HEADER, expiresAsISO);
	        UUID requestId = UUID.randomUUID();
	        jws.setHeader(ApiConstants.REQUEST_ID_HEADER, requestId.toString());
	        jws.setHeader(ApiConstants.JWT_VERSION_HEADER, ApiConstants.JWT_VERSTION_4);
	        JSONObject jo = new JSONObject();
	        jo.put(ApiConstants.Signature.JWT_BODY_FIELD, data);
	        jws.setPayload(jo.toJSONString());
	        jws.setDoKeyValidation(false); // relaxes the key length requirement

	        try {
	            return jws.getCompactSerialization().getBytes();
	        } catch (JoseException e) {
	            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
	        }
	    }
	    
	    private String getExpirationDate() {
			LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
	        LocalDateTime expires = now.plusMinutes(5);
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
	        String expiresAsISO = formatter.format(expires);
			return expiresAsISO;
		}

	    public JsonWebSignature jwtVerify(byte[] data) throws SignatureException {
	        logger.debug("Jwt verify");
	        if (data == null) {
	            throw new SignatureException("payload is null");
	        }

	        String strData = new String(data);
	        JsonWebSignature jws = new JsonWebSignature();

	        try {
	            jws.setCompactSerialization(strData);
	            String accountId = jws.getHeader(ApiConstants.ACCOUNT_ID_HEADER);
	            String token = jws.getHeader(ApiConstants.TOKEN_HEADER);
	            if (accountId == null || token == null) {
	                logger.info("JWT signature cannot find accountId or token required header=%s", jws.getHeaders());
	                throw new SignatureException(strData);
	            }
	            jws.setKey(signingKey);
	            jws.setDoKeyValidation(false); // relaxes the key length requirement

	            return jws;
	        } catch (org.jose4j.lang.IntegrityException e) {
	            String[] jwt = strData.split(".");
	            if (jwt.length == 3) {
	                throw new SignatureException();
	            }
	            throw new SignatureException(strData);
	        } catch (JoseException e) {
	            throw new SignatureException(strData);
	        } catch (Exception e) {
	            throw new SignatureException();
	        }
	    }

	    public void comparePayloads(String reqHeaderPayload, String reqPayload) throws SignatureException {
	        logger.debug("Compare payloads reqHeaderPayload=\"%s\" reqPayload=\"%s\"", reqHeaderPayload, reqPayload);
	        if (reqHeaderPayload == null || reqPayload == null) {
	            throw new SignatureException("Payload cannot be NULL");
	        }

	        if (!reqHeaderPayload.equals(reqPayload)) {
	            logger.error(String.format("reqHeaderPayload=\"%s\" reqPayload=\"%s\" are not equal", reqHeaderPayload, reqPayload));
	            throw new SignatureException("Request and header payload are not equal");
	        }
	    }

	    public JsonWebSignature jwtVerify(byte[] bytes, SecretKey key) {
	        return null;
	    }
}
