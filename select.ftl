<html>
<body>
<div align="center">
Select your story
</div>

<div align="center">
Welcome ${user}<br>

</div>
<div align="center">
<form action="read">
<ul>
  <#list stories as story>
    <li><input type="radio" name=${story}>${story}</li>
    <br> 
  </#list>
</ul>

<br><br>
<input type="submit" value="Submit">
</form>
</div>


<form action="create">
Click to access creating a story
<input type="submit" value="Submit">
</form>
</body>
</html>
