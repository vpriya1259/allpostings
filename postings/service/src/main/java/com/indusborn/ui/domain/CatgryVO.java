package com.indusborn.ui.domain;

public class CatgryVO {
   private String code;
   private String name;
   public CatgryVO(String code, String name) {
      this.code = code;
      this.name = name;
   }
   public String getCode() {
      return code;
   }
   public void setCode(String code) {
      this.code = code;
   }
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   
}
