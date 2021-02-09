<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="../resources/tabs.css">
<link rel="stylesheet" type="text/css" href="../resources/jquery-ui.css">
<script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
<script src="//code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
<script src="https://code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Interfolio</title>
<script type="text/javascript">
   $(function () {
     $( "#tabs-1" ).tabs();
   });
</script>
<style>
#tabs-1 {
	font-size: 14px;
}

.ui-widget-header {
	background: #00274C;
	border: none;
	color: white;
	font-weight: bold;
	border-bottom: solid 2px #E17000;
}
</style>

</head>
<body style="width: auto;margin: 0px;">
	<div class="header">
		<h1></h1>
	</div>
	<div id="tabs-1">
		<ul>
			<li style="margin:0px;"><a href="../resources/fileUpload.jsp">Upload File</a></li>
			<li><a href="/getTemplate">Create Case</a></li>
			<li><a href="/getPreProcessData">Data Pre-processing</a></li>
			<li><a href="/geSOQStatData">SOQ Statistical Reports</a></li>
			<li><a href="/geSOQCommentData">SOQ Comments Reports</a></li>
			<li><a href="/geSOQGradedClassData">Graded Class List Reports</a></li>
		</ul>
	</div>
</body>
</html>