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
		<div id="postPanelHeader">
			<div class="postPanelHeaderRow">
				<div id="postPanelTitle"><c:out value="${postingVO.title}"/></div>			
			</div>
			<s:url value="/posts/{catgry}/{postId}" var="postUrl">
				<s:param name="postId" value="${postingVO.id}" />
			</s:url>
			
			<div class="postPanelHeaderRow">
				<div id="postPanelShare">
					<span class="st_facebook_button"></span>
					<span class="st_twitter_button"></span>
					<span class="st_email_button" displayText="Email"></span>
				</div>
			</div>
		</div>
		<div class="clear"></div>
		<div id="postPanelDetails">		
			<div class="postPanelDetailsRow">
				<div class="postPanelDetailsLbl">Posted on:</div>
			</div>
			<div class="postPanelDetailsRow">
				<div class="postPanelDetailsVal">
					<c:out value="${postingVO.htmlUpdated}"/>
				</div>
			</div>
			<div class="postPanelDetailsRow">
				<div class="postPanelDetailsLbl">Category:</div>
			</div>
			<div class="postPanelDetailsRow">
				<div class="postPanelDetailsVal"><c:out value="${postingVO.subCatgry}"/></div>
			</div>
			
			<div class="clear"></div>
			
			<div class="postPanelDetailsRow">
				<div class="postPanelDetailsLbl">Email to:</div>
			</div>
			<div class="postPanelDetailsRow">
				<div class="postPanelDetailsVal"><c:out value="${postingVO.email}"/></div>
			</div>
			<c:if test="${postingVO.realPrice}">
				<div class="postPanelDetailsRow">
					<div class="postPanelDetailsLbl">Price:</div>
				</div>
				<div class="postPanelDetailsRow">
					<div class="postPanelDetailsVal"><c:out value="${postingVO.currencyCode}${postingVO.price}"/></div>
				</div>
			</c:if>
			
			<div class="clear"></div>
						
			<div class="postPanelDetailsRow">
				<div class="postPanelDetailsLbl">Address:</div>
			</div>
		
			<div class="postPanelDetailsRow">
				<div class="postPanelDetailsVal"><c:out value="${postingVO.address}"/></div>
			</div>
			
			<div class="clear"></div>

			<div id="postPanelActionRow">
				<s:url value="/posts/mail/{catgry}/{postId}" var="postMailUrl">
					<s:param name="catgry" value="${postingVO.catgry}" />
					<s:param name="postId" value="${postingVO.id}" />
				</s:url>
				<s:url value="http://maps.google.com/?q={address}" var="googleMapsUrl">
					<s:param name="address" value="${postingVO.address}" />
				</s:url>
				<ul>
					<li><a href="${postMailUrl}">Contact</a></li>
					<li><a href="${googleMapsUrl}">Get directions</a></li>
				</ul>
			
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
			<img src="${imgUrl}"/>
	    </c:forEach>
	    
	    <div id="postPanelOrigLink">
			<c:if test="${postingVO.src != null}">
				<s:url value="${postingVO.src}" var="postPanelViewLinkUrl"/>
				Click <a href="${postPanelViewLinkUrl}">here</a> to see the original posting.
			</c:if>
		</div>
	    	
	</c:if>
</div>
