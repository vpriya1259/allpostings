<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<script>
	var locsUrl = '/locs/';
	var subCatgriesUrl = '/rental/catgries';
	var searchUrl = '/posts/search/Rental';
	var catgryUrl;
	
	$(document).ready(function () {
		locsUrl = document.getElementById('locsUrl').value;
		subCatgriesUrl = document.getElementById('subCatgriesUrl').value;
		searchUrl = document.getElementById('searchUrl').value;
		catgryUrl = document.getElementById('catgryUrl').value;
		
		$('#catgry').bind('change', function () {
	          var url = catgryUrl + "/" + $(this).val(); // get selected value
	          if (url) { // require a URL
	              window.location = url; // redirect
	          }
	          return false;
		});
		
		
		$( "#locText" ).autocomplete({
			source: function(request, response) {
				   $.ajax({
				      url: locsUrl,
				      dataType: "jsonp",
				      data: {
				         q: request.term,
				         limit: 15
				      },
				      success: function(data) {
				         response($.map(data, function(item) {
				               return {
				                  label: item,
				                  value: item
				               }
				         }))
				      }
				   })
			},
			minLength: 2
		});

		$( "#searchText" ).autocomplete({
			source: function(request, response) {
				   $.ajax({
				      url: subCatgriesUrl,
				      dataType: "jsonp",
				      data: {
				         q: request.term,
				         limit: 15
				      },
				      success: function(data) {
				         response($.map(data, function(item) {
				               return {
				                  label: item,
				                  value: item
				               }
				         }))
				      }
				   })
			},
			minLength: 1
		});
		
		
	});

	function geocodeLocText() {
		var address = document.getElementById("locationText").value;
		geocoder.geocode( { 'address': address}, function(results, status) {
		  if (status == google.maps.GeocoderStatus.OK) {
			  	var latLng = results[0].geometry.location;		  
				$("#searchForm").append("<input type='hidden' name='lat' value="+latLng.lat()+" />");
				$("#searchForm").append("<input type='hidden' name='lng' value="+latLng.lng()+" />");
				var i = 0;
				while( i < results[0].address_components.length) {
					var addressComponent = results[0].address_components[i];
					var types = addressComponent.types;
					var j = 0;
					while(j < types.length) {
						if(types[j] == "locality") {
							$("#searchForm").append("<input type='hidden' name='locality' value='"+addressComponent.long_name+"' />");
						} else if(types[j] == "country") {
							$("#searchForm").append("<input type='hidden' name='country' value='"+addressComponent.long_name+"' />");
							$("#searchForm").append("<input type='hidden' name='countryCode' value='"+addressComponent.short_name+"' />");
						} else if(types[j] == "administrative_area_level_1") {
							$("#searchForm").append("<input type='hidden' name='adminArea' value='"+addressComponent.long_name+"' />");
						} else if(types[j] == "administrative_area_level_2") {
							$("#searchForm").append("<input type='hidden' name='subAdminArea' value='"+addressComponent.long_name+"' />");
						} 
						j = j + 1;
					}
					i = i + 1;
				}
				$("#searchForm").attr("action", searchUrl);
				$("#searchForm").submit();
		  } else {
			  $("#locationTextError").text(" is invalid...").show();
		  }
		});
	}
</script>
<c:url value="/locs" var="locsUrl"/>
<c:url value="/posts/{catgry}/subcatgries" var="subCatgriesUrl"/>
<c:url value="/posts/search" var="searchUrl" />
<c:url value="/posts" var="catgryUrl" />
<input type="hidden" value="${locsUrl}" id="locsUrl"/>
<input type="hidden" value="${subCatgriesUrl}" id="subCatgriesUrl"/>
<input type="hidden" value="${searchUrl}" id="searchUrl"/>
<input type="hidden" value="${catgryUrl}" id="catgryUrl"/>

