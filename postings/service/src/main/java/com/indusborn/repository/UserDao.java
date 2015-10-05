package com.indusborn.repository;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.security.encrypt.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.indusborn.common.AppServerException;
import com.indusborn.common.MessageKeys;
import com.indusborn.domain.ActivateAccount;
import com.indusborn.domain.Feedback;
import com.indusborn.domain.ResetPassword;
import com.indusborn.domain.User;
import com.indusborn.util.TimeUtil;


@Transactional(readOnly = false)
public class UserDao {
   private static Log log = LogFactory.getLog(UserDao.class);
   private PasswordEncoder passwordEncoder;
   private SessionFactory sessionFactory;

   public PasswordEncoder getPasswordEncoder() {
      return passwordEncoder;
   }

   public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
      this.passwordEncoder = passwordEncoder;
   }

   private Session getSession() {
      return this.sessionFactory.getCurrentSession();
   }

   /**
    * Gets user by email
    * 
    * @param email
    * @return
    * @throws AppServerException
    */
   @Transactional(readOnly = true)
   public User getUser(String email) {
      return (User) getSession()
            .createQuery("from User user where user.email = :email")
            .setString("email", email)
            .uniqueResult();
   }

   /**
    * Gets user by ID
    * 
    * @param email
    * @return
    * @throws AppServerException
    */
   @Transactional(readOnly = true)
   public User getUser(int id) {
      return (User) getSession()
            .createQuery("from User user where user.id = :id")
            .setInteger("id", id)
            .uniqueResult();
   }

   /**
    * Registers user
    * 
    * @param user
    * @throws AppServerException
    */
   @Transactional
   public void registerUser(String activateAcctToken, User user) {
      Session session = getSession();
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      session.save(user);
      ActivateAccount account = new ActivateAccount();
      account.setCreated(TimeUtil.getCurrTimeInUTC());
      account.setOwner(user);
      account.setToken(activateAcctToken);
      session.save(account);
   }

   /**
    * Unregisters an user
    * 
    * @param user
    * @throws AppServerException
    */
   public void unregisterUser(User user) {
      Session session = getSession();
      User tmpUser = getUser(user.getEmail());
      // TODO how about deleting foreign key references
      //Clear Activate Account entries
      @SuppressWarnings("unchecked")
      List<ActivateAccount> accts =
            (List<ActivateAccount>) session
                  .createQuery("from ActivateAccount acct where acct.owner.id = :id")
                  .setInteger("id", tmpUser.getId())
                  .list();

      if (accts != null && accts.size() > 0) {
         log.info(String.format("User %d has %d activate account records!",
               tmpUser.getId(), accts.size()));
         for (ActivateAccount acct : accts) {
            session.delete(acct);
         }
      }
      session.delete(tmpUser);
   }

   /**
    * Updates the user password
    * 
    * @param user
    * @throws AppServerException
    */
   public void updatePassword(User user) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      getSession().saveOrUpdate(user);
   }

   /**
    * Authenticates user
    * 
    * @param username
    * @param password
    * @return
    * @throws AppServerException
    */
   @Transactional(readOnly = true)
   public User authenticateUser(String username, String password)
         throws AppServerException {
      User user = getUser(username);
      if (user == null) {
         log.error("Username " + username + " doesn't exist.");
         throw new AppServerException(MessageKeys.USER_DAO_USER_DONT_EXIST);
      }

      if (user != null && !user.isEnabled()) {
         log.error("User account " + username + " is not activated...");
         throw new AppServerException(MessageKeys.USER_DAO_USER_NOT_ACTIVE);
      }

      if (passwordEncoder.matches(password, user.getPassword())) {
         return user;
      } else {
         throw new AppServerException(MessageKeys.USER_DAO_INVALID_CRED);
      }
   }

   /**
    * Gets the activate account given its token
    * 
    * @param token
    * @return
    * @throws AppServerException
    */
   @Transactional(readOnly = true)
   public ActivateAccount getActivateAccount(String token) {
      return (ActivateAccount) getSession()
                  .createQuery("from ActivateAccount acct where acct.token = :token")
                  .setString("token", token)
                  .uniqueResult();
   }

   /**
    * Activates user account by turning on the enabled flag
    * 
    * @param userId
    * @throws AppServerException
    */
   public User activateUser(String token) throws AppServerException {
      User user = null;
      ActivateAccount activateAccount = getActivateAccount(token);
      if (activateAccount == null) {
         throw new AppServerException(MessageKeys.USER_DAO_NO_ACTIVATE_ACCOUNT);
      }
      user = getUser(activateAccount.getOwner().getId());
      user.setEnabled(true);
      getSession().saveOrUpdate(user);
      getSession().delete(activateAccount);
      return user;
   }

   /**
    * Saves the reset password token
    * 
    * @param token
    * @param user
    * @throws AppServerException
    */
   public void addResetPassword(String token, User user) {
      Session session = getSession();
      ResetPassword resetPasswd = new ResetPassword();
      resetPasswd.setCreated(TimeUtil.getCurrTimeInUTC());
      resetPasswd.setOwner(user);
      resetPasswd.setToken(token);
      session.save(resetPasswd);
   }

   /**
    * Gets the reset password entry given its token
    * 
    * @param token
    * @return
    * @throws AppServerException
    */
   @Transactional(readOnly = true)
   public ResetPassword getResetPassword(String token)
         throws AppServerException {
      Session session = getSession();
      ResetPassword resetPasswd =
            (ResetPassword) session
                  .createQuery("from ResetPassword resetpasswd left outer join fetch resetpasswd.owner where resetpasswd.token = :token")
                  .setString("token", token)
                  .uniqueResult();

      if (resetPasswd == null) {
         throw new AppServerException(MessageKeys.USER_DAO_NO_RESET_PASSWORD);
      }
      return resetPasswd;
   }

   /**
    * Sets user password to the updated one
    * 
    * @param token
    * @param password
    * @return
    * @throws AppServerException
    */
   public User resetPassword(String token, String password)
         throws AppServerException {
      User user = null;
      ResetPassword resetPassword = getResetPassword(token);
      user = getUser(resetPassword.getOwner().getId());
      user.setPassword(password);
      updatePassword(user);
      getSession().delete(resetPassword);
      return user;
   }

   /**
    * Adds feedback
    * @param feedback
    */
   public void addFeedback(Feedback feedback) {
      getSession().save(feedback);
   }
   
   /**
    * Deletes feedback
    */
   public void delFeedback(Feedback feedback) {
      getSession().delete(feedback);
   }
   
   /**
    * Updates user details
    * 
    * @param user
    * @throws AppServerException
    */
   public void updateDetails(User user){
      getSession().saveOrUpdate(user);
   }

   public void setSessionFactory(SessionFactory sessionFactory) {
      this.sessionFactory = sessionFactory;
   }

}
