<html>
<head>
<title>AdminPage</title>
<meta charset="UTF-8">
<style>
#editForm {
margin-top: 10px;
margin-bottom: 10px;
}

#startForm {
margin: 10px;
border: thin solid black;
width: 500px;
}

</style>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script type="text/javascript">
var key = "freekey";
var storepk=-1;
var url  = "http://zirkleac.383.csi.miamioh.edu:8080/htmlApp";

function makeList() {
		 $.getJSON(url + "/rest/"+key+"/storylist",function(result){
			 for (var i=0,len=result.StoryList.length;i<len;i++) {
				 $("#stories").append("<option value=\"" + i + "\">" + result.StoryList[i].title +"</option");
			 }
		 });
}


$(document).ready(function(){
	$("#deleteForm").css("visibility","hidden");
	$("#create").css("visibility","hidden");
	$("#editForm").css("visibility","hidden");
	$("#startForm").css("visibility","hidden");
	makeList();
	
	$("#deleteButton").click(function(){
		  $("#startForm").css("visibility","visible");
		  $("#deleteForm").css("visibility","visible");
		  $("#create").css("visibility","hidden");
		  $("#editForm").css("visibility","hidden");
	});	
	$("#createButton").click(function(){
		  $("#startForm").css("visibility","hidden");
		  $("#create").css("visibility","visible");
		  $("#deleteForm").css("visibility","hidden");
		  $("#editForm").css("visibility","hidden");
	});
	$("#editButton").click(function(){
		$("#startForm").css("visibility","visible");
		  $("#startEdit").css("visibility","visible");

	});
	
	
	$("#startEdit").submit(function(evt){
		  $("#startForm").css("visibility","visible");
		  $("#editForm").css("visibility","visible");
		  $("#deleteForm").css("visibility","hidden");
		  $("#create").css("visibility","hidden");
	});

/*	$("#pickstory").submit(function(evt){
		evt.preventDefault();
		pk=$("#stories").val();
		if (pk>0) {
			storepk=pk;
			  $.getJSON(url+"/rest/"+key+"/story/" + pk ,function(result){
			  console.log(result.title);
			  $("#editForm").css("visibility","visible");
			  $("#title").val(result.title);
			  $("page").val(result.page);

				  });
		}
	});*/
	$("#editname").submit(function(evt){
		evt.preventDefault();
		var pk=$("#titleEdit").val();
		var elements = document.getElementById("stories").elements;
		var newTitle;
		for (var i = 0, element; element = elements[i++];) {
			if(element.value ===pk)
				newTitle=element.textContent;
        }
		
		
		var page = $("#pageEdit").text();
		  console.log(newTitle);
		$.ajax({
				type:"POST",
				url:url+"/rest/"+key+"/edit",
				data: "{\"title\":\"" + newTitle + "\",\"page\":\"" + page + "\"}",
				dataType: "json",
				success:function(result) {
					$("#msg").html("Title updated");
					  $("#editForm").css("visibility","hidden");
					  $("#titleEdit").text("");
					  $("#pageEdit").text("");
				},error:function(xhr,result,error){
					var json = xhr.responseJSON;
					$("#msg").html("<h1>" + json.error + "</h1>");
				}
				});
		  });

		  
	$("#create").submit(function(evt){
		evt.preventDefault();
		var newTitle=$("#titleCreate").val();
		  console.log(newTitle);
		page=$("#pageCreate").val();
		$.ajax({
				type:"POST",
				url:"http://zirkleac.383.csi.miamioh.edu:8080/htmlApp/rest/"+key+"/create",
				data: "{\"page\":\"" + page + "\",\"title\":\"" + newTitle + "\"}",
				dataType: "json",
				success:function(result) {
					$("#msg").html("Title updated");
					  $("#createForm").css("visibility","hidden");
					  $("#titleCreate").val("");
					  $("#pageCreate").val("");
					  $("#stories").empty();
						  makeList();
				},error:function(xhr,result,error){
					var json = xhr.responseJSON;
					$("#msg").html("<h1>" + json.error + "</h1>");
				}
				});
		  });

	$("#deleteStory").submit(function(evt){
		evt.preventDefault();
		var title=$("#stories").text();
		
		if(confirm('Are you sure you want to delete '+title+'')) {
			  $.ajax({
				type:"POST",
				url:url+"/rest/"+key+"/delete",
				data: "{\"title\":\"" + title + "\"}",
				dataType: "json",
				success:function(result) {
					$("#msg").html("Deleted");
					$("#stories").empty();
					  makeList();
				},error:function(xhr,result,error){
					var json = xhr.responseJSON;
					$("#msg").html("<h1>" + json.error + "</h1>");
			}
			});

		}
	});
});



</script>
</head>
<body>
Admin<br>

<div id="createButton">Create</div><br>
<div id="editButton">Edit</div><br>
<div id="deleteButton">Delete</div><br>

<div id="startForm">
Story: <select id='stories'></select>
</div>

<div id='editForm'>
<form id='startEdit'>
<input type='submit'>
</form>
<form id='editname'>
Name: <input type='text' id='titleEdit'>
<br>
Page: <textarea type='text' id='pageEdit' rows="1" cols="1" style="width: 200px;height: 200px;word-wrap: break-word;"></textarea>
<br>
<input type='submit'>
</form>
</div>

<div id='create'>
<form id='create'>
Name: <input type='text' id='titleCreate'>
<br>
Page: <textarea type='text' id='pageCreate' rows="1" cols="1" style="width: 200px;height: 200px;word-wrap: break-word;"></textarea>
<br>
<input type='submit'>
</form>
</div>

<div id="deleteForm">
Choose a story to delete
<form id='deleteStory'>
<input type='submit'>
</form>
</div>

<div id="msg"></div>
</body>
</html>