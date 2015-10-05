<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:if test="${userVO != null}">
	<div id="usrActionNav">
		<ul>
			<li><a href="<c:url value="/usr/new" />">New Posting</a></li>
			<li><a href="<c:url value="/usr/all" />">My Postings</a></li>
			<c:choose>
				<c:when test="${currentDevice.mobile}">
					<li><a href="<c:url value="/m/posts/search" />">Search</a></li>
				</c:when>
				<c:otherwise>
					<li><a href="<c:url value="/posts/posting" />">All Postings</a></li>
				</c:otherwise>
			</c:choose>
		</ul>
	</div>
	<div class="clear"></div>
</c:if>

