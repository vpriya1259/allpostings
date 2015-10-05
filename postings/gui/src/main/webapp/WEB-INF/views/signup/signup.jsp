<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<div class="sign">
	<c:if test="${not empty message}">
		<div class="${message.type.cssClass}">${message.text}</div>
	</c:if>
	
	<c:url value="/signup" var="signupUrl" />
	<form:form id="signup" action="${signupUrl}" method="post" modelAttribute="signupForm">
		<div class="formInfo">
	  		<h2>Register</h2>
	  		<s:bind path="*">
	  		<c:choose>
	  		<c:when test="${status.error}">
	  			<div class="error">Unable to sign up. Please fix the errors below and resubmit.</div>
	  		</c:when>
	  		</c:choose>
	  		</s:bind>
		</div>
		<fieldset>
			<form:label path="email">Email<form:errors path="email" cssClass="error" /></form:label>
			<form:input path="email"/>	
			<form:label path="password">Password (min 5 chars) <form:errors path="password" cssClass="error" /></form:label>
			<form:password path="password" />
			<form:label path="confirmPassword">Confirm Password <form:errors path="confirmPassword" cssClass="error" /></form:label>
			<form:password path="confirmPassword" />
			<form:label path="firstName">First Name <form:errors path="firstName" cssClass="error" /></form:label>
			<form:input path="firstName" />
			<form:label path="lastName">Last Name <form:errors path="lastName" cssClass="error" /></form:label>
			<form:input path="lastName" />
			<button type="submit">Register</button>		
		</fieldset>		
	</form:form>
</div>
