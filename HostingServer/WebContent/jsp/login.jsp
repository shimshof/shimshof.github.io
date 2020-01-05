<%@page import="com.pingidentity.moderno.utils.Util"%>
<%@page import="com.pingidentity.moderno.api.APIUtils"%>
<%@page import="com.pingidentity.moderno.qrcode.QRCodeUtils"%>

<%@page import="com.pingidentity.moderno.api.PingIDSdkConfiguration"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.util.Properties"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="com.pingidentity.moderno.model.api.AuthenticationTokenResponse"%>
<%@page import="com.pingidentity.pingfed.PingFedData"%>
<%@page import="com.pingidentity.pingfed.PingFederateRequestGenerator"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale = 1.0, maximum-scale=1.0, user-scalable=no">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<link rel="shortcut icon" href="..\assets\images\partialLogo.png"/>
<link rel="stylesheet" type="text/css" href="../login.css" media="screen" />
<title>Moderno Financial</title>
</head>
<script>
/*
 ****************************     SCRIPT FUNCTIONS    ****************************
 *
 * In this section you can find the various functions that are used by Moderno Sample browser.
 *
 */
 
// The timer is unsually used whenever the broswer is polling "Moderno" server.
var timer;

/*
 * General method. Its job is to show a div element by removing the "hide-status" class from the element.
 */

function showDivElement(divId) {
	var divElement = document.getElementById(divId);
	divElement.classList.remove('hide-status');
}

/*
 * General method. Its job is to hide a div element by adding the "hide-status" class to the element.
 */
function hideDivElement(divId) {
	var divElement = document.getElementById(divId);
	divElement.classList.add('hide-status');
}

/*
 * General method. Its job is to hide a div element and show a div element
 */
function changeStatus(oldStatusId, newStatusId) {
	hideDivElement(oldStatusId);
	showDivElement(newStatusId);
}

/*
 * This method is called once the user clicks on "sign on" button.
 */
function authenticateUser(operation) {
	var user = document.getElementById("userId").value;
	// In this sample, the password is not used
	var password = document.getElementById("passwordId").value;

	// invokes authentication using PingID SDK API
	authenticate(operation, user, password);
}

/*
 * This method is executed once the user clicks on the "sign on" button
 */
