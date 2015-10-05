<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="sign">
	<c:if test="${not empty message}">
		<div class="${message.type.cssClass}">${message.text}</div>
	</c:if>
	
	<form id="resetPassword" method="post">
		<div class="formInfo">
	  		<h2>Reset Password</h2>
		</div>
		<fieldset>
		  	<label for="signin">Email <c:if test="${signin.error}"><span class="error">${signin.errorMessage}</span></c:if></label>
		  	<input id="siginin" type="text" name="signin" value="${signin.value}" />
			<p><button type="submit">Submit</button></p>		  	
		</fieldset>
	</form>
</div>
