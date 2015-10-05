package com.indusborn.ui.domain;

import java.io.Serializable;

public class UserVO implements Serializable
{
   private int id;
   private String password;
   private boolean enabled;
   private String fname;
   private String lname;
   private String email;
   private String address;
   private String zip;
   private String phone;
   
   public UserVO() {
      
   }
   
   public UserVO(int id, String fname, String lname, String email) {
      this.id = id;
      this.fname = fname;
      this.lname = lname;
      this.email = email;
   }
   
   public int getId()
   {
      return id;
   }
   public void setId(int id)
   {
      this.id = id;
   }
   public String getPassword()
   {
      return password;
   }
   public void setPassword(String password)
   {
      this.password = password;
   }
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
   public String getEmail()
   {
      return email;
   }
   public void setEmail(String email)
   {
      this.email = email;
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
   public boolean isEnabled()
   {
      return enabled;
   }
   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }

}
