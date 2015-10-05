package com.shoora.craigslist.crawler;

import java.util.Random;
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

import com.shoora.craigslist.dataobject.LeafPage;
import com.shoora.craigslist.dataobject.LeafPageRequest;

public class LeafPageProcessor {
   private static Log log = LogFactory.getLog(LeafPageProcessor.class);
   private SrcPageProcessor srcPageProcessor;
   private AtomicInteger savedPostsCount = new AtomicInteger(0);
   private AtomicInteger discardedPostsCount = new AtomicInteger(0);

   private ArrayBlockingQueue<LeafPageRequest> leafPageReqs;
   private ExecutorService complExecService = Executors.newFixedThreadPool(10);
   private CompletionService<LeafPage> leafPageCompletionService =
         new ExecutorCompletionService<LeafPage>(complExecService);

   private ExecutorService leafPageSubmitter = Executors
         .newSingleThreadExecutor();
   private Runnable leafPageSubmitTask = new Runnable() {
      public void run() {
         submitLeafPages();
      }
   };

   private ExecutorService leafPageCollector = Executors
         .newSingleThreadExecutor();
   private Runnable leafPageCollectorTask = new Runnable() {
      public void run() {
         collectAndStoreLeafPages();
      }
   };
   
   private LeafPage2PostingVO leafPageSaver;   
   
   public LeafPageProcessor(ArrayBlockingQueue<LeafPageRequest> reqs) {
      this.leafPageReqs = reqs;
      leafPageSubmitter.submit(leafPageSubmitTask);
      leafPageCollector.submit(leafPageCollectorTask);
   }

   public LeafPageProcessor(SrcPageProcessor srcPageProcessor) {
      this(srcPageProcessor.getLeafPageReqs());
      this.srcPageProcessor = srcPageProcessor;
      leafPageSaver = srcPageProcessor.getLeafPage2PostingVO();
   }

   public void submitLeafPages() {
      Random random = new Random();
      while (true) {
         try {
            LeafPageRequest leafPageReq = leafPageReqs.take();
            leafPageCompletionService.submit(new LeafPageFetcher(leafPageReq));
            Thread.sleep(random.nextInt(5) * 250);
         } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return;
         }
      }
   }

   public void collectAndStoreLeafPages() {
      while (true) {
         Future<LeafPage> leafPageFut = null;
         try {
            leafPageFut = leafPageCompletionService.take();
            LeafPage leafPage = leafPageFut.get();
            if (leafPage != null) {
               log.info(leafPage);
            }
            if (isValid(leafPage) && leafPageSaver != null) {
               if(leafPageSaver.saveLeafPage(leafPage)) {
                  savedPostsCount.getAndIncrement();
                  log.info(String.format("Total postings saved %d", savedPostsCount.get()));
               }
            } else {
               log.info(String.format("Total postings discarded %d", discardedPostsCount.incrementAndGet()));
            }
         } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return;
         } catch (ExecutionException ex) {
            if (leafPageFut != null) {
               leafPageFut.cancel(true);
            }
         } catch (Exception ex) {
            log.error("Unknown exception!", ex);
         }
      }
   }

   public boolean isValid(LeafPage leafPage) {
      if (leafPage == null) {
         return false;
      }

      if (leafPage.getCreated() == 0 || leafPage.getTitle() == null
            || leafPage.getDesc() == null || leafPage.getAddress() == null) {
         return false;
      }
      return true;
   }

   public void terminateThreadPools() {
      leafPageSubmitter.shutdown();
      try {
         leafPageSubmitter.awaitTermination(3, TimeUnit.SECONDS);
      } catch (InterruptedException ex) {
         Thread.currentThread().interrupt();
      }
      leafPageSubmitter.shutdownNow();

      complExecService.shutdown();
      try {
         complExecService.awaitTermination(5, TimeUnit.MINUTES);
      } catch (InterruptedException ex) {
         Thread.currentThread().interrupt();
      }
      complExecService.shutdownNow();

      leafPageCollector.shutdown();
      try {
         leafPageCollector.awaitTermination(3, TimeUnit.SECONDS);
      } catch (InterruptedException ex) {
         Thread.currentThread().interrupt();
      }
      leafPageCollector.shutdownNow();
   }


   public static void main(String[] args) {
      ArrayBlockingQueue<LeafPageRequest> reqs =
            new ArrayBlockingQueue<LeafPageRequest>(1000);
      LeafPageRequest req1 =
            new LeafPageRequest(
                  "http://sfbay.craigslist.org/pen/sci/2809790689.html",
                  "Rental", "Others", true);
      try {
         reqs.put(req1);
         LeafPageProcessor leafPageProcessor = new LeafPageProcessor(reqs);
         Thread.sleep(50000);
         leafPageProcessor.terminateThreadPools();
      } catch (InterruptedException ex) {
         Thread.currentThread().interrupt();
      }


   }

   /*
      String url = "http://sfbay.craigslist.org/pen/roo/2761727106.html";
      Future<LeafPage> future = leafPageThreadPool.submit(new LeafPageFetcher(url));
      LeafPage leafPage;
      try {
         leafPage = future.get();
         System.out.println("Title "+leafPage.getTitle());
      } catch(ExecutionException ex) {
         future.cancel(true);
      } catch(InterruptedException ex) {
         Thread.currentThread().interrupted();
      }
   
    */
}
