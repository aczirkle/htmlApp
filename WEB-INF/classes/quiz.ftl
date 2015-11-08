<html>
<body>
<div>
Hello World
</div>
<div>
<table>
<tr><td>Time</td><td>${TIME}</td></tr>
<tr><td>Number of visits</td><td>${VISITS}</td></tr>
</table>
</div>
<div>
<#list data as d>
<li>${d.name}</li>
</#list>
</div>
</body>
</html>
