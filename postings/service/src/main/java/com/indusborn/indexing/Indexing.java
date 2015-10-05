package com.indusborn.indexing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.indusborn.common.Constants;
import com.indusborn.common.ResultObject;
import com.indusborn.domain.Posting;
import com.indusborn.repository.IndexingDao;
import com.indusborn.repository.PostingDao;
import com.indusborn.service.PostingService;
import com.indusborn.ui.domain.PostingVO;
import com.indusborn.ui.domain.PostingsVO;
import com.indusborn.util.CatgryUtil;
import com.indusborn.util.TimeUtil;

public class Indexing {
   private static final Log log = LogFactory.getLog(Indexing.class);
   private static String[] paths = {"indusborn-business.xml" };
   private static ApplicationContext ctx;
   private static final int CHUNK_SIZE = 200;

   public static void main(String[] args) throws Exception {
      ctx = new ClassPathXmlApplicationContext(paths);
      PostingService postingService = (PostingService) ctx.getBean("postingService");
      PostingDao postingDao = (PostingDao) ctx.getBean("postingDao");
      String[] catgries = {Constants.RENTAL, Constants.ROOMMATE, Constants.BUYSELL, Constants.JOB, Constants.CAR};
      
      for (String catgry:catgries) {
         log.info("Processing category "+catgry);
         long startTime = TimeUtil.getPostingStartTime();
         long size = postingDao.getPostingSize(CatgryUtil.getPostingClass(catgry), startTime);
         long count = 0;
         int pageNum = 0;
         while (count < size) {
            ResultObject result = postingService.getPostings(CatgryUtil.getPostingClass(catgry), pageNum, CHUNK_SIZE);
            if (result.hasError()) {
               break;
            }
            PostingsVO postingsVO = (PostingsVO)result.getData();
            if (postingsVO.getPostingVOs().size() == 0) {
               break;
            }
            
            log.info(String.format("Processing pageNum %d pageSize %d count %d ", pageNum, postingsVO.getPostingVOs().size(), count));            
            
            for(PostingVO postingVO:postingsVO.getPostingVOs()) {
               postingService.addPosting(postingVO);
            }
            pageNum += 1;
            count += CHUNK_SIZE;
         }
      }
      
      log.info("DONE!");
   }

}
