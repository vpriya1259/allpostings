<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript">
	var newformUrl = '/usr/new';
	var subCatgriesUrl;
	var siteName;
	
	$(document).ready(function () {
		newformUrl = document.getElementById('newformUrl').value;
		subCatgriesUrl = document.getElementById('subCatgriesUrl').value;
		siteName = document.getElementById('siteName').value.toLowerCase();
		
		$('#catgry').change(function() {
			checkSubCatgries();
		});	
		
	});
	
	function checkSubCatgries() {
		$.getJSON(subCatgriesUrl, { catgry: $('#catgry').val() }, function(availability) {
			if (availability.catgries) {
				var catgries = availability.catgries;
				var options = '';
				catgries.forEach(function(item) {
					options += '<option value="' +item+'">' +item+'</option>';					
					});
				$("select[name^=subcatgry]").html(options);
			}
			
			if ($('#catgry').val() == 'job') {
				$('#imageFieldSet').remove();
				$('#priceSet').remove();
			} else {
				if ($('#imageFieldSet').length == 0) {
					var imageFieldSet = '<fieldset id="imageFieldSet"><legend>Upload pictures (Each picture size limit is 2MB)</legend><input type="file" id="image1" name="image1" /><input type="file" id="image2" name="image2" /><input type="file" id="image3" name="image3" /><input type="file" id="image4" name="image4" /><input type="file" id="image5" name="image5"/></fieldset>';
					$("#imageSection").html(imageFieldSet);					
				}
				if ($('#priceSet').length == 0) {
					var priceSet = '<div id="priceSet"><label>Price</label><div><div class="newPostPriceItem"><div id="newPostPriceCurr"><select id="currency" name="currency"><option value="USD">USD</option><option value="INR">INR</option></select></div></div><div class="newPostPriceItem"><div id="newPostPriceInput"><input id="price" name="price"/></div></div></div></div>';
					$("#priceSection").html(priceSet);
				}
			}
			
		});
	}
	function isBlank(pString){
	    if (!pString || pString.length == 0) {
	        return true;
	    }
	    // checks for a non-white space character 
	    // which I think [citation needed] is faster 
	    // than removing all the whitespace and checking 
	    // against an empty string
	    return !/[^\s]+/.test(pString);
	}

	function geocodeLocText() {
		var address = document.getElementById("address").value;
		var title = document.getElementById("title").value;
		var desc = document.getElementById("desc").value;
		if (isBlank(title) || isBlank(desc)) {
			if (isBlank(title)) {
				$("#titleError").text(" is invalid...").show();
			}
			if (isBlank(desc)) {
				$("#descError").text(" is invalid...").show();
			}
			return;
		}
		
		geocoder.geocode( { 'address': address}, function(results, status) {
		  if (status == google.maps.GeocoderStatus.OK) {
			  	var latLng = results[0].geometry.location;		  
				$("#newForm").append("<input type='hidden' name='lat' value="+latLng.lat()+" />");
				$("#newForm").append("<input type='hidden' name='lng' value="+latLng.lng()+" />");
				var i = 0;
				while( i < results[0].address_components.length) {
					var addressComponent = results[0].address_components[i];
					var types = addressComponent.types;
					var j = 0;
					while(j < types.length) {
						if(types[j] == "locality") {
							$("#newForm").append("<input type='hidden' name='locality' value='"+addressComponent.long_name+"' />");
						} else if(types[j] == "country") {
							$("#newForm").append("<input type='hidden' name='country' value='"+addressComponent.long_name+"' />");
							$("#newForm").append("<input type='hidden' name='countryCode' value='"+addressComponent.short_name+"' />");
						} else if(types[j] == "administrative_area_level_1") {
							$("#newForm").append("<input type='hidden' name='adminArea' value='"+addressComponent.long_name+"' />");
						} else if(types[j] == "administrative_area_level_2") {
							$("#newForm").append("<input type='hidden' name='subAdminArea' value='"+addressComponent.long_name+"' />");
						} 
						j = j + 1;
					}
					i = i + 1;
				}
				$("#newForm").attr("action", newformUrl);
				$("#newForm").submit();
		  } else {
			  $("#locError").text(" is invalid...").show();
		  }
		});
	}

	
	function onAnonymClicked(value) {
		if(document.getElementById('anonym').checked) {
			document.getElementById('newPostEmail').firstChild.nodeValue 
				= "postXXX@"+siteName+"-appspotmail.com";	
		}
		else {
			document.getElementById('newPostEmail').firstChild.nodeValue 
				= value;
		}
	}	
