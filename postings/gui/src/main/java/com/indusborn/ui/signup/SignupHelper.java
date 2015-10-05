package com.indusborn.ui.signup;

import org.springframework.validation.BindingResult;

import com.indusborn.common.InvalidActivateAccountTokenException;
import com.indusborn.common.MessageKeys;
import com.indusborn.common.ResultObject;
import com.indusborn.service.AuthService;
import com.indusborn.ui.domain.UserVO;
import com.indusborn.util.AccountUtils;

public class SignupHelper {
   private AuthService authService;

   public AuthService getAuthService() {
      return authService;
   }

   public void setAuthService(AuthService authService) {
      this.authService = authService;
   }

   private UserVO prepareUserVO(SignupForm signupForm) {
      UserVO userVO = new UserVO();
      userVO.setFname(signupForm.getFirstName());
      userVO.setLname(signupForm.getLastName());
      userVO.setPassword(signupForm.getPassword());
      userVO.setEmail(signupForm.getEmail());
      return userVO;
   }

   public boolean signup(SignupForm signupForm, BindingResult formBinding) {
      UserVO userVO = prepareUserVO(signupForm);
      ResultObject result = authService.registerUser(userVO);

      if (result.hasError()) {
         if (result.containsError(MessageKeys.AUTH_SERVICE_REG_DUP_EMAIL)) {
            formBinding.rejectValue("email", "email.duplicateEmail",
                  "Email already on file");
         }
         return false;
      }
      return true;
   }

   /**
    * Checks if the given token is valid
    * 
    * @param token
    * @return
    */
   public boolean isValidActivateAccountToken(String token) {
      ResultObject result = authService.getActivateAccountToken(token);
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
   public void activateAccount(String token)
         throws InvalidActivateAccountTokenException {
      ResultObject result = authService.activateUser(token);
      if (result.hasError()) {
         if (result.containsError(MessageKeys.USER_DAO_NO_ACTIVATE_ACCOUNT)) {
            throw new InvalidActivateAccountTokenException(token);
         }
         return;
      }
      UserVO userVO = (UserVO) result.getData();
      AccountUtils.signin(userVO);
   }


}
