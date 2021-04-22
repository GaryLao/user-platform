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
<%--		Hello,World 2021--%>
		<div style="text-align: center">通过ThreadLocal获取到的配置数据 application.name=<%=configThreadLocal.get().getConfigValue("application.name").getValue() %></div>
		<br>
		<a href="/user/register" type="button" class="btn btn-lg btn-primary btn-block">注册</a>

		<div style="text-align: center;margin-top: 20px;">
			<a href="https://github.com/login/oauth/authorize?client_id=0e30270bcec0b4e2b6a7&redirect_uri=http://localhost:8080/oauth/redirect&state=javademo">
				github登录：
				<svg t="1587352912571" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
					 p-id="2196" width="32" height="32">
					<path d="M512 42.666667A464.64 464.64 0 0 0 42.666667 502.186667 460.373333 460.373333 0 0 0 363.52 938.666667c23.466667 4.266667 32-9.813333 32-22.186667v-78.08c-130.56 27.733333-158.293333-61.44-158.293333-61.44a122.026667 122.026667 0 0 0-52.053334-67.413333c-42.666667-28.16 3.413333-27.733333 3.413334-27.733334a98.56 98.56 0 0 1 71.68 47.36 101.12 101.12 0 0 0 136.533333 37.973334 99.413333 99.413333 0 0 1 29.866667-61.44c-104.106667-11.52-213.333333-50.773333-213.333334-226.986667a177.066667 177.066667 0 0 1 47.36-124.16 161.28 161.28 0 0 1 4.693334-121.173333s39.68-12.373333 128 46.933333a455.68 455.68 0 0 1 234.666666 0c89.6-59.306667 128-46.933333 128-46.933333a161.28 161.28 0 0 1 4.693334 121.173333A177.066667 177.066667 0 0 1 810.666667 477.866667c0 176.64-110.08 215.466667-213.333334 226.986666a106.666667 106.666667 0 0 1 32 85.333334v125.866666c0 14.933333 8.533333 26.88 32 22.186667A460.8 460.8 0 0 0 981.333333 502.186667 464.64 464.64 0 0 0 512 42.666667"
						  fill="#2c2c2c" p-id="2197"></path>
				</svg>
			</a>
		</div>
	</div>
</body>