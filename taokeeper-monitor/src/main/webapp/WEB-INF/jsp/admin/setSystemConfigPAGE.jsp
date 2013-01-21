<%@ page language="java" pageEncoding="UTF-8"%><%@ include
	file="/WEB-INF/common/taglibs.jsp"%>


<link rel="stylesheet" type="text/css" href="css/table.css" />


<h1>管理员-ZooKeeper 节点自检 线程数</h1>
<div align="center" class="mytable" id="tab">
<form name="setSystemConfigHandle"  id="setSystemConfigHandle"  action="admin.do?method=setSystemConfigHANDLE"  method="post">
	<table border="0" cellspacing="0" cellpadding="0">
		
		<tr style="background-color:#DDDDDE; text-align:center;">
			<td><b>ID</b></td>
			<td>${settingsId}<input type="hidden" name="settingsId" id="settingsId" value="${settingsId}" size="50"/></td>
		</tr>
		<tr style="background-color:#DDDDDE; text-align:center;">
			<td><b>环境名称</b></td>
			<td><input type="text" name="envName" id="envName" value="${envName}" size="50"/></td>
		</tr>
		<tr style="background-color:#DDDDDE; text-align:center;">
			<td><b>节点自检 线程数</b></td>
			<td><input type="text" name="maxThreadsOfZooKeeperCheck" id="maxThreadsOfZooKeeperCheck" value="${maxThreadsOfZooKeeperCheck}" size="50"/></td>
		</tr>
		<tr style="background-color:#DDDDDE; text-align:center;">
			<td><b>描述</b></td>
			<td><input type="text" name="description" id="description" value="${description}" size="50"/></td>
		</tr>
		<tr>
			<td>
				
			</td>
			<td align="right" ><input type="submit"  value="Update"  size="90"/> <font color="red">${handleMessage}</font> </td>
		</tr>			
	</table>
	
</form>
</div>

<script type="text/javascript" src="js/table.js">
</script>









