<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>

<h1><a title="<fmt:message key="siteName"/>" href="<c:url value="/" />"><img src="<c:url value="/resources/logo-header.png" />" alt="<fmt:message key="siteName"/>" /></a></h1>
<div id="nav">
	<ul>
		<c:if test="${userVO == null}">
			<li><a href="<c:url value="/signin" />">Sign In</a></li>
			<li><a href="<c:url value="/signup" />">Register</a></li>		
		</c:if>
		<c:if test="${userVO != null}">
			<li><c:out value="${userVO.fname} ${userVO.lname}" /></li>
			<li><a href="<c:url value="/usr/new" />">Home</a></li>
			<s:url value="/sett/upd/details/{userId}" var="updDetailsUrl">
				<s:param name="userId" value="${userVO.id}" />
			</s:url>
			<li><a href="${updDetailsUrl}">Settings</a></li>
			<li><a href="<c:url value="/signout" />">Sign Out</a></li>
		</c:if>
	</ul>
</div>