<div class="postSearchPanel">
	<form action="javascript: geocodeLocText()" method="post" id="searchForm">
		<div class="postSearchPanelSearch">
			<div id="postSearchPanelCatgry">
				<label>Category <span id="categoryError" class="error"></span></label>
				<select id="catgry" name="catgry">
					<c:forEach items="${catgries}" var="catgry">
						<c:choose>
							<c:when test="${(not empty postingsVO.catgry) && (postingsVO.catgry == catgry.code)}">
								<option value="${catgry.code}" selected>${catgry.name}</option>
							</c:when>
							<c:otherwise>
								<option value="${catgry.code}">${catgry.name}</option>
							</c:otherwise>
						</c:choose>
					
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="postSearchPanelSearch">
			<div id="postSearchPanelSearchLoc">
				<label><font color="red">*</font>Location <span id="locationTextError" class="error"></span></label>
				<c:choose>
					<c:when test="${not empty postingsVO.locationText}">
						<input id="locationText" name="locationText" class="search" value="${postingsVO.locationText}"/>
					</c:when>
					<c:otherwise>
						<input id="locationText" name="locationText" class="search"/>				
					</c:otherwise>				
				</c:choose>
			</div>
		</div>
		<div class="postSearchPanelSearch">
			<div id="postSearchPanelSearchText">
				<label>Looking for <span id="searchTextError" class="error"></span></label>
				<c:choose>
					<c:when test="${not empty postingsVO.searchText}">
						<input id="searchText" name="searchText" class="search" value="${postingsVO.searchText}"/>
					</c:when>
					<c:otherwise>
						<input id="searchText" name="searchText" class="search"/>					
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="postSearchPanelSearch">
			<div id="postSearchPanelSearchRadius">
				<label>Radius<span id="radius" class="error"></span></label>
				<div>
					<div class="postSearchPanelSearchRadius1">
						<select id="radius" name="radius">
							<c:forEach items="${radii}" var="tmpRadius">
								<c:choose>
									<c:when test="${(not empty postingsVO.radius) && (postingsVO.radius == tmpRadius.distance)}">
										<option value="${tmpRadius.label}" selected>${tmpRadius.distance}</option>
									</c:when>
									<c:otherwise>
										<option value="${tmpRadius.label}">${tmpRadius.distance}</option>
									</c:otherwise>
								</c:choose>
								
							</c:forEach>
						</select>
					</div>
					<div class="postSearchPanelSearchRadius1">
						<div id="postSearchPanelSearchMiles">
							Miles
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="postSearchPanelSearch" align="center" valign="middle">
			<button type="submit">Search</button>
		</div>
		
	</form>
</div>

