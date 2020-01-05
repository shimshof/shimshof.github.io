package com.pingidentity.moderno.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.pingidentity.moderno.api.APIUtils;
import com.pingidentity.pingfed.PingFedData;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.io.FileInputStream;

/**
 * HandlerFactory
 *
 * Listener which initializes "PingID SDK" data
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public class InitializationContextListener  implements ServletContextListener {
	
	private static final Logger logger = LoggerFactory.getLogger(InitializationContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		initializePingIDSDKData();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}
	
	private void initializePingIDSDKData(){
	  /*
        "Moderno" sample takes all the needed information from a properties file.
        This file should be located under: "/env/moderno-props/".
        The following properties are mandatory:
       	 api_key= <<<the API key>>>
       	 token=  <<<the token >>>
       	 pingidsdk_url=https://sdk.pingid.com/pingid
       	 account_id=bb09a7a1-b359-418c-9c66-d8b91d83fda4 (example)
       	 app_id=3f02bbd2-1291-41ae-9663-3a2b75956d6a  (example)
       
       "PingFederate" endpoint is mandatory only if you wish to integrate "Moderno" with PingFederate:
       	pingfed_base_endpoint=http://localhost:9032 (example)
       
       The following properties are optional. If you wish to send a dynamic data to PingID SDK Adapter (in PingFederate), you can fill the following
       properties: (Read the documentation for further details)
       	
       	pingfed_base64_private_key=<<< private key which should be used signed the dynamic data>>>
       	pingfed_sign_alg=<<<The sign algorithm >>> 
       	pingfed_qr_code_user_approval_required=<<<true or false>>>
       	pingfed_qr_code_client_context=QR Code scanned! Are you sure you want to sign on to Moderno? (example)
       	pingfed_qr_code_push_message_title=Moderno (QR Code) (example)
       	pingfed_qr_code_push_message_body=Moderno QR Code Authentication Request (example)
       	pingfed_application_id=3f02bbd2-1291-41ae-9663-3a2b75956d6a (example. If you wish to override the application configured in PingID SDK Adapter)
       	pingfed_client_context=Are you sure you want to sign on to Moderno? (example)
       	pingfed_push_message_title=Moderno (example)
       	pingfed_push_message_body=Moderno Authentication Request (example)
       	pingfed_qr_code_web_user_selection=<<<true or false>>>

       */
		try {
			FileInputStream stream = null;
			try {
				Properties prop = new Properties();
				String fileName = APIUtils.getPropFileName("/env/moderno-props/");
				
				if(fileName == null){
					logger.error("PingID SDK properties not found. In order to start working with this sample, please download PingID SDK properties file and place it in /env/moderno-props/ folder");
					return;
				}
				
				stream = new FileInputStream("/env/moderno-props/" + fileName);
				prop.load(stream);
				String url = prop.getProperty("pingidsdk_url");
				String accountId = prop.getProperty("account_id");
				String appId = prop.getProperty("app_id");
				String token = prop.getProperty("token");
				String apiKey = prop.getProperty("api_key");
				String qrCodeWebUserSelectionStr = prop.getProperty("qr_code_web_user_selection");
				
				if(StringUtils.isBlank(accountId)){
					logger.error("account_id property is missing in the properties file");
					return;
				}
				
				if(StringUtils.isBlank(appId)){
					logger.error("app_id property is missing in the properties file");
					return;
				}
				
				if(StringUtils.isBlank(token)){
					logger.error("token property is missing in the properties file");
					return;
				}
				
				if(StringUtils.isBlank(apiKey)){
					// use_base64_key is the old property name for api_key
					apiKey = prop.getProperty("use_base64_key");
					if(StringUtils.isBlank(apiKey)){
						logger.error("api_key property is missing in the properties file");
						return;
					}
				}
				
				boolean qrCodeWebUserSelection = false;
				if(StringUtils.isNotBlank(qrCodeWebUserSelectionStr)){
					qrCodeWebUserSelection = Boolean.valueOf(qrCodeWebUserSelectionStr);
				}
				
				// pingFedRequestData reads all the properties which are related to PingFederate (if exist)
				PingFedData pingFedRequestData =  new PingFedData.Builder().build(prop);
				APIUtils.initializePingIDSdk(url.trim(), accountId.trim(), appId.trim(), token.trim(), apiKey.trim(), qrCodeWebUserSelection, pingFedRequestData);

			} finally {
				if (stream != null) {
					stream.close();
				}
			}


		} catch (Throwable e) {
			logger.error("error occured while trying to initalize PingID SDK configuration: " + e.getMessage());
		}
	}

}
