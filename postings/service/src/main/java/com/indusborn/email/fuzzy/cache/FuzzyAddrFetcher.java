/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * FuzzyAddrFetcher.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.email.fuzzy.cache;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.indusborn.repository.MailDao;

public class FuzzyAddrFetcher implements Runnable {
   private static Log _log = LogFactory.getLog(FuzzyAddrFetcher.class);
   private ConcurrentHashMap<String, String> fuzzyAddrCache;
   private final ArrayBlockingQueue<FuzzyAddrRequest> reqs;
   private final MailDao mailDao;

   public FuzzyAddrFetcher(ArrayBlockingQueue<FuzzyAddrRequest> reqs,
         ConcurrentHashMap<String, String> fuzzyAddrCache, MailDao mailDao) {
      this.fuzzyAddrCache = fuzzyAddrCache;
      this.reqs = reqs;
      this.mailDao = mailDao;
   }

   public void run() {
      String email;
      int userId;
      String key;
      String fuzzyAddr;
      while (true) {
         try {
            FuzzyAddrRequest req = reqs.take();
            _log.debug("Serving the fuzzyAddressReq: " + req);
            switch (req.getType()) {
            case CREATE:
               email = req.getEmail();
               userId = req.getUserId();
               key = userId + "";
               fuzzyAddr = mailDao.createFuzzyAddress(email);
               _log.debug("Creating the fuzzyAddress " + key + "->" + fuzzyAddr);
               fuzzyAddrCache.put(key, fuzzyAddr);
               break;
            case PURGE:
               fuzzyAddr = req.getFuzzyEmail();
               mailDao.purgeFuzzyAddress(fuzzyAddr);
               _log.debug("Purging the fuzzyAddress " + fuzzyAddr);
               break;
            case CLEAN:
               userId = req.getUserId();
               key = userId + "";
               fuzzyAddr = fuzzyAddrCache.get(key);
               if (fuzzyAddr == null) {
                  break;
               }
               mailDao.purgeFuzzyAddress(fuzzyAddr);
               _log.debug("Cleaning the fuzzyAddress " + fuzzyAddr);
               fuzzyAddrCache.remove(key);
               break;
            }
         } catch(InterruptedException ex) {
            _log.warn("Interrupted!", ex);
            Thread.currentThread().interrupt();
            return;
         } catch (Exception ex) {
            _log.warn("Unable to fetch the fuzzy address", ex);
         }
      }
   }

}
