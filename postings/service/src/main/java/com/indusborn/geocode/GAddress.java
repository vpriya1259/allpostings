
/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * GAddress.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.geocode;
public class GAddress
{
   private String countryName;
   private String countryCode;
   private String adminAreaName;
   private String subAdminAreaName;
   private String localityName;
   private double lat;
   private double lng;
   
   public String getCountryName()
   {
      return countryName;
   }
   public void setCountryName(String countryName)
   {
      this.countryName = countryName;
   }
   public String getCountryCode()
   {
      return countryCode;
   }
   public void setCountryCode(String countryCode)
   {
      this.countryCode = countryCode;
   }
   public String getSubAdminAreaName()
   {
      return subAdminAreaName;
   }
   public void setSubAdminAreaName(String subAdminAreaName)
   {
      this.subAdminAreaName = subAdminAreaName;
   }
   public String getLocalityName()
   {
      return localityName;
   }
   public void setLocalityName(String locality)
   {
      this.localityName = locality;
   }
   public String getAdminAreaName()
   {
      return adminAreaName;
   }
   public void setAdminAreaName(String state)
   {
      this.adminAreaName = state;
   }
   public double getLat()
   {
      return lat;
   }
   public void setLat(double lat)
   {
      this.lat = lat;
   }
   public double getLng()
   {
      return lng;
   }
   public void setLng(double lng)
   {
      this.lng = lng;
   }
   
   @Override
   public String toString()
   {
      StringBuffer desc = new StringBuffer();
      
      if(countryName != null && countryName.length() > 0) {
         desc.append("countryName: "+countryName+ " ");
      }      
      if(adminAreaName != null && adminAreaName.length() > 0) {
         desc.append("adminAreaName: "+adminAreaName+ " ");
      }
      if(subAdminAreaName != null && subAdminAreaName.length() > 0) {
         desc.append("subAdminAreaName: "+subAdminAreaName+ " ");
      }
      if(localityName != null && localityName.length() > 0) {
         desc.append("locality: "+localityName+ " ");
      }
      
      return desc.toString();
   }
}
