/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * SettingsHelper.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.ui.settings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.indusborn.common.MessageKeys;
import com.indusborn.common.MessageValues;
import com.indusborn.common.ResultObject;
import com.indusborn.service.AuthService;
import com.indusborn.ui.domain.UserVO;


public class SettingsHelper {
   private static Log log = LogFactory.getLog(SettingsHelper.class);
   private AuthService authService;

   public AuthService getAuthService() {
      return authService;
   }

   public void setAuthService(AuthService authService) {
      this.authService = authService;
   }


   public String updDetails(UserVO userVO) {
      ResultObject result = authService.updateDetails(userVO);

      if (result.hasError()) {
         if (result
               .containsError(MessageKeys.AUTH_SERVICE_UPD_DETAILS_DUP_EMAIL)) {
            return MessageValues.UPD_DETAILS_DUP_EMAIL;
         } else {
            return MessageValues.APP_SERVER_ERROR;
         }
      }
      return null;
   }

   public String updPasswd(UserVO userVO) {
      ResultObject result = authService.updatePassword(userVO);

      if (result.hasError()) {
         if (result
               .containsError(MessageKeys.AUTH_SERVICE_UPD_DETAILS_DUP_EMAIL)) {
            return MessageValues.UPD_DETAILS_DUP_EMAIL;
         } else {
            return MessageValues.APP_SERVER_ERROR;
         }
      }
      return null;
   }


}
