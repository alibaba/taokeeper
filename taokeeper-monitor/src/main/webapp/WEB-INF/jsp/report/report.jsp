<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/common/taglibs.jsp"%>

<script src="js/report/amcharts.js" type="text/javascript"></script>
<script src="js/report/raphael.js" type="text/javascript"></script>
<script type="text/javascript" src="js/calendarDateInput.js"></script>


<h1>ZooKeepr集群状态趋势图 <font size="2">机器IP:${server},统计日期：${statDate}  <a href="zooKeeperStatus.do?method=showZooKeeperStatusPAGE&clusterId=${ clusterId }">返回监控</a></font></h1>
<div style="width:50px;">
<form>
	<script>DateInput('orderdate', true, 'YYYY-MM-DD')</script>
	<input id="server" name="server" type="hidden" value="${ server }" />
	<input id="clusterId" name="clusterId" type="hidden" value="${ clusterId }" />
	<input type="button" onClick="return showStat(this.form.orderdate.value);" value="Show">
</form>
</div>

<script type="text/javascript">
<!--
	function showStat( statDate ){
		window.location = "report.do?method=reportPAGE&clusterId="+ document.getElementById('clusterId').value +"&server="+ document.getElementById('server').value +"&statDate=" + statDate;
	}
//-->
</script>


<input id="contentOfReport" name="contentOfReport" type="hidden" value="${ contentOfReport }" />


<script type="text/javascript">

            var chartDataStr = document.getElementById("contentOfReport").value;
            var chartData = eval('(' + chartDataStr + ')');

            AmCharts.ready(function () {
                // generate some random data first

                // SERIAL CHART    
                chart = new AmCharts.AmSerialChart();
                chart.pathToImages = "../amcharts/images/";
                chart.zoomOutButton = {
                    backgroundColor: '#000000',
                    backgroundAlpha: 0.15
                };
                chart.dataProvider = chartData;
                chart.categoryField = "date";

                // listen for "dataUpdated" event (fired when chart is inited) and call zoomChart method when it happens
                chart.addListener("dataUpdated", zoomChart);

                // AXES
                // category                
                var categoryAxis = chart.categoryAxis;
                //categoryAxis.parseDates = true; // as our data is date-based, we set parseDates to true
                //categoryAxis.minPeriod = "DDDDDD"; // our data is daily, so we set minPeriod to DD
                categoryAxis.dashLength = 2;
                categoryAxis.gridAlpha = 0.15;
                categoryAxis.axisColor = "#DADADA";

                // first value axis (on the left)
                var valueAxis1 = new AmCharts.ValueAxis();
                valueAxis1.axisColor = "#FF6600";
                valueAxis1.axisThickness = 2;
                valueAxis1.gridAlpha = 0;
                chart.addValueAxis(valueAxis1);

                // second value axis (on the right) 
                var valueAxis2 = new AmCharts.ValueAxis();
                valueAxis2.position = "right"; // this line makes the axis to appear on the right
                valueAxis2.axisColor = "#FCD202";
                valueAxis2.gridAlpha = 0;
                valueAxis2.axisThickness = 2;
                chart.addValueAxis(valueAxis2);

                // third value axis (on the left, detached)
                valueAxis3 = new AmCharts.ValueAxis();
                valueAxis3.offset = 50; // this line makes the axis to appear detached from plot area
                valueAxis3.gridAlpha = 0;
                valueAxis3.axisColor = "#B0DE09";
                valueAxis3.axisThickness = 2;
                chart.addValueAxis(valueAxis3);

                // GRAPHS
                // first graph
                var graph1 = new AmCharts.AmGraph();
                graph1.valueAxis = valueAxis1; // we have to indicate which value axis should be used
                graph1.title = "订阅者数量";
                graph1.valueField = "watchers";
                graph1.bullet = "round";
                graph1.hideBulletsCount = 30;
                chart.addGraph(graph1);

                // second graph                
                var graph2 = new AmCharts.AmGraph();
                graph2.valueAxis = valueAxis2; // we have to indicate which value axis should be used
                graph2.title = "客户端连接数";
                graph2.valueField = "conns";
                graph2.bullet = "square";
                graph2.hideBulletsCount = 30;
                chart.addGraph(graph2);

                // third graph
                var graph3 = new AmCharts.AmGraph();
                graph3.valueAxis = valueAxis3; // we have to indicate which value axis should be used
                graph3.valueField = "znodes";
                graph3.title = "ZNode数量";
                graph3.bullet = "triangleUp";
                graph3.hideBulletsCount = 30;
                chart.addGraph(graph3);

                // CURSOR
                var chartCursor = new AmCharts.ChartCursor();
                chartCursor.cursorPosition = "mouse";
                chart.addChartCursor(chartCursor);

                // SCROLLBAR
                var chartScrollbar = new AmCharts.ChartScrollbar();
                chart.addChartScrollbar(chartScrollbar);

                // LEGEND
                var legend = new AmCharts.AmLegend();
                legend.marginLeft = 110;
                chart.addLegend(legend);

                // WRITE
                chart.write("chartdiv");
            });

            // this method is called when chart is first inited as we listen for "dataUpdated" event
            function zoomChart() {
                // different zoom methods can be used - zoomToIndexes, zoomToDates, zoomToCategoryValues
                chart.zoomToIndexes(10, 20);
            }
        </script>

<div id="chartdiv" style="width: 1000px; height: 500px;"></div>
