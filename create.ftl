<html>
<head>
<title>Create</title>
<style>
#editForm {
margin-top: 10px;
margin-bottom: 10px;
}

#startForm {
margin: 10px;
border: thin solid black;
width: 200px;
}

</style>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script type="text/javascript">
var key = "freekey";
var storepk=-1;

$(document).ready(function(){
	//$("#create").css("visibility","hidden");
	//$("#pickstory").submit(function(evt){
	//	evt.preventDefault();
		//pk=$("#pk").val();
		//if (pk>0) {
		//	storepk=pk;
			 // $.getJSON("/htmlApp/rest/create/" + pk ,function(result){
			 // console.log(result.title);
		//	  $("#create").css("visibility","visible");
			 // $("#title").val(result.title);

				//  });
		//}
	//});
	$("#create").submit(function(evt){
		evt.preventDefault();
		var newTitle=$("#title").val();
		  console.log(newTitle);
		page=$("#page").val();
		$.ajax({
				type:"POST",
				url:"http://zirkleac.383.csi.miamioh.edu:8080/htmlApp/htmlApp/rest/"+key+"/create",
				data: "{\"page\":\"" + page + "\",\"title\":\"" + newTitle + "\"}",
				dataType: "json",
				success:function(result) {
					$("#msg").html("Title updated");
					  $("#editForm").css("visibility","hidden");
					  $("#pk").val("");
				},error:function(xhr,result,error){
					var json = xhr.responseJSON;
					$("#msg").html("<h1>" + json.error + "</h1>");
				}
				});
		  });

});

</script>
</head>
<body>
Create Story
<br>
<div id='create'>
<form id='create'>
Name: <input type='text' id='title'>
<br>
Page: <input type='text' id='page' style="width: 200px;height: 200px;">
<br>
<input type='submit'>
</form>
</div>
<div id="msg"></div>
</body>
</html>
