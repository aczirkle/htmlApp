<html>
<body>
<div>
Select your story
</div>

<div align="center">
User: "+user+"<br>"
Email: "+email+"<br>"
</div>
<div>
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
</body>
</html>
