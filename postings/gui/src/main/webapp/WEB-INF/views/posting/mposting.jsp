<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<div id="postPanel">
	<c:if test="${not empty message}">
		<div class="${message.type.cssClass}">${message.text}</div>
	</c:if>

	<c:if test="${not empty postingVO}">
		<div id="postPanelTitle"><c:out value="[${postingVO.type}] ${postingVO.title}"/></div>			
		<div id="postPanelDetails">
			<c:if test="${not empty postingVO.email}">
				<div class="postPanelDetailsLbl">
					Email:
				</div>		
				<div class="postPanelDetailsVal">
					<c:out value="${postingVO.email}"/>
				</div>
			</c:if>
			<c:if test="${not empty postingVO.address}">
				<div class="postPanelDetailsLbl">
					Address:
				</div>
				<div class="postPanelDetailsVal"><c:out value="${postingVO.address}"/></div>
			</c:if>
			
			<s:url value="/posts/mail/{catgry}/{postId}" var="postMailUrl">
				<s:param name="catgry" value="${postingVO.catgry}" />
				<s:param name="postId" value="${postingVO.id}" />
			</s:url>
			<div id="sendMailLink">
				<a href="${postMailUrl}">Send an email</a>
			</div>
		</div>
		<div id="postPanelDesc">
			${postingVO.htmlDesc}
		</div>
	    <c:forEach items="${postingVO.imageVOs}" var="imageVO">
			<s:url value="/resources/posting-images/{postingId}-{imageId}-large.jpg" var="imgUrl">
				<s:param name="postingId" value="${postingVO.id}" />
				<s:param name="imageId" value="${imageVO.id}" />
			</s:url>
	    
			<div id="postPanelImage">
				<img src="${imgUrl}" width="250px" height="250px"/>
			</div>
	    </c:forEach>
	    <div id="postPanelOrigLink">
			<c:if test="${postingVO.src != null}">
				<s:url value="${postingVO.src}" var="postPanelViewLinkUrl"/>
				Click <a href="${postPanelViewLinkUrl}">here</a> to see the original posting.
			</c:if>
		</div>
	</c:if>
</div>
