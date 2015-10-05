
/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * SendMailForm.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.ui.mail;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class SendMailForm
{
   @NotEmpty
   private String name;
   
   @NotEmpty
   @Email
   private String email;
   
   private String phoneNumber;
   
   @NotEmpty
   private String message;
   
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
   public String getPhoneNumber()
   {
      return phoneNumber;
   }
   public void setPhoneNumber(String phoneNumber)
   {
      this.phoneNumber = phoneNumber;
   }
   public String getMessage()
   {
      return message;
   }
   public void setMessage(String message)
   {
      this.message = message;
   }

}
