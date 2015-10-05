<%@ page session="false" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="sign">
	<c:if test="${not empty message}">
		<div class="${message.type.cssClass}">${message.text}</div>
	</c:if>

	<form action="<c:url value="/signin/authenticate" />" method="post">
		<div class="formInfo">
	  		<h2>Sign In</h2>
	  		<c:if test="${signinError}">
		  		<div class="error">
		  			Your sign in information was incorrect.
		  			Please try again or <a href="<c:url value="/signup" />">sign up</a>.
		  		</div>
	 	 	</c:if>
		</div>
		<fieldset>
			<label for="login">Email</label>
			<input id="login" name="j_username" type="text" size="25" autocorrect="off" autocapitalize="off" autocorrect="off" autocapitalize="off" <c:if test="${not empty signinErrorMessage}">value="${SPRING_SECURITY_LAST_USERNAME}"</c:if> />
			<label for="password">Password</label>
			<input id="password" name="j_password" type="password" size="25" />
			<button type="submit">Sign In</button>
			<div id="forgotPasswd"><a href="<c:url value="/reset" />">Forgot your password?</a></div>
			
		</fieldset>
	</form>
	
</div>

