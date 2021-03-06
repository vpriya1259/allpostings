package com.indusborn.domain;

import org.hibernate.search.annotations.Indexed;

@Indexed
public class Job extends Posting {
   public Job() {

   }

   public Job(long created, long updated, String subCatgry, String type, String title, String desc,
         String price, String currency, String email, String address,
         double lat, double lng, String src, User owner) {
      super(created, updated, subCatgry, type, title, desc, price, currency, email, address,
            lat, lng, src, owner);
   }

}
