<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>

<div id="settNav">
	<s:url value="/sett/upd/details/{userId}" var="updDetailsUrl">
		<s:param name="userId" value="${userVO.id}" />
	</s:url>
	
	<s:url value="/sett/upd/passwd/{userId}" var="updPasswdUrl">
		<s:param name="userId" value="${userVO.id}" />
	</s:url>

	<ul>
		<li><a href="${updDetailsUrl}">Update Details</a></li>
		<li><a href="${updPasswdUrl}">Update Password</a></li>
	</ul>

</div>
<div class="clear"></div>
