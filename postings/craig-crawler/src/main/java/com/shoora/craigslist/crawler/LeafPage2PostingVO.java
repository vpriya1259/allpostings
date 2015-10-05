package com.shoora.craigslist.crawler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.indusborn.common.ResultObject;
import com.indusborn.domain.User;
import com.indusborn.geocode.GAddress;
import com.indusborn.geocode.GGcoder;
import com.indusborn.repository.MailDao;
import com.indusborn.repository.UserDao;
import com.indusborn.service.PostingService;
import com.indusborn.ui.domain.ImageVO;
import com.indusborn.ui.domain.PostingVO;
import com.shoora.craigslist.dataobject.CImage;
import com.shoora.craigslist.dataobject.LeafPage;

public class LeafPage2PostingVO {
   private static final Log log = LogFactory.getLog(LeafPage2PostingVO.class); 
   private UserDao userDao;
   private GGcoder geoCoder;
   private PostingService postingService;
   private String userEmail;
   private MailDao mailDao;

   public UserDao getUserDao() {
      return userDao;
   }

   public GGcoder getGeoCoder() {
      return geoCoder;
   }

   public PostingService getPostingService() {
      return postingService;
   }
   
   public MailDao getMailDao() {
      return mailDao;
   }

   public LeafPage2PostingVO(UserDao userDao, MailDao mailDao, GGcoder geoCoder,
         PostingService postingService, String userEmail) {
      this.userDao = userDao;
      this.geoCoder = geoCoder;
      this.postingService = postingService;
      this.userEmail = userEmail;
      this.mailDao = mailDao;
   }
   
   public boolean saveLeafPage(LeafPage leafPage) {
      PostingVO postingVO = preparePostingVO(leafPage);
      if (postingVO == null) {
         log.warn(String.format("Failed to save leaf page %s", leafPage));
         return false;
      }
      ResultObject result = postingService.addPosting(postingVO);
      if (result.hasError()) {
         log.warn(String.format("Failed to save leaf page %s", leafPage));
         return false;
      }
      log.info(String.format("Saved a leaf page %s", leafPage));
      return true;
   }
   
   public PostingVO preparePostingVO(LeafPage leafPage) {
      PostingVO postingVO = new PostingVO();
      postingVO.setCatgry(leafPage.getCatgry());
      postingVO.setSubCatgry(leafPage.getSubCatgry());
      User user = userDao.getUser(userEmail);
      if (user == null) {
         return null;
      }
      postingVO.setOwnerId(userDao.getUser(userEmail).getId());
      postingVO.setCreated(leafPage.getCreated());
      postingVO.setUpdated(leafPage.getCreated());
      postingVO.setType("Offered");
      postingVO.setTitle(leafPage.getTitle());
      postingVO.setDesc(leafPage.getDesc());
      postingVO.setPrice(leafPage.getPrice());
      postingVO.setCurrency(leafPage.getCurrency());
      postingVO.setSrc(leafPage.getSrcUrl());
      try {
         if (leafPage.getEmail() != null) {
            postingVO.setEmail(leafPage.getEmail());
         } else {
            postingVO.setEmail(mailDao.createFuzzyAddress(userEmail));
         }
      } catch(Exception ex) {
         log.info(String.format("Fetching fuzzy address failed for %s", leafPage));
         return null;
      }

      GAddress gAddress = null;
      try {
         postingVO.setAddress(leafPage.getAddress());
         gAddress = geoCoder.geocode(leafPage.getAddress());
         postingVO.setLat(gAddress.getLat());
         postingVO.setLng(gAddress.getLng());
      } catch (Exception ex) {
         log.info("Geocoding the address failed!" + leafPage.getAddress());
         return null;
      }

      if (leafPage.getPrice() != null) {
         postingVO.setPrice(leafPage.getPrice());
      }
      for (CImage image:leafPage.getImages()) {
         ImageVO imageVO = new ImageVO();
         imageVO.setData(image.getContent());
         postingVO.imageVOs.add(imageVO);
      }
      return postingVO;
   }

}
