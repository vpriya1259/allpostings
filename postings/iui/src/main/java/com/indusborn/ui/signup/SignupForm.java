
/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * SignupForm.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.ui.signup;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.indusborn.ui.validators.Confirm;


@Confirm(field="password", message="could not confirm")
public class SignupForm
{
   @Size(min=5, message="must be at least 5 characters")
   private String password;
   
   private String confirmPassword;
   
   @NotEmpty
   private String firstName;

   @NotEmpty
   private String lastName;

   @NotEmpty
   @Email
   private String email;


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

   public String getFirstName()
   {
      return firstName;
   }

   public void setFirstName(String firstName)
   {
      this.firstName = firstName;
   }

   public String getLastName()
   {
      return lastName;
   }

   public void setLastName(String lastName)
   {
      this.lastName = lastName;
   }

   public String getEmail()
   {
      return email;
   }

   public void setEmail(String email)
   {
      this.email = email;
   }
}
