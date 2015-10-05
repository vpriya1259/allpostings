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
package com.indusborn.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.indusborn.common.Constants;
import com.indusborn.common.MessageKeys;
import com.indusborn.common.MessageValues;
import com.indusborn.ui.domain.CurrencyVO;
import com.indusborn.ui.domain.Radius;
import com.indusborn.ui.domain.RegionVO;

public class GeneralUtil {

   public static final int MAX_SHORT_TITLE_CHARS = 60;
   private static final int SHORT_DESC_TOTAL_LINES = 2;
   public static final int SHORT_DESC_LINE_LENGTH = 120;

   public static int getStartIndex(int pageNum, int pageSize, int total) {
      int startIndex = pageNum * pageSize;
      if (startIndex >= total) {
         return -1;
      }
      return startIndex;
   }

   public static int getEndIndex(int pageNum, int pageSize, int total) {
      int startIndex = getStartIndex(pageNum, pageSize, total);
      if (startIndex == -1) {
         return -1;
      }
      int endIndex = startIndex + pageSize;
      if (endIndex > total) {
         return total;
      }
      return endIndex;
   }

   public static String convertStreamToString(InputStream is) throws Exception {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      StringBuilder sb = new StringBuilder();
      String line = null;
      while ((line = reader.readLine()) != null) {
         sb.append(line + "\n");
      }
      is.close();
      return sb.toString();
   }

   public static String getShortenedDesc(String desc) {
      StringTokenizer tokenizer = new StringTokenizer(desc, "\n");
      StringBuffer shortenedDesc = new StringBuffer();
      List<String> lines = new ArrayList<String>();

      while (tokenizer.hasMoreTokens()) {
         lines.add(tokenizer.nextToken());
      }

      int counter = 0;
      while (counter < lines.size() && counter < SHORT_DESC_TOTAL_LINES) {
         shortenedDesc = shortenedDesc.append(lines.get(counter) + "\n");
         counter++;
      }

      if (shortenedDesc.toString().length() > SHORT_DESC_LINE_LENGTH) {
         return shortenedDesc.substring(0, SHORT_DESC_LINE_LENGTH) + "...";
      } else {
         return shortenedDesc.toString();
      }
   }

   public static String getShortenedTitle(String title) {
      StringTokenizer tokenizer = new StringTokenizer(title);
      StringBuffer buff = new StringBuffer();

      while (tokenizer.hasMoreTokens()) {
         if (buff.toString().length() > MAX_SHORT_TITLE_CHARS) {
            buff.append("...");
            return buff.toString();
         }
         buff.append(tokenizer.nextToken() + " ");
      }
      return buff.toString();
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

   public static String filterSearchText(String searchText) {
      if (searchText.startsWith("*") || searchText.startsWith(":")
            || searchText.startsWith("?")) {
         return searchText.substring(1);
      }
      return searchText;
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

   public static String getSafeText(String text) {
      if (text == null) {
         return "";
      }
      String safeText = text.replaceAll("/", " ");
      return safeText.trim();
   }


   public static String getHtmlDesc(String desc) {
      Pattern pattern =
            Pattern.compile("\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)"
                  + "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov"
                  + "|mil|biz|info|mobi|name|aero|jobs|museum"
                  + "|travel|[a-z]{2}))(:[\\d]{1,5})?"
                  + "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?"
                  + "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?"
                  + "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)"
                  + "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?"
                  + "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*"
                  + "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");

      Matcher matcher = pattern.matcher(desc);
      HashSet<String> urlList = new HashSet<String>();
      while (matcher.find()) {
         urlList.add(matcher.group());
      }

      StringBuffer htmlDesc = new StringBuffer();
      StringTokenizer lineTokenizer = new StringTokenizer(desc, "\n");
      while (lineTokenizer.hasMoreTokens()) {
         String line = lineTokenizer.nextToken();
         StringTokenizer wordTokenizer = new StringTokenizer(line);
         while (wordTokenizer.hasMoreTokens()) {
            String word = wordTokenizer.nextToken();
            if (urlList.contains(word)) {
               word = String.format("<a href='%s'>%s</a>", word, word);
            }
            htmlDesc.append(word + " ");
         }

         htmlDesc.append("<br>");
      }
      return htmlDesc.toString();
   }

}
