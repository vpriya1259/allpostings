
/* ******************************************************************************
 * Copyright (C) 2009 VMware, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * Catgry.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.domain;

import java.io.Serializable;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Indexed
public class Region implements Serializable
{
   @DocumentId
   @Field(index = Index.UN_TOKENIZED, store = Store.YES)   
   private int id;
   
   @Field(index=Index.TOKENIZED, store=Store.NO)   
   private String countryName;
   
   @Field(index=Index.TOKENIZED, store=Store.NO)   
   private String countryCode;
   
   @Field(index=Index.TOKENIZED, store=Store.NO)   
   private String adminAreaName;
   
   @Field(index=Index.TOKENIZED, store=Store.NO)   
   private String subAdminAreaName;
   
   @Field(index=Index.TOKENIZED, store=Store.NO)   
   private String localityName;
   
   public int getId()
   {
      return id;
   }
   public void setId(int id)
   {
      this.id = id;
   }
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
   public String getAdminAreaName()
   {
      return adminAreaName;
   }
   public void setAdminAreaName(String adminAreaName)
   {
      this.adminAreaName = adminAreaName;
   }
   public String getSubAdminAreaName()
   {
      return subAdminAreaName;
   }
   public void setSubAdminAreaName(String subAdminAreaName)
   {
      this.subAdminAreaName = subAdminAreaName;
   }
   @Override
   public String toString()
   {
      // TODO Auto-generated method stub
      return localityName + ", " + adminAreaName;
   }
   public String getLocalityName()
   {
      return localityName;
   }
   public void setLocalityName(String locality)
   {
      this.localityName = locality;
   }

}
