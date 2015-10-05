package com.shoora.craigslist.crawler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.indusborn.geocode.GGcoder;
import com.indusborn.repository.MailDao;
import com.indusborn.repository.UserDao;
import com.indusborn.service.PostingService;

public class Crawler 
{
   private static final Log log = LogFactory.getLog(Crawler.class);
   
   public static void main( String[] args ) throws Exception
   {
      String[] paths = {"indusborn-business.xml"};
      ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);
      UserDao userDao = (UserDao)ctx.getBean("userDao");
      GGcoder geoCoder = (GGcoder) ctx.getBean("geoCoder");
      PostingService postingService = (PostingService) ctx.getBean("postingService");
      MailDao mailDao = (MailDao)ctx.getBean("mailDao");
      Resource srcsRes = ctx.getResource("classpath:srcs.txt");
      String userEmail = "f2001272@gmail.com";
      log.info("Instantiated all the beans...");
      LeafPage2PostingVO leafPage2PostingVO = new LeafPage2PostingVO(userDao, mailDao, geoCoder, postingService, userEmail);
      SrcPageProcessor srcPageProcessor = new SrcPageProcessor(leafPage2PostingVO, srcsRes);
      
      try {
         log.info("Waiting for 5 mins...");
         Thread.sleep( 5 * 60 * 60 * 1000);
      } catch(InterruptedException ex) {
         log.warn("Main thread interrupted!", ex);
      }
      
      srcPageProcessor.terminateThreadPools(10);
      log.info("All the processing complete!");
      System.exit(0);
      
   }
}
