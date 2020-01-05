<%@page import="java.util.Locale"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.apache.commons.lang3.tuple.Pair"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.pingidentity.moderno.utils.SessionData"%>
<%@page import="com.pingidentity.moderno.utils.SessionUtils"%>
<%@page import="com.pingidentity.moderno.utils.Util"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
  <link rel="stylesheet" type="text/css" href="../transactions.css" media="screen" />
  <link rel="shortcut icon" href="..\assets\images\partialLogo.png"/>
  <title>Moderno Financial</title>
</head>

<!-- 

*********  Transactions page ********* 

This page demonstrates how to use PingID SDK for transaction approval.

In this sample, money is transferred between account.
The "transactions page" is rendered:

1. Once the user has successfully authenticated with PingID SDK (Moderno sample handles the authentication request)
OR
2. Once the user has successfully authenticated with PingID SDK Adapter (PingFederate handles the authentication request)

If Moderno handled the authentication request, this sample demonstrates how to handle authentication request with a dedicated client context.
The client context can be any string that is sent from the customer server (Moderno, for example) to the Mobile app.
The mobile app should be able to handle the client context (Only the customer logic is able to deal with the client context).
In addition to the client context, there are several other parameters which can be sent in the authentication request:
push message title, push message body, QR code client context, QR code push message title, QR code push message body.

All of the above can be used in order to pass the context to the mobile app.

Observe: TransferHandler.class to see how the context is passed.

If PingFederate handled the authentication request, the dynamic data is sent to PingFederate.
Observe: PingFederateRequestGenerator.class
-->

<%
Pair<String, Float> subAccountChecking = null;
Pair<String, Float> subAccountSavings = null;
SessionData sessionData = null;
try {
	String sessionId = Util.getCookieValue(request, SessionUtils.SESSION_NAME);
	if (StringUtils.isBlank(sessionId)) {
		response.sendRedirect("login.jsp");
		return;
	}

	sessionData = SessionUtils.getSessionData(sessionId);
	if (sessionData == null) {
		response.sendRedirect("login.jsp");
		return;
	}
	
	subAccountChecking = sessionData.getSubAccountChecking();
	subAccountSavings = sessionData.getSubAccountSavings();
	
} catch (Throwable e) {
	out.println("ERROR!!! " + e.getMessage());
}
%>
<script type="text/javascript">


function selectMenuItem(selectedMenuItem, selectedSectionBody) {
	activateItem('top-menu-items', selectedMenuItem, "top-menu-items-selected");
	activateItem('body-section', selectedSectionBody, "show-section");
	
	createCookie('currentMenuItem', selectedMenuItem, 1);
	createCookie('currentTab', selectedSectionBody, 1);
}

function activateItem(groupClassName, selectedMenuItem, addedDeletedClass) {
	var items = document.getElementsByClassName(groupClassName);
	for (i = 0; i < items.length; i++) {
		menuItem = items[i];
		menuItem.classList.remove(addedDeletedClass);
	    if (menuItem.id === selectedMenuItem) {
	    	menuItem.classList.add(addedDeletedClass);
	    }
	}
}

function signOut() {
	 window.location.href = "login.jsp";
}

function showOverlay() {
	var divElement = document.getElementById('overlay');
	divElement.classList.remove('hide-overlay');
}

function hideOverlay() {
	var divElement = document.getElementById("overlay");
	divElement.classList.add('hide-overlay');
}

function showDivElement(divId) {
	var divElement = document.getElementById(divId);
	divElement.classList.remove('hide-overlay');
}

function hideDivElement(divId) {
	var divElement = document.getElementById(divId);
	divElement.classList.add('hide-overlay');
}

function transfer(operation) {
	var pingFederateSession = <%=sessionData.isPingFederateSession() %>
	var user = "<%=sessionData.getUserName()%>";
	var sum = document.getElementById("transferedSumId").value;
	if(pingFederateSession){
		transferSumWithPingFed(user, sum)
	}else{
		transferSum(operation, user, sum);
	}
	
}

/**
 * Transer the money with PingFederate
 */
function transferSumWithPingFed(user, sum){
	// Redirects the user to PingFederate authorization endpoint
	window.location.replace("../pingfederate/transaction?sum=" + sum +"&user=" + user );
}

function changeStatus(oldStatusId, newStatusId) {
	hideDivElement(oldStatusId);
	showDivElement(newStatusId);
}

