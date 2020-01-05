package com.pingidentity.pingidsdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pingidentity.pingidsdk.api.User;

/**
*
* PingIDSdkSMSHelper
*
* This helper class is a start point for developers who wish to integrate their existing server with "PingID SDK" SDK.
* 
* First, in order to do a pairing with SMS, you need to enable this option from the admin portal.
* 
* Go to the admin portal -> Applications -> PingID SDK Applications 
*   
* then choose your application click the edit button and then enable the option "ALTERNATE AUTHENTICATION METHODS".
* 
* This class demonstrates how to:
* 
*   1. Pair a user with SMS (if the user doesn't exist we create him)
* 
*   2. Finalize the Pair using the otp that the user got from the first stage Authenticate user (if the user is already paired)
* 
* the request can come from the Web.
*
* Created by Ping Identity on 12/18/17.
* Copyright © 2017 Ping Identity. All rights reserved.
*/

public class PingIDSdkSMSHelper {

private static final Logger logger = LoggerFactory.getLogger(PingIDSdkSMSHelper.class);

	public static final String DEFAULT_SMS_MESSAGE = "Your PingID SDK pairing code is: ${otp}";
	public static final String DEFAULT_SENDER_NAME = "Ping";
	
	// URL constants. used in order to send requests to the "PingID SDK" server
	public static final String USERS_GET_URL = "/accounts/%s/applications/%s/users/%s";
	public static final String USERS_POST_URL = "/accounts/%s/users";
	public static final String PAIRING_POST_URL = "/accounts/%s/applications/%s/users/%s/pairings";
	public static final String PAIRING_PUT_URL = "/accounts/%s/applications/%s/users/%s/pairings/%s/otp";

	/**
	 * The account ID. Any request to "PingID SDK" server is sent in a specific account context.
	 * The account ID appears in the pingidsdk.properties file which can be download from the admin portal
	 */
	private String accountId;

	/**
	 * The application ID. Some of the requests to "PingID SDK" server are sent in a specific application context.
	 * The application ID appears in the admin portal under each application in the applications tab
	 */
	private String applicationId;
	
	/**
	 * "PingID SDK" API helper. This layer sends the various requests to the "PingID SDK" server
	 */
	private PingIDSdkAPI apiHelper;
	
	/**
	 * PingIDSdkSMSHelper constructor
	 * @param url the url to execute
	 * @param accountId The account ID. Any request to "PingID SDK" server is sent in a specific account context
	 * @param apiKey The account API key. The API key is specific for the account. appears in the pingidsdk.properties file which can be download from the admin portal
	 * @param token The account token. The token is specific for the account. appears in the pingidsdk.properties file which can be download from the admin portal
	 * @param applicationId Some of the requests to "PingID SDK" server are sent in a specific application context
	 * The application ID appears in the admin portal under each application in the applications tab
	 */
	public PingIDSdkSMSHelper(String url, String accountId, String apiKey, String token, String applicationId) {
		this.accountId = accountId;
		this.applicationId = applicationId;
		apiHelper =  new PingIDSdkAPI(url, accountId, apiKey, token);
	}
	
	/**
	 * Return the user if exists already in "PingID SDK" or creates it if the user does not exist in "PingID SDK"
	 * @param username the user name. must be unique under a specific account
	 * @return the user 
	 * @throws PingIDSdkException
	 */
	public User getUserOrCreateIfUserNotExist(String username) throws PingIDSdkException{
		User user = getUserFromPingIDSdk(username);

		if (user == null) {
			user = addUserToPingIDSdk(username);
		}
		return user;
	}
	
	/**
	 * Returns the user from "PingID SDK". null - if not exist
	 * @param username the user name
	 * @return  the user from "PingID SDK". null - if not exist
	 * @throws PingIDSdkException
	 */
	private User getUserFromPingIDSdk(String username) throws PingIDSdkException {
		String url = String.format(USERS_GET_URL, accountId, applicationId, username);
		User user = apiHelper.get(User.class, url);
		return user;
	}

	/**
	 * Adds the user to "PingID SDK"
	 * @param username the user name
	 * @return the user
	 * @throws PingIDSdkException
	 */
	private User addUserToPingIDSdk(String username) throws PingIDSdkException {
		String url = String.format(USERS_POST_URL, accountId);
		User user = new User();
		user.setUsername(username);
		user.setFirstName(""); //Optional (in this sample, no first name)
		user.setLastName(""); //Optional (in this sample, no last name)
		User createdUser = apiHelper.post(User.class, url, user);
		logger.info(String.format("User created in PingID SDK Server. user name: %s", createdUser.getUsername()));
		return createdUser;
	}

}
