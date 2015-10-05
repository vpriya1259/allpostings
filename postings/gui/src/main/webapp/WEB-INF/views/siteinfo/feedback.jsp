<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<div id="sendMail">
	<c:if test="${not empty message}">
		<div class="${message.type.cssClass}">${message.text}</div>
	</c:if>
	
	<s:url value="/info/feedback" var="feedbackUrl">
	</s:url>
	
	<form:form id="feedbackForm" action="${feedbackUrl}" method="post" modelAttribute="feedbackForm" enctype="multipart/form-data">
		<div class="formInfo">
	  		<h2>Feedback</h2>
	  		<s:bind path="*">
	  		<c:choose>
	  		<c:when test="${status.error}">
	  			<div class="error">Unable to submit your feedback. Please fix the errors below and resubmit.</div>
	  		</c:when>
	  		</c:choose>
	  		</s:bind>
		</div>
	
		<form:label path="name">Name <form:errors path="name" cssClass="error" /></form:label>
		<form:input path="name" class="name"/>
		
		<form:label path="email">Email <form:errors path="email" cssClass="error" /></form:label>
		<form:input path="email" class="email"/>

		<form:label path="message">Message <form:errors path="message" cssClass="error" /></form:label>
		<form:textarea path="message"/>
		<p><button type="submit">Submit</button></p>
	</form:form>
	
</div>
