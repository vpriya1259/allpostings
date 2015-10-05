
/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * FeedbackVO.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.ui.domain;
public class FeedbackVO
{
   public String name;
   public String email;
   public String mssg;
   public String ipaddr;
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
