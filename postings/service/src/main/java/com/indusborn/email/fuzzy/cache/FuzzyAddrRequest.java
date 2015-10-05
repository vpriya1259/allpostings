/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * FuzzyAddrRequest.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.email.fuzzy.cache;

public class FuzzyAddrRequest {
   public enum Type {
      CREATE, PURGE, CLEAN
   }
   private Type type;
   private int userId;
   private String email;
   private String fuzzyEmail;

   public String getFuzzyEmail() {
      return fuzzyEmail;
   }

   public void setFuzzyEmail(String fuzzyEmail) {
      this.fuzzyEmail = fuzzyEmail;
   }

   public int getUserId() {
      return userId;
   }

   public void setUserId(int userId) {
      this.userId = userId;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   @Override
   public String toString() {
      return String.format("FuzzyAddrReq: %d -> %s ", userId, email);
   }

   public Type getType() {
      return type;
   }

   public void setType(Type type) {
      this.type = type;
   }
}
