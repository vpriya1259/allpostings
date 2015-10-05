package com.indusborn.repository;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.indusborn.common.AppServerException;
import com.indusborn.common.MessageKeys;

public class IndexingDao extends HibernateDaoSupport {
   private Log log = LogFactory.getLog(IndexingDao.class);
   private boolean reIndexAlways;
   private File indexDirectory;

   public void setReIndexAlways(boolean reIndexAlways) {
      this.reIndexAlways = reIndexAlways;
   }

   public void setIndexDirectory(File indexDirectory) {
      this.indexDirectory = indexDirectory;
   }
   

   public void indexPostings() throws AppServerException {
      log.info("indexPostings() called. Should we re-index always? = "
            + this.reIndexAlways);
      if (!this.reIndexAlways) {
         if (indexDirectory.exists()
               && FileUtils.sizeOfDirectory(indexDirectory) > FileUtils.ONE_MB) {
            // if there is some index present, don't reindex files.
            log.info("Index present in the directory = " + this.indexDirectory
                  + ". Skipping index creation");
            return;
         }
      }
      FullTextSession fullTextSession = Search.getFullTextSession(getSession());
      try {
         fullTextSession.createIndexer().startAndWait();
      } catch (Exception ex) {
         log.error("Indexing the content failed!", ex);
         throw new AppServerException(MessageKeys.POSTING_DAO_INDEX_DB_CONTENT,
               ex.getCause());
      } finally {
         fullTextSession.close();
      }
   }

}
