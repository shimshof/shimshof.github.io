package com.pingidentity.pingfed;


import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInfoHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(UserInfoHandler.class);
	
	public static UserInfo getUserInfo(String accessToken, String idToken){
		decodeIdToken(idToken);
		return PingfedAPI.getUserInfo(accessToken);
	}
	
	
	private static void decodeIdToken(String idToken){

        String[] split_string = idToken.split("\\.");
        String base64EncodedHeader = split_string[0];
        String base64EncodedBody = split_string[1];

        Base64 base64Url = new Base64(true);
        String header = new String(base64Url.decode(base64EncodedHeader));
        System.out.println("JWT Header : " + header);

        String body = new String(base64Url.decode(base64EncodedBody));
        System.out.println("JWT Body : "+body);
        
        logger.info(String.format("header = %s, body = %s", header, body));
	}

}
