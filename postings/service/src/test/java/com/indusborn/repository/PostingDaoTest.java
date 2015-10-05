package com.indusborn.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.indusborn.common.AppServerException;
import com.indusborn.common.Constants;
import com.indusborn.common.IndusBornTestParam;
import com.indusborn.domain.Posting;
import com.indusborn.domain.PostingImage;
import com.indusborn.domain.Rental;
import com.indusborn.domain.Roommate;
import com.indusborn.domain.User;
import com.indusborn.geocode.GAddress;
import com.indusborn.util.CatgryUtil;
import com.indusborn.util.TimeUtil;

public class PostingDaoTest extends IndusBornTestParam {
   private static Log log = LogFactory.getLog(PostingDaoTest.class);

   //Posting CRUD operations
   @Test(dataProvider = "postingParams")
   public void crudPosting(Map<String, ArrayList<String>> catgries,
         String[] titles, String[] addrs, String[] emails, String[] currencies,
         String[] types) throws Exception {
      String catgry =
            (String) catgries.keySet().toArray()[generator.nextInt(catgries
                  .size())];
      int subCatgryLen = catgries.get(catgry).size();
      String subCatgry =
            catgries.get(catgry).get(generator.nextInt(subCatgryLen));
      log.info(String.format("AddPosting <%s, %s>", catgry, subCatgry));
      String title = titles[generator.nextInt(titles.length)];
      String addr = addrs[generator.nextInt(addrs.length)];
      String email = emails[generator.nextInt(emails.length)];
      String currency = currencies[generator.nextInt(currencies.length)];
      String type = types[generator.nextInt(types.length)];
      Posting posting =
            preparePosting(catgry, subCatgry, title, addr, email, currency,
                  type);
      //Create
      postingDao.addPosting(posting);
      log.info("Posting ID" + posting.getId());
      long postingId = posting.getId();
      posting = null;

      //Read
      posting = postingDao.getPosting(postingId);
      Assert.assertNotNull(posting);

      //Update
      Set<PostingImage> images = posting.getImages();
      for (PostingImage image : images) {
         image.setStatus(PostingImage.Status.DELETE);
      }
      posting.setTitle(posting.getTitle() + " Updated "
            + System.currentTimeMillis());

      //Update -- Delete all the images and update title
      postingDao.addPosting(posting);

      posting = null;
      //Read
      posting = postingDao.getPosting(postingId);
      Assert.assertEquals(posting.getImages().size(), 0);

      //Delete
      postingDao.delPosting(posting);

      //Read
      posting = postingDao.getPosting(postingId);
      Assert.assertNull(posting);
   }

   //Get all postings
   @Test(dataProvider="postingParams")
   public void allPostings(Map<String, ArrayList<String>> catgries,
         String[] titles, String[] addrs, String[] emails, String[] currencies,
         String[] types) throws Exception {

      int totalPostings = 5;
      long startTime = System.currentTimeMillis();
      //Add postings
      List<Long> postingIds = new ArrayList<Long>();
      for (int i = 0; i < totalPostings; i++) {
         String catgry =
               (String) catgries.keySet().toArray()[generator.nextInt(catgries
                     .size())];
         int subCatgryLen = catgries.get(catgry).size();
         String subCatgry =
               catgries.get(catgry).get(generator.nextInt(subCatgryLen));
         log.info(String.format("AddPosting <%s, %s>", catgry, subCatgry));
         String title = titles[generator.nextInt(titles.length)];
         String addr = addrs[generator.nextInt(addrs.length)];
         String email = emails[generator.nextInt(emails.length)];
         String currency = currencies[generator.nextInt(currencies.length)];
         String type = types[generator.nextInt(types.length)];
         Posting posting =
               preparePosting(catgry, subCatgry, title, addr, email, currency,
                     type);
         postingDao.addPosting(posting);
         postingIds.add(posting.getId());
      }
      
      //Get postings
      List<? extends Posting> postings = postingDao.getPostings(Posting.class, 0, 100, startTime);
      Assert.assertEquals(postings.size(), totalPostings);
      
      //Delete postings
      for(Long postingId:postingIds) {
         Posting posting = postingDao.getPosting(postingId);
         postingDao.delPosting(posting);
      }
   }

   //Get user postings
   @Test(dataProvider="postingParams")
   public void userPostings(Map<String, ArrayList<String>> catgries,
         String[] titles, String[] addrs, String[] emails, String[] currencies,
         String[] types) throws Exception {

      int totalPostings = 5;
      long startTime = System.currentTimeMillis();
      String email = emails[generator.nextInt(emails.length)];
      
      //Add postings
      List<Long> postingIds = new ArrayList<Long>();
      for (int i = 0; i < totalPostings; i++) {
         String catgry =
               (String) catgries.keySet().toArray()[generator.nextInt(catgries
                     .size())];
         int subCatgryLen = catgries.get(catgry).size();
         String subCatgry =
               catgries.get(catgry).get(generator.nextInt(subCatgryLen));
         log.info(String.format("AddPosting <%s, %s>", catgry, subCatgry));
         String title = titles[generator.nextInt(titles.length)];
         String addr = addrs[generator.nextInt(addrs.length)];
         String currency = currencies[generator.nextInt(currencies.length)];
         String type = types[generator.nextInt(types.length)];
         Posting posting =
               preparePosting(catgry, subCatgry, title, addr, email, currency,
                     type);
         postingDao.addPosting(posting);
         postingIds.add(posting.getId());
      }
      
      //Get user
      User user = userDao.getUser(email);
      Assert.assertNotNull(user);
      
      //Get user postings
      List<? extends Posting> postings = postingDao.getUserPostings(user.getId(), 0, 100, startTime);
      
      Assert.assertEquals(postings.size(), totalPostings);
      
      //Delete postings
      for(Long postingId:postingIds) {
         Posting posting = postingDao.getPosting(postingId);
         postingDao.delPosting(posting);
      }
   }
   
   public Posting preparePosting(String catgry, String subCatgry, String title,
         String addr, String email, String currency, String type)
         throws Exception {
      Posting posting;
      if (catgry.equalsIgnoreCase(Constants.RENTAL)) {
         posting = new Rental();
      } else if (catgry.equalsIgnoreCase(Constants.ROOMMATE)) {
         posting = new Roommate();
      } else if (catgry.equalsIgnoreCase(Constants.CAR)) {
         posting = new Roommate();
      } else if (catgry.equalsIgnoreCase(Constants.BUYSELL)) {
         posting = new Roommate();
      } else {
         posting = new Posting();
      }
      posting.setOwner(userDao.getUser(email));
      posting.setCreated(System.currentTimeMillis());
      posting.setUpdated(System.currentTimeMillis());
      posting.setSubCatgry(subCatgry);
      posting.setTitle(title);
      posting.setDesc(desc + " "
            + new Date(System.currentTimeMillis()).toString());
      posting.setPrice("100");

      if (online) {
         posting.setEmail(mailDao.createFuzzyAddress(email));
      } else {
         posting.setEmail(email);
      }

      if (online) {
         try {
            posting.setAddress(addr);
            GAddress gAddr = geoCoder.geocode(addr);
            posting.setLat(gAddr.getLat());
            posting.setLng(gAddr.getLng());
         } catch (Exception ex) {
            log.info("Couldn't geocode the address " + addr);
         }
      } else {
         posting.setLat(generator.nextDouble());
         posting.setLng(generator.nextDouble());
      }
      posting.setPrice("100");
      posting.setType(type);
      posting.addPostingImage(new PostingImage());
      posting.addPostingImage(new PostingImage());
      return posting;
   }
}
