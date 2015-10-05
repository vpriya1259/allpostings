<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<div id="sett">
	<c:if test="${not empty message}">
		<div class="${message.type.cssClass}">${message.text}</div>
	</c:if>
	
	<s:url value="/sett/upd/details/{userId}" var="updDetailsUrl">
		<s:param name="userId" value="${userVO.id}" />
	</s:url>
	
	<form:form id="settForm" action="${updDetailsUrl}" method="post" modelAttribute="updDetailsForm" enctype="multipart/form-data">
		<div class="formInfo">
	  		<h2>Update details</h2>
	  		<s:bind path="*">
	  		<c:choose>
	  		<c:when test="${status.error}">
	  			<div class="error">Unable to update your details. Please fix the errors below and resubmit.</div>
	  		</c:when>
	  		</c:choose>
	  		</s:bind>
		</div>
	
		<form:label path="fname">First Name <form:errors path="fname" cssClass="error" /></form:label>
		<form:input path="fname" class="fname"/>
		
		<form:label path="lname">Last Name <form:errors path="lname" cssClass="error" /></form:label>
		<form:input path="lname" class="lname"/>

		<form:label path="address">Location <form:errors path="address" cssClass="error" /></form:label>
		<form:input path="address" />
		
		<form:label path="zip">Zip <form:errors path="zip" cssClass="error" /></form:label>
		<form:input path="zip" />
		
		<form:label path="phone">Phone <form:errors path="phone" cssClass="error" /></form:label>
		<form:input path="phone" />
		
		<p><button type="submit">Update</button></p>		
	</form:form>
	
</div>
