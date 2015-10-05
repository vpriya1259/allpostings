package com.indusborn.ui.domain;

public class RegionVO {
   public int id;
   private String name;
   private String countryName;
   private String countryCode;
   private String adminAreaName;
   private String subAdminAreaName;
   private String localityName;
   private double lat = Double.MAX_VALUE;
   private double lng = Double.MAX_VALUE;

   public double getLat() {
      return lat;
   }

   public void setLat(double lat) {
      this.lat = lat;
   }

   public double getLng() {
      return lng;
   }

   public void setLng(double lng) {
      this.lng = lng;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getCountryName() {
      return countryName;
   }

   public void setCountryName(String countryName) {
      this.countryName = countryName;
   }

   public String getCountryCode() {
      return countryCode;
   }

   public void setCountryCode(String countryCode) {
      this.countryCode = countryCode;
   }

   public String getAdminAreaName() {
      return adminAreaName;
   }

   public void setAdminAreaName(String adminAreaName) {
      this.adminAreaName = adminAreaName;
   }

   public String getSubAdminAreaName() {
      return subAdminAreaName;
   }

   public void setSubAdminAreaName(String subAdminAreaName) {
      this.subAdminAreaName = subAdminAreaName;
   }

   @Override
   public String toString() {
      // TODO Auto-generated method stub
      return localityName + ", " + adminAreaName;
   }

   public String getLocalityName() {
      return localityName;
   }

   public void setLocalityName(String locality) {
      this.localityName = locality;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
