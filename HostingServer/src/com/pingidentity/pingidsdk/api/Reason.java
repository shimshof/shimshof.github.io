package com.pingidentity.pingidsdk.api;

public enum Reason {
	DENIED_BY_USER, BLOCKED_BY_USER, CANCELED_BY_USER, DEVICE_BLOCKED, DEVICE_ROOTED;
	
	public static Reason get(String reason){
		if(reason == null){
			return null;
		}
		
		for(Reason reasonValue : Reason.values()){
			if(reasonValue.name().equalsIgnoreCase(reason)){
				return reasonValue;
			}
		}
		
		return null;
	}
}
