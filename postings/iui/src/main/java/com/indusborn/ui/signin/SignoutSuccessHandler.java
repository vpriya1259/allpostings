/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * SignoutSuccessHandler.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.ui.signin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.indusborn.repository.MailDao;
import com.indusborn.ui.domain.UserVO;


public class SignoutSuccessHandler implements LogoutSuccessHandler {
   private static Log _log = LogFactory.getLog(SignoutSuccessHandler.class);
   private MailDao mailDao;

   public MailDao getMailDao() {
      return mailDao;
   }

   public void setMailDao(MailDao mailDao) {
      this.mailDao = mailDao;
   }

   public void onLogoutSuccess(HttpServletRequest request,
         HttpServletResponse response, Authentication auth) throws IOException,
         ServletException {
      if (auth != null && auth.getPrincipal() instanceof UserVO) {
         UserVO userVO = (UserVO) auth.getPrincipal();
         _log.info("User " + userVO.getId() + " logged out!");
         try {
            mailDao.cleanFuzzyAddrs(userVO.getId());
         } catch (Exception ex) {
            _log.error(String.format(
                  "Cleaning up the fuzzy addresses for user %d failed!",
                  userVO.getId()), ex);
         }
      }
      response.sendRedirect(request.getSession().getServletContext()
            .getContextPath());
   }


}
