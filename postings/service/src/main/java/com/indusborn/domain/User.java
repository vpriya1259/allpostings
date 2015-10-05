package com.indusborn.domain;

import java.io.Serializable;

public class User implements Serializable {
   private int id;
   private long created;
   private long updated;
   private String email;
   private String password;
   private boolean enabled;
   private String fname;
   private String lname;
   private String address;
   private String zip;
   private String phone;

   public User() {
      
   }
   
   public User(long created, long updated, String email, String password, boolean enabled, String fname, String lname, String address, String zip, String phone) {
      this.created = created;
      this.updated = updated;
      this.email = email;
      this.password = password;
      this.enabled = enabled;
      this.fname = fname;
      this.lname = lname;
      this.address = address;
      this.zip = zip;
      this.phone = phone;
   }
   
   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getFname() {
      return fname;
   }

   public void setFname(String fname) {
      this.fname = fname;
   }

   public String getLname() {
      return lname;
   }

   public void setLname(String lname) {
      this.lname = lname;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getAddress() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public String getZip() {
      return zip;
   }

   public void setZip(String zip) {
      this.zip = zip;
   }

   public String getPhone() {
      return phone;
   }

   public void setPhone(String phone) {
      this.phone = phone;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
   public long getCreated() {
      return created;
   }

   public void setCreated(long created) {
      this.created = created;
   }

   public long getUpdated() {
      return updated;
   }

   public void setUpdated(long updated) {
      this.updated = updated;
   }
}
