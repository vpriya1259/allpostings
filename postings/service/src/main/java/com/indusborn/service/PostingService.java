package com.indusborn.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.indusborn.common.AppServerException;
import com.indusborn.common.Constants;
import com.indusborn.common.ErrorObject;
import com.indusborn.common.MessageKeys;
import com.indusborn.common.ResultObject;
import com.indusborn.domain.Buysell;
import com.indusborn.domain.Car;
import com.indusborn.domain.Job;
import com.indusborn.domain.Posting;
import com.indusborn.domain.PostingImage;
import com.indusborn.domain.Rental;
import com.indusborn.domain.Roommate;
import com.indusborn.domain.User;
import com.indusborn.geocode.GAddress;
import com.indusborn.geocode.GGcoder;
import com.indusborn.geocode.SimpleGeo;
import com.indusborn.repository.MailDao;
import com.indusborn.repository.PostingDao;
import com.indusborn.repository.UserDao;
import com.indusborn.ui.domain.ImageVO;
import com.indusborn.ui.domain.MailVO;
import com.indusborn.ui.domain.PostingVO;
import com.indusborn.ui.domain.PostingsVO;
import com.indusborn.util.CatgryUtil;
import com.indusborn.util.GeneralUtil;
import com.indusborn.util.TimeUtil;

public class PostingService {
   private static Log log = LogFactory.getLog(PostingService.class);
   private PostingDao postingDao;
   private UserDao userDao;
   private MailDao mailDao;
   private GGcoder geoCoder;
   private SimpleGeo simpleGeo;
   private PostingImageService postingImageService;

   public void setPostingImageService(PostingImageService postingImageService) {
      this.postingImageService = postingImageService;
   }

   public void setSimpleGeo(SimpleGeo simpleGeo) {
      this.simpleGeo = simpleGeo;
   }

   public void setGeoCoder(GGcoder geoCoder) {
      this.geoCoder = geoCoder;
   }

   public void setMailDao(MailDao mailDao) {
      this.mailDao = mailDao;
   }

   public void setPostingDao(PostingDao postingDao) {
      this.postingDao = postingDao;
   }

   public void setUserDao(UserDao userDao) {
      this.userDao = userDao;
   }

   /**
    * Prepares the posting
    * 
    * @param <U>
    * @param posting
    * @return
    * @throws Exception
    */
   private <U extends Posting> PostingVO preparePostingVO(U posting)
         throws SQLException {
      //posting = postingDao.getPosting(posting.getId());
      PostingVO postingVO = new PostingVO();
      postingVO.setId(posting.getId());
      postingVO.setUpdated(posting.getUpdated());
      postingVO.setType(posting.getType());
      //TODO Based on the type of the class, set respective fields
      postingVO.setCatgry(CatgryUtil.getPostingCatgry(posting));
      postingVO.setSubCatgry(posting.getSubCatgry());
      postingVO.setTitle(posting.getTitle());
      postingVO.setDesc(posting.getDesc());
      String price = posting.getPrice();
      try {
         Double.parseDouble(price);
         postingVO.setRealPrice(true);
         String currency = posting.getCurrency();
         if (currency != null) {
            postingVO.setCurrency(currency);
            String symbol = GeneralUtil.getCurrencySymbol(currency);
            postingVO.setCurrencyCode(symbol);
            postingVO.setPrice(posting.getPrice());
         }
      } catch (Exception ex) {
         //Ignore
      }
      postingVO.setEmail(posting.getEmail());
      postingVO.setAddress(posting.getAddress());
      postingVO.setLat(posting.getLat());
      postingVO.setLng(posting.getLng());
      postingVO.setSrc(posting.getSrc());
      postingVO.setOwnerId(posting.getOwner().getId());
      for (PostingImage img : posting.getImages()) {
         ImageVO imageVO = new ImageVO();
         imageVO.setId(img.getId());
         postingVO.getImageVOs().add(imageVO);
      }
      return postingVO;
   }

