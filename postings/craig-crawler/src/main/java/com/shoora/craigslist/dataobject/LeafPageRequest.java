package com.shoora.craigslist.dataobject;

public class LeafPageRequest {
   private final String url;
   private final String catgry;
   private final String subCatgry;
   private final boolean pic;
   
   public LeafPageRequest(String url, String catgry, String subCatgry, boolean pic) {
      this.url = url;
      this.catgry = catgry;
      this.subCatgry = subCatgry;
      this.pic = pic;
   }

   public boolean hasPic() {
      return pic;
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
