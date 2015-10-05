
/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * UpdPasswdForm.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.ui.settings;

import javax.validation.constraints.Size;

import com.indusborn.ui.validators.Confirm;


@Confirm(field="password")
public class UpdPasswdForm
{
   @Size(min=5, message="must be at least 5 characters")
   private String password;
   
   private String confirmPassword;

   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   public String getConfirmPassword()
   {
      return confirmPassword;
   }

   public void setConfirmPassword(String confirmPassword)
   {
      this.confirmPassword = confirmPassword;
   }
   
   
}
