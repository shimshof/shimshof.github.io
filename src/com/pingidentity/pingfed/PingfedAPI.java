package com.pingidentity.pingfed;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingidentity.moderno.api.PingIDSdkConfiguration;

import java.util.List;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PingfedAPI {
	
	private static final Logger logger = LoggerFactory.getLogger(PingfedAPI.class);
	
	public static AccessToken getAccessToken(String authorizationCode, String redirectUri){
		
		AccessToken token = null;
		try {
				
			CloseableHttpClient httpClient = HttpClients.createDefault();

			try {
				String tokenEndpoint = PingIDSdkConfiguration.instance().getPingFedRequestData().getPingFedBaseEndPoint() + "/as/token.oauth2";
				HttpPost httpPost = new HttpPost(tokenEndpoint);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("client_id", "moderno_client"));
				params.add(new BasicNameValuePair("redirect_uri", redirectUri));
				
				/*
				 * !!!Note: using an hard coded client secret is not recommended. This is just for demonstration only
				 */
				params.add(new BasicNameValuePair("client_secret",
						"abc123DEFghijklmnop4567rstuvwxyzZYXWUT8910SRQPOnmlijhoauthplaygroundapplication"));
				params.add(new BasicNameValuePair("grant_type", "authorization_code"));
				params.add(new BasicNameValuePair("code", authorizationCode));
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				CloseableHttpResponse response = httpClient.execute(httpPost);
				int status = response.getStatusLine().getStatusCode();
				String responseEntity = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
				logger.debug(String.format("response entity = %s, status = %d", responseEntity, status));
				if (status == HttpStatus.SC_OK) {
					ObjectMapper mapper = new ObjectMapper();
					token = mapper.readValue(responseEntity, AccessToken.class);
				} else {
					logger.info("failed to get the access token for the user");
				} 
			} finally {
				httpClient.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return token;
	}
	
	
	public static UserInfo getUserInfo(String accessToken){
		
		UserInfo userInfo = null;
		try {
			
			CloseableHttpClient httpClient = HttpClients.createDefault();
			try {
				String userEndpoint = PingIDSdkConfiguration.instance().getPingFedRequestData().getPingFedBaseEndPoint() + "/idp/userinfo.openid";
				HttpGet httpGet = new HttpGet(userEndpoint);
				httpGet.addHeader("Authorization", String.format("Bearer %s", accessToken));
				CloseableHttpResponse response = httpClient.execute(httpGet);
				int status = response.getStatusLine().getStatusCode();
				String responseEntity = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
				logger.info(String.format("response entity = %s, status = %d", responseEntity, status));
				
				if (status == HttpStatus.SC_OK) {
					ObjectMapper mapper = new ObjectMapper();
					userInfo = mapper.readValue(responseEntity, UserInfo.class);
				} else {
					logger.info("failed to get user info for the user");
				} 
			} finally {
				httpClient.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return userInfo;
	}

}
