package com.indusborn.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.indusborn.common.IndusBornTestParam;
import com.indusborn.domain.ActivateAccount;
import com.indusborn.domain.Feedback;
import com.indusborn.domain.ResetPassword;
import com.indusborn.domain.User;
import com.indusborn.util.TimeUtil;

public class UserDaoTest extends IndusBornTestParam {
   private static Log log = LogFactory.getLog(UserDaoTest.class);

   @Test(dataProvider = "existingUserEmails")
   public void getUser(String[] emails) throws Exception {
      String email = emails[generator.nextInt(emails.length)];
      User user = userDao.getUser(email);
      Assert.assertNotNull(user);
      User tmpUser = userDao.getUser(user.getId());
      Assert.assertNotNull(tmpUser);
   }
   
   @Test(dataProvider = "existingUserEmails")
   public void updatePassword(String[] emails) throws Exception {
      String email = emails[generator.nextInt(emails.length)];
      User user = userDao.getUser(email);
      Assert.assertNotNull(user);
      String updatedPassword = "abcdb";
      user.setPassword(updatedPassword);
      userDao.updatePassword(user);
      user = userDao.getUser(email);
      Assert.assertTrue(passwordEncoder.matches(updatedPassword, user.getPassword()));
   }
   
   @Test(dataProvider = "existingUserEmails")
   public void resetPassword(String emails[]) throws Exception {
      String email = emails[generator.nextInt(emails.length)];
      User user = userDao.getUser(email);
      String token = tokenGenerator.generateKey();
      userDao.addResetPassword(token, user);
      ResetPassword resetPasswd = userDao.getResetPassword(token);
      Assert.assertNotNull(resetPasswd);
      String newPasswd = "abcdbfgh";
      userDao.resetPassword(token, newPasswd);
      user = null;
      user = userDao.getUser(email);
      Assert.assertNotNull(user);
      Assert.assertTrue(passwordEncoder.matches(newPasswd, user.getPassword()));
   }
   
   @Test(dataProvider = "existingUserEmails")
   public void authenticateUser(String[] emails) throws Exception {
      String email = emails[generator.nextInt(emails.length)];
      User user = userDao.getUser(email);
      Assert.assertNotNull(user);
      String updatedPassword = "abcdb";
      user.setPassword(updatedPassword);
      userDao.updatePassword(user);
      User signedUser = userDao.authenticateUser(user.getEmail(), updatedPassword);
      Assert.assertNotNull(signedUser);
   }
   
   private User getTemporaryUser() {
      User user = new User();
      user.setCreated(TimeUtil.getCurrTimeInUTC());
      user.setUpdated(TimeUtil.getCurrTimeInUTC());
      user.setEnabled(false);
      user.setPassword("abcd");
      user.setFname("Hello");
      user.setLname("World");
      return user;
   }
   
   @Test(dataProvider = "newUserEmails")
   public void activateAccount(String[] emails) throws Exception {
      String token = tokenGenerator.generateKey();
      String email = emails[generator.nextInt(emails.length)];
      User newUser = getTemporaryUser();
      newUser.setEmail(email);
      userDao.registerUser(token, newUser);
      ActivateAccount acct = userDao.getActivateAccount(token);
      Assert.assertNotNull(acct);
      userDao.activateUser(token);
      newUser = userDao.getUser(email);
      Assert.assertEquals(newUser.isEnabled(), true);
      userDao.unregisterUser(newUser);
   }
   

   
   @Test(dataProvider = "existingUserEmails")
   public void updateDetails(String emails[]) throws Exception {
      String email = emails[generator.nextInt(emails.length)];
      User user = userDao.getUser(email);
      String oldFname = user.getFname();
      String newFname = "asdfasdfadsf";
      user.setFname(newFname);
      userDao.updateDetails(user);
      user = userDao.getUser(email);
      Assert.assertEquals(user.getFname(), newFname);
      user.setFname(oldFname);
      userDao.updateDetails(user);
   }
   
   @Test(dataProvider = "newUserEmails")
   public void registerAndUnregister(String[] emails) throws Exception {
      User user = new User();
      user.setCreated(TimeUtil.getCurrTimeInUTC());
      user.setUpdated(TimeUtil.getCurrTimeInUTC());
      user.setEnabled(false);
      user.setPassword("abcd");
      user.setFname("Hello");
      user.setLname("World");
      String email = emails[generator.nextInt(emails.length)]; 
      user.setEmail(email);
      String token = tokenGenerator.generateKey();
      userDao.registerUser(token, user);
      log.info("Successfully added the user with email "+email);
      userDao.unregisterUser(user);
      log.info("Successfully deleted the user with email "+email);
   }
   
   @Test(dataProvider = "newUserEmails")
   public void addFeedback(String[] emails) throws Exception {
      Feedback fb = new Feedback();
      fb.setCreated(TimeUtil.getCurrTimeInUTC());
      fb.setEmail(emails[generator.nextInt(emails.length)]);
      fb.setMssg("Your site is good!");
      fb.setName("Praveen");
      fb.setIpaddr("127.0.0.1");
      userDao.addFeedback(fb);
      Assert.assertTrue(fb.getId() > 0);
      userDao.delFeedback(fb);
   }
}
