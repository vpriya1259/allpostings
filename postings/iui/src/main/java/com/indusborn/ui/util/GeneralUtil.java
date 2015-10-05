/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * GeneralUtil.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.ui.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.flash.Message;
import org.springframework.web.flash.MessageType;

import com.indusborn.common.Constants;
import com.indusborn.common.ErrorObject;
import com.indusborn.common.MessageKeys;
import com.indusborn.common.MessageValues;
import com.indusborn.ui.domain.CurrencyVO;
import com.indusborn.ui.domain.Radius;
import com.indusborn.ui.domain.RegionVO;
import com.indusborn.util.LRUCache;

public class GeneralUtil {
   public static final int LRU_CACHE_SIZE = 1000;

   public static final List<Radius> radii = new ArrayList<Radius>();
   public static final List<CurrencyVO> currencies =
         new ArrayList<CurrencyVO>();
   private static LRUCache<String, RegionVO> locRegionMap =
         new LRUCache<String, RegionVO>(LRU_CACHE_SIZE);
   private static Map<String, String> errorMessages =
         new HashMap<String, String>();

   static {
      radii.add(new Radius("10", 10));
      radii.add(new Radius("20", 20));
      radii.add(new Radius("50", 50));
/*      radii.add(new Radius("100", 100));
      radii.add(new Radius("200", 200));
      radii.add(new Radius("400", 400));
      radii.add(new Radius("1000", 1000));
*/
      currencies.add(new CurrencyVO(Constants.CURRENCY_USD,
            Constants.CURRENCY_USD));
      currencies.add(new CurrencyVO(Constants.CURRENCY_INR,
            Constants.CURRENCY_INR));
      errorMessages.put(MessageKeys.INVALID_SEARCH_REQUEST,
            MessageValues.INVALID_SEARCH_REQUEST);
      errorMessages.put(MessageKeys.INVALID_CATGRY,
            MessageValues.INVALID_CATGRY);
      errorMessages.put(MessageKeys.INVALID_LOCATION,
            MessageValues.INVALID_LOCATION);
      errorMessages.put(MessageKeys.INVALID_POSTING_ID,
            MessageValues.INVALID_POSTING_ID);
      errorMessages.put(MessageKeys.POSTINGS_NOT_FOUND,
            MessageValues.POSTINGS_NOT_FOUND);
      errorMessages.put(MessageKeys.POSTING_NOT_FOUND,
            MessageValues.POSTING_NOT_FOUND);
      errorMessages.put(MessageKeys.APP_SERVER_ERROR, MessageValues.APP_SERVER_ERROR);
      errorMessages.put(MessageKeys.POSTING_ADD_FAIL, MessageValues.POSTING_ADD_FAIL);
      errorMessages.put(MessageKeys.POSTING_DEL_FAIL, MessageValues.POSTING_DEL_FAIL);
      errorMessages.put(MessageKeys.POSTING_IMG_NOT_FOUND, MessageValues.POSTING_IMG_NOT_FOUND);
      errorMessages.put(MessageKeys.POSTING_SEARCH_FAIL, MessageValues.POSTING_SEARCH_FAIL);
      errorMessages.put(MessageKeys.USER_DAO_USER_DONT_EXIST, MessageValues.USER_DAO_USER_DONT_EXIST);
   }

   public static void putRegionVO(String locName, RegionVO regionVO) {
      locRegionMap.put(locName, regionVO);
   }

   public static RegionVO getRegionVO(String locName) {
      return locRegionMap.get(locName);
   }


   public static String getCurrencySymbol(String code) {
      if (code.equalsIgnoreCase("USD")) {
         return "$";
      } else if (code.equalsIgnoreCase("INR")) {
         return "Rs.";
      }
      return "$";
   }


   public static String getCurrencyText(String code, String value) {
      StringBuffer currText = new StringBuffer();
      if (code.equalsIgnoreCase("USD")) {
         currText.append("$");
      } else if (code.equalsIgnoreCase("INR")) {
         currText.append("Rs.");
      }
      currText.append(value);
      return currText.toString();
   }

   public static RegionVO getRegionVO(HttpServletRequest request) {
      RegionVO regionVO = new RegionVO();

      try {
         Double lat = Double.parseDouble(request.getParameter("lat"));
         regionVO.setLat(lat);
         Double lng = Double.parseDouble(request.getParameter("lng"));
         regionVO.setLng(lng);
         String locality = request.getParameter("locality");
         regionVO.setLocalityName(locality);
         String countryCode = request.getParameter("countryCode");
         regionVO.setCountryCode(countryCode);
         String country = request.getParameter("country");
         regionVO.setCountryName(country);
         String adminArea = request.getParameter("adminArea");
         regionVO.setAdminAreaName(adminArea);
         String subAdminArea = request.getParameter("subAdminArea");
         regionVO.setSubAdminAreaName(subAdminArea);

      } catch (Exception ex) {
         return null;
      }

      return regionVO;
   }

   public static List<Radius> getRadii() {
      return radii;
   }

   public static List<CurrencyVO> getCurrencies() {
      return currencies;
   }

   public static Message getMessage(ErrorObject error) {
      String message = errorMessages.get(error.getMessage().getMessageKey());
      if (message == null) {
         message = MessageValues.APP_SERVER_ERROR;
      }
      return new Message(MessageType.ERROR, message);
   }

}
