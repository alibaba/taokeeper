<%@ page language="java" pageEncoding="GBK"%><%@ include
	file="/WEB-INF/common/taglibs.jsp"%>
<div id="navcolumn" style="height: 500px;">
	<ul>

		<li>Monitor
			<ul>
				<li><a
					href="<c:url value="/zooKeeper.do?method=zooKeeperSettingsPAGE" />">集群配置</a></li>
				<li><a
					href="<c:url value="/zooKeeperStatus.do?method=showZooKeeperStatusPAGE" />">集群监控</a></li>
				<li><a
					href="<c:url value="/hostPerformance.do?method=showHostPerformancePAGE" />">机器监控</a></li>
				<li><a
					href="<c:url value="/alarmSettings.do?method=alarmSettingsPAGE" />">报警设置</a></li>
			</ul>
		</li>

		<br>
		<li>Admin
			<ul>
				<li><a
					href="<c:url value="admin.do?method=switchOfNeedAlarmPAGE" />">报警开关</a></li>
				<li><a
					href="<c:url value="admin.do?method=setSystemConfigPAGE" />">系统设置</a></li>
			</ul>
		</li>

		<br>
		<!-- 
	<li>Reports
		<ul>
			<li><a href="">日报</a></li>
			<li><a href="">周报</a></li>
			<li><a href="">趋势</a></li>
		</ul>
	</li>
-->




	</ul>
</div>
