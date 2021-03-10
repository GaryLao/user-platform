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
					sb.append(violation.getPropertyPath() + "字段值:" + violation.getInvalidValue() + "不合法,原因 " + violation.getMessage()).append("<br/>");
				}
			}
		%>
		<!-- Content here -->
		<br>
		<div style="text-align: center">注册失败，<%=request.getAttribute("REGISTER_FAIL_MESSAGE") %></div>
		<br>
		<div style="text-align: center">原因：<%=sb.toString() %></div>

		<a href="/register" type="button" class="btn btn-lg btn-primary btn-block">返回</a>
	</div>
</body>