package com.pingidentity.pingidsdk.api;

/**
 * AuthenticationType
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import java.util.HashMap;
import java.util.Map;

public enum AuthenticationType {
//  CONFIRM("api_confirm_auth"),// TODO: do we need it?
  AUTHENTICATE("api_authenticate"),
//  USER_DEFINED("api_user_defined"),// TODO: do we need it?
  FINGERPRINT_PERMISSIVE("fingerprint_permissive_auth"),
  FINGERPRINT_RESTRICTIVE("fingerprint_restrictive_auth"),
  FINGERPRINT_HARD_RESTRICTIVE("fingerprint_hard_restrictive_auth"),
  OTP("otp_auth"),
  SWIPE("swipe_auth"),
  SILENT_AUTHENTICATE("silent_auth"),
  FINGERPRINT_RESTRICTEIVE_HARD_NO_FALLBACK("fingerprint_restrictive_hard_no_fallback");

  private String name;
  private static Map<String, AuthenticationType> types;

  private AuthenticationType(String name) {
      this.name = name;
  }

  public String getName() {
      return this.name;
  }

  public void setName(String name) {
      this.name = name;
  }

  public static AuthenticationType get(String authType) {
      return (AuthenticationType) types.get(authType);
  }

  static {
      types = new HashMap<String, AuthenticationType>();
      AuthenticationType[] arr$ = values();
      int len$ = arr$.length;

      for (int i$ = 0; i$ < len$; ++i$) {
          AuthenticationType type = arr$[i$];
          types.put(type.getName(), type);
      }

  }
}
