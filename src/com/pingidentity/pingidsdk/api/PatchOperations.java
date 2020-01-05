package com.pingidentity.pingidsdk.api;

/**
 * PatchOperations
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
import java.io.Serializable;
import com.github.fge.jsonpatch.JsonPatch;

public class PatchOperations implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JsonPatch  operations;

	public JsonPatch getOperations() {
		return operations;
	}

	public void setOperations(JsonPatch operations) {
		this.operations = operations;
	}	

}
