<head>
<jsp:directive.include
	file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>My Home Page</title>
</head>
<body>
	<div class="container-lg">
		<!-- Content here -->
		<%=request.getParameter("code")%>
		<div style="text-align: center">oauth test</div>
	</div>
</body>