function authenticate(operation, user, password) {
	// build the data the "Moderno" server expects in the request
	var data = JSON.stringify({
		operation : operation,
		user : user,
		password : password
	});
	
	/*
	 The following code hides and displays div elements.
	 It hids the screen that displays the user/password
	 and displays a "spinner"
	*/
	hideDivElement('invalidOtpBanner');
 	showDivElement('statusId');
	changeStatus('loginId', 'authenticatingId');
	/* in case we were in otp mode */
	changeStatus('statusOtpId', 'authenticatingId');
	
	// Sends a post request to the "Moderno" server
	$(document).ready(
		function() {
			$.ajax({
				type : "POST",
				url : "../pingidsdk",
				dataType: "json",
				contentType: 'application/json',
				data : data,
				success : function(result) {
					// Handle a success result:
					
					hideDivElement('authenticatingId');
					/*
					 Status list: (Please note: the following statuses are "Moderno" sample statuses and they are different than the "PingID SDK Server" authentication statuses)
							
							OK(0, "SUCCESS"),
							FAILED(1000, "FAILED") //GENERAL failure
							USER_NOT_AUTHENTICATED(1010, "user is not authenticated"),
							AUTHENTICATION_IN_PROGRESS(1012,"Authentication in progress"),
							AUTHENTICATION_DENIED(1014,"Authentication is denied"),
							OTP(1015,"OTP required"),
							TIMEDOUT(1016,"Timedout"),
							SELECT_DEVICE(1018,"second factor authenticator must be approved by one of the user trusted devices"),
							INVALID_INPUT_DATA(1020,"Invalid input data"),
							AUTHENTICATION_IS_IN_NOT_OFFLINE_MODE(1021,"PingID Authentication is not in offline mode"),
							IGNORED_DEVICE(1023,"Ignored Device"),
							USER_NOT_ACTIVE(1024,"User is not active. Please install the mobile application..."),
							INVALID_OTP(1025,"Invalid OTP"),
							BYPASSED_DEVICE(1026,"Bypassed Device"), 
							BLOCKED(1027, "user is blocked"),
							ROOTED_DEVICE(1028,"Rooted Device");

					*/
					
					// The authentication is successful or the device is set to be bypassed (0 - success, 1026 - Bypassed Device)
					if (result.status == 0 || result.status == 1026) {
						showDivElement('authenticatedId');
						setTimeout(
							function() {
								window.location="Transactions.jsp";
						    },5000);
					}
					// OTP ("One-time passcode") is required. For example, if the device is unreachable...(1015 - OTP required)
					else if (result.status == 1015) {
						hideDivElement('statusId');
						changeStatus('authenticatingId', 'statusOtpId');
						localStorage.setItem("device", result.currentAuthenticatingDeviceData.deviceType);
						setTextAcordingToDeviceType();
						$('#otpId').focus();
						setTimeout(changeSubmitAvailability, 200);
					// The authentication is denied (1014 - Authentication is denied)
					} else if (result.status == 1014) {
						showDivElement('deniedId');
						setTimeout(
							function() {
								hideDivElement('statusId');
								changeStatus('deniedId', 'loginId');
						}, 5000);
					// the authentication is denied, because the authenticating device is rooted or jailbroken (1028 - rooted device)
					} else if (result.status == 1028) {
						showDivElement('blockedId');
						setTimeout(
							function() {
								hideDivElement('statusId');
								changeStatus('blockedId', 'loginId');
						}, 5000);
				    // Timeout (1016 - "Timedout")
					} else if (result.status == 1016) {
						showDivElement('timedoutId');

						setTimeout(
							function() {
								hideDivElement('statusId');
								changeStatus('timedoutId', 'loginId');
						}, 5000);
					// the user is blocked (1027 - user is blocked)
					}else if (result.status == 1027) {
						showDivElement('blockedId');

						setTimeout(
							function() {
								hideDivElement('statusId');
								changeStatus('timedoutId', 'loginId');
						}, 5000);
					
					// In all other use cases - display error
					}else {
						showDivElement('errorId');
							
						setTimeout(
							function() {
								hideDivElement('statusId');
								changeStatus('errorId', 'loginId');
							}, 5000);
					}
				}
			});
		}
	);
}

/*
 * This method is called once the user clicks on "SIGN ON WITH PING FEDERATE" button.
 */
function authenticateUserWithPingFed(){
	 var pingFederateForm = document.getElementById("pingFederateForm");
	 
	 /*
	    Observe PingFederateServlet class which handles this request.
	    The servlet creates the request (which contains all the relevant PingID SDK data)
	 	and redirects the user to PingFederate authorization endpoint
	 */
	 pingFederateForm.submit();

}

/* OTP FLOW. 
 * 
 *This method is called once the user types the OTP ("One Time Passcode") and clicks on the "Sign On" button
 */
function authenticateOffline() {
	
		var otp = document.getElementById("otpId").value;
		var data = JSON.stringify({
			operation : "auth_offline_user",
			otp : otp
		});
	
		$('offlineButtonId').prop('disabled', true);
		$('retryButtonId').prop('disabled', true);
		
		
		$(document).ready(
			function() {
				$.ajax({
					type : "POST",
					url : "../pingidsdk",
					dataType: "json",
					contentType: 'application/json',
					data : data,
					success : function(result) {
						hideDivElement('authenticatingId');
						// The authentication is successful or the device is set to be bypassed (0 - success, 1026 - Bypassed Device)
						if (result.status == 0 || result.status == 1026) {
							showDivElement('statusId');
							changeStatus('statusOtpId','authenticatedId');
							
							setTimeout(
								function() {
									window.location="Transactions.jsp";
							    },2000);
					    // the user entered an invalid OTP (1025 - Invalid OTP)
						} else if (result.status == 1025) {
							showDivElement('invalidOtpBanner');
							$('#otpId').css('color','red');
							$('#otpId').click(function() {
								hideDivElement('invalidOtpBanner');
								$('#otpId').css('color','#999999');
								$('#otpId').val('');
								setTextAcordingToDeviceType();
								setTimeout(changeSubmitAvailability, 200);
						    })
						// The authentication is denied (1014 - Authentication is denied)	
	 					} else if (result.status == 1014) {
	 						showDivElement('statusId');
	 						changeStatus('statusOtpId','deniedId');
							
	 						setTimeout(
									function() {
										window.location="login.jsp";
								    },3000);
	 					// the user is blocked (1027 - user is blocked)
	 					}else if (result.status == 1027) {
	 						showDivElement('statusId');
	 						changeStatus('statusOtpId','blockedId');
							
	 						setTimeout(
									function() {
										window.location="login.jsp";
								    },3000);
	 					}
					}
				});
			});
}

