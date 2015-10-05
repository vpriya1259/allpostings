package com.shoora.craigslist.dataobject;

public class SrcPageRequest {
   private String url;
   private String catgry;
   private String subCatgry;
   public SrcPageRequest(String url, String catgry, String subCatgry) {
      this.url = url;
      this.catgry = catgry;
      this.subCatgry = subCatgry;
   }
   public String getUrl() {
      return url;
   }
   public String getCatgry() {
      return catgry;
   }
   
   public String getSubCatgry() {
      return subCatgry;
   }
}
