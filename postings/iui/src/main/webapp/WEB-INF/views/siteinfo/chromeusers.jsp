<%@ page session="false" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="sign">
	<h3>For Google Chrome Browser users</h3>
	
	<p>We have developed a Google Chrome extension to plot the addresses on a Google Map. Click <b><a href="https://chrome.google.com/extensions/detail/bekgdjhkncajgkpolbkdhlcedpbjmmjc">here</a></b> to install it.</p>
	
	<p>Here is a simple usage after installing the extension on Chrome Browser:</p>

	<p>
		<ul>
			<li>Click on the small Google Maps icon at the end of the URL bar</li>
			<li>Click on the Plot Addresses on the map button</li>
			<li>Find all the posts addresses plotted on the Google Map</li>
		</ul>
	</p>
	<br>
	<br>
	<img src="<c:url value="/resources/chrome-usage.png"/>" width="700" height="400"/>
</div>

