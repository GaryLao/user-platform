<%@ page import="javax.validation.ConstraintViolation" %>
<%@ page import="java.util.Set" %>
<head>
<jsp:directive.include
	file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>My Home Page</title>
</head>
<body>
	<div class="container-lg" style="font-size: 16px;">
		<%
			StringBuilder sb = new StringBuilder();
			if (request.getAttribute("REGISTER_FAIL_REASON") != null) {
				Set<ConstraintViolation> violations = (Set<ConstraintViolation>) request.getAttribute("REGISTER_FAIL_REASON");
				for (ConstraintViolation violation : violations) {
					sb.append(violation.getMessage()).append("<br/>");
				}
			}
		%>
		<!-- Content here -->
		<br>
		<div style="text-align: center">注册失败，<%=request.getAttribute("REGISTER_FAIL_MESSAGE") %></div>
		<c:set var="csb" scope="session" value="<%=sb.length()%>"/>
		<c:if test="${csb*1 > 0}">
		<br>
		<div style="text-align: center"><%=sb.toString() %></div>
		</c:if>

		<a href="/user/register" type="button" class="btn btn-lg btn-primary btn-block">返回</a>
	</div>
</body>