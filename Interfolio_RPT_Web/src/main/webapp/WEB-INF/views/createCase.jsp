<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
<script src="//code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
<script src="https://code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
<link rel="stylesheet" type="text/css"
	href="../resources/datatables.min.css">
<script src="../resources/datatables.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Interfolio</title>
<style type="text/css">
table {
	width: 50%;
	height: none;
}

table, th, td {
	border: 1px solid black;
	border-collapse: collapse;
}

th, td {
	padding: 5px;
	text-align: left;
	text-align: center;
}

#t01 tr:nth-child(even) {
	background-color: #eee;
}

#t01 tr:nth-child(odd) {
	background-color: #fff;
}

#t01 th {
	background-color: #00274C;
	color: white;
}
</style>
	<script type="text/javascript" language="javascript">
	var base_url = "";
	var sub_url = "/interfolio/createCase";
		$(document).ready(function() {
					$('#t01').DataTable();
					$.getJSON("https://api.ipify.org/?format=json", function(e) {
						base_url = e.ip;
					}); 
					var url_create_case = base_url + sub_url;
					$("#btn").click(function (e) {
						e.preventDefault();
						$.ajax({
							type : "GET",
							contentType : "application/json;charset=utf-8",
							url : url_create_case,
							success : function(data) {
							}
						});
					});
			});
	</script>
</head>
<body>
<div>
	<div align="center">
		<table class="display" id="t01">
			<thead>
				<tr>
					<th>Template ID</th>
					<th>Candidate Name</th>
					<th>Status</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach var="createCase" items="${templates}">
					<tr>
						<td>${createCase.template_id}</td>
						<td>${createCase.cand_name}</td>
						<td>${createCase.status}</td>
					</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
	<div align="center" style="padding-top: 20px;">
		<input class="ui-button" type="button" class="button"
			value="Create Case" id="btn" />
	</div>
	</div>
</body>
</html>