<%@page import="com.pingidentity.moderno.utils.Util"%>
<%@page import="com.pingidentity.moderno.api.APIUtils"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.util.Properties"%>
<%@page import="java.io.FileInputStream"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<link rel="stylesheet" type="text/css" href="../login.css" media="screen" />
<link rel="shortcut icon" href="..\assets\images\partialLogo.png"/>
<title>Moderno Financial</title>
</head>
<script>

function authenticate() {
	var data = JSON.stringify({
		operation : "auth_user"
	});
	
	$(document).ready(
		function() {
			$.ajax({
				type : "POST",
				url : "../pingidsdk",
				dataType: "json",
				contentType: 'application/json',
				data : data,
				success : function(result) {
				}
			});
		}
	);
}


$(document).ready(function() {	
	authenticate();
});


</script>

<body>
	<div class="footer">
		<label>© 2017 Moderno Financial</label>
		<label>v1.3</label>
	</div>
</body>
</html>