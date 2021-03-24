<%@ page import="org.eclipse.microprofile.config.Config" %>
<head>
<jsp:directive.include
	file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>My Home Page</title>
</head>
<body>
	<div class="container-lg">
		<%
			ThreadLocal<Config> configThreadLocal = (ThreadLocal<Config>) request.getAttribute("CONFIG");
		%>
		<!-- Content here -->
		Hello,World 2021
		<div style="text-align: center">通过ThreadLocal获取到的配置数据 application.name=<%=configThreadLocal.get().getConfigValue("application.name").getValue() %></div>
		<br>
		<a href="/user/register" type="button" class="btn btn-lg btn-primary btn-block">注册</a>
	</div>
</body>