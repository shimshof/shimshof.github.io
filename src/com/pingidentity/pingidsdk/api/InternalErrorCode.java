package com.pingidentity.pingidsdk.api;

/**
 * InternalErrorCode
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public enum InternalErrorCode {
	INVALID_VALUE,
	REQUIRED_VALUE,
	EMPTY_VALUE,
	OUT_OF_RANGE,
	SIZE_LIMIT_EXCEEDED,
    NOT_FOUND,
    INVALID_PATTERN,
    RESOURCE_IN_USE,
    UNIQUE_VALUE_REQUIRED,
    SERVICE_ERROR,
    DEVICE_BYPASSED,
    DEVICE_IGNORED,
    DEVICE_BLOCKED,
    APPLICATION_DISABLED,
    USER_DISABLED,
    APPLICATION_SHARED_WITH_NON_EXIST_ID,
    INVALID_PRODUCTION_CERTIFICATE,
    INVALID_SANDBOX_CERTIFICATE,
    PRODUCTION_CERTIFICATE_EXPIRED,
    SANDBOX_CERTIFICATE_EXPIRED,
    PRODUCTION_CERTIFICATE_FAIL_TO_CONNECT,
    SANDBOX_CERTIFICATE_FAIL_TO_CONNECT,
    FCM_FAIL_TO_CONNECT, 
    INVALID_USER_STATUS, 
    RESOURCE_ALREADY_EXISTS,
    SMS_QUOTA_EXCEEDED,
    SMS_NOT_ENABLED,
    RETRY_LIMIT_EXCEEDED, 
    EMAIL_NOT_ENABLED
}
