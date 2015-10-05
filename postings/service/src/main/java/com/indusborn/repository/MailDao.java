/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * MailDao.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.repository;

import com.indusborn.common.AppServerException;
import com.indusborn.domain.Posting;
import com.indusborn.domain.User;
import com.indusborn.ui.domain.MailVO;

public interface MailDao {
   public void sendRegisterMail(String toName, String toAddress, String username)
         throws AppServerException;

   public void sendResetPasswordMail(String toName, String toAddress,
         String token);

   public void sendActivateAcctMail(String toName, String toAddress,
         String token);

   public void sendUpdatePasswordMail(User user) throws AppServerException;

   public void sendUpdateDetailsMail(User user) throws AppServerException;

   public String createFuzzyAddress(String toAddress) throws AppServerException;

   public void purgeFuzzyAddress(String fuzzyAddr) throws AppServerException;

   public String getFuzzyAddress(int userId, String email)
         throws AppServerException;

   public void sendAddOrUpdatePostingMail(User user, Posting posting, boolean isUpdate);

   public void sendDeletePostingMail(User user, Posting posting);   

   public void sendAnonymContactMail(MailVO mailVO) throws AppServerException;

   public void createFuzzyAddrs(int userId, String email);

   public void cleanFuzzyAddrs(int userId);

   public void deleteFuzzyAddress(String fuzzyEmail);

   public String getUrl();

   public String getSiteName();

   public String getFromAddress();

   public String getFromName();

}
