package com.indusborn.ui.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import com.indusborn.util.GeneralUtil;

public class PostingVO {
   public long id;
   private long created;
   private long updated;
   private String type;
   private String catgry;
   private String subCatgry;
   private String title;
   private String desc;
   private String price;
   private String currency;
   private String email;
   private String address;
   private double lat;
   private double lng;
   private int ownerId;
   private String shortTitle;
   private String shortDesc;
   private String htmlShortDesc;
   private String htmlDesc;
   private boolean realPrice = false;
   private boolean fuzzyEmail = false;
   private String htmlUpdated;
   private String htmlShortUpdated;
   private String catgryName;
   private String currencyCode;
   private RegionVO regionVO;
   public Double distance = new Double(-1);
   public String src;
   public List<ImageVO> imageVOs = new ArrayList<ImageVO>();


   public long getCreated() {
      return created;
   }

   public void setCreated(long created) {
      this.created = created;
   }

   public String getCatgry() {
      return catgry;
   }

   public void setCatgry(String catgry) {
      this.catgry = catgry;
   }

   public String getSubCatgry() {
      return subCatgry;
   }

   public void setSubCatgry(String subCatgry) {
      this.subCatgry = subCatgry;
   }

   public String getCurrency() {
      return currency;
   }

   public void setCurrency(String currency) {
      this.currency = currency;
   }

   public String getShortTitle() {
      return shortTitle;
   }

   public void setShortTitle(String shortTitle) {
      this.shortTitle = shortTitle;
   }

   public String getHtmlShortUpdated() {
      return htmlShortUpdated;
   }

   public void setHtmlShortUpdated(String htmlShortUpdated) {
      this.htmlShortUpdated = htmlShortUpdated;
   }

   public String getHtmlShortDesc() {
      return htmlShortDesc;
   }

   public void setHtmlShortDesc(String htmlShortDesc) {
      this.htmlShortDesc = htmlShortDesc;
   }

   public String getHtmlDesc() {
      return htmlDesc;
   }

   public void setHtmlDesc(String htmlDesc) {
      this.htmlDesc = htmlDesc;
   }

   public String getHtmlUpdated() {
      return htmlUpdated;
   }

   public void setHtmlUpdated(String htmlUpdated) {
      this.htmlUpdated = htmlUpdated;
   }

   public Double getDistance() {
      return distance;
   }

   public void setDistance(Double distance) {
      this.distance = distance;
   }

   public RegionVO getRegionVO() {
      return regionVO;
   }

   public void setRegionVO(RegionVO regionVO) {
      this.regionVO = regionVO;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public int getOwnerId() {
      return ownerId;
   }

   public void setOwnerId(int ownerId) {
      this.ownerId = ownerId;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
      shortTitle = GeneralUtil.getShortenedTitle(title);
   }

   public String getDesc() {
      return desc;
   }

   public void setDesc(String desc) {
      this.desc = desc;
      this.shortDesc = GeneralUtil.getShortenedDesc(desc);
      htmlDesc = GeneralUtil.getHtmlDesc(desc);
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public long getUpdated() {
      return updated;
   }

   public void setUpdated(long updated) {
      this.updated = updated;
      htmlUpdated =
            new java.text.SimpleDateFormat("MMM. d, yyyy HH:mm:ss aaa z")
                  .format(this.updated);
      htmlShortUpdated =
            new java.text.SimpleDateFormat("MMM. d, yyyy").format(this.updated);
   }

   public String getCatgryName() {
      return catgryName;
   }

   public void setCatgryName(String catgryName) {
      this.catgryName = catgryName;
   }

   @Override
   public String toString() {
      return title + " " + address + " " + catgryName;
   }

   public String getAddress() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
   }

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

   public String getPrice() {
      return price;
   }

   public void setPrice(String price) {
      this.price = price;
   }

   public boolean isRealPrice() {
      return realPrice;
   }

   public void setRealPrice(boolean realPrice) {
      this.realPrice = realPrice;
   }

   public String getCurrencyCode() {
      return currencyCode;
   }

   public void setCurrencyCode(String currencyCode) {
      this.currencyCode = currencyCode;
   }

   public boolean isFuzzyEmail() {
      return fuzzyEmail;
   }

   public void setFuzzyEmail(boolean fuzzyEmail) {
      this.fuzzyEmail = fuzzyEmail;
   }

   public String getShortDesc() {
      return shortDesc;
   }

   public void setShortDesc(String shortDesc) {
      this.shortDesc = shortDesc;
      htmlShortDesc = this.shortDesc.replaceAll("\\n", "<br/>");
   }

   public List<ImageVO> getImageVOs() {
      return imageVOs;
   }

   public void setImageVOs(List<ImageVO> imageVOs) {
      this.imageVOs = imageVOs;
   }

   public String getSrc() {
      return src;
   }

   public void setSrc(String src) {
      this.src = src;
   }
}
