<%@ page language="java" pageEncoding="UTF-8"%><%@ include
	file="/WEB-INF/common/taglibs.jsp"%>


<style>
.mytable {align:center;border-collapse:collapse;border:solid #6AA70B;border-width:0px 0 0 0px;width:600;} 
.mytable table tr {padding-top:5px;list-style:none; border-bottom:#6AA70B 1px dotted ;font-size: 12px;;height:20px;} 
.mytable table tr.t1 {background-color:#EEEEEE;}/* 第一行的背景色 */ 
.mytable table tr.t2{background-color:#;}/* 第二行的背景色 */ 
.mytable table tr.t3 {background-color:#CCCCCC;}/* 鼠标经过时的背景色 */ 
</style> 

<style type="text/css">
a:link { text-decoration: none}
a:active { text-decoration:none}
a:hover { text-decoration:none} 
a:visited { text-decoration:none}
</style>



<h1>ZooKeeper机器状态<font size="2">  更新时间：${timeOfUpdateHostPerformanceSet }</font></h1>

<select id="clusterSelector" onchange="javascript:location.href=this.value;" >
	<c:forEach var="zooKeeperCluster" items="${zooKeeperClusterMap}">
		<c:choose>
     			<c:when test="${ zooKeeperCluster.key eq clusterId }"><option value="hostPerformance.do?method=showHostPerformancePAGE&clusterId=${zooKeeperCluster.key}"  selected>${zooKeeperCluster.value.clusterName}</option></c:when>
				<c:otherwise><option value="hostPerformance.do?method=showHostPerformancePAGE&clusterId=${zooKeeperCluster.key}">${zooKeeperCluster.value.clusterName}</option></c:otherwise>
		</c:choose>
	</c:forEach>
</select>
<span>${description}</span><br/><br/>
<div align="center" class="mytable" id="tab">
<table border="0" cellspacing="0" cellpadding="0">

	<tr style="background-color:#DDDDDE; text-align:center;">
		<td><b>Node IP</b></td>
		<td><b>Cpu Usage</b></td>
		<td><b>Memory Usage</b></td>
		<td><b>Load</b></td>
		<td><b>Disk</b></td>
	</tr>

	<c:forEach var="hostPerformanceEntity" items="${hostPerformanceEntityMap}"> 
		<tr>
			<td>${ hostPerformanceEntity.key }</td>
			<td>${ hostPerformanceEntity.value.cpuUsage }</td>
			<td>${ hostPerformanceEntity.value.memoryUsage }</td>
			<td>${ hostPerformanceEntity.value.load }</td>
			<td>${ hostPerformanceEntity.value.diskUsageMap }</td>
		</tr>
	</c:forEach>
</table>
</div>


<script type="text/javascript"> 

var Ptr=document.getElementById("tab").getElementsByTagName("tr"); 
function $() { 
for (i=1;i<Ptr.length+1;i++) { 
Ptr[i-1].className = (i%2>0)?"t1":"t2"; 
} 
} 
window.onload=$; 
for(var i=0;i<Ptr.length;i++) { 
Ptr[i].onmouseover=function(){ 
this.tmpClass=this.className; 
this.className = "t3"; 
}; 
Ptr[i].onmouseout=function(){ 
this.className=this.tmpClass; 
}; 
} 

</script>

