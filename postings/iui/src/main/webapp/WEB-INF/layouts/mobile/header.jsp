<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="headerHorzItem">
	<div id="logo">
		<a id="logoLink" title="<fmt:message key="siteName"/>" href="<c:url value="/" />"><fmt:message key="siteName"/></a>
	</div>
</div>
<div class="headerHorzItem">
	<ul>
		<c:choose>
			<c:when test="${userVO == null}">
				<div id="signinNav">
					<li><a href="<c:url value="/signup" />">Register</a></li>
					<li><a href="<c:url value="/signin" />">Sign In</a></li>
				</div>		
			</c:when>
			<c:otherwise>
				<div id="homeNav">
					<li><a href="<c:url value="/signout" />">Sign Out</a></li>
					<s:url value="/sett/upd/details/{userId}" var="updDetailsUrl">
						<s:param name="userId" value="${userVO.id}" />
					</s:url>
					<li><a href="${updDetailsUrl}">Settings</a></li>
					<li><a href="<c:url value="/usr/new" />">Home</a></li>
				</div>
			</c:otherwise>
		</c:choose>
	</ul>
</div>
