package com.pingidentity.moderno.utils;

import java.io.Serializable;

import org.apache.commons.lang3.tuple.Pair;

import com.pingidentity.pingidsdk.api.Authentication;

/**
 * SessionData
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public class SessionData implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * The user name
	 */
	private String userName;
	
	/**
	 * "PingID SDK" authentication
	 */
	private Authentication pingidAuthentication;
	
	
	private String smsPairingId;
	
	/**
	 * The session ID
	 */
	private String sessionId;

	private Pair<String, Float> subAccountChecking;
	private Pair<String, Float> subAccountSavings;
	
	/**
	 * Whether this is a Ping Federate session
	 */
	private boolean pingFederateSession;
	
	/**
	 * The transfered sum
	 */
	private String transferSum;
	
	private boolean transactionApproved;


	public SessionData(String userName) {
		this.userName = userName;
		setSessionId(java.util.UUID.randomUUID().toString());		
	}
	
	public String getUserName() {
		return userName;
	}

	public void setPingidAuthentication(Authentication pingidAuthentication) {
		this.pingidAuthentication = pingidAuthentication;
	}

	public Authentication getPingidAuthentication() {
		return pingidAuthentication;
	}

	public String getSessionId() {
		return sessionId;
	}

	private void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Pair<String, Float> getSubAccountChecking() {
		return subAccountChecking;
	}

	public void setSubAccountChecking(Pair<String, Float> subAccountChecking) {
		this.subAccountChecking = subAccountChecking;
	}

	public Pair<String, Float> getSubAccountSavings() {
		return subAccountSavings;
	}

	public void setSubAccountSavings(Pair<String, Float> subAccountSavings) {
		this.subAccountSavings = subAccountSavings;
	}
	
	public String getSmsPairingId() {
		return smsPairingId;
	}

	public void setSmsPairingId(String smsPairingId) {
		this.smsPairingId = smsPairingId;
	}
	
	public boolean isPingFederateSession() {
		return pingFederateSession;
	}

	public void setPingFederateSession(boolean pingFederateSession) {
		this.pingFederateSession = pingFederateSession;
	}

	public String getTransferSum() {
		return transferSum;
	}

	public void setTransferSum(String transferSum) {
		this.transferSum = transferSum;
	}
	
	public boolean isTransactionApproved() {
		return transactionApproved;
	}

	public void setTransactionApproved(boolean transactionApproved) {
		this.transactionApproved = transactionApproved;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (pingFederateSession ? 1231 : 1237);
		result = prime * result + ((pingidAuthentication == null) ? 0 : pingidAuthentication.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((smsPairingId == null) ? 0 : smsPairingId.hashCode());
		result = prime * result + ((subAccountChecking == null) ? 0 : subAccountChecking.hashCode());
		result = prime * result + ((subAccountSavings == null) ? 0 : subAccountSavings.hashCode());
		result = prime * result + (transactionApproved ? 1231 : 1237);
		result = prime * result + ((transferSum == null) ? 0 : transferSum.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SessionData other = (SessionData) obj;
		if (pingFederateSession != other.pingFederateSession)
			return false;
		if (pingidAuthentication == null) {
			if (other.pingidAuthentication != null)
				return false;
		} else if (!pingidAuthentication.equals(other.pingidAuthentication))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (smsPairingId == null) {
			if (other.smsPairingId != null)
				return false;
		} else if (!smsPairingId.equals(other.smsPairingId))
			return false;
		if (subAccountChecking == null) {
			if (other.subAccountChecking != null)
				return false;
		} else if (!subAccountChecking.equals(other.subAccountChecking))
			return false;
		if (subAccountSavings == null) {
			if (other.subAccountSavings != null)
				return false;
		} else if (!subAccountSavings.equals(other.subAccountSavings))
			return false;
		if (transactionApproved != other.transactionApproved)
			return false;
		if (transferSum == null) {
			if (other.transferSum != null)
				return false;
		} else if (!transferSum.equals(other.transferSum))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SessionData [userName=" + userName + ", pingidAuthentication=" + pingidAuthentication + ", sessionId=" + sessionId + ", smsPairingId=" + smsPairingId + "]";
	}	

}
