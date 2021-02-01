<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
<script src="//code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
<head>
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
</head>
<body>
	<div class="tab-content" align="center">
		<table class="c" id="t01">
			<tr style="height: 10px;">&nbsp;
			</tr>
			<tr>
				<th>Template ID</th>
				<th>Candidate Name</th>
				<th>Status</th>
			</tr>
			<tr>
				<td>29943</td>
				<td>Finley,Kelsey A</td>
				<td>Creating..</td>
			</tr>
			<tr>
				<td>29943</td>
				<td>Lee,Julie H</td>
				<td>Failed</td>
			</tr>
			<tr>
				<td>29943</td>
				<td>Parga,Deanna Janells</td>
				<td>Created</td>
			</tr>
		</table>
	</div>
	<br>
	<br>
	<br>
	<div align="center">
		<input class="ui-button" type="button" class="button"
			value="CREATE CASE" id="but_upload">
	</div>
	<script type="text/javascript" language="javascript">
    $(document).ready(function () {
        var res = [];
        $.ajax({
            type: "GET",
            contentType: "application/json;charset=utf-8",
            url: "http://localhost:8080/interfolio/getTemplate",
            success: function (data) {
            	var user="";
            	$(data).find("Template").each(function(){
            	$(this).find("cand_name").each(function(){
                    var name = $(this).text();
                    console.log(name);
                    user=user+" Name: "+name;
            	});
                });
                res.push(data);
                array(res);
            }
        });
        function array(arr) {
            console.log(arr);
            
        }
    });
</script>
</body>
</html>