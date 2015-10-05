package com.indusborn.ui.reset;

import com.indusborn.common.InvalidResetPasswordTokenException;
import com.indusborn.common.MessageKeys;
import com.indusborn.common.ResultObject;
import com.indusborn.common.SignInNotFoundException;
import com.indusborn.service.AuthService;

public class ResetPasswordHelper {
   private AuthService authService;


   public AuthService getAuthService() {
      return authService;
   }

   public void setAuthService(AuthService authService) {
      this.authService = authService;
   }
   
   /**
    * Sends ResetPassowrd email to the user
    * @param username
    * @throws SignInNotFoundException
    */
   public void sendResetPasswordMail(String username) throws SignInNotFoundException {
      ResultObject result = authService.sendResetPasswordMail(username);
      if(result.hasError()) {
         if (result.containsError(MessageKeys.USER_DAO_USER_DONT_EXIST)) {
            throw new SignInNotFoundException(username);
         }
      }
   }
   
   /**
    * Checks if the given token is valid
    * 
    * @param token
    * @return
    */
   public boolean isValidResetPasswordToken(String token) {
      ResultObject result = authService.getResetPasswordToken(token);
      if (result.hasError()) {
         return false;
      }
      return true;
   }
   
   /**
    * Activates user account
    * 
    * @param token
    * @throws InvalidActivateAccountTokenException
    */
   public void resetPassword(String token, String password)
         throws InvalidResetPasswordTokenException {
      ResultObject result = authService.resetPassword(token, password);
      if (result.hasError()) {
         if (result.containsError(MessageKeys.USER_DAO_NO_RESET_PASSWORD)) {
            throw new InvalidResetPasswordTokenException(token);
         }
         return;
      }
   }

}
