package com.indusborn.ui.domain;

import java.util.List;

public final class SubCatgries {
   private List<String> catgries;
   
   public List<String> getCatgries() {
      return catgries;
   }

   public SubCatgries(List<String> catgries) {
      this.catgries = catgries;
   }
}
