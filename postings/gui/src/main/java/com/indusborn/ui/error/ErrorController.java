
package com.indusborn.ui.error;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.indusborn.ui.domain.UserVO;


@Controller
public class ErrorController
{
   private static Log _log = LogFactory.getLog(ErrorController.class);   

   @RequestMapping("/error/404")
   public ModelAndView 
   error404(HttpServletRequest request)
   {
      ModelAndView resultView = new ModelAndView("error.404");
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if(auth != null && auth.getPrincipal() instanceof UserVO) {
         request.setAttribute("userVO", auth.getPrincipal());
      }
      return resultView;
   }
   
   @RequestMapping("/error/500")
   public ModelAndView 
   error500(HttpServletRequest request)
   {
      ModelAndView resultView = new ModelAndView("error.500");
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if(auth != null && auth.getPrincipal() instanceof UserVO) {
         request.setAttribute("userVO", auth.getPrincipal());
      }
      return resultView;
   }
   
}