<div id="postListPanel">
	<c:if test="${not empty message}">
		<div class="${message.type.cssClass}">${message.text}</div>
	</c:if>
	
	<c:if test="${not empty postingsVO.postingVOs}">
		<div class="postListPanelNavBar">
			<div id="postListPanelHeader">
				Postings...
			</div>
		</div>
		<c:if test="${not empty postingsVO.locationText}">
			<div class="postListPanelNavBar">
				<div id="postListPanelSortBy">
					<s:url value="/posts/search?catgry={catgry}&locationText={locationText}&radius={radius}&sortBy=time" var="sortByTimeUrl">
						<s:param name="catgry" value="${fn:toLowerCase(postingsVO.catgry)}" />
						<s:param name="locationText" value="${postingsVO.locationText}" />
						<s:param name="radius" value="${postingsVO.radius}" />
					</s:url>
					<s:url value="/posts/search?catgry={catgry}&locationText={locationText}&radius={radius}&sortBy=distance" var="sortByDistUrl">
						<s:param name="catgry" value="${fn:toLowerCase(postingsVO.catgry)}" />
						<s:param name="locationText" value="${postingsVO.locationText}" />
						<s:param name="radius" value="${postingsVO.radius}" />
					</s:url>
  					<c:if test="${(not empty postingsVO.searchText)}">
						<s:url value="/posts/search?catgry={catgry}&locationText={locationText}&radius={radius}&searchText={searchText}&sortBy=time" var="sortByTimeUrl">
							<s:param name="catgry" value="${fn:toLowerCase(postingsVO.catgry)}" />
							<s:param name="locationText" value="${postingsVO.locationText}" />
							<s:param name="radius" value="${postingsVO.radius}" />
							<s:param name="searchText" value="${postingsVO.searchText}"/>
						</s:url>
						<s:url value="/posts/search?catgry={catgry}&locationText={locationText}&radius={radius}&searchText={searchText}&sortBy=distance" var="sortByDistUrl">
							<s:param name="catgry" value="${fn:toLowerCase(postingsVO.catgry)}" />
							<s:param name="locationText" value="${postingsVO.locationText}" />
							<s:param name="radius" value="${postingsVO.radius}" />
							<s:param name="searchText" value="${postingsVO.searchText}"/>
						</s:url>
  					</c:if>
					<c:choose>
						<c:when test="${fn:startsWith(postingsVO.sortBy, 'time')}">
							<div>Sort By: <b>Relevance</b> | <a href="${sortByDistUrl}"><b>Distance</b></a></div>
						</c:when>
						<c:otherwise>
							<div>Sort By: <a href="${sortByTimeUrl}"><b>Relevance</b></a> | <b>Distance</b></div>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</c:if>
	</c:if>
	
	<div class="clear"></div>
	<div id="postListPanelHRule"></div>
	<div class="clear"></div>

	  <c:forEach items="${postingsVO.postingVOs}" var="postingVO">
	  
		<s:url value="/posts/{catgry}/{postId}" var="postPanelViewLinkUrl">
			<s:param name="catgry" value="${fn:toLowerCase(postingVO.catgry)}" />
			<s:param name="postId" value="${postingVO.id}" />
		</s:url>
		<a href="${postPanelViewLinkUrl}">
	    	<div id="postListPostPanel">
	    		<div>
		    		<div class="postListPostPanelTitleItem">
		    			<div id="postListPostPanelTitle"><c:out value="[${postingVO.type}] ${postingVO.title}"/></div>
		    		</div>
		    		<div class="postListPostPanelTitleItem">
		    			<div id="postListPostPanelDate"><c:out value="${postingVO.htmlShortUpdated}"/></div>
		    		</div>
	    		</div>
	    		<div>
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
						<div id="postListPostPanelDesc">
							<c:out value="${postingVO.shortDesc}"/>
						</div>
						<div id="postListPostPanelDetails">
							<div class="postListPostPanelDetailsItem">
								<div id="postListPostPanelAddress">
									@<c:out value="${postingVO.address}"/>
								</div>
							</div>
							<div class="postListPostPanelDetailsItem">
								<div id="postListPostPanelCatgry">
									*<c:out value="${postingVO.subCatgry}"/>
								</div>
							</div>
							<div class="postListPostPanelDetailsItem">
								<c:if test="${postingVO.distance != -1}">
									<div id="postListPostPanelDistance">
									#<c:out value="${postingVO.distance}"/> Miles
									</div>
								</c:if>
							</div>
						</div>
					</div>
					<div class="postListPostPanelHorzItem">
						<c:if test="${postingVO.realPrice}">
							<div id="postListPostPanelPrice">
								<c:out value="${postingVO.currencyCode}${postingVO.price}"/>
							</div>						
						</c:if>
					</div>
	    		</div>
	      	</div>
		</a>	    
    </c:forEach>

    <div id="postListPagination">
    	<c:forEach items="${paginationVO.pageVOs}" var="pageVO">
    		<div class="postListPaginationLink">
    			<c:choose>
    				<c:when test="${!pageVO.selected}">
						<s:url value="/posts/{catgry}?pageNum={pageNum}" var="postsPageUrl">
							<s:param name="catgry" value="${postingsVO.catgry}" />
							<s:param name="pageNum" value="${pageVO.data}" />
						</s:url>
    					<c:if test="${not empty postingsVO.locationText}">
							<s:url value="/posts/search?catgry={catgry}&pageNum={pageNum}&locationText={locationText}&radius={radius}&sortBy={sortBy}" var="postsPageUrl">
								<s:param name="catgry" value="${fn:toLowerCase(postingsVO.catgry)}" />
								<s:param name="pageNum" value="${pageVO.data}" />
								<s:param name="locationText" value="${postingsVO.locationText}" />
								<s:param name="radius" value="${postingsVO.radius}" />
								<s:param name="sortBy" value="${postingsVO.sortBy}"/>
							</s:url>
    					</c:if>
    					<c:if test="${(not empty postingsVO.locationText) && (not empty postingsVO.searchText)}">
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

