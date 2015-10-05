<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<div id="sett">
	<c:if test="${not empty message}">
		<div class="${message.type.cssClass}">${message.text}</div>
	</c:if>
	
	<s:url value="/sett/upd/passwd/{userId}" var="updPasswdUrl">
		<s:param name="userId" value="${userVO.id}" />
	</s:url>
	
	
	<form:form id="settForm" action="${updPasswdUrl}" method="post" modelAttribute="updPasswdForm" enctype="multipart/form-data">
		<div class="formInfo">
	  		<h2>Update password</h2>
	  		<s:bind path="*">
	  		<c:choose>
	  		<c:when test="${status.error}">
	  			<div class="error">Unable to update your password. Please fix the errors below and resubmit.</div>
	  		</c:when>
	  		</c:choose>
	  		</s:bind>
		</div>
		
		<form:label path="password">Password (at least 5 characters) <form:errors path="password" cssClass="error" /></form:label>
		<form:password path="password" />
		
		<form:label path="confirmPassword">Confirm Password <form:errors path="confirmPassword" cssClass="error" /></form:label>
		<form:password path="confirmPassword" />
	
		<p><button type="submit">Update</button></p>
		
	</form:form>
</div>
