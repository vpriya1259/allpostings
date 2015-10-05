package com.indusborn.ui.signup;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.flash.FlashMap;
import org.springframework.web.servlet.ModelAndView;

import com.indusborn.common.InvalidActivateAccountTokenException;

@Controller
public class SignupController {
   private SignupHelper signupHelper;

   public SignupHelper getSignupHelper() {
      return signupHelper;
   }

   public void setSignupHelper(SignupHelper signupHelper) {
      this.signupHelper = signupHelper;
   }

   @RequestMapping("/signin")
   public void signin() {
   }

   @RequestMapping(value = "/signup", method = RequestMethod.GET)
   public SignupForm signupForm() {
      return new SignupForm();
   }

   @RequestMapping(value = "/signup", method = RequestMethod.POST)
   public ModelAndView signup(@Valid SignupForm signupForm,
         BindingResult formBinding) {
      if (formBinding.hasErrors()) {
         return null;
      }
      boolean status = signupHelper.signup(signupForm, formBinding);
      //TODO: if signup has problem, redirect him to a different form
      return status ? new ModelAndView("signup.instruct") : null;
   }


   @RequestMapping(value = "/signup/activate", method = RequestMethod.GET, params = "token")
   public ModelAndView activateAccount(@RequestParam String token) {
      ModelAndView resultView = new ModelAndView("signup.invalidToken");
      if (!signupHelper.isValidActivateAccountToken(token)) {
         return resultView;
      }

      try {
         signupHelper.activateAccount(token);
         resultView = new ModelAndView("redirect:/");
         return resultView;
      } catch (InvalidActivateAccountTokenException ex) {
         FlashMap
               .setErrorMessage("Your activate account session expired.  Please try again.");
         resultView = new ModelAndView("redirect:/signup");
         return resultView;
      } 

   }

}
