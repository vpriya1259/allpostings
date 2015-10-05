<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<div class="postDelPanel">
	<c:if test="${not empty message}">
		<div class="${message.type.cssClass}">${message.text}</div>
	</c:if>
	
	<c:if test="${not empty postingVO}">
		<h3>Delete confirmation...</h3>
		<div id="postDelMessage">
			Do you want to delete the posting "<c:out value="${postingVO.title}"/>"?
		</div>

		<div class="postDelActionRow">
			<div id="postDelActionYes">
				<s:url value="/usr/del/con/{postingId}" var="postDelYesUrl">
					<s:param name="postingId" value="${postingVO.id}" />
				</s:url>
				<a href="${postDelYesUrl}">Yes</a>
			</div>
		</div>
		<div class="postDelActionRow">
			<div id="postDelActionNo">
				<s:url value="/usr/all" var="postDelNoUrl"></s:url>
				<a href="${postDelNoUrl}">No</a>
			</div>
		</div>
	</c:if>
</div>