/*
 * Utility method
 */
function checkEmpty(elementId) {
	var inputs = $('#'+elementId);

    // filter over the empty inputs
    return inputs.filter(function() {
        return !$.trim(this.value) && $(this).is(":visible");
    }).length === 0;
}

/*
 * Utility method
 */
function changeSubmitAvailability() {
	var submit = $("#signOnButtonId");
	submit.prop("disabled", !checkEmpty('userId'));
	
	var submitOtp = $("#offlineButtonId");
	submitOtp.prop("disabled", !checkEmpty('otpId'));
}

/*
 * Utility method
 */
function setTextAcordingToDeviceType(){
	var message = "Enter the passcode displayed in the mobile app to sign on."
	var buttonTxt = "RETRY PUSH"
	
	document.getElementById('retryButtonId').value = buttonTxt;
	document.getElementById('otpMessage').innerHTML = message;
}

/*
 * QR CODE Java Script Functions ---- Start
 */
 
// Once the QR Code is displayed,
// Moderno browser starts polling the server in order to get the current status.
// DEFAULT_AUTHENTICATION_TOKEN_POLLING_DELAY represnts the delay period between each polling call
var DEFAULT_AUTHENTICATION_TOKEN_POLLING_DELAY = 3000; // 3 seconds

// If the user scans the QR code with a device which is paired to more than one user and it is configured that
// the WEB selects the user, the browser starts polling the server in order to see if the QR code is still valid 
// (not expired, canceled etc...). The polling delay at this stage is extended
var USER_SELECTION_TOKEN_POLLING_DELAY = 10000; // 10 seconds

// The QR Code image content is the generated authentication token.
// If application scheme is defined (in the admin web-portal),
// the QR Code content is in the format: <<<application scheme>>>://pingidsdk?authentication_token=<<< generated token >>>>
// If the application scheme is defined, the QR code is the just the generated token.
// authenticationTokenId variable is the generated authentication token ID (this is not the token itself)
var authenticationTokenId = null;

var tokenSchemeUri;



