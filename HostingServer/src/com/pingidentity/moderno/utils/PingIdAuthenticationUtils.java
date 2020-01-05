package com.pingidentity.moderno.utils;

import com.pingidentity.pingidsdk.api.Reason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pingidentity.moderno.handlers.Status;
import com.pingidentity.pingidsdk.api.AuthenticationStatus;

/**
 * PingIdAuthenticationUtils
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public class PingIdAuthenticationUtils {

	private static final Logger logger = LoggerFactory.getLogger(PingIdAuthenticationUtils.class);

	/**
	 * Convert the authentication status received from "PingID SDK" to the hosting server status
	 * @param authenticationStatus
	 * @return
	 */
	public static Status convertPingIdAuthenticationStatusToHostingServerStatus(AuthenticationStatus authenticationStatus) {
		return convertPingIdAuthenticationStatusToHostingServerStatus(authenticationStatus, null);
	}

	public static Status convertPingIdAuthenticationStatusToHostingServerStatus(AuthenticationStatus authenticationStatus, Reason reason) {
		switch (authenticationStatus) {
		case APPROVED:
			return Status.OK;
		case LOCKED:
			return Status.BLOCKED;
		case OTP_IS_BLOCKED:
		case REJECTED:
			if (Reason.DEVICE_ROOTED == reason) {
				return Status.ROOTED_DEVICE;
			}
			return Status.AUTHENTICATION_DENIED;
		case OTP:
			return Status.OTP;
		case TIMEOUT:
			return Status.TIMEDOUT;
		case INVALID_OTP:
			return Status.INVALID_OTP;
		case IN_PROGRESS:
			return Status.AUTHENTICATION_IN_PROGRESS;
		case SELECT_DEVICE:
			return Status.SELECT_DEVICE;
		case IGNORED_DEVICE:
			return Status.IGNORED_DEVICE;
		case BYPASSED_DEVICE:
			return Status.BYPASSED_DEVICE;
		default:
			logger.error(String.format("unexpected pingid authentiaction status: %s", authenticationStatus));
			return Status.FAILED;
		}
	}

}