   /**
    * Prepares PostingsVO
    * 
    * @param postings
    * @param pageNum
    * @param pageSize
    * @param total
    * @return
    * @throws Exception
    */
   private PostingsVO preparePostingsVO(List<? extends Posting> postings,
         int pageNum, int pageSize, Long total) throws SQLException {
      PostingsVO postingsVO = new PostingsVO();
      postingsVO.setPageNum(pageNum);
      postingsVO.setPageSize(pageSize);
      postingsVO.setTotal(total);

      for (Posting posting : postings) {
         postingsVO.getPostingVOs().add(preparePostingVO(posting));
      }
      return postingsVO;
   }

   /**
    * Gets list of postings
    * 
    * @param posting
    * @param pageNum
    * @param pageSize
    * @return
    */
   public ResultObject getPostings(Class<? extends Posting> posting,
         int pageNum, int pageSize) {
      ResultObject result = new ResultObject();
      long started = System.currentTimeMillis();
      try {
         long startTime = TimeUtil.getPostingStartTime();
         List<? extends Posting> postings =
               postingDao.getPostings(posting, pageNum, pageSize, startTime);
         if (postings == null) {
            result.addError(new AppServerException(
                  MessageKeys.POSTINGS_NOT_FOUND).getErrorObject());
            return result;
         }
         Long total = postingDao.getPostingSize(posting, startTime);
         PostingsVO postingsVO =
               preparePostingsVO(postings, pageNum, pageSize, total);
         postingsVO.setCatgry(CatgryUtil.getPostingCatgry(posting));
         result.setData(postingsVO);
      } catch (Exception ex) {
         log.error(
               String.format("Getting %s postings failed!", posting.getName()),
               ex);
         result.addError(new AppServerException().getErrorObject());
      }
      long ended = System.currentTimeMillis();
      if (log.isDebugEnabled()) {
         log.debug(String.format("Took %d to retrive!", (ended - started)));
      }
      return result;
   }

   /**
    * Returns user postings size
    * 
    * @param userId
    * @return
    */
   public ResultObject getUserPostingsSize(int userId) {
      ResultObject result = new ResultObject();
      try {
         long startTime = TimeUtil.getPostingStartTime();
         Long size = postingDao.getUserPostingSize(userId, startTime);
         result.setData(size);
      } catch (Exception ex) {
         log.error(
               String.format("Getting user %d postings size failed!", userId),
               ex);
         result.addError(new AppServerException().getErrorObject());
      }
      return result;
   }

   /**
    * Gets user postings
    * 
    * @param userId
    * @param pageNum
    * @param pageSize
    * @return
    */
   public ResultObject getUserPostings(int userId, int pageNum, int pageSize) {
      ResultObject result = new ResultObject();
      long started = System.currentTimeMillis();
      try {
         long startTime = TimeUtil.getPostingStartTime();
         List<? extends Posting> postings =
               postingDao.getUserPostings(userId, pageNum, pageSize, startTime);
         if (postings == null) {
            result.addError(new AppServerException(
                  MessageKeys.POSTINGS_NOT_FOUND).getErrorObject());
            return result;
         }
         Long total = postingDao.getUserPostingSize(userId, startTime);
         PostingsVO postingsVO =
               preparePostingsVO(postings, pageNum, pageSize, total);
         result.setData(postingsVO);
      } catch (Exception ex) {
         log.error(String.format("Getting user %d postings failed!", userId),
               ex);
         result.addError(new AppServerException().getErrorObject());
      }
      long ended = System.currentTimeMillis();
      if (log.isDebugEnabled()) {
         log.debug(String.format("Took %d to retrieve!", (ended - started)));
      }
      return result;
   }

