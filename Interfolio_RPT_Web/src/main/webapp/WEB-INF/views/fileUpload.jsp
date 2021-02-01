<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
<script src="//code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
<head>
<link rel="stylesheet" type="text/css" href="tabs.css">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Interfolio</title>
</head>
<body>
	<div align="center">
		<div class="tab-content" style="height: 100px">
			<div
				style="background-color: grey; width: 500px; height: 60px; padding-top: 22px;">
				<input class="ui-button" type="file" id="file" name="file" /> <input
					class="ui-button" type="button" class="button" value="Upload"
					id="but_upload">
			</div>
		</div>
	</div>

	<script>
		$(function() {
			var url = "http://localhost:8080/interfolio/uploadCSVFIle";
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
				e.preventDefault();
				var formData = {
					file_data : base_64
				};
				const jsonString = JSON.stringify(formData);
				console.log(jsonString)
				$.ajax({
					url : url,
					data : jsonString,
					contentType : "application/json; charset=utf-8",
					type : "POST",
					success : function(data) {
						if (data == "Success") {
							location.reload();
						}
						alert("Response is : " + data)
					},
					error : function(result) {
						alert("Something went wrong")
					}
				});
			});
		});
	</script>
</body>
</html>