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
var pageNum=0;
var numPages=-1;
var editRes= "";
var newPage="";
var ogTitle="";
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
	$("#startEdit").css("visibility","hidden");
	$("#editUpPage").css("visibility","hidden");
	$("#editDownPage").css("visibility","hidden");
	makeList();
	
	$("#deleteButton").click(function(){
		  $("#startForm").css("visibility","visible");
		  $("#deleteForm").css("visibility","visible");
		  $("#create").css("visibility","hidden");
		  $("#editForm").css("visibility","hidden");
		  $("#editUpPage").css("visibility","hidden");
		  $("#editDownPage").css("visibility","hidden");
		  $("#startEdit").css("visibility","hidden");
	});	
	$("#createButton").click(function(){
		  $("#startForm").css("visibility","hidden");
		  $("#create").css("visibility","visible");
		  $("#deleteForm").css("visibility","hidden");
		  $("#editForm").css("visibility","hidden");
		  $("#editUpPage").css("visibility","hidden");
		  $("#editDownPage").css("visibility","hidden");
	});
	$("#editButton").click(function(){
		$("#startForm").css("visibility","visible");
		$("#startEdit").css("visibility","visible");
		$("#deleteForm").css("visibility","hidden");
		$("#create").css("visibility","hidden");
		

	});
	
	
	$("#startEdit").click(function(){
		var title=$("#stories option:selected").text();
		pageNum=0;
		ogTitle=$("#stories option:selected").text();
		//need to insert edit data
		$.getJSON(url+"/rest/"+key+"/story/" + title ,function(result){
			editRes=result;
			  console.log(result.title);
			  $("#titleEdit").val(result.title);
			  numPages=result.numPages;
				$("#pageEdit").append(result.pages[pageNum].page );
				$("#editUpPage").css("visibility","visible");
			  
		  $("#startForm").css("visibility","visible");
		  $("#editForm").css("visibility","visible");
		 
		});
	});
	
	
	$("#editUpPage").click(function(){
		try{
		$("#editDownPage").css("visibility","visible");
		if(pageNum >numPages){
			throw new Excecption("Ran out of pages")
		}
		pageNum++;
		$("#pageEdit").val(editRes.pages[pageNum].page );
		}
		catch(e){
			$("#pageEdit").val(newPage);
			$("#editUpPage").css("visibility","hidden");
		//$("#editUpPage").css("visibility","hidden");
		}

			
	});
	$("#editDownPage").click(function(){
		
		$("#editUpPage").css("visibility","visible");
		if(pageNum>=numPages){
			newPage = $("#pageEdit").val();
		}
		pageNum--;
		$("#editForm").css("visibility","hidden");
		$("#pageEdit").val('');
		$("#pageEdit").val(editRes.pages[pageNum].page );
		
		$("#editForm").css("visibility","visible");
		try{
		if(editRes.pages[pageNum-1].page === "undefined")
			$("#editDownPage").css("visibility","hidden");

		}
		catch(e){$("#editDownPage").css("visibility","hidden");}
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
		var newTitle=$("#titleEdit").val();
		var page = $("#pageEdit").val();
		  console.log(newTitle);
		  console.log(page);
		  console.log("{\"title\":\"" + newTitle + "\",\"page\":\"" + page + "\"\,\"pageNum\":\"" + pageNum + "\"\,\"oldTitle\":\"" + ogTitle + "\"}");
		  if(page==="" ||newTitle==="")
			return;
		$.ajax({
				type:"POST",
				url:url+"/rest/"+key+"/edit",
				data: "{\"title\":\"" + newTitle + "\",\"page\":\"" + page + "\"\,\"pageNum\":\"" + pageNum + "\"\,\"oldTitle\":\"" + ogTitle + "\"}",
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
		var title=$("#stories option:selected").text();
		
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


<div id='startEdit'>Submit</div>

<div id='editForm'>
<form id='editname'>
Name: <input type='text' id='titleEdit'>
<br>
Page: <br><textarea type='text' id='pageEdit' rows="1" cols="1" style="width: 200px;height: 200px;word-wrap: break-word;"></textarea>
<br>
Please Submit any changes per page. Any changes without hitting submit will not be saved.<br>
<input type='submit'>
</form>
</div>
<div id='editDownPage'>Page Down</div>
<div id='editUpPage'>Page Up</div>


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