   /**
    * Adds or updates the posting
    * 
    * @param postingVO
    * @return
    */
   public ResultObject addPosting(PostingVO postingVO) {
      ResultObject result = new ResultObject();
      boolean isUpdate = false;
      try {
         String catgry = postingVO.getCatgry();
         Posting posting = CatgryUtil.createNewPosting(catgry);
         int userId = postingVO.getOwnerId();
         User owner = userDao.getUser(userId);
         posting.setOwner(owner);
         posting.setCreated(postingVO.getCreated());
         posting.setUpdated(postingVO.getUpdated());
         posting.setSubCatgry(postingVO.getSubCatgry());
         posting.setType(postingVO.getType());
         posting.setTitle(postingVO.getTitle());
         posting.setDesc(postingVO.getDesc());
         posting.setPrice(postingVO.getPrice());
         posting.setCurrency(postingVO.getCurrency());
         String email = postingVO.getEmail();
         posting.setEmail(email);
         if (postingVO.isFuzzyEmail()) {
            String fuzzyEmail = mailDao.getFuzzyAddress(userId, email);
            posting.setEmail(fuzzyEmail);
         }
         posting.setAddress(postingVO.getAddress());
         posting.setLat(postingVO.getLat());
         posting.setLng(postingVO.getLng());
         posting.setSrc(postingVO.getSrc());
         posting.setContent(CatgryUtil.getContent(postingVO));

         List<ImageVO> imageVOs = postingVO.getImageVOs();
         List<ImageVO> newImageVOs = new ArrayList<ImageVO>();
         List<ImageVO> delImageVOs = new ArrayList<ImageVO>();
         Set<Long> knownImageIds = new HashSet<Long>();
         for (ImageVO imageVO : imageVOs) {
            PostingImage postingImage = new PostingImage();
            if (imageVO.getStatus() == PostingImage.Status.NEW) {
               newImageVOs.add(imageVO);
            } else {
               if (imageVO.getStatus() == PostingImage.Status.DELETE) {
                  //Delete from the file system
                  delImageVOs.add(imageVO);
               }
               postingImage.setId(imageVO.getId()); //True for KEEP and DELETE
               knownImageIds.add(imageVO.getId());
            }
            postingImage.setStatus(imageVO.getStatus());
            posting.addPostingImage(postingImage);
         }

         long postingId = postingVO.getId();
         if (postingId > 0) { //Update
            isUpdate = true;
            posting.setId(postingId);
         }

         postingDao.addPosting(posting);

         //Save new images onto the file system. Get Image IDs from the DB
         List<PostingImage> dbSavedImages =
               new ArrayList<PostingImage>(posting.getImages());
         if (dbSavedImages != null && dbSavedImages.size() > 0) {
            int counter = 0;
            for (PostingImage image : dbSavedImages) {
               if (!knownImageIds.contains(image.getId())
                     && (counter < newImageVOs.size())) {
                  postingImageService.savePostingImage(posting.getId(),
                        image.getId(), newImageVOs.get(counter).getData());
                  counter++;
               }
            }
         }
         //Delete images from the file system
         for (ImageVO imageVO : delImageVOs) {
            postingImageService.delPostingImage(posting.getId(),
                  imageVO.getId());
         }

         mailDao.sendAddOrUpdatePostingMail(owner, posting, isUpdate);
         result.setData("Successful");
      } catch (Exception ex) {
         log.error(String.format("Adding posting %s failed!", postingVO), ex);
         result.addError(new AppServerException(MessageKeys.POSTING_ADD_FAIL)
               .getErrorObject());
      }
      return result;

   }

   /**
    * Deletes posting
    * 
    * @param postingId
    * @return
    */
   public ResultObject deletePosting(long postingId) {
      ResultObject result = new ResultObject();
      try {
         Posting posting = postingDao.getPosting(postingId);
         if (posting == null) {
            log.error(String.format("Posting %d not found!", postingId));
            result.addError(new AppServerException(
                  MessageKeys.POSTING_NOT_FOUND).getErrorObject());
            return result;
         }
         String fuzzyEmail = posting.getEmail();
         postingDao.delPosting(posting);
         if (fuzzyEmail.indexOf(Constants.FUZZY_EMAIL_DOMAIN) != -1) {
            log.info("Purging the email address " + fuzzyEmail);
            mailDao.deleteFuzzyAddress(fuzzyEmail);
         }
         result.setData("Successful");
      } catch (Exception ex) {
         log.error(String.format("Deleting posting %d failed!", postingId), ex);
         result.addError(new AppServerException(MessageKeys.POSTING_DEL_FAIL)
               .getErrorObject());
      }
      return result;
   }

