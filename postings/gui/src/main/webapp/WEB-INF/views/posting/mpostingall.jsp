<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<div id="postListPanel">
	<c:if test="${not empty message}">
		<div class="${message.type.cssClass}">${message.text}</div>
	</c:if>
	
	<c:forEach items="${postingsVO.postingVOs}" var="postingVO">
		<s:url value="/posts/{catgry}/{postId}" var="postPanelViewLinkUrl">
			<s:param name="catgry" value="${fn:toLowerCase(postingVO.catgry)}" />
			<s:param name="postId" value="${postingVO.id}" />
		</s:url>
	  
		<a href="${postPanelViewLinkUrl}">
	    	<div id="postListPostPanel">
				<c:choose>
					<c:when test="${fn:toLowerCase(postingVO.catgry) != 'job'}">
						<div class="postListPostPanelHorzItem">
							<div id="postListPostPanelImage" valign="middle" align="center">
								<c:choose>
									<c:when test="${fn:length(postingVO.imageVOs) == 0}">
										<s:url value="/resources/defimage.png" var="imgUrl">
										</s:url>
										<img src="${imgUrl}" width="50" height="50"/>
									</c:when>
									<c:otherwise>
										<s:url value="/resources/posting-images/{postingId}-{imageId}-small.jpg" var="imgUrl">
											<s:param name="postingId" value="${postingVO.id}" />
											<s:param name="imageId" value="${postingVO.imageVOs[0].id}" />
										</s:url>
										<img src="${imgUrl}" width="50" height="50"/>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<div class="postListPostPanelHorzItem">
							<s:url value="/resources/job.png" var="imgUrl">
							</s:url>
							<img src="${imgUrl}" width="50" height="50"/>
						</div>
					</c:otherwise>
    			</c:choose>

				<div class="postListPostPanelHorzItem">
	    			<div id="postListPostPanelTitle"><c:out value="[${postingVO.type}] ${postingVO.title}"/></div>
	    			<div id="postListPostPanelUpdated"><c:out value="${postingVO.htmlShortUpdated}"/></div>
	    		</div>
	      </div>
		</a>	    
	</c:forEach>
	<div id="postListPagination">
    	<c:forEach items="${paginationVO.pageVOs}" var="pageVO">
    		<div class="postListPaginationLink">
    			<c:choose>
    				<c:when test="${!pageVO.selected}">
						<s:url value="/posts/search?catgry={catgry}&pageNum={pageNum}&locationText={locationText}&radius={radius}&sortBy={sortBy}" var="postsPageUrl">
							<s:param name="catgry" value="${fn:toLowerCase(postingsVO.catgry)}" />
							<s:param name="pageNum" value="${pageVO.data}" />
							<s:param name="locationText" value="${postingsVO.locationText}" />
							<s:param name="radius" value="${postingsVO.radius}" />
							<s:param name="sortBy" value="${postingsVO.sortBy}"/>
						</s:url>
    					<c:if test="${not empty searchText}">
							<s:url value="/posts/search?catgry={catgry}&pageNum={pageNum}&locationText={locationText}&radius={radius}&searchText={searchText}&sortBy={sortBy}" var="postsPageUrl">
								<s:param name="catgry" value="${fn:toLowerCase(postingsVO.catgry)}" />							
								<s:param name="pageNum" value="${pageVO.data}" />
								<s:param name="locationText" value="${postingsVO.locationText}" />
								<s:param name="radius" value="${postingsVO.radius}" />
								<s:param name="searchText" value="${postingsVO.searchText}"/>
								<s:param name="sortBy" value="${postingsVO.sortBy}"/>								
							</s:url>
    					</c:if>
						<a  href="${postsPageUrl}"><c:out value="${pageVO.label}"/></a>
    				</c:when>
    				<c:otherwise>
    					<c:out value="${pageVO.label}"/>
    				</c:otherwise>
    			</c:choose>
    		</div>
    	</c:forEach>
   </div>	
</div>

