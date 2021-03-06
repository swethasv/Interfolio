<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
<script src="//code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<head>
<link rel="stylesheet" type="text/css" href="tabs.css">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Interfolio</title>
<style type="text/css">
#msg {
	text-align: center;
	color: #194d19;
	font-size: 25px;
	font-style: italic;
}
</style>
</head>
<body>
	<div align="center" style="padding-top: 90px;">
		<div class="tab-content">
			<div
				style="background-color: grey; width: 500px; height: 60px; padding-top: 22px;">
				<form action="" id = "my_form">
				<input class="ui-button" type="file" id="file" name="file" /> <input
					class="ui-button" type="button" class="button" value="Upload"
					id="but_upload">
				</form>
			</div>
			<p id="msg"></p>
		</div>
	</div>
	<script>
		$(function() {
			var base_url = "";
			var sub_url = "/interfolio/uploadCSVFile";
			$.getJSON("https://api.ipify.org/?format=json", function(e) {
				base_url = e.ip;
			}); 
			var url = base_url + sub_url;
			var base_64 = "";
			File.prototype.convertToBase64 = function(callback) {
				var reader = new FileReader();
				reader.onloadend = function(e) {
					callback(e.target.result, e.target.error);
				};
				reader.readAsDataURL(this);
			};
			$("#file").on('change', function() {
				var selectedFile = this.files[0];
				selectedFile.convertToBase64(function(base64) {
					base_64 = base64.split(",")[1];
				})
			});

			$("#but_upload").click(function(e) {
				if (confirm('Are you sure, you want to upload file?')) {
				$('#but_upload').attr('disabled', true);
				e.preventDefault();
				var formData = {
					file_data : base_64
				};
				const jsonString = JSON.stringify(formData);
				$.ajax({
					url : url,
					data : jsonString,
					contentType : "application/json; charset=utf-8",
					type : "POST",
					success : function(data) {
						$('#but_upload').attr('disabled', false);
						document.getElementById('msg').innerHTML = "Input data created successfully!";
						document.getElementById("my_form").reset(); 
					},
					error : function(result) {
						alert("Something went wrong")
					}
				});
				}
			});
		});
	</script>
</body>
</html>