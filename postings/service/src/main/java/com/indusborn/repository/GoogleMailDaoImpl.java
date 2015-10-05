/* ******************************************************************************
 * Copyright (C) 2010 VMWare, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * GoogleMailDaoImpl.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.indusborn.common.AppServerException;
import com.indusborn.common.Constants;
import com.indusborn.common.MessageKeys;
import com.indusborn.common.MessageObject;
import com.indusborn.domain.Posting;
import com.indusborn.domain.User;
import com.indusborn.email.fuzzy.cache.FuzzyAddrFetcher;
import com.indusborn.email.fuzzy.cache.FuzzyAddrRequest;
import com.indusborn.email.fuzzy.cache.FuzzyAddrRequest.Type;
import com.indusborn.ui.domain.MailVO;
import com.indusborn.util.CatgryUtil;
import com.indusborn.util.GeneralUtil;

public class GoogleMailDaoImpl implements MailDao {
   private static Log log = LogFactory.getLog(GoogleMailDaoImpl.class);
   private String fromName;
   private String fromAddress;
   private String siteName;
   private String url;
   private String fuzzyCreateUrl;
   private String fuzzyPurgeUrl;
   private String fuzzyChallenge;
   private String fuzzyCreator;

   private ConcurrentHashMap<String, String> fuzzyCache =  new ConcurrentHashMap<String, String>(1000);
   private ArrayBlockingQueue<FuzzyAddrRequest> fuzzyReqs = new ArrayBlockingQueue<FuzzyAddrRequest>(500);
   private ArrayBlockingQueue<HttpPost> sendMailReqs = new ArrayBlockingQueue<HttpPost>(1000);
   private ExecutorService executorService;
   private String appSecUrl;
   
   public GoogleMailDaoImpl () {
      this.executorService = Executors.newFixedThreadPool(20);

      //code to safely shutdown the scheduled threads.
      Runtime.getRuntime().addShutdownHook(new Thread() {
         public void run() {
            try {
               GoogleMailDaoImpl.this.executorService.awaitTermination(2,
                     TimeUnit.SECONDS);
            } catch (InterruptedException e) {
               log.error("Interrupted while trying to wait for termination", e);
            } finally {
               GoogleMailDaoImpl.this.executorService.shutdownNow();
            }
         }
      });
   }
   

   public String getAppSecUrl() {
      return appSecUrl;
   }

   public void setAppSecUrl(String appSecUrl) {
      this.appSecUrl = appSecUrl;
   }

   public String getFuzzyCreateUrl() {
      return fuzzyCreateUrl;
   }

   public void setFuzzyCreateUrl(String fuzzyCreateUrl) {
      this.fuzzyCreateUrl = fuzzyCreateUrl;
   }

   public String getFuzzyPurgeUrl() {
      return fuzzyPurgeUrl;
   }

   public void setFuzzyPurgeUrl(String fuzzyPurgeUrl) {
      this.fuzzyPurgeUrl = fuzzyPurgeUrl;
   }

   public String getFuzzyChallenge() {
      return fuzzyChallenge;
   }

   public void setFuzzyChallenge(String fuzzyChallenge) {
      this.fuzzyChallenge = fuzzyChallenge;
   }

   public String getFuzzyCreator() {
      return fuzzyCreator;
   }

   public void setFuzzyCreator(String fuzzyCreator) {
      this.fuzzyCreator = fuzzyCreator;
   }

   public String getFromName() {
      return fromName;
   }

   public void setFromName(String fromName) {
      this.fromName = fromName;
   }

   public String getFromAddress() {
      return fromAddress;
   }

   public void setFromAddress(String fromAddress) {
      this.fromAddress = fromAddress;
   }

   public String getSiteName() {
      return siteName;
   }

   public void setSiteName(String siteName) {
      this.siteName = siteName;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public void init() {
      for (int i = 0; i < 10; ++i) {
         this.executorService.execute(new FuzzyAddrFetcher(
               fuzzyReqs, fuzzyCache, this));
         this.executorService.execute(new SendMailThread(
               sendMailReqs));
      }
      
   }

   public void createFuzzyAddrs(int userId, String email) {
      FuzzyAddrRequest req = new FuzzyAddrRequest();
      req.setUserId(userId);
      req.setEmail(email);
      req.setType(Type.CREATE);
      addFuzzyReqToQueue(req);
   }

   public void cleanFuzzyAddrs(int userId) {
      FuzzyAddrRequest req = new FuzzyAddrRequest();
      req.setUserId(userId);
      req.setType(Type.CLEAN);
      addFuzzyReqToQueue(req);
   }

   public void sendResetPasswordMail(String toName, String toAddress,
         String token) {
      HttpPost httpPost = new HttpPost(url);

      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("FromName", fromName));
      nvps.add(new BasicNameValuePair("FromAddress", fromAddress));
      nvps.add(new BasicNameValuePair("ToName", toName));
      nvps.add(new BasicNameValuePair("ToAddress", toAddress));
      nvps.add(new BasicNameValuePair("Subject",
            Constants.MAIL_SUB_RESET_PASSWORD));
      StringBuffer message = new StringBuffer();
      message.append("Hello " + toName + ",\n\n");
      message.append("You requested a password reset.\n");
      message.append("\n");
      message.append("To reset your password, click the following link:\n\n");
      String resetUrl = appSecUrl + "/reset?token=" + token;
      message.append(resetUrl);
      message.append("\n\n");
      message.append("Thanks,\n");
      message.append(siteName + " team");
      nvps.add(new BasicNameValuePair("MessageBody", message.toString()));
      try {
         httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
      } catch (Exception ex) {
         log.error(String.format("SendResetPasswordMail failed for %s address",
               toAddress), ex);
      }
      addToSendMailReqsQueue(httpPost);
   }

   public void sendActivateAcctMail(String toName, String toAddress,
         String token) {
      HttpPost httpPost = new HttpPost(url);

      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("FromName", fromName));
      nvps.add(new BasicNameValuePair("FromAddress", fromAddress));
      nvps.add(new BasicNameValuePair("ToName", toName));
      nvps.add(new BasicNameValuePair("ToAddress", toAddress));
      nvps.add(new BasicNameValuePair("Subject", Constants.MAIL_SUB_ACTIVATE_ACCOUNT));
      StringBuffer message = new StringBuffer();
      message.append("Hello " + toName + ",\n\n");
      message.append("Thank you very much for registering on " + siteName
            + ".\n");
      message.append("\n");
      message.append("To activate your account, click the following link:\n\n");
      String resetUrl = appSecUrl + "/signup/activate?token=" + token;
      message.append(resetUrl);
      message.append("\n\n");
      message.append("Thanks,\n");
      message.append(siteName + " team");
      nvps.add(new BasicNameValuePair("MessageBody", message.toString()));
      try {
         httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
      } catch (Exception ex) {
         log.error(String.format("SendActivateAcctMail failed for %s address",
               toAddress), ex);
      }
      addToSendMailReqsQueue(httpPost);
   }

   public void sendRegisterMail(String toName, String toAddress, String username)
         throws AppServerException {
      HttpPost httpPost = new HttpPost(url);

      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("FromName", fromName));
      nvps.add(new BasicNameValuePair("FromAddress", fromAddress));
      nvps.add(new BasicNameValuePair("ToName", toName));
      nvps.add(new BasicNameValuePair("ToAddress", toAddress));
      nvps.add(new BasicNameValuePair("Subject", Constants.MAIL_SUB_REGISTER));
      StringBuffer message = new StringBuffer();
      message.append("Hello " + toName + ",\n\n");
      message.append("Your registartion with " + siteName
            + " is successful.\n\n");
      message.append("Your username is " + username + "\n");
      message.append("\n");
      message.append("Thank you very much for registering! \n\n");
      message.append("Thanks,\n");
      message.append(siteName + " team");
      nvps.add(new BasicNameValuePair("MessageBody", message.toString()));
      try {
         httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
      } catch (Exception ex) {
         log.error(String.format("SendRegisterMail failed for %s address",
               toAddress), ex);
      }
      addToSendMailReqsQueue(httpPost);
   }

   public void sendUpdateDetailsMail(User user) throws AppServerException {
      HttpPost httpPost = new HttpPost(url);
      String toName = user.getFname() + " " + user.getLname();
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("FromName", fromName));
      nvps.add(new BasicNameValuePair("FromAddress", fromAddress));
      nvps.add(new BasicNameValuePair("ToName", toName));
      nvps.add(new BasicNameValuePair("ToAddress", user.getEmail()));
      nvps.add(new BasicNameValuePair("Subject",
            Constants.MAIL_SUB_UPDATE_DETAILS));
      StringBuffer message = new StringBuffer();
      message.append("Hello " + toName + ",\n\n");
      message.append("Your account details have been updated!\n\n");
      if (user.getFname() != null) {
         message.append("First Name: " + user.getFname() + "\n");
      }
      if (user.getLname() != null) {
         message.append("Last Name: " + user.getLname() + "\n");
      }
      if (user.getAddress() != null && user.getAddress().length() > 0) {
         message.append("Address: " + user.getAddress() + "\n");
      }
      if (user.getPhone() != null && user.getPhone().length() > 0) {
         message.append("Phone: " + user.getPhone() + "\n");
      }
      if (user.getZip() != null && user.getZip().length() > 0) {
         message.append("Zip: " + user.getZip() + "\n");
      }
      message.append("\n");
      message.append("Thanks,\n");
      message.append(siteName + " team");
      nvps.add(new BasicNameValuePair("MessageBody", message.toString()));
      try {
         httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
      } catch (Exception ex) {
         log.error(String.format("SendUpdateDetailsMail failed for %s address",
               user.getFname() + " " + user.getLname()), ex);
      }
      addToSendMailReqsQueue(httpPost);
   }

   public void sendUpdatePasswordMail(User user) throws AppServerException {
      HttpPost httpPost = new HttpPost(url);
      String toName = user.getFname() + " " + user.getLname();
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("FromName", fromName));
      nvps.add(new BasicNameValuePair("FromAddress", fromAddress));
      nvps.add(new BasicNameValuePair("ToName", toName));
      nvps.add(new BasicNameValuePair("ToAddress", user.getEmail()));
      nvps.add(new BasicNameValuePair("Subject",
            Constants.MAIL_SUB_UPDATE_PASSWORD));
      StringBuffer message = new StringBuffer();
      message.append("Hello " + toName + ",\n\n");
      message.append("Your password has been updated successfully.\n");
      // message.append("It is set to: "+user.getPassword()+"\n");
      message.append("\n");
      message.append("Thanks,\n");
      message.append(siteName + " team");
      nvps.add(new BasicNameValuePair("MessageBody", message.toString()));
      try {
         httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
      } catch (Exception ex) {
         log.error(String.format(
               "SendUpdatePasswordMail failed for %s address", user.getFname()
                     + " " + user.getLname()), ex);
      }
      addToSendMailReqsQueue(httpPost);
   }
   
   public void sendAddOrUpdatePostingMail(User user, Posting posting, boolean isUpdate) {
      HttpPost httpPost = new HttpPost(url);
      String toName = user.getFname() + " " + user.getLname();
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("FromName", fromName));
      nvps.add(new BasicNameValuePair("FromAddress", fromAddress));
      nvps.add(new BasicNameValuePair("ToName", toName));
      nvps.add(new BasicNameValuePair("ToAddress", user.getEmail()));
      nvps.add(new BasicNameValuePair("Subject", posting.getTitle()));
      StringBuffer message = new StringBuffer();
      message.append("Hello " + toName + ",\n\n");
      if (isUpdate) {
         message.append("The posting is updated. ");
      }
      message.append("You can view the posting at:\n\n");
      String postingUrl = String.format("%s/posts/%s/%d", appSecUrl, CatgryUtil.getPostingCatgry(posting), posting.getId());
      message.append(postingUrl);
      message.append("\n\n");
      message.append("Thanks,\n");
      message.append(siteName + " team");
      nvps.add(new BasicNameValuePair("MessageBody", message.toString()));
      try {
         httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
      } catch (Exception ex) {
         log.error(String.format("SendAddOrUpdatePostingMail failed for %s address",
               user.getEmail()), ex);
      }
      addToSendMailReqsQueue(httpPost);
   }
   
   public void sendDeletePostingMail(User user, Posting posting) {
      HttpPost httpPost = new HttpPost(url);
      String toName = user.getFname() + " " + user.getLname();
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("FromName", fromName));
      nvps.add(new BasicNameValuePair("FromAddress", fromAddress));
      nvps.add(new BasicNameValuePair("ToName", toName));
      nvps.add(new BasicNameValuePair("ToAddress", user.getEmail()));
      nvps.add(new BasicNameValuePair("Subject", posting.getTitle()));
      StringBuffer message = new StringBuffer();
      message.append("Hello " + toName + ",\n\n");
      message.append(String.format("The posting \"%s\" is deleted.", posting.getTitle()));
      message.append("\n\n");
      message.append("Thanks,\n");
      message.append(siteName + " team");
      nvps.add(new BasicNameValuePair("MessageBody", message.toString()));
      try {
         httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
      } catch (Exception ex) {
         log.error(String.format("SendDeletePostingMail failed for %s address",
               user.getEmail()), ex);
      }
      addToSendMailReqsQueue(httpPost);
   }
   

   public void sendAnonymContactMail(MailVO mailVO) throws AppServerException {

      HttpPost httpPost = new HttpPost(url);
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("FromName", fromName));
      nvps.add(new BasicNameValuePair("FromAddress", fromAddress));
      nvps.add(new BasicNameValuePair("ToName", "Hello"));
      nvps.add(new BasicNameValuePair("ToAddress", mailVO.getToAddress()));
      nvps.add(new BasicNameValuePair("Subject", mailVO.getSubject()));
      StringBuffer message = new StringBuffer();
      message.append("Hello,\n\n");
      message.append("You have received a message from "
            + mailVO.getAnonymName() + "...\n\n");
      message.append(mailVO.getMessageBody() + "\n\n");
      message.append("Here are more details about " + mailVO.getAnonymName()
            + "...\n\n");
      message.append("EmailID: " + mailVO.getAnonymAddress() + "\n");
      if (mailVO.getPhoneNum() != null && mailVO.getPhoneNum().length() > 0) {
         message.append("Phone Num: " + mailVO.getPhoneNum() + "\n");
      }
      message.append("\n\n");
      message.append("Thanks,\n");
      message.append(siteName + " team");
      nvps.add(new BasicNameValuePair("MessageBody", message.toString()));

      try {
         httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
      } catch (Exception ex) {
         log.error(String.format("sendAnonymContactMail failed for %s address",
               mailVO.getAnonymName()), ex);
      }
      addToSendMailReqsQueue(httpPost);
   }

   private void addToSendMailReqsQueue(HttpPost httpPost) {
      boolean insertFailed = false;
      do {
         try {
            sendMailReqs.put(httpPost);
            insertFailed = false;
         } catch (InterruptedException ex) {
            insertFailed = true;
         }
      } while (insertFailed);
   }

   public String createFuzzyAddress(String toAddress) throws AppServerException {
      DefaultHttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(fuzzyCreateUrl);
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("creator", fuzzyCreator));
      nvps.add(new BasicNameValuePair("recepients", toAddress));
      nvps.add(new BasicNameValuePair("challenge", fuzzyChallenge));
      nvps.add(new BasicNameValuePair("prefix", Constants.FUZZY_ADDR_PREFIX));
      StringBuffer message = new StringBuffer();
      nvps.add(new BasicNameValuePair("MessageBody", message.toString()));
      String fuzzyAddress = null;
      try {
         log.debug("Getting fuzzy address for " + toAddress);
         httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
         HttpResponse response = httpClient.execute(httpPost);

         if (!response.getStatusLine().toString().contains("200")) {
            MessageObject mssg =
                  new MessageObject(MessageKeys.MAIL_DAO_CREATE_FUZZY_ADDRESS,
                        null);
            throw new AppServerException(mssg);
         }
         fuzzyAddress =
               GeneralUtil.convertStreamToString(response.getEntity()
                     .getContent());
         log.debug(fuzzyAddress);
      } catch (Exception ex) {
         throw new AppServerException(
               MessageKeys.MAIL_DAO_CREATE_FUZZY_ADDRESS, ex.getCause());
      }
      return fuzzyAddress;
   }

   public void purgeFuzzyAddress(String fuzzyAddr) throws AppServerException {
      if (fuzzyAddr == null) {
         throw new AppServerException(MessageKeys.MAIL_DAO_CLEAN_FUZZY_ADDRESS);
      }
      StringTokenizer tokenizer = new StringTokenizer(fuzzyAddr, "@");
      String token = tokenizer.nextToken();
      if (token == null) {
         throw new AppServerException(MessageKeys.MAIL_DAO_CLEAN_FUZZY_ADDRESS);
      }

      DefaultHttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(fuzzyPurgeUrl);
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("creator", fuzzyCreator));
      nvps.add(new BasicNameValuePair("token", token));
      StringBuffer message = new StringBuffer();
      nvps.add(new BasicNameValuePair("MessageBody", message.toString()));
      String fuzzyAddress = null;
      try {
         log.debug("Purging the fuzzy address: " + fuzzyAddr);
         httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
         HttpResponse response = httpClient.execute(httpPost);

         if (!response.getStatusLine().toString().contains("200")) {
            MessageObject mssg =
                  new MessageObject(MessageKeys.MAIL_DAO_CLEAN_FUZZY_ADDRESS,
                        null);
            throw new AppServerException(mssg);
         }
         fuzzyAddress =
               GeneralUtil.convertStreamToString(response.getEntity()
                     .getContent());
         log.debug(fuzzyAddress);
      } catch (Exception ex) {
         throw new AppServerException(MessageKeys.MAIL_DAO_CLEAN_FUZZY_ADDRESS,
               ex.getCause());
      }
   }

   public String getFuzzyAddress(int userId, String email)
         throws AppServerException {
      String key = userId + "";
      if (fuzzyCache.containsKey(key)) {
         String fuzzyAddr = fuzzyCache.get(key);
         fuzzyCache.remove(key);
         FuzzyAddrRequest req = new FuzzyAddrRequest();
         req.setUserId(userId);
         req.setEmail(email);
         req.setType(Type.CREATE);
         addFuzzyReqToQueue(req);
         return fuzzyAddr;
      } else {
         return createFuzzyAddress(email);
      }
   }

   public void deleteFuzzyAddress(String fuzzyEmail) {
      FuzzyAddrRequest req = new FuzzyAddrRequest();
      req.setFuzzyEmail(fuzzyEmail);
      req.setType(Type.PURGE);
      addFuzzyReqToQueue(req);
   }

   private void addFuzzyReqToQueue(FuzzyAddrRequest req) {
      boolean insertFailed = false;
      do {
         try {
            fuzzyReqs.put(req);
            insertFailed = false;
         } catch (InterruptedException ex) {
            insertFailed = true;
         }
      } while (insertFailed);      
   }

}