   /**
    * Gets the posting
    * 
    * @param postingId
    * @return
    */
   public ResultObject getPosting(long postingId) {
      ResultObject result = new ResultObject();
      try {
         Posting posting = postingDao.getPosting(postingId);
         if (posting == null) {
            log.error(String.format("Posting %d not found!", postingId));
            result.addError(new AppServerException(
                  MessageKeys.POSTING_NOT_FOUND).getErrorObject());
            return result;
         }
         result.setData(preparePostingVO(posting));
      } catch (Exception ex) {
         log.error(String.format("Getting posting %d failed!", postingId), ex);
         result.addError(new AppServerException().getErrorObject());
      }
      return result;
   }

   /**
    * Gets the posting image
    * 
    * @param postingImageId
    * @return
    */
   public ResultObject getPostingImage(long postingImageId) {
      ResultObject result = new ResultObject();
      try {
         PostingImage postingImage = postingDao.getPostingImage(postingImageId);
         if (postingImage == null) {
            log.error(String.format("PostingImage %d not found!",
                  postingImageId));
            result.addError(new AppServerException(
                  MessageKeys.POSTING_IMG_NOT_FOUND).getErrorObject());
            return result;
         }
         ImageVO imageVO = new ImageVO();
         imageVO.setId(postingImage.getId());
         result.setData(imageVO);
      } catch (Exception ex) {
         log.error(String.format("Gettin the posting image %d failed!",
               postingImageId), ex);
         result.addError(new AppServerException().getErrorObject());
      }
      return result;
   }

   /**
    * Gets search results
    * 
    * @param posting
    * @param searchText
    * @param ids
    * @param pageNum
    * @param pageSize
    * @return
    * @throws AppServerException
    * @throws SQLException
    */
   private PostingsVO getSearchResults(Class<? extends Posting> posting,
         String searchText, List<Long> ids, int pageNum, int pageSize)
         throws AppServerException, SQLException {
      List<Posting> postings = new ArrayList<Posting>();
      Long total = new Long(0);
      if (ids != null && ids.size() > 0) {
         postings =
               postingDao.search(posting, searchText, ids, pageNum, pageSize);
         if (postings != null) {
            total = new Long(postings.size());
         }
      }
      PostingsVO newPostingsVO =
            preparePostingsVO(postings, pageNum, pageSize, total);
      return newPostingsVO;
   }

   /**
    * Gets empty results object
    * 
    * @param pageNum
    * @param pageSize
    * @return
    */
   public ResultObject getNoSearchResultsObj(int pageNum, int pageSize) {
      ResultObject result = new ResultObject();
      PostingsVO noPostingsVO = new PostingsVO();
      noPostingsVO.setPageNum(pageNum);
      noPostingsVO.setPageSize(pageSize);
      result.setData(noPostingsVO);
      return result;
   }

