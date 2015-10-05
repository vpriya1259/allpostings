package com.indusborn.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermsFilter;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.indusborn.common.AppServerException;
import com.indusborn.common.Constants;
import com.indusborn.common.MessageKeys;
import com.indusborn.domain.Posting;
import com.indusborn.domain.PostingImage;
import com.indusborn.domain.Region;
import com.indusborn.util.CatgryUtil;

@Repository
@Transactional(readOnly = false)
public class PostingDao {

   private SessionFactory sessionFactory;
   private Log log = LogFactory.getLog(PostingDao.class);

   private Session getSession() {
      return this.sessionFactory.getCurrentSession();
   }


   /**
    * Inserts posting in the DB
    * 
    * @param <U>
    * @param posting
    * @throws AppServerException
    */
   @Transactional
   public void addPosting(Posting posting) throws AppServerException {
      if (posting == null) {
         return;
      }
      Set<PostingImage> allImages = posting.getImages();
      Set<PostingImage> keepImages = new HashSet<PostingImage>();
      if (allImages != null) {
         for (PostingImage image : allImages) {
            if (image.getStatus() == PostingImage.Status.DELETE) {
               int del =
                     getSession()
                           .createQuery(
                                 "delete PostingImage img where img.id = :id")
                           .setLong("id", image.getId()).executeUpdate();
               log.debug(String.format("Deleted image %d ", image.getId()));
            } else {
               keepImages.add(image);
            }
         }
      }
      posting.setImages(keepImages);
      getSession().saveOrUpdate(posting);
   }

   /**
    * Deletes posting
    * 
    * @param <U>
    * @param posting
    * @throws AppServerException
    */
   @Transactional
   public void delPosting(Posting posting) {
      getSession().delete(posting);
   }

   /**
    * Gets posting image size
    * 
    * @param postingId
    * @return
    * @throws AppServerException
    */
   @Transactional
   private Long getPostingImageSize(long postingId) {
      return (Long) getSession()
            .createQuery(
                  "select count(img) from PostingImage img where img.posting.id = :id")
            .setLong("id", postingId).uniqueResult();
   }

   /**
    * Gets user's posts size
    * 
    * @param userId
    * @return
    * @throws AppServerException
    */
   @Transactional(readOnly = true)
   public Long getUserPostingSize(int userId, long startTime) {
      return (Long) getSession()
            .createQuery(
                  "select count(posting) from Posting posting where posting.owner.id = :id and posting.updated > :startTime")
            .setInteger("id", userId).setLong("startTime", startTime).uniqueResult();
   }

   /**
    * Gets posting size
    * 
    * @param <U>
    * @param posting
    * @return
    * @throws AppServerException
    */
   @Transactional(readOnly = true)
   public Long getPostingSize(Class<? extends Posting> posting, long startTime) {
      return (Long) getSession().createQuery(
            String.format("select count(posting) from %s posting where posting.updated > :startTime",
                  posting.getName())).setLong("startTime", startTime).uniqueResult();
   }

   /**
    * Gets user postings
    * 
    * @param userId
    * @param pageNum
    * @param pageSize
    * @return
    * @throws AppServerException
    */
   @Transactional(readOnly = true)
   @SuppressWarnings("unchecked")
   public List<? extends Posting> getUserPostings(int userId, int pageNum,
         int pageSize, long startTime) {
      List<? extends Posting> results =
            getSession()
                  .createQuery(
                        "select posting from "
                              + "Posting posting "
                              + "where posting.owner.id = :id and posting.updated > :startTime "
                              + "order by posting.updated desc")
                  .setLong("startTime", startTime).setInteger("id", userId)
                  .setFirstResult(pageNum * pageSize).setMaxResults(pageSize)
                  .list();
      if (results == null || results.isEmpty()) {
         return Collections.emptyList();
      }
      List<Long> idList = new ArrayList<Long>();
      for (Posting pos : results) {
         idList.add(pos.getId());
      }
      return getSession()
            .createQuery(
                  "select distinct posting from Posting posting left outer join fetch posting.images where posting.id in (:ids) order by posting.updated desc")
            .setParameterList("ids", idList).list();
   }

   /**
    * Gets user postings
    * 
    * @param userId
    * @param pageNum
    * @param pageSize
    * @return
    * @throws AppServerException
    */
   @SuppressWarnings("unchecked")
   @Transactional(readOnly = true)
   public List<? extends Posting> getPostings(Class<? extends Posting> posting,
         int pageNum, int pageSize, long startTime) {
      List<? extends Posting> results =
            getSession()
                  .createQuery(
                        String.format("select posting from " + "%s posting "
                              + "where posting.updated > :startTime "
                              + "order by posting.updated desc",
                              posting.getName()))
                  .setLong("startTime", startTime)
                  .setFirstResult(pageNum * pageSize).setMaxResults(pageSize)
                  .list();
      if (results == null || results.isEmpty()) {
         return Collections.emptyList();
      }
      List<Long> idList = new ArrayList<Long>();
      for (Posting pos : results) {
         idList.add(pos.getId());
      }
      return getSession()
            .createQuery(
                  String.format(
                        "select distinct posting from %s posting left outer join fetch posting.images where posting.id in (:ids) order by posting.updated desc",
                        posting.getName())).setParameterList("ids", idList)
            .list();
   }

