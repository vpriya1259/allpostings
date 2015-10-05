package com.indusborn.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.encrypt.SecureRandomStringKeyGenerator;
import org.springframework.security.encrypt.StringKeyGenerator;

import com.indusborn.common.AppServerException;
import com.indusborn.common.ErrorObject;
import com.indusborn.common.MessageKeys;
import com.indusborn.common.ResultObject;
import com.indusborn.domain.ActivateAccount;
import com.indusborn.domain.Feedback;
import com.indusborn.domain.ResetPassword;
import com.indusborn.domain.User;
import com.indusborn.repository.MailDao;
import com.indusborn.repository.UserDao;
import com.indusborn.ui.domain.FeedbackVO;
import com.indusborn.ui.domain.UserVO;
import com.indusborn.util.TimeUtil;

public class AuthService implements AuthenticationProvider {
   private static Log log = LogFactory.getLog(AuthService.class);
   private final StringKeyGenerator tokenGenerator =
         new SecureRandomStringKeyGenerator();
   private UserDao userDao;
   private MailDao mailDao;

   public MailDao getMailDao() {
      return mailDao;
   }

   public void setMailDao(MailDao mailDao) {
      this.mailDao = mailDao;
   }

   public UserDao getUserDao() {
      return userDao;
   }

   public void setUserDao(UserDao userDao) {
      this.userDao = userDao;
   }

   public Authentication authenticate(Authentication authentication)
         throws AuthenticationException {
      UsernamePasswordAuthenticationToken token =
            (UsernamePasswordAuthenticationToken) authentication;
      try {
         User user =
               userDao.authenticateUser(token.getName(),
                     (String) token.getCredentials());
         UserVO userVO = new UserVO();
         userVO.setId(user.getId());
         userVO.setFname(user.getFname());
         userVO.setLname(user.getLname());
         userVO.setPassword(user.getPassword());
         userVO.setEmail(user.getEmail());
         userVO.setPhone(user.getPhone());
         userVO.setAddress(user.getAddress());
         userVO.setZip(user.getZip());
         mailDao.createFuzzyAddrs(userVO.getId(), userVO.getEmail());
         return authenticatedToken(userVO, authentication);
      } catch (AppServerException ex) {
         if (ex.getMessageObject().getMessageKey()
               .equals(MessageKeys.USER_DAO_USER_DONT_EXIST)) {
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException(
                  token.getName(), ex);
         } else {
            throw new BadCredentialsException("Wrong password!", ex);
         }
      }
   }

   public boolean supports(Class<? extends Object> authentication) {
      return UsernamePasswordAuthenticationToken.class.equals(authentication);
   }

   private Authentication authenticatedToken(UserVO userVO,
         Authentication original) {
      List<GrantedAuthority> authorities = null;
      UsernamePasswordAuthenticationToken authenticated =
            new UsernamePasswordAuthenticationToken(userVO, null, authorities);
      authenticated.setDetails(original.getDetails());
      return authenticated;
   }

   /**
    * Registers an user and sends an email to the user email address
    * 
    * @param userVO
    * @return
    */
   public ResultObject registerUser(UserVO userVO) {
      ResultObject result = new ResultObject();
      try {
         User user = userDao.getUser(userVO.getEmail());
         if (user != null) {
            log.error("Duplicate " + userVO.getEmail());
            result.addError(new ErrorObject(
                  MessageKeys.AUTH_SERVICE_REG_DUP_EMAIL));
            return result;
         }

         user = new User();
         user.setFname(userVO.getFname());
         user.setLname(userVO.getLname());
         user.setCreated(TimeUtil.getCurrTimeInUTC());
         user.setUpdated(TimeUtil.getCurrTimeInUTC());
         user.setEmail(userVO.getEmail().trim());
         user.setPassword(userVO.getPassword());
         user.setEnabled(userVO.isEnabled());
         user.setAddress(userVO.getAddress());
         user.setZip(userVO.getZip());
         user.setPhone(userVO.getPhone());
         String activateAcctToken = tokenGenerator.generateKey();
         userDao.registerUser(activateAcctToken, user);
         mailDao.sendActivateAcctMail(
               userVO.getFname() + " " + userVO.getLname(), userVO.getEmail(),
               activateAcctToken);
         result.setData(prepareUserVO(userDao.getUser(user.getEmail())));
      } catch (Exception ex) {
         log.error(
               String.format("Registering user %s failed!", userVO.getFname()
                     + " " + userVO.getLname()), ex);
         result.addError(new AppServerException().getErrorObject());
      }
      return result;
   }

   private UserVO prepareUserVO(User user) {
      UserVO userVO = new UserVO();
      userVO.setId(user.getId());
      userVO.setFname(user.getFname());
      userVO.setLname(user.getLname());
      userVO.setPassword(user.getPassword());
      userVO.setEmail(user.getEmail());
      userVO.setPhone(user.getPhone());
      userVO.setAddress(user.getAddress());
      userVO.setZip(user.getZip());
      return userVO;
   }

   /**
    * Gets activate account for given token
    * 
    * @param token
    * @return
    */
   public ResultObject getActivateAccountToken(String token) {
      ResultObject result = new ResultObject();
      try {
         ActivateAccount activateAccount = userDao.getActivateAccount(token);
         if (activateAccount == null) {
            result.addError(new ErrorObject(
                  MessageKeys.USER_DAO_NO_ACTIVATE_ACCOUNT));
            return result;
         }
         result.setData(activateAccount);
      } catch (Exception ex) {
         log.error(String.format(
               "Getting ActivateAccount for the token %s failed!", token), ex);
         result.addError(new AppServerException().getErrorObject());
      }
      return result;
   }

