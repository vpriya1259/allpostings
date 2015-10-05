
/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * MailVO.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.ui.domain;
public class MailVO
{
   private String anonymName;
   private String anonymAddress;
   private String toAddress;
   private String subject;
   private String messageBody;
   private String phoneNum;
   public String getPhoneNum()
   {
      return phoneNum;
   }
   public void setPhoneNum(String phoneNum)
   {
      this.phoneNum = phoneNum;
   }
   public String getAnonymName()
   {
      return anonymName;
   }
   public void setAnonymName(String anonymName)
   {
      this.anonymName = anonymName;
   }
   public String getAnonymAddress()
   {
      return anonymAddress;
   }
   public void setAnonymAddress(String anonymAddress)
   {
      this.anonymAddress = anonymAddress;
   }
   public String getToAddress()
   {
      return toAddress;
   }
   public void setToAddress(String toAddress)
   {
      this.toAddress = toAddress;
   }
   public String getSubject()
   {
      return subject;
   }
   public void setSubject(String subject)
   {
      this.subject = subject;
   }
   public String getMessageBody()
   {
      return messageBody;
   }
   public void setMessageBody(String messageBody)
   {
      this.messageBody = messageBody;
   }
}