function transferSum(operation, user, sum) {
	
	var otp = document.getElementById("otpId").value;
	var data = JSON.stringify({
		operation : operation,
		user : user,
		sum: sum,
		otp: otp
	});
	
	showOverlay();
	
	if (otp == ''){
		showDivElement('authenticatingId');
	}
	
	$.ajax({
		type : "POST",
		url : "../pingidsdk",
		dataType: "json",
		contentType: 'application/json',
		data : data,
		success : function(result) {
			hideDivElement('authenticatingId');

			if(result.status == 0 || result.status == 1026){
				hideDivElement("statusOtpId");
				showDivElement('authenticatedId');

				setTimeout(
						function() {
							window.location="Transactions.jsp";
			            },5000);
			} else if (result.status == 1014) {
				showDivElement('deniedId');

				setTimeout(
					function() {
						hideDivElement('deniedId');
						hideOverlay();
				}, 5000);
			} /* case: OTP required */
			else if (result.status == 1015) {
				changeStatus('authenticatingId', 'statusOtpId');
				setTextAcordingToDeviceType();
				$('#otpId').focus();
				setTimeout(changeSubmitAvailability, 200);
			} else if (result.status == 1016) {
				showDivElement('timedoutId');

				setTimeout(
					function() {
						hideDivElement('timedoutId');
						hideOverlay();
				}, 5000);
			} else if (result.status == 1025) {
				hideDivElement('otpHeader');
				showDivElement('invalidOtpBanner');
				$('#otpId').css('color','red');
				$('#otpId').click(function() {
					hideDivElement('invalidOtpBanner');
					showDivElement('otpHeader');
					$('#otpId').css('color','#999999');
					$('#otpId').val('');
					setTextAcordingToDeviceType();
					setTimeout(changeSubmitAvailability, 200);
			    })
			}else if(result.status == 1027){ 
				hideDivElement("statusOtpId");
				showDivElement('blockedId');
				$('#otpId').val('');
				setTimeout(
						function() {
							hideDivElement('blockedId');
							hideOverlay();
					}, 5000);				
			}else {
				showDivElement('errorId');
				
				setTimeout(
						function() {
							hideDivElement('errorId');
							hideOverlay();
					}, 5000);				
			}
		}
	});
}

function createCookie(name,value,days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days*24*60*60*1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + value + expires + "; path=/";
}

function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}

function eraseCookie(name) {
    createCookie(name,"",-1);
}

function selectCurrentTab() {
	var currentTab = readCookie("currentTab");
	var currentMenuItem = readCookie("currentMenuItem");
	
	if (currentTab && currentMenuItem) {
		selectMenuItem(currentMenuItem, currentTab);
	}
}

function checkEmpty(elementId) {
	var inputs = $('#'+elementId);

    // filter over the empty inputs
    return inputs.filter(function() {
        return !$.trim(this.value) && $(this).is(":visible");
    }).length === 0;
}


function changeSubmitAvailability() {
	var submit = $("#signOnButtonId");
	submit.prop("disabled", !checkEmpty('userId'));
	
	var submitOtp = $("#offlineButtonId");
	submitOtp.prop("disabled", !checkEmpty('otpId'));
}

function setTextAcordingToDeviceType(){
	var message = "Your device couldn't be reached. Use the mobile app to get a one-time passcode and enter it here to authenticate.";
	var buttonTxt = "RETRY PUSH";

	/* document.getElementById('retryButtonId').value = buttonTxt; */
	document.getElementById('otpMessage').innerHTML = message;
}

$(window).load(function() {

	document.getElementById("transferedSumId").addEventListener("keyup", function(event) {
	    event.preventDefault();

	    if (event.keyCode == 13) {
	        document.getElementById("transferButtonId").click();
	    }
	});
	
	document.getElementById("otpId").addEventListener("keyup", function(event) {
	    event.preventDefault();

	    if (event.keyCode == 13) {
	        document.getElementById("offlineButtonId").click();
	    }
	});
	
	var inputs = $('input[type=text]');
    inputs.on('blur input', changeSubmitAvailability);
    
    var moneyTransfered = <%=sessionData.getTransferSum() != null%>;

	function func() {
		selectCurrentTab();
		if(moneyTransfered){
			showOverlay();
			var transactionApproved = <%=sessionData.isTransactionApproved()%>;
			if(transactionApproved){
				showDivElement('authenticatedId');
			}else{
				showDivElement('deniedId');
			}
			
			setTimeout(
					function() {
						if(transactionApproved){
							hideDivElement('authenticatedId');
						}else{
							hideDivElement('deniedId');
						}
						
						hideOverlay();
			}, 5000);		
		}
		changeSubmitAvailability();
	}
	
	setTimeout(func, 200);
});
		
