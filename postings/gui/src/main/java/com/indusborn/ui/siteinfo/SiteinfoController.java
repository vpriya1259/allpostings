/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * SiteinfoController.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.ui.siteinfo;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.flash.Message;
import org.springframework.web.flash.MessageType;
import org.springframework.web.servlet.ModelAndView;

import com.indusborn.common.MessageValues;
import com.indusborn.common.ResultObject;
import com.indusborn.service.AuthService;
import com.indusborn.ui.domain.FeedbackVO;

@Controller
public class SiteinfoController {
   private AuthService authService;

   public void setAuthService(AuthService authService) {
      this.authService = authService;
   }

   @RequestMapping("/info/about")
   public ModelAndView about() {
      ModelAndView resultView = new ModelAndView("about");
      return resultView;
   }

   @RequestMapping("/info/privacy")
   public ModelAndView privacy() {
      ModelAndView resultView = new ModelAndView("privacy");
      return resultView;
   }

   @RequestMapping("/info/facebookusers")
   public ModelAndView facebookusers() {
      ModelAndView resultView = new ModelAndView("facebookusers");
      return resultView;
   }

   @RequestMapping("/info/chromeusers")
   public ModelAndView chromeusers() {
      ModelAndView resultView = new ModelAndView("chromeusers");
      return resultView;
   }

   @RequestMapping(value = "/info/feedback", method = RequestMethod.GET)
   public ModelAndView feedback(Model model) {
      ModelAndView resultView = new ModelAndView("feedback");
      model.addAttribute("feedbackForm", new FeedbackForm());
      return resultView;
   }

   @RequestMapping(value = "/info/feedback", method = RequestMethod.POST)
   public ModelAndView feedback(@Valid FeedbackForm feedbackForm,
         BindingResult formBinding, HttpServletRequest request, Model model) {
      ModelAndView resultView = new ModelAndView("feedback");
      model.addAttribute("feedbackForm", feedbackForm);
      Message message;

      //Form validation
      if (formBinding.hasErrors()) {
         return resultView;
      }

      FeedbackVO feedbackVO = getFeedbackVO(feedbackForm, request);
      
      ResultObject result = authService.addFeedback(feedbackVO);
      if (result.hasError()) {
         message = new Message(MessageType.ERROR, MessageValues.FEEDBACK_SUB_FAIL);
         model.addAttribute("message", message);
         return resultView;
      }

      message =
            new Message(MessageType.SUCCESS, MessageValues.FEEDBACK_SUB_SUCC);
      model.addAttribute("message", message);
      model.addAttribute("feedbackForm", new FeedbackForm());
      return resultView;
   }

   /**
    * @param feedbackForm
    * @param request
    * @return
    */
   public FeedbackVO getFeedbackVO(FeedbackForm feedbackForm, HttpServletRequest request) {
      FeedbackVO feedbackVO = new FeedbackVO();
      feedbackVO.setName(feedbackForm.getName());
      feedbackVO.setEmail(feedbackForm.getEmail());
      feedbackVO.setMssg(feedbackForm.getMessage());
      feedbackVO.setIpaddr(request.getRemoteAddr());
      return feedbackVO;
   }

}
