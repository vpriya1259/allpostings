package com.indusborn.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.indusborn.common.IndusBornTestParam;
import com.indusborn.common.ResultObject;
import com.indusborn.domain.Posting;
import com.indusborn.geocode.GAddress;
import com.indusborn.ui.domain.PostingVO;
import com.indusborn.ui.domain.RegionVO;
import com.indusborn.util.Util;

public class PostingServiceTest extends IndusBornTestParam {
   private static Log log = LogFactory.getLog(PostingServiceTest.class);

   @Test(dataProvider = "existingUserEmails")
   public void getPostings(String[] emails) {
      ResultObject postings = postingService.getPostings(Posting.class, 0, 10);
      Assert.assertTrue(postings.getData() != null);
   }
   //@Test(dataProvider = "addPostingsParams")
   public void addPostingsParams(Map<String, ArrayList<String>> catgries,
         String[] titles, String[] addrs, String[] emails, String[] currencies)
         throws Exception {
      log.info("PostingService#addPostings");
      int catgrySize = catgries.size();

      for (int i = 0; i < numOfAdds; i++) {
         String catgry =
               (String) catgries.keySet().toArray()[generator
                     .nextInt(catgrySize)];
         int subCatgryLen = catgries.get(catgry).size();
         String subCatgry =
               catgries.get(catgry).get(generator.nextInt(subCatgryLen));
         log.info(String
               .format("AddPostingsParams <%s, %s>", catgry, subCatgry));
         PostingVO postingVO = new PostingVO();
         postingVO.setCatgry(catgry);
         postingVO.setOwnerId(userDao.getUser(emails[i % emails.length])
               .getId());
         postingVO.setCreated(System.currentTimeMillis());
         postingVO.setUpdated(System.currentTimeMillis());
         if (i % 2 == 0) {
            postingVO.setType("Offered");
         } else {
            postingVO.setType("Wanted");
         }
         postingVO.setSubCatgry(subCatgry);
         postingVO.setTitle(titles[i % titles.length] + " "
               + System.currentTimeMillis());
         postingVO.setDesc(desc + " "
               + new Date(System.currentTimeMillis()).toString());
         postingVO.setPrice("100");
         if (online) {
            postingVO.setEmail(mailDao.createFuzzyAddress(emails[i
                  % emails.length]));
         } else {
            postingVO.setEmail(emails[i % emails.length]);
         }

         String addr = addrs[i % addrs.length];
         GAddress gAddress = null;
         if (online) {
            try {
               postingVO.setAddress(addr);
               gAddress = geoCoder.geocode(addr);
               postingVO.setLat(gAddress.getLat());
               postingVO.setLng(gAddress.getLng());
            } catch (Exception ex) {
               log.info("Couldn't geocode the address " + addr);
            }
         } else {
            postingVO.setLat(SUNNYVALE_LAT);
            postingVO.setLng(SUNNYVALE_LNG);
         }

         postingVO.setPrice("100");
         postingVO.imageVOs.add(Util.getImageVO(image1));
         postingVO.imageVOs.add(Util.getImageVO(image2));

         if (gAddress != null) {
            RegionVO regVO = new RegionVO();
            if (gAddress.getCountryName() != null
                  && gAddress.getCountryName().length() > 0)
               regVO.setCountryName(gAddress.getCountryName());
            if (gAddress.getCountryCode() != null
                  && gAddress.getCountryCode().length() > 0)
               regVO.setCountryCode(gAddress.getCountryCode());
            if (gAddress.getAdminAreaName() != null
                  && gAddress.getAdminAreaName().length() > 0)
               regVO.setAdminAreaName(gAddress.getAdminAreaName());
            if (gAddress.getSubAdminAreaName() != null
                  && gAddress.getSubAdminAreaName().length() > 0)
               regVO.setSubAdminAreaName(gAddress.getSubAdminAreaName());
            if (gAddress.getLocalityName() != null
                  && gAddress.getLocalityName().length() > 0)
               regVO.setLocalityName(gAddress.getLocalityName());
            postingVO.setRegionVO(regVO);
         }

         postingService.addPosting(postingVO);
      }
   }

}
