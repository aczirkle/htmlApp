<html>
<body>
<div>
Select your story
</div>
<div>
<form action="read">
<ul>
  <#list stories as story>
    <li><input type="radio" name=${story}>${story}</li>
    <br> 
  </#list>
</ul>

Username:<br>
<input type="text" name="user">
<br>
Email:<br>
<input type="text" name="email">

<br><br>
<input type="submit" value="Submit">
</form>
</div>
</body>
</html>
