<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<script type="text/javascript">
	var postingUpdUrl;
	var subCatgriesUrl;

	$(document).ready(function() {
		postingUpdUrl = document.getElementById('postingUpdUrl').value;
		subCatgriesUrl = document.getElementById('subCatgriesUrl').value;
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
		geocoder.geocode(
						{
							'address' : address
						},
						function(results, status) {
							if (status == google.maps.GeocoderStatus.OK) {
								var latLng = results[0].geometry.location;
								$("#updForm").append(
										"<input type='hidden' name='lat' value="
												+ latLng.lat() + " />");
								$("#updForm").append(
										"<input type='hidden' name='lng' value="
												+ latLng.lng() + " />");
								var i = 0;
								while (i < results[0].address_components.length) {
									var addressComponent = results[0].address_components[i];
									var types = addressComponent.types;
									var j = 0;
									while (j < types.length) {
										if (types[j] == "locality") {
											$("#updForm")
													.append(
															"<input type='hidden' name='locality' value='"+addressComponent.long_name+"' />");
										} else if (types[j] == "country") {
											$("#updForm")
													.append(
															"<input type='hidden' name='country' value='"+addressComponent.long_name+"' />");
											$("#updForm")
													.append(
															"<input type='hidden' name='countryCode' value='"+addressComponent.short_name+"' />");
										} else if (types[j] == "administrative_area_level_1") {
											$("#updForm")
													.append(
															"<input type='hidden' name='adminArea' value='"+addressComponent.long_name+"' />");
										} else if (types[j] == "administrative_area_level_2") {
											$("#updForm")
													.append(
															"<input type='hidden' name='subAdminArea' value='"+addressComponent.long_name+"' />");
										}
										j = j + 1;
									}
									i = i + 1;
								}
								$("#updForm").attr("action", postingUpdUrl);
								$("#updForm").submit();
							} else {
								$("#locError").text(" is invalid...").show();
							}
						});
	}

	function onAnonymClicked(value) {
		if (document.getElementById('anonym').checked) {
			document.getElementById('newPostEmail').firstChild.nodeValue = "rental-XXXX@baybaz-appspotmail.com";
		} else {
			document.getElementById('newPostEmail').firstChild.nodeValue = value;
		}
	}

	function onDeleteClicked(imageBrowseId, imageImgId, imageUrl) {
		document.getElementById(imageImgId).src = imageUrl;
		document.getElementById(imageBrowseId).disabled = true;
	}

	function onKeepClicked(imageBrowseId, imageImgId, imageUrl) {
		document.getElementById(imageImgId).src = imageUrl;
		document.getElementById(imageBrowseId).disabled = true;
	}

	function onChangeClicked(imageBrowseId, imageImgId, imageUrl) {
		document.getElementById(imageImgId).src = imageUrl;
		document.getElementById(imageBrowseId).disabled = false;
	}
</script>

<s:url value="/usr/upd/{postingId}" var="postingUpdUrl">
	<s:param name="postingId" value="${postingVO.id}" />
</s:url>

<input type="hidden" value="${postingUpdUrl}" id="postingUpdUrl" />
<c:url value="/posts/subcatgries" var="subCatgriesUrl" />
<input type="hidden" value="${subCatgriesUrl}" id="subCatgriesUrl"/>


<div id="newPost">
	<c:if test="${not empty message}">
		<div class="${message.type.cssClass}">${message.text}</div>
	</c:if>

	<c:if test="${not empty postingVO}">

		<form action="javascript: geocodeLocText()" method="post" id="updForm"
			enctype="multipart/form-data">
			<div class="formInfo">
				<h2>Update Posting</h2>
			</div>

			<fieldset>
				<legend>Post in</legend>

				<label>Postal Address (E.g. Cupertino, CA)<span id="locError" class="error"></span> </label> 
				<input type="text" class="address" id="address" name="address" value="${postingVO.address}"/> 
				
				<label>Category</label>
				<div>
					<select id="catgry" name="catgry">
						<c:forEach items="${catgries}" var="catgry">
							<c:if test="${catgry.code == postingVO.catgry}">
								<option value="${catgry.code}" selected>${catgry.name}</option>
							</c:if>
						</c:forEach>
					</select>
				</div>
				
				<label>Sub Category</label>
				<div>
					<select id="subcatgry" name="subcatgry">
						<c:forEach items="${subCatgries}" var="subCatgry">
							<c:choose>
								<c:when test="${subCatgry == postingVO.subCatgry}">
									<option value="${subCatgry}" selected>${subCatgry}</option>
								</c:when>
								<c:otherwise>
									<option value="${subCatgry}">${subCatgry}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</div>

				<label>Type</label>
				<div class="radio">
					<c:choose>
						<c:when test="${postingVO.type ==  'Offered'}">
							<input type="radio" name="type" value="Offered" checked />Offered
						</c:when>
						<c:otherwise>
							<input type="radio" name="type" value="Offered"/>Offered						
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${postingVO.type ==  'Wanted'}">
							<input type="radio" name="type" value="Wanted" checked />Wanted
						</c:when>
						<c:otherwise>
							<input type="radio" name="type" value="Wanted"/>Wanted				
						</c:otherwise>
					</c:choose>
				</div>
			</fieldset>


			<fieldset>
				<legend>Details</legend>

				<label>Title</label> 
				<input id="title" name="title" class="title" value="${postingVO.title}"/>

				<label>Description</label>
				<textarea id="desc" name="desc">${postingVO.desc}</textarea>

				<c:if test="${postingVO.catgry != 'job'}">
					<label>Price</label>
					<div>
						<div class="newPostPriceItem">
							<div id="newPostPriceCurr">
								<select id="currency" name="currency">
									<c:forEach items="${currencyVOs}" var="currencyVO">
										<c:choose>
											<c:when test="${postingVO.currency == currencyVO.name}">
												<option value="${currencyVO.code}" selected>${currencyVO.name}</option>
											</c:when>
											<c:otherwise>
												<option value="${currencyVO.code}">${currencyVO.name}</option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</select>
							</div>
						</div>
							<div class="newPostPriceItem">
								<div id="newPostPriceInput">
									<c:choose>
										<c:when test="${not empty postingVO.price}">
											<input id="price" name="price" value="${postingVO.price}"/>		
										</c:when>
										<c:otherwise>
											<input id="price" name="price" />
										</c:otherwise>
									</c:choose>
								</div>
							</div>
					</div>
				</c:if>

				<div class="clear"></div>

				<label>Email </label>
				<div>
					<div class="newPostEmailItem">
						<div id="newPostEmail">
							<c:out value="${postingVO.email}"/>
						</div>
					</div>
				</div>
			</fieldset>
			<c:if test="${postingVO.catgry != 'job'}">
				<fieldset>
					<legend>Upload pictures</legend>
	
					<c:forEach var="i" begin="1" end="${maxImages}">
						<div class="imageRow">
							<div class="imageItem">
								<c:set value="${postingVO.imageVOs[i-1]}" var="imageVO" />
								<s:url value="/resources/defimage.png" var="smallImgUrl"></s:url>
								<c:choose>
									<c:when test="${not empty imageVO}">
										<s:url value="/resources/posting-images/{postingId}/{imageId}-large.jpg" var="imgUrl">
											<s:param name="postingId" value="${postingVO.id}" />
											<s:param name="imageId" value="${imageVO.id}" />
										</s:url>
										<div>
											<img src="${imgUrl}" id="image${i}Img" width="200px"
												height="150px" />
										</div>
										<div class="radio">
											<input type="radio" name="image${i}Status" id="image${i}Keep"
												value="Keep${imageVO.id}" checked
												onclick="onKeepClicked('image${i}Browse', 'image${i}Img', '${imgUrl}');" />
											Keep
											<input type="radio" name="image${i}Status" id="image${i}Keep"
												value="Change"
												onclick="onChangeClicked('image${i}Browse', 'image${i}Img', '${smallImgUrl}');" />
											Change
											<input type="radio" name="image${i}Status" id="image${i}Delete"
												value="Delete${imageVO.id}"
												onclick="onDeleteClicked('image${i}Browse', 'image${i}Img', '${smallImgUrl}');" />
											Delete
										</div>
										<input type="file" name="image${i}" id="image${i}Browse"
											disabled="disabled" />
									</c:when>
									<c:otherwise>
										<div>
											<img src="${smallImgUrl}" width="200px" height="150px" />
										</div>
										<input type="file" name="image${i}" />
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</c:forEach>
	
				</fieldset>
			</c:if>

			<p>
				<button type="submit">Update</button>
			</p>
		</form>
	</c:if>
</div>