var serviceUuid='0000fff0-0000-1000-8000-00805f9b34fb'
var characteristicUuid='0000fff1-0000-1000-8000-00805f9b34fb'
var valuecharacteristicUuid = '44ab01ee-dc55-4f03-8b7c-ce011779e6da'

    //, 'battery_service'
    let optionalServices = [parseInt('0x' + serviceUuid)]

    function getSupportedProperties(characteristic) {
        let supportedProperties = [];
        for (const p in characteristic.properties) {
            if (characteristic.properties[p] === true) {
            supportedProperties.push(p.toUpperCase());
            }
        }
        return '[' + supportedProperties.join(', ') + ']';
    }

    var bleDevice;
    var gattServer;
    var gattService;
    var serviceCharacteristic;
    var serviceValueCharacteristic;

    function discovery() {
        console.log("Wow!");
        //id: "xJ2V+YPvLTncV8rccU/Kgw=="
        //name: "Galaxy S9"
        navigator.bluetooth.requestDevice({
            acceptAllDevices: true,
            //filters: [
                //{ services: ['battery_service'] },
            //    {name: 'Galaxy S9'}
            //]
            optionalServices: optionalServices
        })
        .then(device => {
            console.log("device");
            console.log(device);
            // Human-readable name of the device.
            
            bleDevice=device;

            console.log(device.name);

            device.addEventListener('gattserverdisconnected', onDisconnected);

            // Attempts to connect to remote GATT Server.
            return device.gatt.connect();
        })
        .then(server => {
            console.log("Getting Services...");
            console.log(server);

            gattServer = server;

            return server.getPrimaryServices();
        })
        .then(services => { 
            console.log('Getting Characteristics...');
            let queue = Promise.resolve();
            services.forEach(service => {
                queue = queue.then(_ => service.getCharacteristics().then(characteristics => {
                    console.log('> Service: ' + service.uuid);
                    characteristics.forEach(characteristic => {
                        console.log('>> Characteristic: ' + characteristic.uuid + ' '
                        //  + getSupportedProperties(characteristic)
                         );
                    });
                }));
            });
            return queue;
        })
        .catch(error => {
            console.log("error");
            console.log(error);
        });
    }

    function logServiceCharacteristics() {
        console.log("Try to connect to device BLE (server)!");
        navigator.bluetooth.requestDevice({
            acceptAllDevices: true,
            optionalServices: optionalServices
        })
        .then(device => {
            console.log("device");
            console.log(device);
            // Human-readable name of the device.
            
            bleDevice=device;

            console.log(device.name);

            device.addEventListener('gattserverdisconnected', onDisconnected);

            // Attempts to connect to remote GATT Server.
            return device.gatt.connect();
        })
        .then(server => {
            console.log("server");
            console.log(server);

            gattServer = server;

            console.log('serviceUuid=' + serviceUuid);

            return server.getPrimaryService(serviceUuid);
        })
        .then(service => { 
            console.log("service");
            console.log(service);
            gattService=service;
            //(characteristicUuid)
            return service.getCharacteristics();
        })
        .then(characteristics => {
            console.log("characteristics");
            console.log(characteristics);
            characteristics.forEach(characteristic => {
                console.log('>> Characteristic: ' + characteristic.uuid + ' ');
            });

            // return characteristic.readValue();
        })
        .catch(error => {
            console.log("error");
            console.log(error);
        });
    }
    
    function handleNotifications(event) {
    	  let value = event.target.value;
    	  let a = [];
    	  // Convert raw data bytes to hex values just for the sake of showing something.
    	  // In the "real" world, you'd use data.getUint8, data.getUint16 or even
    	  // TextDecoder to process raw data bytes.
    	  for (let i = 0; i < value.byteLength; i++) {
    	    a.push('0x' + ('00' + value.getUint8(i).toString(16)).slice(-2));
    	  }
    	  console.log('> ' + a.join(' '));
    	}

    function hackthon() {
        console.log("Try to connect to device BLE (server)!");
        navigator.bluetooth.requestDevice({
        	  filters: [
            { services: [serviceUuid] }
            ],
            optionalServices: optionalServices
        })
        .then(device => {
            console.log("device");
            console.log(device);
            // Human-readable name of the device.
            
            bleDevice=device;

            console.log(device.name);

            device.addEventListener('gattserverdisconnected', onDisconnected);

            // Attempts to connect to remote GATT Server.
            return device.gatt.connect();
        })
        .then(server => {
            console.log("server");
            console.log(server);

            gattServer = server;

            console.log('serviceUuid=' + serviceUuid);

            return server.getPrimaryService(serviceUuid);
        })
        .then(service => { 
            console.log("service");
            console.log(service);
            gattService=service;
            return service.getCharacteristic(valuecharacteristicUuid);
        }).then(valuecharacteristic => {
            console.log("characteristic");
            console.log(valuecharacteristic);
            console.log('Characteristic: ' + valuecharacteristic.uuid);
            serviceValueCharacteristic = valuecharacteristic;
            return valuecharacteristic.startNotifications().then(_ => {
            	console.log('> Notifications started');
                valuecharacteristic.addEventListener('characteristicvaluechanged',
                    handleNotifications);
              });
        }).then(xyz => {
        	return gattService.getCharacteristic(characteristicUuid);
        })	
        .then(characteristic => {
            console.log("characteristic");
            console.log(characteristic);
            console.log('Characteristic: ' + characteristic.uuid);
            writeValueToServiceCharacteristic(characteristic);   
        })
        .catch(error => {
            console.log("error");
            console.log(error);
        });
    }

    function connectToBleServer() {
        console.log("Try to connect to device BLE (server)!");

        if (!('bluetooth' in navigator)) {
            alert('Bluetooth API not supported');
            console.log("Bluetooth API not supported");
            return;
        }

        navigator.bluetooth.requestDevice({
            acceptAllDevices: true,
            optionalServices: optionalServices
        })
        .then(device => {
            console.log("device");
            console.log(device);
            // Human-readable name of the device.
            
            bleDevice=device;

            console.log(device.name);

            bleDevice.addEventListener('gattserverdisconnected', onDisconnected);

            // Attempts to connect to remote GATT Server.
            return device.gatt.connect();
        })
        .then(server => {
            console.log("server");
            console.log(server);

            gattServer = server;

            console.log('serviceUuid=' + serviceUuid);

            return server.getPrimaryService(serviceUuid);
        })
        .then(service => { 
            console.log("service");
            console.log(service);
            gattService=service;
        })
        .catch(error => {
            console.log("error");
            console.log(error);
        });
    }

    function getCharacteristicFromService() {
        console.log("read Characteristic From Service");

        try {
            console.log(gattService);
            
            gattService.getCharacteristic(characteristicUuid).then(characteristic => {
                serviceCharacteristic = characteristic;

                console.log("characteristic");
                console.log(serviceCharacteristic);
                console.log('characteristic: ' + serviceCharacteristic.uuid);
            });
        } catch(error) {
            console.log("error");
            console.log(error);
        };
    }

    function writeValueToServiceCharacteristic(serviceCharacteristic) {
        console.log("write value to Service Characteristic");
        console.log(serviceCharacteristic);

        try {
            let trxId=tokenSchemeUri;

            let encoder = new TextEncoder('utf-8');
            let encodedValue = encoder.encode(trxId);
            // Do we need to use encoded value?

            console.log("Length of str" + trxId.length);
            
            writeLimitedStrToCharacteristic(serviceCharacteristic, trxId).then(v => {
                console.log("inside");
            });
        } catch(error) {
            console.log("error");
            console.log(error);
        };
    }

    function writeStrToCharacteristic(characteristic, str) {
        var maxChunk = 300;
        var j = 0;

        if (str.length > maxChunk) {
            for ( var i = 0; i < str.length; i += maxChunk ) {
                var subStr;
                if ( i + maxChunk <= str.length ) {
                    subStr = str.substring(i, i + maxChunk);
                } else {
                    subStr = str.substring(i, str.length);
                }

                setTimeout(writeStrToCharacteristic, 250 * j, subStr);
                j++;
            }
        } else {
            writeStrToCharacteristic(zpl);
        }
    }

    function writeLimitedStrToCharacteristic(characteristic, str) {
        let buffer = new ArrayBuffer(str.length);
        let dataView = new DataView(buffer);
        for (var i = 0; i <str.length; i++) {
            dataView.setUint8( i, str.charAt(i).charCodeAt() );
        }
        console.log('writing str the device');
        return characteristic.writeValue(buffer);
    }

    function onDisconnected() {
        console.log('Bluetooth Device disconnected');
    }








