
/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * UpdDetailsForm.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.ui.settings;

import org.hibernate.validator.constraints.NotEmpty;

public class UpdDetailsForm
{
   @NotEmpty
   private String fname;
   
   @NotEmpty
   private String lname;
   
   private String address;
   
   private String zip;
   
   private String phone;
   
   public String getFname()
   {
      return fname;
   }
   public void setFname(String fname)
   {
      this.fname = fname;
   }
   public String getLname()
   {
      return lname;
   }
   public void setLname(String lname)
   {
      this.lname = lname;
   }
   public String getAddress()
   {
      return address;
   }
   public void setAddress(String address)
   {
      this.address = address;
   }
   public String getZip()
   {
      return zip;
   }
   public void setZip(String zip)
   {
      this.zip = zip;
   }
   public String getPhone()
   {
      return phone;
   }
   public void setPhone(String phone)
   {
      this.phone = phone;
   }
}
