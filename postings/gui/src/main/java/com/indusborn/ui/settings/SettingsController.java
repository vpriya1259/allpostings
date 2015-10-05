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
package com.indusborn.ui.settings;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.flash.FlashMap;
import org.springframework.web.flash.Message;
import org.springframework.web.flash.MessageType;
import org.springframework.web.servlet.ModelAndView;

import com.indusborn.common.MessageValues;
import com.indusborn.ui.domain.UserVO;
import com.indusborn.util.AccountUtils;


@Controller
public class SettingsController {
   private SettingsHelper settingsHelper;

   public SettingsHelper getSettingsHelper()
   {
      return settingsHelper;
   }

   public void setSettingsHelper(SettingsHelper settingsHelper)
   {
      this.settingsHelper = settingsHelper;
   }

   @RequestMapping(value="/settings", method=RequestMethod.GET)
   public void settingsPage(UserVO userVO, Model model) {

   }

   @RequestMapping(value="/sett/upd/details/{userId}", method=RequestMethod.GET)
   public ModelAndView
   settUpdDetails(@PathVariable Long userId,
                  HttpServletRequest request,
                  Model model)
   {
      ModelAndView resultView = new ModelAndView("sett.upd.details");
      UserVO userVO = (UserVO)request.getAttribute("userVO");      
      UpdDetailsForm updDetailsForm = new UpdDetailsForm();
      Message message;

      if (userId != userVO.getId()) {
         message = new Message(MessageType.ERROR, MessageValues.UPD_DETAILS_NOT_PERMITTED);
         model.addAttribute("message", message);
         return resultView;
      }
      
      updDetailsForm.setFname(userVO.getFname());
      updDetailsForm.setLname(userVO.getLname());
      updDetailsForm.setAddress(userVO.getAddress());
      updDetailsForm.setPhone(userVO.getPhone());
      updDetailsForm.setZip(userVO.getZip());
      
      model.addAttribute("updDetailsForm", updDetailsForm);
      return resultView;
   }


   @RequestMapping(value="/sett/upd/details/{userId}", method=RequestMethod.POST)
   public ModelAndView
   settUpdDetails(@PathVariable Long userId,
                  @Valid UpdDetailsForm updDetailsForm,
                  BindingResult formBinding,
                  HttpServletRequest request,
                  Model model)
   {
      ModelAndView resultView = new ModelAndView("sett.upd.details");
      UserVO userVO = (UserVO)request.getAttribute("userVO");
      Message message;

      if (userId != userVO.getId()) {
         message = new Message(MessageType.ERROR, MessageValues.UPD_DETAILS_NOT_PERMITTED);
         model.addAttribute("message", message);
         return resultView;
      }
      
      if (formBinding.hasErrors()) {
         model.addAttribute("updDetailsForm", updDetailsForm);
         return resultView;
      }
      
      UserVO updUserVO = new UserVO();
      updUserVO.setId(userVO.getId());
      updUserVO.setFname(updDetailsForm.getFname());
      updUserVO.setLname(updDetailsForm.getLname());
      updUserVO.setEmail(userVO.getEmail());
      updUserVO.setAddress(updDetailsForm.getAddress());
      updUserVO.setPhone(updDetailsForm.getPhone());
      updUserVO.setZip(updDetailsForm.getZip());
      
      String status = settingsHelper.updDetails(updUserVO);
      
      if(status != null) {
         message = new Message(MessageType.ERROR, status);
         model.addAttribute("message", message);
         return resultView;
      }
      
      AccountUtils.signin(updUserVO);
      resultView = new ModelAndView("redirect:/sett/upd/details/"+userVO.getId());
      FlashMap.setSuccessMessage(MessageValues.UPD_DETAILS_SUCC);
      return resultView;
   }
   
   
   @RequestMapping(value="/sett/upd/passwd/{userId}", method=RequestMethod.GET)
   public ModelAndView
   settUpdPasswd(@PathVariable Long userId,
                  HttpServletRequest request,
                  Model model)
   {
      ModelAndView resultView = new ModelAndView("sett.upd.passwd");
      UserVO userVO = (UserVO)request.getAttribute("userVO");
      UpdPasswdForm updPasswdForm = new UpdPasswdForm();
      Message message;

      if (userId != userVO.getId()) {
         message = new Message(MessageType.ERROR, MessageValues.UPD_PASSWD_NOT_PERMITTED);
         model.addAttribute("message", message);
         return resultView;
      }
      
      model.addAttribute("updPasswdForm", updPasswdForm);
      return resultView;
   }
   

   @RequestMapping(value="/sett/upd/passwd/{userId}", method=RequestMethod.POST)
   public ModelAndView
   settUpdPasswd(@PathVariable Long userId,
                  @Valid UpdPasswdForm updPasswdForm,
                  BindingResult formBinding,
                  HttpServletRequest request,
                  Model model)
   {
      ModelAndView resultView = new ModelAndView("sett.upd.passwd");
      UserVO userVO = (UserVO)request.getAttribute("userVO");
      Message message;

      if (userId != userVO.getId()) {
         message = new Message(MessageType.ERROR, MessageValues.UPD_PASSWD_NOT_PERMITTED);
         model.addAttribute("message", message);
         return resultView;
      }
      
      if (formBinding.hasErrors()) {
         model.addAttribute("updPasswdForm", updPasswdForm);
         return resultView;
      }
      
      UserVO updUserVO = new UserVO();
      updUserVO.setId(userVO.getId());
      updUserVO.setPassword(updPasswdForm.getPassword());
      updUserVO.setEnabled(true);
      
      String status = settingsHelper.updPasswd(updUserVO);
      
      if(status != null) {
         message = new Message(MessageType.ERROR, status);
         model.addAttribute("message", message);
         return resultView;
      }
      
      resultView = new ModelAndView("redirect:/sett/upd/passwd/"+userVO.getId());
      FlashMap.setSuccessMessage(MessageValues.UPD_PASSWD_SUCC);
      return resultView;
   }
   
   
}
