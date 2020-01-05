package com.pingidentity.pingidsdk.api;

public enum DeviceType {
	SMS;
	
	public static DeviceType get(String deviceType){
		if(deviceType == null){
			return null;
		}
		
		for(DeviceType type : DeviceType.values()){
			if(type.name().equalsIgnoreCase(deviceType)){
				return type;
			}
		}
		
		return null;
	}
}
