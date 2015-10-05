/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * SendMailThread.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.repository;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.indusborn.common.AppServerException;
import com.indusborn.common.MessageKeys;
import com.indusborn.common.MessageObject;

public class SendMailThread implements Runnable {
   private static Log log = LogFactory.getLog(SendMailThread.class);
   private final ArrayBlockingQueue<HttpPost> sendMailReqs;

   public SendMailThread(ArrayBlockingQueue<HttpPost> sendMailReqs) {
      this.sendMailReqs = sendMailReqs;
   }

   public void run() {
      HttpPost httpPost;
      while (true) {
         try {
            httpPost = sendMailReqs.take();
            DefaultHttpClient httpClient = new DefaultHttpClient();
            try {
               HttpResponse response = httpClient.execute(httpPost);
               if (!response.getStatusLine().toString().contains("200")) {
                  MessageObject mssg =
                        new MessageObject(
                              MessageKeys.MAIL_DAO_SEND_MAIL_FAILED, null);
                  throw new AppServerException(mssg);
               }
            } catch (Exception ex) {
               log.error("Sending mail failed!", ex);
            }

         } catch(InterruptedException ex) {
            log.warn("Interrupted!", ex);
            Thread.currentThread().interrupt();
            return;
         } catch (Exception ex) {
            log.warn("Unable to take HttpPost request from the queue.", ex);
         }
      }
   }
}
