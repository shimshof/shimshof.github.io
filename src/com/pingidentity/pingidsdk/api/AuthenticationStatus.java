package com.pingidentity.pingidsdk.api;

/**
 * AuthenticationStatus
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import java.util.HashMap;
import java.util.Map;

public enum AuthenticationStatus {
	    OTP("otp"),
	    REJECTED("rejected"),
	    APPROVED("approved"),
	    IN_PROGRESS("in progress"),
	    TIMEOUT("timeout"),
	    LOCKED("locked"),
	    OTP_IS_BLOCKED("otp.is.blocked"), // TODO: should be error?
	    INVALID_OTP("invalid.otp"),
	    CANCELED("canceled"),
	    SELECT_DEVICE("select.device"),
	    IGNORED_DEVICE("ignored.device"),
	    BYPASSED_DEVICE("bypassed.device");

	    private String name;
	    private static Map<String, AuthenticationStatus> statuses;

	    private AuthenticationStatus(String name) {
	        this.name = name;
	    }

	    public String getName() {
	        return this.name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public static AuthenticationStatus get(String status) {
	        return (AuthenticationStatus) statuses.get(status);
	    }

	    static {
	        statuses = new HashMap<String, AuthenticationStatus>();
	        AuthenticationStatus[] arr$ = values();
	        int len$ = arr$.length;

	        for (int i$ = 0; i$ < len$; ++i$) {
	            AuthenticationStatus type = arr$[i$];
	            statuses.put(type.getName(), type);
	        }

	    }
}