   /**
    * Sets the user's enable flag to true and deletes the saved token from the
    * activate account table
    * 
    * @param token
    * @return
    */
   public ResultObject activateUser(String token) {
      ResultObject result = new ResultObject();
      try {
         User user = userDao.activateUser(token);
         UserVO userVO = prepareUserVO(user);
         result.setData(userVO);
      } catch (AppServerException ex) {
         result.addError(ex.getErrorObject());
      } catch (Exception ex) {
         log.error(String.format(
               "Getting ActivateAccount for the token %s failed!", token), ex);
         result.addError(new AppServerException().getErrorObject());
      }
      return result;
   }

   /**
    * Sends activate account mail to the user
    * 
    * @param userVO
    * @return
    */
   public ResultObject sendResetPasswordMail(String username) {
      ResultObject result = new ResultObject();
      try {
         String token = tokenGenerator.generateKey();
         User user = userDao.getUser(username);
         if (user == null) {
            throw new AppServerException(MessageKeys.USER_DAO_USER_DONT_EXIST);
         }
         userDao.addResetPassword(token, user);
         mailDao.sendResetPasswordMail(user.getFname() + " " + user.getLname(),
               user.getEmail(), token);
         result.setData("Successful!");
      } catch (AppServerException ex) {
         result.addError(ex.getErrorObject());
      } catch (Exception ex) {
         log.error(String.format(
               "Sending reset password mail for user %s failed!", username), ex);
         result.addError(new AppServerException().getErrorObject());
      }
      return result;
   }

   /**
    * Gets activate account for given token
    * 
    * @param token
    * @return
    */
   public ResultObject getResetPasswordToken(String token) {
      ResultObject result = new ResultObject();
      try {
         ResetPassword resetPassword = userDao.getResetPassword(token);
         if (resetPassword == null) {
            throw new AppServerException(MessageKeys.USER_DAO_NO_RESET_PASSWORD);
         }
         result.setData(resetPassword);
      } catch (AppServerException ex) {
         result.addError(ex.getErrorObject());
      } catch (Exception ex) {
         log.error(String.format(
               "Getting ResetPassword for the token %s failed!", token), ex);
         result.addError(new AppServerException().getErrorObject());
      }
      return result;
   }

   /**
    * Sets the user's enable flag to true and deletes the saved token from the
    * activate account table
    * 
    * @param token
    * @return
    */
   public ResultObject resetPassword(String token, String password) {
      ResultObject result = new ResultObject();
      try {
         User user = userDao.resetPassword(token, password);
         UserVO userVO = prepareUserVO(user);
         result.setData(userVO);
      } catch (AppServerException ex) {
         result.addError(ex.getErrorObject());
      } catch (Exception ex) {
         log.error(String.format("Resetting password for the token %s failed!",
               token), ex);
         result.addError(new AppServerException().getErrorObject());
      }
      return result;
   }

   /**
    * Updating user details failed
    * 
    * @param userVO
    * @return
    */
   public ResultObject updateDetails(UserVO userVO) {
      ResultObject result = new ResultObject();
      try {
         User existingUser = userDao.getUser(userVO.getId());
         existingUser.setFname(userVO.getFname());
         existingUser.setLname(userVO.getLname());
         existingUser.setAddress(userVO.getAddress());
         existingUser.setZip(userVO.getZip());
         existingUser.setPhone(userVO.getPhone());
         existingUser.setUpdated(TimeUtil.getCurrTimeInUTC());
         
         userDao.updateDetails(existingUser);
         mailDao.sendUpdateDetailsMail(existingUser);
         result.setData("Successful");
      } catch (AppServerException ex) {
         result.addError(ex.getErrorObject());
      } catch (Exception ex) {
         log.error(
               String.format("Updating user %s details failed!",
                     userVO.getEmail()), ex);
         result.addError(new AppServerException().getErrorObject());
      }
      return result;
   }

   /**
    * Updates user's password
    * 
    * @param userVO
    * @return
    */
   public ResultObject updatePassword(UserVO userVO) {
      ResultObject result = new ResultObject();
      try {
         User user = userDao.getUser(userVO.getId());
         user.setUpdated(TimeUtil.getCurrTimeInUTC());         
         user.setPassword(userVO.getPassword());
         user.setEnabled(userVO.isEnabled());

         mailDao.sendUpdatePasswordMail(user);

         userDao.updatePassword(user);
         result.setData("Successful");
      } catch (AppServerException ex) {
         result.addError(ex.getErrorObject());
      } catch (Exception ex) {
         log.error(
               String.format("Updating user %s password failed!",
                     userVO.getEmail()), ex);
         result.addError(new AppServerException().getErrorObject());
      }
      return result;
   }
   
   /**
    * Adds feedback to the DB
    * @param feedbackVO
    * @return
    */
   public ResultObject addFeedback(FeedbackVO feedbackVO) {
      ResultObject result = new ResultObject();
      try {
         Feedback fb = new Feedback();
         fb.setCreated(TimeUtil.getCurrTimeInUTC());
         fb.setEmail(feedbackVO.getEmail());
         fb.setMssg(feedbackVO.getMssg());
         fb.setName(feedbackVO.getName());
         fb.setIpaddr(feedbackVO.getIpaddr());
         userDao.addFeedback(fb);
      } catch (Exception ex) {
         log.error(String.format("Adding feedback %s failed!"));
         result.addError(new AppServerException().getErrorObject());
      }
      return result;
   }
}