/**
 * Generates the QR Code image.
 * In this sample, the QR code is generated once the page is loaded
 * (see in HTML itself: <body onload="GenerateQRCode()">)
 */
function GenerateQRCode()
{
	// If the authentication token ID is defined, move on.
	// There is no need to re-create it
	if(authenticationTokenId !== null){
		return;
	}
	<%
	  // creates an authentication token.
	  // The QRCodeUtils class sends a request to PingID SDK Server.
	  // PingID SDK Server returns an authentication token.
	  AuthenticationTokenResponse authTokenResponse = QRCodeUtils.createAuthenticationToken();
	  // get the id (this id is required for polling the server to get the current token status)
	  String authenticationTokenId = authTokenResponse.getId();
	  // Get the token scheme uri.
	  // Note: if an application token uri is defined for the application in the admin web portal,
	  // the following will be returned:
	  // <<<application scheme>://pingidsdk?authentication_token=<<<authentication token>>>
	  // (For example: appSmaple://pingidsdk?authentication_token=1234)
	  // otherwise only the token is returned
	  String tokenSchemeUri = authTokenResponse.getTokenSchemeUri();
	%>
	
	// Store the authentication token ID
	authenticationTokenId = "<%=authenticationTokenId%>";
	tokenSchemeUri = "<%=tokenSchemeUri%>";
	// Start polling in order to get the authentication token status
	pollAuthenticationTokenStatus("<%=authenticationTokenId%>");
}

