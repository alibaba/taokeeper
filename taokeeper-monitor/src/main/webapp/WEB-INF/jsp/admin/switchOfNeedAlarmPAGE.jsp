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



<h1>管理员-ZooKeeper 监控报警开关</h1>
<div align="center" class="mytable" id="tab">
<form name="updateSwitchOfAlarm"  id="updateSwitchOfAlarm"  action="admin.do?method=updateSwitchOfNeedAlarmHandle"  method="post">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr style="background-color:#DDDDDE; text-align:center;">
			<td><b>监控报警开关</b></td>
			<td></td>
		</tr>
		<tr>
			<td>
				<c:choose>
					<c:when test="${ needAlarm eq 'true' }"><input type="radio" name="needAlarm" value = "true" checked="checked">开启 <input type="radio" name="needAlarm" value = "false"  >关闭</c:when>
					<c:when test="${ needAlarm eq 'false' }"><input type="radio" name="needAlarm" value = "true" >开启 <input type="radio" name="needAlarm" value = "false"  checked="checked">关闭</c:when>
				</c:choose>	
			</td>
			<td align="right" ><input type="submit"  value="Update"  size="90"/> <font color="red">${handleMessage}</font> </td>
		</tr>			
	</table>
	
</form>
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
var sHTML = content+
      '<p><button onclick="window.neatDialog.close()">关闭</button></p>';
    new NeatDialog(sHTML, "<b>节点详情</b>", false);
  
}
</SCRIPT>









