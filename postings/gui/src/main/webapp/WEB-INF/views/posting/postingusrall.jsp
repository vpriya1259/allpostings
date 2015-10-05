<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<div id="postListPanelMine">
	<c:if test="${not empty message}">
		<div class="${message.type.cssClass}">${message.text}</div>
	</c:if>
	
	<c:if test="${fn:length(postingVOs) != 0}">
		<h4>My Postings</h4>
	</c:if>
    <c:forEach items="${postingVOs}" var="postingVO">
		<div id="postListPostPanelMine">
			<c:if test="${!currentDevice.mobile}">
				<div class="postListPostPanelMineItem">
					<div id="postListPostPanelMineImage">
						<c:choose>
							<c:when test="${fn:length(postingVO.imageVOs) == 0}">
								<s:url value="/resources/defimage.png" var="imgUrl">
								</s:url>
								<img src="${imgUrl}" width="35" height="35"/>
							</c:when>
							<c:otherwise>
								<s:url value="/resources/posting-images/{postingId}-{imageId}-small.jpg" var="imgUrl">
									<s:param name="postingId" value="${postingVO.id}" />
									<s:param name="imageId" value="${postingVO.imageVOs[0].id}" />
								</s:url>
								<img src="${imgUrl}" width="30" height="30"/>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</c:if>
			<div class="postListPostPanelMineItem">
				<div id="postListPostPanelMineTitle">
					<s:url value="/posts/{postingCatgry}/{postingId}" var="postUrl">
						<s:param name="postingCatgry" value="${fn:toLowerCase(postingVO.catgry)}" />
						<s:param name="postingId" value="${postingVO.id}" />
					</s:url>
					<a href="${postUrl}"><c:out value="${postingVO.shortTitle}"/></a>
				</div>
			</div>
			<div class="postListPostPanelMineItem">
				<div id="postListPostPanelMineUpdate">
					<s:url value="/usr/upd/{postingId}" var="postUpdUrl">
						<s:param name="postingId" value="${postingVO.id}" />
					</s:url>
					<c:choose>
						<c:when test="${!currentDevice.mobile}">
							<a href="${postUpdUrl}">Update</a>
						</c:when>
						<c:otherwise>
							<a href="${postUpdUrl}"><img src="<c:url value="/resources/icons/edit.png"/>" width="20" height="20" alt="update" /></a>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="postListPostPanelMineItem">
				<div id="postListPostPanelMineDelete">
					<s:url value="/usr/del/{postingId}" var="postDelUrl">
						<s:param name="postingId" value="${postingVO.id}" />
					</s:url>
					<c:choose>
						<c:when test="${!currentDevice.mobile}">
							<a href="${postDelUrl}">Delete</a>
						</c:when>
						<c:otherwise>
							<a href="${postDelUrl}"><img src="<c:url value="/resources/icons/delete.png"/>" width="20" height="20" alt="delete" /></a>						
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
    </c:forEach>
   
    <div id="postListPagination">
    	<c:forEach items="${paginationVO.pageVOs}" var="pageVO">
    		<div class="postListPaginationLink">
    			<c:choose>
    				<c:when test="${!pageVO.selected}">
						<s:url value="/usr/all?pageNum={pageNum}" var="postingsPageUrl">
							<s:param name="pageNum" value="${pageVO.data}" />
						</s:url>
						<a  href="${postingsPageUrl}"><c:out value="${pageVO.label}"/></a>
    				</c:when>
    				<c:otherwise>
    					<c:out value="${pageVO.label}"/>
    				</c:otherwise>
    			</c:choose>
    		</div>
    	</c:forEach>
    </div>
</div>