/**
 * Once the authentication token is created & a QR Code image is displayed (the QR code image is the token),
 * the browser starts polling to get the current authentication token status.
 * The token status (in this sample...) can be one of the following:
 *	NOT_CLAIMED, CLAIMED, NOT_EXIST, CANCELED, IN_PROGRESS, EXPIRED, DENIED, PENDING_WEB_USER_SELECTION; 
 */
function pollAuthenticationTokenStatus(authenticationTokenId)
{
	// see: GetAuthenticationTokenStatusHandler class. This class handles the polling request
	var data = JSON.stringify({
		operation : "get_authentication_token_status",
		authenticationTokenId:authenticationTokenId
	});
	
	timer=setTimeout(function() {
		jQuery.ajax({ url: "../pingidsdk",
			type: "POST",
			data: data,
			contentType: 'application/json',
			dataType: 'json',
			success: function(result) {
				// In case the token is still not used or in progress, continue the polling
			   if(result.tokenStatus === 'NOT_CLAIMED' || result.tokenStatus === 'IN_PROGRESS'){
				   pollAuthenticationTokenStatus(authenticationTokenId);
			   // If the token status is PENDING_WEB_USER_SELECTION,
			   // it means that the WEB should display a user list (since there are more than one user who is paired to the device that scanned the QR Code image)
			   }else if(result.tokenStatus === 'PENDING_WEB_USER_SELECTION'){
				   handleUserSelection(result.users);
			   }else {
				   // In all the cases, the polling can stop.
				   // the token can be in "CLAIMED" state, which means that the user successfully scanned the QR Code image.
				   // OR: the token can no longer be used (expired, canceled, etc)
				   finalizeAuthenticationTokenStatusPolling(result.tokenStatus); 
			   }
			},
	        error: function(jqXHR, textStatus, errorThrown) {
	        	 
	        }
	       });
	    }, DEFAULT_AUTHENTICATION_TOKEN_POLLING_DELAY);
}

/**
 * This function is called whenever the authentication token is in final state
 */
function finalizeAuthenticationTokenStatusPolling(tokenStatus)
{
	clearTimeout(timer);
	 // If the token is in "CLAIMED" state, it means that the user successfully scanned the QR Code image.
	if(tokenStatus === 'CLAIMED'){
		changeStatus('userSelection', 'statusId');
		changeStatus('loginId', 'authenticatedId');
		setTimeout(
			function() {
				window.location="Transactions.jsp";
		    },5000);
	} else if(tokenStatus === 'DENIED'){
		changeStatus('userSelection', 'statusId');
		changeStatus('loginId', 'deniedId');
		setTimeout(
			function() {	
				location.reload(true);
		    },5000);
	} else {
		// the authentication token does not exist/canceled/denied. The page is reloaded - which causes to re-generate a new token: <body onload="GenerateQRCode()">
		location.reload(true);
	}
}

/**
 * This function is called when there is more than one user who is paired to the device that scanned the QR code.
 * By default, the mobile application selects the user.
 * However, it can be configured in the admin portal that the WEB selects the user.
 */
