
/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * GGcoder.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.geocode;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GGcoder
{
   private static Log _log = LogFactory.getLog(GGcoder.class);
   private String geoCodeURL;
   private String defaultKey;

   public String getGeoCodeURL()
   {
      return geoCodeURL;
   }

   public void setGeoCodeURL(String geoCodeURL)
   {
      this.geoCodeURL = geoCodeURL;
   }

   public String getDefaultKey()
   {
      return defaultKey;
   }

   public void setDefaultKey(String defaultKey)
   {
      this.defaultKey = defaultKey;
   }

   private GAddress geocode(String address, String key) throws Exception {
      long startTime = System.currentTimeMillis();
      URL url = new URL(geoCodeURL + "&q=" + URLEncoder.encode(address, "UTF-8")
            + "&key=" + key);
      URLConnection conn = url.openConnection();
      ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
      IOUtils.copy(conn.getInputStream(), output);
      output.close();

      GAddress gaddr = new GAddress();
      JSONObject json = JSONObject.fromObject(output.toString());
      JSONObject placemark = (JSONObject) query(json, "Placemark[0]");


      _log.debug(placemark.toString());
      String countryName = 
         query(placemark, "AddressDetails.Country.CountryName").toString();
      gaddr.setCountryName(countryName);
      
      String countryCode =
         query(placemark, "AddressDetails.Country.CountryNameCode").toString();
      gaddr.setCountryCode(countryCode);
      
      String adminAreaName = 
         query(placemark, "AddressDetails.Country.AdministrativeArea.AdministrativeAreaName").toString();
      gaddr.setAdminAreaName(adminAreaName);
      
      String subAdminAreaName =
         query(placemark, "AddressDetails.Country.AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName").toString();
      gaddr.setSubAdminAreaName(subAdminAreaName);
      
      String localityName =
         query(placemark, "AddressDetails.Country.AdministrativeArea.SubAdministrativeArea.Locality.LocalityName").toString();
      gaddr.setLocalityName(localityName);

      gaddr.setLat(Double.parseDouble(query(placemark, "Point.coordinates[1]")
            .toString()));
      gaddr.setLng(Double.parseDouble(query(placemark, "Point.coordinates[0]")
            .toString()));
      long endTime = System.currentTimeMillis();
      _log.debug("Geocoding took "+(endTime-startTime)+" mseconds for query "+"\""+address+"\""+" "+gaddr.getLat()+" "+gaddr.getLng());
      return gaddr;
   }

   public GAddress geocode(String address) {
      GAddress gAddress = null;
      try {
         gAddress = geocode(address, defaultKey);
         _log.debug("Geocoded address: \""+address+"\" Lat: \""+gAddress.getLat()+ "\" Lon: \""+gAddress.getLng());
      } catch(Exception ex) {
         _log.error("Geocoding failed!", ex);
      }
      return gAddress;
   }

   /* allow query for json nested objects, ie. Placemark[0].address */
   private Object query(JSONObject jo, String query) {
      try {
         String[] keys = query.split("\\.");
         Object r = queryHelper(jo, keys[0]);
         for (int i = 1; i < keys.length; i++) {
            r = queryHelper(jo.fromObject(r), keys[i]);
         }
         return r;
      } catch (JSONException e) {
         return "";
      }
   }

   /* help in query array objects: Placemark[0] */
   private Object queryHelper(JSONObject jo, String query) {
      int openIndex = query.indexOf('[');
      int endIndex = query.indexOf(']');
      if (openIndex > 0) {
         String key = query.substring(0, openIndex);
         int index = Integer.parseInt(query.substring(openIndex + 1, endIndex));
         return jo.getJSONArray(key).get(index);
      }
      return jo.get(query);
   }

}