   /**
    * Gets Posting Image
    * 
    * @param postingImageId
    * @return
    * @throws AppServerException
    */
   @Transactional(readOnly = true)
   public PostingImage getPostingImage(long postingImageId) {
      return (PostingImage) getSession()
            .createQuery("select img from PostingImage img where img.id = :id")
            .setLong("id", postingImageId).uniqueResult();
   }

   /**
    * Posting updated time comparator
    */
   private static class PostingUpdatedComparator implements Comparator<Posting> {
      public int compare(Posting post1, Posting post2) {
         Long post2Val = new Long(post2.getUpdated());
         Long post1Val = new Long(post1.getUpdated());
         return post2Val.compareTo(post1Val);
      }
   }

   /**
    * Adds region
    * 
    * @param region
    */
   public void addRegion(Region region) {
      try {
         getSession().save(region);
      } catch (Exception ex) {
         log.warn("Duplicate region...");
      }
   }

   /**
    * Gets posting by id
    * 
    * @param postingId
    * @return
    * @throws AppServerException
    */
   @Transactional(readOnly = true)
   @SuppressWarnings("unchecked")
   public <U extends Posting> U getPosting(long postingId) {
      return (U) getSession()
            .createQuery(
                  "select posting from "
                        + "Posting posting left outer join fetch posting.images "
                        + "where posting.id = :id").setLong("id", postingId)
            .uniqueResult();
   }


   /**
    * Creates a boolean query
    * 
    * @param searchText
    * @param ids
    * @return
    * @throws ParseException
    */
   private BooleanQuery createQuery(String searchText, List<Long> ids)
         throws ParseException {
      BooleanQuery bq = new BooleanQuery();
      if (searchText != null && searchText.trim().length() > 0) {
         QueryParser parser =
               new QueryParser(Constants.INDEXING_SEARCH_CONTENT,
                     new StandardAnalyzer());
         String searchPhrase =
               String.format("%s:(%s) ", Constants.INDEXING_SEARCH_CONTENT,
                     searchText);
         org.apache.lucene.search.Query termQuery = parser.parse(searchPhrase);
         bq.add(termQuery, BooleanClause.Occur.MUST);
      }
      
      if (ids.size() > 0) {
         TermsFilter tf = new TermsFilter();
         for (Long id : ids) {
            tf.addTerm(new Term("id", id.toString()));
         }
         org.apache.lucene.search.Query wrappedQuery = new ConstantScoreQuery(tf);
         bq.add(wrappedQuery, BooleanClause.Occur.MUST);
      }

      if (log.isDebugEnabled()) {
         log.debug(String.format("SearchQuery %s...", bq.toString()));
      }
      return bq;
   }

   public List<Posting> search(Class<? extends Posting> posting,
         String inputText, List<Long> ids, int pageNum, int pageSize)
         throws AppServerException {
      FullTextSession fullTextSession = Search.getFullTextSession(getSession());
      List<Posting> matchPostings = new ArrayList<Posting>();
      try {
         BooleanQuery bq = createQuery(inputText, ids);
         FullTextQuery fullQuery =
               fullTextSession.createFullTextQuery(bq, posting);
         Sort sort =
               new Sort(new SortField(Constants.POSTING_SORT_FIELD,
                     SortField.LONG, true)); // DESC
         //fullQuery.setSort(sort);
         fullQuery.setFirstResult(pageNum * pageSize);
         fullQuery.setMaxResults(pageSize);
         matchPostings = fullQuery.list();
      } catch (Exception ex) {
         log.error(String.format("Searching <%s, %s> failed!",
               posting.getName(), inputText), ex);
         throw new AppServerException(MessageKeys.POSTING_DAO_SEARCH,
               ex.getCause());
      }
      if (matchPostings != null) {
         log.info("Matched postings size is " + matchPostings.size());
      }
      return matchPostings;
   }

   /**
    * Gets search results size
    * 
    * @param posting
    * @param inputQuery
    * @return
    * @throws AppServerException
    */
   public int getSearchSize(Class<? extends Posting> posting,
         final String inputQuery) throws AppServerException {
      FullTextSession fullTextSession = Search.getFullTextSession(getSession());
      int size = 0;
      try {
         QueryParser parser =
               new QueryParser(Constants.INDEXING_SEARCH_CONTENT,
                     new StandardAnalyzer());
         org.apache.lucene.search.Query query = parser.parse(inputQuery);
         FullTextQuery fullQuery =
               fullTextSession.createFullTextQuery(query, posting);
         size = fullQuery.getResultSize();
      } catch (Exception ex) {
         log.error(
               String.format("Getting search size <%s, %s> failed!",
                     posting.getName(), inputQuery), ex);
         throw new AppServerException(MessageKeys.POSTING_DAO_GET_SEARCH_SIZE,
               ex.getCause());
      }
      return size;
   }

   public void setSessionFactory(SessionFactory sessionFactory) {
      this.sessionFactory = sessionFactory;
   }


}