function handleUserSelection(userList){
	// Display the user list in the browser
	buildUserSelectionList(userList);
	changeStatus('loginId', 'userSelection');
	
	// starts polling the server to see if the token is expired/canceled while the user list is displayed
	pollInUserSelectionState(authenticationTokenId)
}

/**
 * This function builds the user list using jquery
 */
function buildUserSelectionList(userList) {
	//  <div class="select-user__header">Select User</div>
	var formHeader$=$('<div/>');
	formHeader$.addClass("select-user__header");
	formHeader$.html("Select User");
	$("#userSelectionForm").append(formHeader$);
	
	var userSelectionList$ = $('<ul/>');
	userSelectionList$.attr("id", "userSelectList");
	userSelectionList$.addClass("select-user__list");
	for (var i = 0; i < userList.length; i++) {
		var username = userList[i]["username"];
		var firstName = userList[i]["firstName"];
		var lastName = userList[i]["lastName"];
		var row$ = $('<li/>');
		row$.attr("id", username);
		row$.attr("tabindex", "0");
		row$.on("click", userSelected);
		
		if(!firstName){
			firstName = "";
		}
		
		if(!lastName){
			lastName = "";
		}
		
		if(!firstName && !lastName){
			row$.html(username);
		}else if(!firstName){
			row$.html(lastName);
		}else{
			row$.html(firstName + " " + lastName);
		}
		
		userSelectionList$.append(row$);
	}
	$("#userSelectionForm").append(userSelectionList$);
	var footer$ = $('<div/>');
	footer$.addClass("select-user__footer");
	$("#userSelectionForm").append(footer$);
}

/**
 * This function is called when the end-user selects which user will be authenticated
 * using the authentication token
 */
function userSelected() {
	var username =  $(this).attr("id");
	
	// See: UpdateAuthenticationTokenUserNameHandler class which handles this request
	var data = JSON.stringify({
		operation : "update_authentication_token_username",
		authenticationTokenId:authenticationTokenId,
		user:username
	});
	
	// clear the poller timeout
	clearTimeout(timer);
	
	$('#userSelectList li').each(function(i)
	{
		$(this).prop("onclick", null).off("click");
	});
	
	jQuery.ajax({ url: "../pingidsdk",
		type: "POST",
		data: data,
		contentType: 'application/json',
		dataType: 'json',
		success: function(result) {
			// starts polling if the token is still in progress
		   if(result && (result.tokenStatus === 'NOT_CLAIMED' || result.tokenStatus === 'IN_PROGRESS')){
			   pollAuthenticationTokenStatus(authenticationTokenId);  
		   }else {
			   finalizeAuthenticationTokenStatusPolling(result.tokenStatus); 
		   }
		},
        error: function(jqXHR, textStatus, errorThrown) {
        	
        }
       });
}

/**
 * This function is called when the user selection list appears.
 * This function ensures that the token is still valid.
 * If not - the page is reloaded
 */
function pollInUserSelectionState(authenticationTokenId) {
	var data = JSON.stringify({
		operation : "get_authentication_token_status",
		authenticationTokenId:authenticationTokenId
	});

	timer=setTimeout(function() {
		jQuery.ajax({ url: "../pingidsdk",
			type: "POST",
			data: data,
			contentType: 'application/json',
			dataType: 'json',
			success: function(result) {
			   if(result.tokenStatus === 'PENDING_WEB_USER_SELECTION'){
				   pollInUserSelectionState(authenticationTokenId);  
			   }else{
				   if(result.tokenStatus === null || result.tokenStatus === 'EXPIRED' ||  result.tokenStatus === 'CANCELED' ||  result.tokenStatus === 'NOT_EXIST'){
					   location.reload(true);  
				   }
			   }
			},
	        error: function(jqXHR, textStatus, errorThrown) {
	        
	        }
	       });
	    }, USER_SELECTION_TOKEN_POLLING_DELAY);
}

/*
 * QR CODE Java Script Functions ---- End
 */

$(window).load(function() {
	setTimeout(changeSubmitAvailability, 200);
});
</script>

<body onload="GenerateQRCode()">

