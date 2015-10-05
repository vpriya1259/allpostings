package com.shoora.craigslist.dataobject;

import java.util.ArrayList;
import java.util.List;


public class LeafPage {
   private String srcUrl;
   private String price;
   private String currency;
   private long created;
   private String title;
   private String desc;
   private String address;
   private String catgry;
   private String subCatgry;
   private String email;
   List<CImage> images = new ArrayList<CImage>();

   public String getEmail() {
      return email;
   }
   public void setEmail(String email) {
      this.email = email;
   }
   public String getCurrency() {
      return currency;
   }
   public void setCurrency(String currency) {
      this.currency = currency;
   }
   public String getSrcUrl() {
      return srcUrl;
   }
   public void setSrcUrl(String srcUrl) {
      this.srcUrl = srcUrl;
   }
   public String getSubCatgry() {
      return subCatgry;
   }
   public void setSubCatgry(String subCatgry) {
      this.subCatgry = subCatgry;
   }
   public String getCatgry() {
      return catgry;
   }
   public void setCatgry(String catgry) {
      this.catgry = catgry;
   }
   public String getAddress() {
      return address;
   }
   public void setAddress(String address) {
      this.address = address;
   }
   public String getTitle() {
      return title;
   }
   public void setTitle(String title) {
      this.title = title;
   }
   public String getDesc() {
      return desc;
   }
   public void setDesc(String desc) {
      this.desc = desc;
   }
   public long getCreated() {
      return created;
   }
   public void setCreated(long created) {
      this.created = created;
   }
   public List<CImage> getImages() {
      return images;
   }
   public void setImages(List<CImage> images) {
      this.images = images;
   }
   public String getPrice() {
      return price;
   }
   public void setPrice(String price) {
      this.price = price;
   }
   
   @Override
   public String toString() {
      return srcUrl;
   }
}
