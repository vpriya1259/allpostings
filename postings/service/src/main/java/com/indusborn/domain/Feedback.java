
/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * Feedback.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.domain;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class Feedback implements Serializable
{
   private long id;
   private long created;
   private String ipaddr;
   private String name;
   private String email;
   private String mssg;
   public long getCreated() {
      return created;
   }
   public void setCreated(long created) {
      this.created = created;
   }
   public long getId()
   {
      return id;
   }
   public void setId(long id)
   {
      this.id = id;
   }
   public String getIpaddr()
   {
      return ipaddr;
   }
   public void setIpaddr(String ipaddr)
   {
      this.ipaddr = ipaddr;
   }
   public String getName()
   {
      return name;
   }
   public void setName(String name)
   {
      this.name = name;
   }
   public String getEmail()
   {
      return email;
   }
   public void setEmail(String email)
   {
      this.email = email;
   }
   public String getMssg()
   {
      return mssg;
   }
   public void setMssg(String mssg)
   {
      this.mssg = mssg;
   }
}
