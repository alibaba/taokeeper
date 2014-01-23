<%@ page language="java" pageEncoding="UTF-8"%><%@ include
	file="/WEB-INF/common/taglibs.jsp"%>


<style>
.mytable {align:center;border-collapse:collapse;border:solid #6AA70B;border-width:0px 0 0 0px;width:600;} 
.mytable table tr {padding-top:5px;list-style:none; border-bottom:#6AA70B 1px dotted ;font-size: 12px;;height:20px;} 
.mytable table tr.t1 {background-color:#EEEEEE;}/* 第一行的背景色 */ 
.mytable table tr.t2{background-color:#;}/* 第二行的背景色 */ 
.mytable table tr.t3 {background-color:#CCCCCC;}/* 鼠标经过时的背景色 */ 
</style> 
<link rel="stylesheet" type="text/css" href="css/popDiv.css" />
<style type="text/css">
a:link { text-decoration: none}
a:active { text-decoration:none}
a:hover { text-decoration:none} 
a:visited { text-decoration:none}
</style>
<script src="http://code.highcharts.com/highcharts.js"></script>
<script src="http://code.highcharts.com/modules/exporting.js"></script>




<h1>ZooKeeper集群状态<font size="2">  更新时间：${timeOfUpdateZooKeeperStatusSet }</font><font size="2">  <a href="zooKeeper.do?method=zooKeeperRegisterPAGE">加入监控</a></font></h1>

<select id="clusterSelector" onchange="javascript:location.href=this.value;" >
	<c:forEach var="zooKeeperCluster" items="${zooKeeperClusterMap}">
		<c:choose>
     			<c:when test="${ zooKeeperCluster.key eq clusterId }"><option value="${zooKeeperCluster.key}"  selected>${zooKeeperCluster.value.clusterName}</option></c:when>
				<c:otherwise><option value="zooKeeperStatus.do?method=showZooKeeperStatusPAGE&clusterId=${zooKeeperCluster.key}">${zooKeeperCluster.value.clusterName}</option></c:otherwise>
		</c:choose>
	</c:forEach>
</select>
<span>${description}</span><br/><br/>
<div align="left" class="mytable" id="tab">
<table border="0" cellspacing="0" cellpadding="0">

	<tr style="background-color:#DDDDDE;">
		<td><b>Node IP</b></td>
		<td><b>Role</b></td>
		<td><b>连接数</b></td>
		<td><b>Watch数</b></td>
		<td><b>Watched /Total Path</b></td>
		<td><b>数据量 Sent/Received</b></td>
		<td><b>状态</b></td>
		<td><b>节点自检状态</b></td>
		<td><b>查看趋势</b></td>
	</tr>

	<c:forEach var="zooKeeperStatus" items="${zooKeeperStatusMap}"> 
		<tr>
			<td>${ zooKeeperStatus.key }</td>
			<td>${ zooKeeperStatus.value.mode }</td>
			<td>${fn:length( zooKeeperStatus.value.connections )}<img style="cursor:pointer;" src="img/seeDetail.png"  onclick="openDialog('<pre>${zooKeeperStatus.value.connectionsContent}</pre>')" /></td>
			<td>${ zooKeeperStatus.value.watches }<img style="cursor:pointer;" src="img/seeDetail.png"  onclick="openDialog('<pre>${zooKeeperStatus.value.watchedPathMapContent}</pre>')" /></td>
			<td>${ zooKeeperStatus.value.watchedPaths }/${ zooKeeperStatus.value.nodeCount }</td>
			<td>${ zooKeeperStatus.value.sent }/${ zooKeeperStatus.value.received }</td>
			<td>&nbsp;<img style="cursor:pointer;" src="img/seeDetail.png"  onclick="openDialog('${zooKeeperStatus.value.statContent}')" /></td>
			
			<c:choose>
	     			<c:when test="${ zooKeeperStatus.value.statusType eq 1 }"><td style="background-color: green;" >OK</td></c:when>
	     			<c:when test="${ zooKeeperStatus.value.statusType eq 0 }"><td style="background-color: yellow;" >Checking</td></c:when>
	     			<c:when test="${ zooKeeperStatus.value.statusType eq -1 }"><td style="background-color: #D1F3D6;" >Initializing</td></c:when>
					<c:otherwise><td style="background-color: red;" >ERROR</td></c:otherwise>
			</c:choose>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;<a href="report.do?method=reportPAGE&clusterId=${clusterId}&server=${ zooKeeperStatus.key }"><img style="cursor:pointer;" src="img/seeDetail.png" /></a></td>		
		</tr>
	</c:forEach>
</table>


<br/><br/>
<b>提示</b>:<br><br>
 1. 节点自检 是指对集群中每个IP所在ZK节点上的PATH:  <b>/YINSHI.MONITOR.ALIVE.CHECK</b> 定期进行三次如下流程 : <br/>
<b>节点连接</b> - <b>数据发布</b> - <b>修改通知</b> - <b>获取数据</b> - <b>数据对比</b>, 三次流程均成功视为该节点处于正常状态。<br><br>
 2. 角色分类：<b>L</b>: Leader, <b>F</b>: Follower, <b>O</b>: Observer, <b>S</b>: Standalone
 
 </div>
 <br>
 <br>

<!--
<h1>ZooKeeper实时RT监控</h1>
<table border="0" cellspacing="0" cellpadding="0" style="width: 50px;">

    <tr>
        <td>cluster:${ clusterName }</td>
        <c:forEach var="each" items="${clusterRTStats}">
            <td>${ each.key }</td>
            <td style="background-color:#DDDDDE;">${ each.value } </td>
        </c:forEach>
    </tr>


        <c:forEach var="clusterRTStats" items="${clusterRTStatsMap}">
    <tr>
                <td>server:${ clusterRTStats.key }</td>
                <c:forEach var="each" items="${clusterRTStats.value}">
                    <td>${ each.key }</td>
                    <td style="background-color:#DDDDDE;">${ each.value } </td>
                </c:forEach>
    </tr>
        </c:forEach>
</table>
-->

 <br>
  <h1>ZooKeeper实时读写TPS</h1>
  <table border="0" cellspacing="0" cellpadding="0" style="width: 50px;">
      <tr>
      <c:forEach var="zooKeeperStatus" items="${zooKeeperStatusMap}">
               <td>${ zooKeeperStatus.key }</td>
               <td style="background-color:#DDDDDE;">${ zooKeeperStatus.value.rwps }</td>
        </c:forEach>
      </tr>
  </table>
	
   <div id="container2" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
 
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




<SCRIPT type=text/javascript>
function NeatDialog(sHTML, sTitle, bCancel)
{
  window.neatDialog = null;
  this.elt = null;
  if (document.createElement  &&  document.getElementById)
  {
    var dg = document.createElement("div");
    dg.className = "neat-dialog";
    if (sTitle)
      sHTML = '<div class="neat-dialog-title">'+sTitle+
              ((bCancel)?
                '<img src="x.gif" alt="Cancel" class="nd-cancel" />':'')+
                '</div>\n' + sHTML;
    dg.innerHTML = sHTML;
    var dbg = document.createElement("div");
    dbg.id = "nd-bdg";
    dbg.className = "neat-dialog-bg";
    var dgc = document.createElement("div");
    dgc.className = "neat-dialog-cont";
    dgc.appendChild(dbg);
    dgc.appendChild(dg);
    if (document.body.offsetLeft > 0)
    dgc.style.marginLeft = document.body.offsetLeft + "px";
    document.body.appendChild(dgc);
    if (bCancel) document.getElementById("nd-cancel").onclick = function()
    {
      window.neatDialog.close();
    };
    this.elt = dgc;
    window.neatDialog = this;
  }
}
NeatDialog.prototype.close = function()
{
  if (this.elt)
  {
    this.elt.style.display = "none";
    this.elt.parentNode.removeChild(this.elt);
  }
  window.neatDialog = null;
}

function openDialog( content )
  {
var sHTML = '<p><button onclick="window.neatDialog.close()">关闭</button></p>' + content + '<p><button onclick="window.neatDialog.close()">关闭</button></p>';
    new NeatDialog(sHTML, "<b>详情</b>", false);
  
}
</SCRIPT>


<script>

window.onload = function(){

	
	// Radialize the colors
	Highcharts.getOptions().colors = Highcharts.map(Highcharts.getOptions().colors, function(color) {
	    return {
	        radialGradient: { cx: 0.5, cy: 0.3, r: 0.7 },
	        stops: [
	            [0, color],
	            [1, Highcharts.Color(color).brighten(-0.3).get('rgb')] // darken
	        ]
	    };
	});
	
	// Build the chart
    jQuery('#container').highcharts({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: 'Browser market shares at a specific website, 2010'
        },
        tooltip: {
    	    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    color: '#000000',
                    connectorColor: '#000000',
                    formatter: function() {
                        return '<b>'+ this.point.name +'</b>: '+ Math.round(this.percentage) +' %';
                    }
                }
            }
        },
        series: [{
            type: 'pie',
            name: 'Browser share',
            data: [
                ['Firefox',   145.0],
                ['IE',       26.8],
                {
                    name: 'Chrome',
                    y: 12.8,
                    sliced: true,
                    selected: true
                },
                ['Safari',    48.5],
                ['Opera',     6.2],
                ['Others',   140.7]
            ]
        }]
    });
	
}




</script>







