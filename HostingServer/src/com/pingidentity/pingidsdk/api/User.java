package com.pingidentity.pingidsdk.api;

/**
 * User
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseResource {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private String username;


    private String firstName;


    private String lastName;


    private UserStatus status;

    private boolean isUserInBypass;


    private Date lastLogin;

    private Date bypassExpiration;

    /**
     *
     * @param username the accells user name to edit
     * @param fName the new first name of the user
     * @param lName the new last name of the user
     */
    public User(String username, String fName, String lName) {
        super();
        this.username = username;
        this.firstName = fName;
        this.lastName = lName;
    }

    public User() {
        super();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName == null ? "" : lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public boolean isUserInBypass() {
        return isUserInBypass;
    }

    public void setUserInBypass(boolean userInBypass) {
        isUserInBypass = userInBypass;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getBypassExpiration() {
        return bypassExpiration;
    }

    public void setBypassExpiration(Date bypassExpiration) {
        this.bypassExpiration = bypassExpiration;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("username='").append(username).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", status=").append(status);
        sb.append(", isUserInBypass=").append(isUserInBypass);
        sb.append(", lastLogin=").append(lastLogin);
        sb.append(", bypassExpiration=").append(bypassExpiration);
        sb.append('}');
        return sb.toString();
    }
}
