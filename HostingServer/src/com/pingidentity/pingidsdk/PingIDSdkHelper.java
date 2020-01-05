package com.pingidentity.pingidsdk;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pingidentity.pingidsdk.api.Authentication;
import com.pingidentity.pingidsdk.api.AuthenticationStatus;
import com.pingidentity.pingidsdk.api.AuthenticationToken;
import com.pingidentity.pingidsdk.api.AuthenticationType;
import com.pingidentity.pingidsdk.api.Device;
import com.pingidentity.pingidsdk.api.OTP;
import com.pingidentity.pingidsdk.api.RegistrationToken;
import com.pingidentity.pingidsdk.api.User;
import com.pingidentity.pingidsdk.api.UserName;
import com.pingidentity.pingidsdk.api.UserStatus;
import com.pingidentity.pingidsdk.api.UserWithDevices;

/**
 *
 * PingIDSdkHelper
 *
 * This helper class is a start point for developers who wish to integrate their existing server with "PingID SDK" SDK.
 * 
 * This class demonstrates how to:
 * 
 *   1. Pair user "on the fly" (which means that if the user has no paired device, this code will generate a registration token for the user)
 * 
 *   2. Authenticate user (if the user is already paired)
 * 
 * Any request can come from two types of callers:
 * 
 *   1. The mobile application. In this case, the request contains a payload. Once the "PingID SDK" is integrated into the mobile application,
 *      each mobile application request contains this payload (the payload is generated in the SDK)
 *    
 *   2. The Web. In this case, the request does not contain a payload
 *  
 *  It is impossible to generate a RegToken for a non-active user without a payload - which means that it is impossible to pair user device from the web (only from the mobile)
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public class PingIDSdkHelper {

	private static final Logger logger = LoggerFactory.getLogger(PingIDSdkHelper.class);
	
	//  url constants. used in order to send requests to the "PingID SDK" server
	public static final String USERS_GET_URL = "/accounts/%s/applications/%s/users/%s";
	public static final String USERS_POST_URL = "/accounts/%s/users";
	public static final String REGISTRATIONS_TOKENS_POST_URL = "/accounts/%s/applications/%s/users/%s/registrationtokens";
	public static final String AUTHENTICATIONS_POST_URL = "/accounts/%s/applications/%s/users/%s/authentications";
	public static final String AUTHENTICATIONS_GET_URL = "/accounts/%s/applications/%s/users/%s/authentications/%s";
	public static final String AUTHENTICATIONS_PUT_URL = "/accounts/%s/applications/%s/users/%s/authentications/%s/otp";
	public static final String AUTHENTICATIONS_DELETE_URL = "/accounts/%s/applications/%s/users/%s/authentications/%s";
	public static final String AUTHENTICATION_TOKENS_POST_URL = "/accounts/%s/applications/%s/authenticationtokens";
	public static final String AUTHENTICATION_TOKENS_GET_URL = "/accounts/%s/applications/%s/authenticationtokens/%s";
	public static final String AUTHENTICATION_TOKENS_PUT_USERNAME_URL = "/accounts/%s/applications/%s/authenticationtokens/%s/username";

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
	 * PingIDForCustomersHelper constructor
	 * @param accountId The account ID. Any request to "PingID SDK" server is sent in a specific account context
	 * @param apiKey The account API key. The API key is specific for the account. appears in the pingidsdk.properties file which can be download from the admin portal
	 * @param token The account token. The token is specific for the account. appears in the pingidsdk.properties file which can be download from the admin portal
	 * @param applicationId Some of the requests to "PingID SDK" server are sent in a specific application context
	 * The application ID appears in the admin portal under each application in the applications tab
	 */
	public PingIDSdkHelper(String url, String accountId, String apiKey, String token, String applicationId) {
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
	 * Returns true if the user is active (that is, the user has at least one paired device)
	 * @param user the user
	 * @return true if the user is active
	 */
	public boolean isUserActive(User user) {
		return user.getStatus() != null && user.getStatus().equals(UserStatus.ACTIVE);
	}
	

	/**
	 * Creates a registration token for a non-active user (that is, the user has no paired devices)
	 * If the user is already active, an exception is thrown.
	 * If the payload is empty, an exception is thrown. Note: only mobile [which is integrated with "PingID SDK"] requests contain
	 * payload (which is generated in the mobile SDK)
	 * @param user the user
	 * @param pingIdPayloadMobile the mobile SDK payload
	 * @return RegToken for a non active user (that is, the user has no paired device).
	 * @throws PingIDSdkException
	 */
	public RegistrationToken createRegistrationTokenForNonActiveUser(User user, String pingIdPayloadMobile) throws PingIDSdkException {

		if (user == null) {
			logger.error("Cannot create a RegToken without a user");
			throw new PingIDSdkException("Cannot create a RegToken without user");
		}
		
		boolean isUserActive = isUserActive(user);

		if (isUserActive) {
			logger.error("Cannot create a RegToken for a user who is already active");
			throw new PingIDSdkException("Cannot create a RegToken for a user who is already active");
		}
		
		if(StringUtils.isBlank(pingIdPayloadMobile)){
			logger.error("Cannot create a RegToken without mobile SDK payload");
			throw new PingIDSdkException("Cannot create a RegToken without mobile SDK payload");
		}

		RegistrationToken regToken = createRegistrationToken(user.getUsername(), pingIdPayloadMobile);
		return regToken;
	}

	/**
	 * Authenticates a user with PingID SDK. The authenticate fails if the user has no paired device
	 * @param username the user name
	 * @param pingIdPayloadMobile the mobile SDK payload (if the request is not from the mobile, pingIdPayloadMobile is empty)
	 * @param deviceId the device ID. optional. Use for authentication with specific device
	 * @return the authentication
	 * @throws PingIDSdkException
	 */
	public Authentication authenticate(String username, String pingIdPayloadMobile, String deviceId, String customizedPushTitle, String customizedPushBody, String clientContext)
			throws PingIDSdkException {
		// Step (1) authenticate the user with "PingID SDK"
		Authentication authentication = authenticateWithPingIDSdk(username, pingIdPayloadMobile, deviceId, customizedPushTitle, customizedPushBody, clientContext);

		// Step (2) handle the returned authentication results.

		// if the authentication is still in progress , the hosting server
		// should poll the "PingID SDK" server
		// (sending "get" authentication requests) until final status is
		// returned.
		// Each hosting server can handle it in a different way.
		// For example, the hosting server can return the call and poll in a
		// different thread.
		// In this sample, the hosting server polls until a final status is
		// returned
		if (authentication.getStatus() == AuthenticationStatus.IN_PROGRESS) {
			authentication = pollPingIDSdkUntilFinalStatus(username, authentication.getId());
		}
		
		return authentication;

	}
	
	/** 
	 * Authenticates an offline user with PingID SDK. (if the mobile application is not reachable (e.g- airplane mode), or if the authenticate is done against sms or email).  
	 * The authenticate fails if the user has typed an invalid OTP - one time passcode.
	 * @param username the user name
	 * @param pingIdPayloadMobile the mobile SDK payload (if the request is not from the mobile application, pingIdPayloadMobile is empty)
	 * @param authId - the Id returned form first call to authenticate - this is the id of the authentication process
	 * @param OTP - the one time passcode that the user got by sms or email or displayed in the mobile Application
	 * @return the authentication
	 * @throws PingIDSdkException
	 */
	public Authentication authenticateOffline(String username,String pingIdPayloadMobile, String authId, String otp) throws PingIDSdkException {
			String url = String.format(AUTHENTICATIONS_PUT_URL, accountId, applicationId, username, authId);
			//a simple object that contains the one time passcode the user entered and a pingIdPayloadMobile if exist
			OTP otpResource = new OTP();
			otpResource.setOtp(otp);
			otpResource.setPayload(pingIdPayloadMobile);
			Authentication offlineAuthentication = apiHelper.put(Authentication.class, url, otpResource);
			return offlineAuthentication;
		}
	
	/**
	 * Returns the user devices
	 * @param username the user name
	 * @return the user devices
	 * @throws PingIDSdkException
	 */
	public List<Device> getUserDevices(String username) throws PingIDSdkException {
		String url = String.format(USERS_GET_URL, accountId, applicationId, username);
		UserWithDevices userWithDevices = apiHelper.get(UserWithDevices.class, url,"devices");
		logger.info(String.format("user with devices: %s", userWithDevices));
		return userWithDevices.getDevices();
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
		user.setFirstName(""); // optional (in this sample, no first name)
		user.setLastName(""); // optional (in this sample, no last name)
		User createdUser = apiHelper.post(User.class, url, user);
		logger.info(
				String.format("User created in PingID SDK Server. user name: %s", createdUser.getUsername()));
		return createdUser;
	}

	/**
	 * Create a registration token for a user
	 * @param username the user name. must not be null
	 * @param pingIdPayloadMobile. must not be null
	 * @return RegToken for a user
	 * @throws PingIDSdkException
	 */
	private RegistrationToken createRegistrationToken(String username, String pingIdPayloadMobile) throws PingIDSdkException {
		String url = String.format(REGISTRATIONS_TOKENS_POST_URL, accountId, applicationId,username);
		RegistrationToken regToken = new RegistrationToken();
		regToken.setPayload(pingIdPayloadMobile);
		RegistrationToken createdRegToken = apiHelper.post(RegistrationToken.class, url, regToken);
		logger.info(String.format("RegToken: %s created for user: %s", createdRegToken.getId(), username));
		return createdRegToken;
	}

	/**
	 * Authenticates with "PingID SDK"
	 * @param username the user name
	 * @param pingIdPayloadMobile the pingIF mobile SDK payload (null if the call is not from the mobile)
	 * @param deviceId. optional - the authenticating device ID
	 * @param customizedPushTitle 
	 * * @param customizedPushBody
	 * @return the authentication result
	 * @throws PingIDSdkException
	 */
	private Authentication authenticateWithPingIDSdk(String username, String pingIdPayloadMobile, String deviceId, String customizedPushTitle, String customizedPushBody, String clientContext) throws PingIDSdkException {
		logger.debug("Before authentication to PingID SDK...");

		String url = String.format(AUTHENTICATIONS_POST_URL, accountId, applicationId,
				username);
		Authentication authentication = new Authentication();
		authentication.setDeviceId(deviceId); // may be null
		authentication.setPayload(pingIdPayloadMobile); // null if the call is not from the mobile application
		authentication.setPushMessageTitle(customizedPushTitle);
		authentication.setPushMessageBody(customizedPushBody);
		authentication.setClientContext(clientContext);
		authentication.setAuthenticationType(AuthenticationType.AUTHENTICATE);
		Authentication createdAuthentication = apiHelper.post(Authentication.class, url, authentication);
		logger.info(String.format("Authentication Status: %s for user: %s", createdAuthentication.getStatus(), username));
		return createdAuthentication;
	}
	

	/**
	 * Polls "PingID SDK" until final status is returned.
	 * @param username the user name
	 * @param authenticationId the authentication ID
	 * @return the authentication
	 * @throws PingIDSdkException
	 */
	private Authentication pollPingIDSdkUntilFinalStatus(String username, String authenticationId) throws PingIDSdkException {
		String url = String.format(AUTHENTICATIONS_GET_URL, accountId, applicationId, username, authenticationId);
		while (true) {
			Authentication authentication = apiHelper.get(Authentication.class, url);
			if (authentication.getStatus() != AuthenticationStatus.IN_PROGRESS) {
				logger.info(String.format("Authentication Status: %s for username: %s", authentication.getStatus(), username));
				return authentication;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.info("unexpected Interrupt...");
			}
		}
	}

	/**
	 * Delete "live" authentication
	 * @param username the user name
	 * @param the existing authentication
	 * @throws PingIDSdkException 
	 * */
	public void deleteAuthentication(String username, String authenticationId) throws PingIDSdkException {
		String url = String.format(AUTHENTICATIONS_GET_URL, accountId, applicationId, username, authenticationId);
		apiHelper.delete(url);	
	}
	

    /**
     * Creates an authentication token
	 * Note: an authentication token can be created in a specific user context
	 * @param webUserSelection - If the device that scans the QR code is paired with more than one user - the user selection can be performed in the mobile application (default) or in the web
     * @param clientContext - any message or structure which the customer server wishes to pass to the device that successfully scans the QR code image - optional
     * @param customizedPushTitle - relevant if push is sent after the web selects the user (read the documentation for further details) - optional
     * @param customizedPushBody - relevant if push is sent after the web selects the user (read the documentation for further details) - optional
     * @return the generated authentication token
     * @throws PingIDSdkException
     */
    public AuthenticationToken createAuthenticationToken(boolean webUserSelection,String clientContext, String customizedPushTitle, String customizedPushBody) throws PingIDSdkException {
    	logger.debug("Before creating an authentication token...");
    	String url = String.format(AUTHENTICATION_TOKENS_POST_URL, accountId, applicationId);
	    AuthenticationToken authenticationToken = new AuthenticationToken();
	    authenticationToken.setClientContext(clientContext);
	    authenticationToken.setPushMessageTitle(customizedPushTitle);
	    authenticationToken.setPushMessageBody(customizedPushBody);
	    authenticationToken.setWebUserSelection(webUserSelection);
	    
	     // In this sample, user approval is required. It means that user must approve the authentication in the mobile application
	    authenticationToken.setUserApprovalRequired(true); 
	    // user name - an authentication token can be created in a specific user context (which means that only this specific user can use this token to authenticate).
	    // In this case, the user is unknown.
	    authenticationToken.setUsername(null);
	    return apiHelper.post(AuthenticationToken.class, url, authenticationToken);
    }
    
    /**
     * Returns the authentication token (if exists), null otherwise
     * @param id the authentication token if
     * @return the authentication token
     * @throws PingIDSdkException
     */
    public AuthenticationToken getAuthenticationToken(String id) throws PingIDSdkException {
 		String url = String.format(AUTHENTICATION_TOKENS_GET_URL, accountId, applicationId, id); 
 	    return apiHelper.get(AuthenticationToken.class, url);
     }
    
    /**
     * Update Authentication Token user name
     * It is possible to update the user name only if the authentication status == "PENDING_USER_SELECTION" and it is configured in the admin web portal
     * that the selection is done in the web (rather than in the mobile application - which is the default)
     * @param id the authentication token id
     * @param username to update with
     * @return the updated authentication token
     * @throws PingIDSdkException
     */
    public AuthenticationToken updateAuthenticationTokenUsername(String id, String username) throws PingIDSdkException {
    		String url = String.format(AUTHENTICATION_TOKENS_PUT_USERNAME_URL, accountId, applicationId, id);
    		UserName usernamePayload = new UserName();
    		usernamePayload.setUsername(username);
    		AuthenticationToken authenticationToken = apiHelper.put(AuthenticationToken.class, url, usernamePayload);
    		return authenticationToken;
    }	
}