</script>
<body>
	<div class="container">
		<div align="center" class="header">
			<div class="header-row">
				<div class="header-cell-left">
					<img alt="" src="../assets/images/logo.png">
				</div>
					
				<div align="center" class="header-cell-center">
					<a id="topMenuItemAccounts" href="javascript: selectMenuItem('topMenuItemAccounts', 'accountSectionId')" class="top-menu-items top-menu-items-selected">Accounts</a>
					
					<a id="topMenuItemTransfers" href="javascript: selectMenuItem('topMenuItemTransfers', 'transferSectionId')" class="top-menu-items">Transfers</a>
						
					<a id="topMenuItemBillPay" href="" class="top-menu-items top-menu-items-not-active">Bill Pay</a>
						
					<a id="topMenuItemSettings" href="" class="top-menu-items top-menu-items-not-active">Settings</a>
				</div>
					
				<div class="header-cell-right">
					<button class="signout-button"  onclick="javascript: signOut();" >SIGN OUT</button>
				</div>
			</div>
		</div>
		<div class="title-border">
		</div>

		<div class="main-body">
			<div id="accountSectionId" class="body-section show-section">
				<div class="body-section-title">Account Balance</div>
				<div class="account-balance"><%= new DecimalFormat("#,###.00").format(subAccountChecking.getValue()) %></div>
			</div>
		
			<div id="transferSectionId" class="body-section">
				<div class="body-section-title">Transfer Between Accounts</div>
				<div id="subAccountChecking" class="section-row">
					<div id="subAccountCheckingId" class="sub-account"><%= subAccountChecking.getKey() %></div>
					<div class="sub-account">Checking &ndash; </div>
					<div id="subAccountCheckingSum" class="sub-account"><%= new DecimalFormat("#,###.00").format(subAccountChecking.getValue()) %></div>
				</div>
				<div id="subAccountSavings" class="section-row">
					<div id="subAccountSavingsId" class="sub-account"><%= subAccountSavings.getKey() %></div>
					<div class="sub-account">Savings &ndash; </div>
					<div id="subAccountSavingsSum" class="sub-account"><%= new DecimalFormat("#,###.00").format(subAccountSavings.getValue()) %></div>
				</div>
				
				<div class="amount-container">
					<div class="body-section-title">Amount</div>
					<input id="transferedSumId" type="text" class="editBox transfer-sum" value="1,000">
					<button id="transferButtonId" class="green-button transfer-button" onclick="javascript: transfer('transfer');" >TRANSFER</button>
				</div>
			</div>
		</div>

	</div>
	<div id="overlay" align="center" class="overlay hide-overlay">
		<div id="authenticatingId" class="status-container-mock status-authenticating-mock hide-overlay">
			<!-- <img class="device-icon" src="../assets/images/icon-app.gif">
			<img class="spinner" src="..\assets\images\spinner.jpg">
			<div class="auth-text">Authenticating</div> -->
		</div>
		
		<div id="authenticatedId" class="status-container-mock status-authenticated-mock hide-overlay">
			</div>
		
		<!-- OTP Flow -Offline device -->
		<div id="statusOtpId" class="status-container-mock otp-container hide-overlay">	
			<div id="invalidOtpBanner" class="invalidInput hide-overlay">
				<label>Incorrect passcode</label>
			</div>
			<div id="otpHeader" class="body-section-text">
				<label>Authentication</label>
			</div>		
			<label for="otpId" class = "body-section-text" id="otpMessage"></label>
			<input type="text" class="editBox" id="otpId" name="otp" placeholder="Passcode" autofocus />
			<input type="button" id="offlineButtonId" class="green-button sign-on-button" value="APPROVE" onclick="javascript: transfer('transfer');">
			<!-- <input type="button" id="retryButtonId" class="blue-button sign-on-button" value="RETRY PUSH" onclick="javascript: transfer('retry');"> -->
			
		</div>
		<!--  -->
		
		<div id="deniedId" class="status-container-mock status-denied-mock hide-overlay">
		</div>
		
		<div id="blockedId" class="status-container-mock status-blocked-mock hide-overlay">
		</div>
		
		<div id="timedoutId" class="status-container-mock status-timedout-mock hide-overlay">
		</div>
		
		<div id="errorId" class="status-container-mock status-error-mock hide-overlay">
		</div>
	</div>
	<div class="footer">
		<label>© 2017 Moderno Financial</label>
		<label>v1.3</label>
	</div>
</body>
</html>