</script>
<c:url value="/usr/new" var="newformUrl" />
<input type="hidden" value="${newformUrl}" id="newformUrl"/>
<input type="hidden" value="<fmt:message key='siteName'/>" id="siteName"/>
<c:url value="/posts/subcatgries" var="subCatgriesUrl" />
<input type="hidden" value="${subCatgriesUrl}" id="subCatgriesUrl"/>

<input type="hidden" name="MAX_FILE_SIZE" value="100" />

<div id="newPost">
	<c:if test="${not empty message}">
	<div class="${message.type.cssClass}">${message.text}</div>
	</c:if>
	
	
	<form action="javascript: geocodeLocText()" id="newForm" method="post" enctype="multipart/form-data">
		<div class="formInfo">
	  		<h2>New Posting</h2>
		</div>
	
		<fieldset>
			<legend>Post in</legend>
			
			<label >Postal Address (E.g. Cupertino, CA)<span id="locError" class="error"></span></label>
			<input type="text" class="address" id="address" name="address"/>
			
			<label>Category</label>
			<div>
				<select id="catgry" name="catgry">
					<c:forEach items="${catgries}" var="tmpCatgry">
						<c:choose>
							<c:when test="${tmpCatgry.code == catgry}">
								<option value="${tmpCatgry.code}" selected>${tmpCatgry.name}</option>
							</c:when>
							<c:otherwise>
								<option value="${tmpCatgry.code}">${tmpCatgry.name}</option>
							</c:otherwise>
						</c:choose>
					
					</c:forEach>
				</select>
			</div>
			<label>Sub Category</label>
			<div>
				<select id="subcatgry" name="subcatgry">
					<c:forEach items="${subCatgries}" var="subCatgry">
						<option>${subCatgry}
					</c:forEach>
				</select>
			</div>
			<label >Type</label>
			<div class="radio">
				<input type="radio" name="type" value="Offered" checked/>Offered
				<input type="radio" name="type" value="Wanted"/>Wanted
			</div>
		</fieldset>
		
		<fieldset>
			<legend>Details</legend>
			
			<label>Title</label><span id="titleError" class="error"></span>
			<input id="title" name="title" class="title"/>
			
			<label>Description</label><span id="descError" class="error"></span>
			<textarea id="desc" name="desc"></textarea>
			
			<div id="priceSection">
				<c:if test="${catgry != 'job'}">
					<div id="priceSet">
						<label>Price</label>
						<div>
							<div class="newPostPriceItem">
								<div id="newPostPriceCurr">
									<select id="currency" name="currency">
										<option value="USD">USD</option>
										<option value="INR">INR</option>
									</select>
								</div>
							</div>
							<div class="newPostPriceItem">
								<div id="newPostPriceInput">
									<input id="price" name="price"/>
								</div>
							</div>
						</div>								
					</div>
				</c:if>
			</div>
			
			<div class="clear"> </div>
			
			<label>Email </label>
			<div>
				<div class="newPostEmailItem">
					<div id="newPostEmail">
						<c:out value="${userVO.email}"/>
					</div>
				</div>
			</div>
			
			<div class="clear"></div>
			<div>
				<div class="newPostEmailItem">
					<div id="newPostAnonym">
						<input type="checkbox" id="anonym" name="anonym" onclick="onAnonymClicked('${userVO.email}');"/>
					</div>
				</div>
				<div class="newPostEmailItem">
					<label>
						Display email as anonymous
					</label>
				</div>
			</div>
			
		</fieldset>

		<div id="imageSection">		
			<c:if test="${catgry != 'job'}">
				<fieldset id="imageFieldSet">
					<legend>Upload pictures (Each picture size limit is 2MB)</legend>
					<input type="file" id="image1" name="image1" />
					<input type="file" id="image2" name="image2" />
					<input type="file" id="image3" name="image3" />
					<input type="file" id="image4" name="image4" />
					<input type="file" id="image5" name="image5"/>
				</fieldset>
			</c:if>
		</div>
	
		<p><button type="submit">Submit</button></p>
	</form>
</div>