   /**
    * Searches for the given query within the given radius
    * 
    * @param postingsVO
    * @return
    */
   public ResultObject search(PostingsVO postingsVO) {
      ResultObject result = new ResultObject();
      result.setData(postingsVO);
      String postingCatgry = postingsVO.getCatgry();
      Class<? extends Posting> posting =
            CatgryUtil.getPostingClass(postingCatgry);
      long started = System.currentTimeMillis();
      try {
         String locationText = postingsVO.getLocationText();
         Map<Long, Double> idDistMap = null;
         List<Long> ids = new ArrayList<Long>();

         if (locationText == null || locationText.length() == 0) {
            return result;
         }

         //Get input location's lat n lng
         double lat, lng;
         if (postingsVO.getRegionVO().getLat() != Double.MAX_VALUE
               && postingsVO.getRegionVO().getLng() != Double.MAX_VALUE) {
            lat = postingsVO.getRegionVO().getLat();
            lng = postingsVO.getRegionVO().getLng();
         } else {
            GAddress gAddress = geoCoder.geocode(locationText);
            if (gAddress == null) {
               result.addError(new ErrorObject(
                     MessageKeys.POSTING_SERVICE_CANT_GEOCODE));
               return result;
            }
            lat = gAddress.getLat();
            lng = gAddress.getLng();
         }

         //Get all the records in the square
         long startTime = TimeUtil.getPostingStartTime();
         idDistMap =
               simpleGeo.getPostingRecords(lat, lng, postingsVO.getRadius(),
                     startTime);

         if (idDistMap == null || idDistMap.size() == 0) {
            return result;
         }

         ids = new ArrayList<Long>(idDistMap.keySet());
         if (ids.size() > 5000) {
            ids = ids.subList(0, 5000); //Workaroun
         }
         log.info(String.format("Searching for <%s, %s>...", postingsVO
               .getLocationText(), (postingsVO.getSearchText() == null) ? ""
               : postingsVO.getSearchText()));

         //Get all matching records in the square
         PostingsVO newPostingsVO =
               getSearchResults(posting, postingsVO.getSearchText(),
                     ids, 0, ids.size());
         List<PostingVO> postingVOs = newPostingsVO.getPostingVOs();

         //Populate the distances
         for (PostingVO postingVO : postingVOs) {
            if (idDistMap.containsKey(postingVO.getId())) {
               postingVO.setDistance(idDistMap.get(postingVO.getId()));
            }
         }

         //Sort the postings based on the field
         if (postingsVO.getSortBy().equalsIgnoreCase(
               Constants.SORTBY_VALUE_DISTANCE)) {
            Collections.sort(postingVOs, new PostingDistanceComparator());
         }

         //Paginate the results
         int startIndex =
               GeneralUtil.getStartIndex(postingsVO.getPageNum(),
                     postingsVO.getPageSize(), postingVOs.size());
         int endIndex =
               GeneralUtil.getEndIndex(postingsVO.getPageNum(),
                     postingsVO.getPageSize(), postingVOs.size());

         List<PostingVO> postingVOPage = new ArrayList<PostingVO>();
         postingsVO.setTotal(newPostingsVO.getTotal());
         if (startIndex == -1 && endIndex == -1) {
            postingsVO.setPostingVOs(postingVOPage);
            result.setData(postingsVO);
            return result;
         }
         postingVOPage = postingVOs.subList(startIndex, endIndex);
         postingsVO.setPostingVOs(postingVOPage);
         result.setData(postingsVO);
      } catch (Exception ex) {
         log.error(
               String.format("Searching for the <%s, %s> failed!",
                     posting.getName(), postingsVO.getLocationText()), ex);
         result.addError(new AppServerException(MessageKeys.POSTING_SEARCH_FAIL)
               .getErrorObject());
      }
      long ended = System.currentTimeMillis();
      if (log.isDebugEnabled()) {
         log.debug(String.format("Search took %d...", (ended - started)));
      }
      return result;
   }

   /**
    * Posting updated time comparator
    */
   private static class PostingDistanceComparator implements
         Comparator<PostingVO> {
      public int compare(PostingVO post1, PostingVO post2) {
         return post1.getDistance().compareTo(post2.getDistance());
      }
   }

   /**
    * Delivers anonymous's mail to the person who posted it.
    * 
    * @param mailVO
    * @return
    */
   public ResultObject sendAnonymContactMail(MailVO mailVO) {
      ResultObject result = new ResultObject();
      try {
         mailDao.sendAnonymContactMail(mailVO);
         result.setData("Successful");
      } catch (AppServerException ex) {
         result.addError(ex.getErrorObject());
      } catch (Exception ex) {
         log.error(String.format("Delivering anonymous %s mail failed!",
               mailVO.getAnonymAddress()));
         result.addError(new AppServerException().getErrorObject());
      }
      return result;
   }


}
