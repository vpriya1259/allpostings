package com.indusborn.domain;

import java.io.Serializable;

public class ResetPassword implements Serializable{
   private String token;
   private long created;
   private User owner;
   public String getToken() {
      return token;
   }
   public void setToken(String token) {
      this.token = token;
   }
   public long getCreated() {
      return created;
   }
   public void setCreated(long created) {
      this.created = created;
   }
   public User getOwner() {
      return owner;
   }
   public void setOwner(User owner) {
      this.owner = owner;
   }
}
