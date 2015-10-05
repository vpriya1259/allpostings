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
	
	$(document).ready(function () {
		locsUrl = document.getElementById('locsUrl').value;
		subCatgriesUrl = document.getElementById('subCatgriesUrl').value;
		searchUrl = document.getElementById('searchUrl').value;
		
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
<s:url value="/posts/search/" var="searchUrl" >
</s:url>
<input type="hidden" value="${locsUrl}" id="locsUrl"/>
<input type="hidden" value="${subCatgriesUrl}" id="subCatgriesUrl"/>
<input type="hidden" value="${searchUrl}" id="searchUrl"/>


<div id="postSearchPanel">
	<c:if test="${not empty message}">
		<div class="${message.type.cssClass}">${message.text}</div>
	</c:if>
	<form action="javascript: geocodeLocText()" method="post" id="searchForm">
		<div id="postSearchPanelCatgry">
			<label>Category<span id="catgry" class="error"></span></label>
			<div>
				<select id="catgry" name="catgry">
					<c:forEach items="${catgries}" var="catgry">
						<option value="${catgry.code}">${catgry.name}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div id="postSearchPanelSearchLoc">
			<label>Location <span id="locationTextError" class="error"></span></label>
			<c:choose>
				<c:when test="${not empty locationText}">
					<input id="locationText" name="locationText" class="search" value="${locationText}"/>
				</c:when>
				<c:otherwise>
					<input id="locationText" name="locationText" class="search"/>				
				</c:otherwise>				
			</c:choose>
		</div>
		<div id="postSearchPanelSearchText">
			<label>Looking for <span id="searchTextError" class="error"></span></label>
			<c:choose>
				<c:when test="${not empty searchText}">
					<input id="searchText" name="searchText" class="search" value="${searchText}"/>
				</c:when>
				<c:otherwise>
					<input id="searchText" name="searchText" class="search"/>					
				</c:otherwise>
			</c:choose>
			
		</div>
		<div id="postSearchPanelSearchRadius">
			<label>Radius<span id="radius" class="error"></span></label>
			<div>
				<div class="postSearchPanelSearchRadius1">
					<select id="radius" name="radius">
						<c:forEach items="${radii}" var="tmpRadius">
							<c:choose>
								<c:when test="${(not empty radius) && (radius == tmpRadius.distance)}">
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
		<div class="clear"></div>
		<div id="postSearchPanelSearchBttn">
			<button type="submit">Search</button>
		</div>
	</form>
</div>


