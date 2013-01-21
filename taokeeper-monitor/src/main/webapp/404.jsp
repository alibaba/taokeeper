<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="GBK"%><%@ include
	file="/WEB-INF/common/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="Expires" content="-1" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="content-type" content="text/html; charset=GBK" />
<title><decorator:title default="OpsSecurity" /></title>
<link rel="stylesheet" href="<c:url value="/css/default.css" />"
	type="text/css" />
<link rel="stylesheet" href="<c:url value="/css/displaytag.css" />"
	type="text/css" />
<link rel="stylesheet"
	href="<c:url value="/css/smoothness/jquery-ui-1.8.5.custom.css" />"
	type="text/css" />
<script type="text/javascript"
	src="<c:url value="/js/jquery-1.4.2.min.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/js/jquery-ui-1.8.5.custom.min.js" />"></script>
</head>
<body class="composite">
<div id="banner"><a href="index.jsp" id="bannerLeft"> <img
	src="img/ops-security.png" alt="" /> </a>
<div class="clear">
<hr />
</div>
</div>
<div id="breadcrumbs">
<div class="xright"><a href="<c:url value="/login.do" />">登录</a> |
<a href="<c:url value="/logout.do" />">退出</a></div>
<div class="clear">
<hr />
</div>
</div>
<div id="leftColumn"><jsp:include
	page="/WEB-INF/common/header.jsp" flush="true" /></div>
<div id="rightColumn" style="padding: 20px 0 0 20px;">您请求的页面不存在！</div>
<jsp:include page="/WEB-INF/common/footer.jsp" flush="true" />
</body>
</html>
