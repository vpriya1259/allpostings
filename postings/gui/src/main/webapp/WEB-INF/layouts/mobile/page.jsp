<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>

<html>
<head>
	<title><tiles:insertAttribute name="title" defaultValue="local classifieds" /></title>
    <meta name="keywords" content="local classifieds nearby classified rentals rental rommates rommate cars car jobs job buy sell single bed room double three 1br 2br 1bath 2bath 1.5bath 2.5bath 3bath condo town house acura audi bmw antiques accessories books cameras computers furniture stuff sports electronics ipods adobe flex photoshop .net C# business analyst hibernate orm informatica iphoe android apps java j2ee mysql networking oracle dba qa salesforce sap sas sharepoint spring related system administration terradata dba tomcat admin buick cadillac chevrolet chrysler dodge ford honda hummer hyundai infinity jaguar jeep kia land rover lexus lincoln mazda mercedes mitsubishi nissan pontiac porche saturn subaru suzuki toyota volkswagen volvo" />
	<meta http-equiv="content-type" content="text/html;charset=utf-8" />
	<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;" />	    
	<link rel="stylesheet" href="<c:url value="/resources/mobile/page.css" />" type="text/css" media="screen" />	
	<script type="text/javascript" src="<c:url value="/resources/jquery/1.6/jquery.js" />"></script>
	<script type="text/javascript" src="<c:url value="/resources/jquery-cookie/1.0/jquery-cookie.js" />"></script>
	<script type="text/javascript">
		window.scrollTo(0, 1);
	</script>
	
	
	<script type="text/javascript">
	
	  var _gaq = _gaq || [];
	  _gaq.push(['_setAccount', 'UA-26847732-1']);
	  _gaq.push(['_trackPageview']);
	
	  (function() {
	    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	  })();
	
	</script>
	
	<script type="text/javascript" src="http://w.sharethis.com/button/buttons.js"></script><script type="text/javascript">stLight.options({publisher:'8424221d-8a82-494e-b7ca-63d7034407b7'});</script>
	
	<link href="http://code.google.com/apis/maps/documentation/javascript/examples/standard.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
	<script type="text/javascript">
	  var geocoder;
	  var map;
	  function initialize() {
	    geocoder = new google.maps.Geocoder();
	    var latlng = new google.maps.LatLng(-34.397, 150.644);
	    map = new google.maps.Map(document.getElementById("map_canvas"));
	  }
	</script>	
</head>
<body onload="initialize()">
                             	    
  	<div id="header">
		<tiles:insertAttribute name="header" />
	</div>
	<div id="content">
		<tiles:insertAttribute name="content" />
	</div>
  	<div id="footer">
		<tiles:insertAttribute name="footer" />
	</div>
	<div id="map_canvas"></div>	
</body>
</html>
