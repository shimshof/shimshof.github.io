package com.pingidentity.moderno.api;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import com.pingidentity.pingfed.PingFedData;

/**
 * APIUtils
 *
 * Created by Ping Identity on 3/23/17. Copyright © 2017 Ping Identity. All
 * rights reserved.
 */
public class APIUtils {

	/**
	 * Get the properties file from the given path. The proerties file contains
	 * all the data needed to send request to "PingID SDK"
	 * 
	 * @param folderPath
	 *            the folder path
	 * @return the properties file from the given path.
	 */
	public static String getPropFileName(String folderPath) {
		File folder = new File(folderPath);

		if (!folder.isDirectory()) {
			return null;
		}

		Optional<String> propFile = Arrays.stream(folder.list()).filter(file -> file.endsWith(".properties")).findAny();

		if (propFile.isPresent()) {
			return propFile.get();
		}
		return null;
	}

	/**
	 * Initialization
	 * @param url
	 * @param accountId
	 * @param appId
	 * @param token
	 * @param apiKey
	 * @param qrCodeWebUserSelection
	 * @param pingFedRequestData
	 */
	public static void initializePingIDSdk(String url, String accountId, String appId, String token, String apiKey,boolean qrCodeWebUserSelection, PingFedData pingFedRequestData) {
		// initializes the configuration
		PingIDSdkConfiguration.instance().initialize(url, accountId, appId, token, apiKey, qrCodeWebUserSelection, pingFedRequestData);
	}
}
