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

#msg {
	text-align: center;
	color: red;
	font-size: 30px;
	font-style: italic;
}
</style>
<script type="text/javascript" language="javascript">
	$(document).ready(function() {
		$('#table_validate_data').DataTable();
	});
</script>
</head>
<body>
	<div align="center">
		<table class="display" id="table_validate_data">
			<thead>
				<tr>
					<th>Template ID</th>
					<th>Candidate Name</th>
					<th>Status</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="soq" items="${soqData}">
					<tr>
						<td>${soq.template_id}</td>
						<td>${soq.cand_name}</td>
						<td>${soq.status}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div align="center" style="padding-top: 30px;">
		<input class="ui-button" type="button" class="button"
			value="Start Data Pre-process" id="btn" />
	</div>
</body>
</html>