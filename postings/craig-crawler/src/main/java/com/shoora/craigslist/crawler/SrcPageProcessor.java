package com.shoora.craigslist.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

import com.indusborn.common.Constants;
import com.indusborn.geocode.GGcoder;
import com.indusborn.repository.UserDao;
import com.indusborn.service.PostingService;
import com.shoora.craigslist.dataobject.LeafPageRequest;
import com.shoora.craigslist.dataobject.SrcPageRequest;

public class SrcPageProcessor {
   private Log log = LogFactory.getLog(SrcPageProcessor.class);
   private static Map<String, Integer> catgryNumOfPostings = new HashMap<String, Integer>();

   static {
      catgryNumOfPostings.put(Constants.RENTAL, CrawlerConstants.MAX_RENTAL_PAGE_REQS_PER_SRC);
      catgryNumOfPostings.put(Constants.ROOMMATE, CrawlerConstants.MAX_ROOMMATE_PAGE_REQS_PER_SRC);
      catgryNumOfPostings.put(Constants.BUYSELL, CrawlerConstants.MAX_BUYSELL_PAGE_REQS_PER_SRC);
      catgryNumOfPostings.put(Constants.CAR, CrawlerConstants.MAX_CAR_PAGE_REQS_PER_SRC);
      catgryNumOfPostings.put(Constants.JOB, CrawlerConstants.MAX_JOB_PAGE_REQS_PER_SEC);
   }

   private ArrayList<SrcPageRequest> srcPageReqs =
         new ArrayList<SrcPageRequest>();
   private ArrayBlockingQueue<LeafPageRequest> leafPageReqs =
         new ArrayBlockingQueue<LeafPageRequest>(1000);
   private LeafPageProcessor leafPageProcessor; 

   private ExecutorService srcPageReqsSubmitter = Executors
         .newSingleThreadExecutor();
   private Runnable srcPageReqsSubmitTask = new Runnable() {
      public void run() {
         submitSrcPageReqs();
      }
   };
   
   private ExecutorService leafPageReqsCollector = Executors.newSingleThreadExecutor();
   private Runnable leafPageReqsCollectorTask = new Runnable() {
      public void run() {
         collectLeafPageReqs();
      }
   };

   private ExecutorService complExecService = Executors.newFixedThreadPool(10);
   private CompletionService<List<LeafPageRequest>> srcPageCompletionService =
         new ExecutorCompletionService<List<LeafPageRequest>>(complExecService);

   
   public ArrayBlockingQueue<LeafPageRequest> getLeafPageReqs() {
      return leafPageReqs;
   }
   
   private LeafPage2PostingVO leafPageSaver;
   
   public LeafPage2PostingVO getLeafPage2PostingVO() {
      return leafPageSaver;
   }
   
   private AtomicInteger totalLeafPageReqs = new AtomicInteger(0);
   
   /**
    * Constructor
    */
   public SrcPageProcessor(LeafPage2PostingVO leafPageSaver, Resource srcsRes) {
      srcPageReqs.clear();
      loadSrcPageReqs(srcsRes);
      log.info(String.format("Source pages to process %d.", srcPageReqs.size()));
      this.leafPageSaver = leafPageSaver;      
      if (srcPageReqs.size() > 0) {
         srcPageReqsSubmitter.submit(srcPageReqsSubmitTask);
         srcPageReqsSubmitter.shutdown();
         leafPageReqsCollector.submit(leafPageReqsCollectorTask);
         leafPageProcessor = new LeafPageProcessor(this);
      }
   }
   

   private void submitSrcPageReqs() {
      Random random = new Random(); 
      for (SrcPageRequest srcPageReq : srcPageReqs) {
         srcPageCompletionService.submit(new SrcPageFetcher(srcPageReq));
         try {
            Thread.sleep(random.nextInt(5) * 1000);
         } catch(InterruptedException ex) {
            log.error(ex);
         }
      }
   }
   
   private void collectLeafPageReqs() {
      Random rand = new Random();
      while (true) {
         Future<List<LeafPageRequest>> leafPageReqsFut = null;
         try {
            leafPageReqsFut = srcPageCompletionService.take();
            List<LeafPageRequest> tmpLeafPageReqs = leafPageReqsFut.get();
            if (tmpLeafPageReqs != null && tmpLeafPageReqs.size() > 0) {
               log.info(String.format("SrcPageRequest fetched %d leaf pages", tmpLeafPageReqs.size()));
               Integer catgryLimit = catgryNumOfPostings.get(tmpLeafPageReqs.get(0).getCatgry().toLowerCase());
               if (tmpLeafPageReqs.size() > catgryLimit) {
                  int counter = 0;
                  while(counter < catgryLimit) {
                     leafPageReqs.put(tmpLeafPageReqs.get(rand.nextInt(tmpLeafPageReqs.size())));
                     totalLeafPageReqs.getAndIncrement();
                     counter++;
                  }
               } else {
                  for (LeafPageRequest leafPageReq:tmpLeafPageReqs) {
                     leafPageReqs.put(leafPageReq);
                     totalLeafPageReqs.getAndIncrement();
                  }
               }
               log.info(String.format("TotalLeafPageReqs submitted %d!", totalLeafPageReqs.get()));
            }
         } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return;
         } catch (ExecutionException ex) {
            if (leafPageReqsFut != null) {
               leafPageReqsFut.cancel(true);
            }
         } catch (Exception ex) {
            log.error("Unknown exception...", ex);
         }
      }
   }

   private void loadSrcPageReqs(Resource srcsRes) {
      try {
         BufferedReader fileBuff = new BufferedReader(new InputStreamReader(srcsRes.getInputStream()));
         String line;
         while ((line = fileBuff.readLine()) != null) {
            StringTokenizer tokenizer = new StringTokenizer(line, "$");
            String url = tokenizer.nextToken();
            if (url == null) {
               continue;
            }
            String catgry = tokenizer.nextToken();
            if (catgry == null) {
               continue;
            }
            String subCatgry = tokenizer.nextToken();
            if (subCatgry == null) {
               continue;
            }
            SrcPageRequest srcPageReq = new SrcPageRequest(url, catgry, subCatgry);
            srcPageReqs.add(srcPageReq);
         }
      } catch (IOException ex) {
         log.error(ex);
         System.exit(1);
      }
   }
   
   public void terminateThreadPools(int timeInSecs) {
      if (!srcPageReqsSubmitter.isShutdown()) {
         try {
            srcPageReqsSubmitter.awaitTermination(timeInSecs, TimeUnit.SECONDS);
         } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
         }
         srcPageReqsSubmitter.shutdownNow();
      }
      complExecService.shutdown();
      try {
         complExecService.awaitTermination(timeInSecs, TimeUnit.SECONDS);
      } catch(InterruptedException ex) {
         Thread.currentThread().interrupt();
      }
      complExecService.shutdownNow();
      
      leafPageReqsCollector.shutdown();
      try {
         leafPageReqsCollector.awaitTermination(timeInSecs, TimeUnit.SECONDS);
      } catch(InterruptedException ex) {
         Thread.currentThread().interrupt();
      }
      leafPageReqsCollector.shutdownNow();
      
      if (leafPageProcessor != null) {
         leafPageProcessor.terminateThreadPools();
      }
   }


   /**
    * 1. Read the src file with urls 2. Crawl each url 3. Extract urls 4. Put
    * them in the shared queue
    */
   public static void main(String[] args) {
      //SrcPageProcessor srcPageProc = new SrcPageProcessor();
   }

}
