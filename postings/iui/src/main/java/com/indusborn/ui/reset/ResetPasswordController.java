/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indusborn.ui.reset;

import javax.validation.Valid;

import org.springframework.model.FieldModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.flash.FlashMap;

import com.indusborn.common.InvalidResetPasswordTokenException;
import com.indusborn.common.SignInNotFoundException;

@Controller
public class ResetPasswordController {

   private ResetPasswordHelper resetPasswordHelper;

   public ResetPasswordHelper getResetPasswordHelper() {
      return resetPasswordHelper;
   }

   public void setResetPasswordHelper(ResetPasswordHelper resetPasswordHelper) {
      this.resetPasswordHelper = resetPasswordHelper;
   }

   /**
    * Render the reset password form to the person as HTML in their web browser.
    */
   @RequestMapping(value = "/reset", method = RequestMethod.GET)
   public void resetPage(Model model) {
      model.addAttribute("username", new FieldModel<String>());
   }

   /**
    * Process a request by a person to reset a member password. Calls the
    * {@link ResetPasswordService} to send a reset password mail. The mail
    * message will contain a request token that must be presented to continue
    * the password reset operation.
    */
   @RequestMapping(value = "/reset", method = RequestMethod.POST)
   public String sendResetMail(@RequestParam String signin, Model model) {
      try {
         resetPasswordHelper.sendResetPasswordMail(signin);
         FlashMap
               .setInfoMessage("An email has been sent to you.  Follow its instructions to reset your password.");
         return "redirect:/reset";
      } catch (SignInNotFoundException e) {
         model.addAttribute("signin", FieldModel.error("not on file", signin));
         return null;
      }
   }

   /**
    * Render a change password form to the user once the reset token is
    * validated.
    */
   @RequestMapping(value = "/reset", method = RequestMethod.GET, params = "token")
   public String changePasswordForm(@RequestParam String token, Model model) {
      if (!resetPasswordHelper.isValidResetPasswordToken(token)) {
         return "reset/invalidToken";
      }
      model.addAttribute(new ChangePasswordForm());
      return "reset/changePassword";
   }

   /**
    * Process the change password submission and reset the user's password.
    */
   @RequestMapping(value = "/reset", method = RequestMethod.POST, params = "token")
   public String changePassword(@RequestParam String token,
         @Valid ChangePasswordForm form, BindingResult formBinding, Model model) {
      if (formBinding.hasErrors()) {
         model.addAttribute("token", token);
         return "reset/changePassword";
      }
      try {
         resetPasswordHelper.resetPassword(token, form.getPassword());
         FlashMap.setSuccessMessage("Your password has been reset");
         return "redirect:/signin";
      } catch (InvalidResetPasswordTokenException e) {
         FlashMap
               .setErrorMessage("Your reset password session has expired.  Please try again.");
         return "redirect:/reset";
      }
   }

}