<%
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
				StringBuilder orgOfHostingServerBuilder = new StringBuilder();
				// The properties file must be located under /env/moderno-props/
				String fileName = APIUtils.getPropFileName("/env/moderno-props/");
				
				if(fileName == null){
					orgOfHostingServerBuilder.append("<b>Error: In order to start working with this sample, please download PingID SDK properties file and place it in /env/moderno-props/ folder</b>");
					out.println(orgOfHostingServerBuilder);
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
					orgOfHostingServerBuilder.append("<b>Error: account_id property is missing in the properties file</b>");
					out.println(orgOfHostingServerBuilder);
					return;
				}
				
				if(StringUtils.isBlank(appId)){
					orgOfHostingServerBuilder.append("<b>Error: app_id property is missing in the properties file</b>");
					out.println(orgOfHostingServerBuilder);
					return;
				}
				
				if(StringUtils.isBlank(token)){
					orgOfHostingServerBuilder.append("<b>Error: token property is missing in the properties file</b>");
					out.println(orgOfHostingServerBuilder);
					return;
				}
				
				if(StringUtils.isBlank(apiKey)){
					// use_base64_key is the old property name for api_key
					apiKey = prop.getProperty("use_base64_key");
					if(StringUtils.isBlank(apiKey)){
						orgOfHostingServerBuilder.append("<b>Error: use_base64_key property is missing in the properties file</b>");
						out.println(orgOfHostingServerBuilder);
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

			Util.eraseCookie(request, response);

		} catch (Throwable e) {
			out.println("ERROR!!! " + e.getMessage());
		}
%>

	<div class="container">
 		<div align="center" class="main-body">
 			
 			<div id="loginId" align="left" class="login-form">
 			 	<img class="loginLogo" src="..\assets\images\logo.png" id="topLogo" >
				<input type="button" id="signOnBlueToothButtonId" class="green-button sign-on-button" value="SIGN ON WITH BLUETOOTH" onclick="javascript: hackthon();">				
			</div>
			
			<!-- OTP Flow -Offline device -->
			<div id="statusOtpId" class="login-form hide-status">
			 	<img class="loginLogo" src="..\assets\images\logo.png" id="topLogo" >
				<div id="invalidOtpBanner" class="invalidInput hide-status">
					<label>Incorrect passcode</label>
				</div>
				<label for="otpId" class = "body-section-text" id="otpMessage"></label>
				<input type="text" class="editBox" id="otpId" name="otp" placeholder="Passcode" autofocus />
				<input type="button" id="offlineButtonId" class="green-button sign-on-button" value="SIGN ON" onclick="javascript: authenticateOffline();">
				<input type="button" id="retryButtonId" class="blue-button sign-on-button" value="RETRY PUSH" onclick="javascript: authenticateUser('retry');">
				
			</div>
			<!--  -->

			<div id="statusId" class="login-form hide-status">
				<img class="statusLogo" src="..\assets\images\logo.png">
				
				<div id="authenticatingId" class="status-container-mock status-authenticating-mock hide-status">
				</div>
				
				<div id="authenticatedId" class="status-container-mock status-authenticated-mock hide-status">
				</div>
	
				<div id="deniedId" class="status-container-mock status-denied-mock hide-status">
				</div>
				
				<div id="blockedId" class="status-container-mock status-blocked-mock hide-status">
				</div>
	
				<div id="timedoutId" class="status-container-mock status-timedout-mock hide-status">
				</div>
	
				<div id="errorId" class="status-container-mock status-error-mock hide-status">
				</div>
			</div>
			
			<!--  user selection section start -->
			<div id="userSelection" class="select-user hide-status">
				<div class="select-user__logo">
					<img class="loginLogo" src="..\assets\images\logo.png" id="topLogo" >
				</div>
				<div id="userSelectionForm" class="select-user__form">
				</div>
			</div>		
			<!--  user selection section end   -->
		</div>
	</div>
	<div class="footer">
		<label>© 2017 Moderno Financial</label>
		<label>v1.3</label>
	</div>
</body>
</html>