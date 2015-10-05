package com.indusborn.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.indusborn.common.Constants;
import com.indusborn.domain.Buysell;
import com.indusborn.domain.Car;
import com.indusborn.domain.Job;
import com.indusborn.domain.Posting;
import com.indusborn.domain.Rental;
import com.indusborn.domain.Roommate;
import com.indusborn.ui.domain.CatgryVO;
import com.indusborn.ui.domain.PostingVO;

public class CatgryUtil {

   private static final Map<String, ArrayList<String>> catgriesMap =
         new HashMap<String, ArrayList<String>>();
   private static final ArrayList<CatgryVO> catgries = new ArrayList<CatgryVO>();
   private static final ArrayList<CatgryVO> allMainCatgries = new ArrayList<CatgryVO>();
   private static final Map<String, String> catgriesDescMap = new HashMap<String, String>();
   private static final Map<String, String> keywords = new HashMap<String, String>();
   private static final Map<String, Set<String>> keywordsTokens = new HashMap<String, Set<String>>();

   static {
      ArrayList<String> rentalSubCatgries = new ArrayList<String>();
      rentalSubCatgries.add(Constants.RENTAL_ONE_BR_ONE_BATH);
      rentalSubCatgries.add(Constants.RENTAL_TWO_BR_ONE_BATH);
      rentalSubCatgries.add(Constants.RENTAL_TWO_BR_ONE_HALF_BATH);
      rentalSubCatgries.add(Constants.RENTAL_TWO_BR_TWO_BATH);
      rentalSubCatgries.add(Constants.RENTAL_THREE_BR_ONE_BATH);
      rentalSubCatgries.add(Constants.RENTAL_THREE_BR_TWO_BATH);
      rentalSubCatgries.add(Constants.RENTAL_THREE_BR_TWO_HALF_BATH);
      rentalSubCatgries.add(Constants.RENTAL_THREE_BR_THREE_BATH);
      rentalSubCatgries.add(Constants.RENTAL_FOUR_BR_TWO_BATH);
      rentalSubCatgries.add(Constants.RENTAL_FOUR_BR_THREE_BATH);
      rentalSubCatgries.add(Constants.RENTAL_FOUR_BR_THREE_HALF_BATH);
      rentalSubCatgries.add(Constants.RENTAL_FOUR_BR_FOUR_BATH);
      rentalSubCatgries.add(Constants.RENTAL_STUDIO);
      rentalSubCatgries.add(Constants.RENTAL_TOWN_HOUSE);
      rentalSubCatgries.add(Constants.RENTAL_CONDO);
      rentalSubCatgries.add(Constants.RENTAL_OTHERS);
      catgriesMap.put(Constants.RENTAL, rentalSubCatgries);
      keywords.put(Constants.RENTAL, Constants.RENTAL_KEYWORDS);
      keywords.put(Constants.RENTAL_ONE_BR_ONE_BATH, Constants.RENTAL_KEYWORDS_ONE_BR_ONE_BATH);
      keywords.put(Constants.RENTAL_TWO_BR_ONE_BATH, Constants.RENTAL_KEYWORDS_TWO_BR_ONE_BATH);
      keywords.put(Constants.RENTAL_TWO_BR_ONE_HALF_BATH, Constants.RENTAL_KEYWORDS_TWO_BR_ONE_HALF_BATH);
      keywords.put(Constants.RENTAL_TWO_BR_TWO_BATH, Constants.RENTAL_KEYWORDS_TWO_BR_TWO_BATH);
      keywords.put(Constants.RENTAL_THREE_BR_ONE_BATH, Constants.RENTAL_KEYWORDS_THREE_BR_ONE_BATH);
      keywords.put(Constants.RENTAL_THREE_BR_TWO_BATH, Constants.RENTAL_KEYWORDS_THREE_BR_TWO_BATH);
      keywords.put(Constants.RENTAL_THREE_BR_TWO_HALF_BATH, Constants.RENTAL_KEYWORDS_THREE_BR_TWO_HALF_BATH);
      keywords.put(Constants.RENTAL_THREE_BR_THREE_BATH, Constants.RENTAL_KEYWORDS_THREE_BR_THREE_BATH);
      keywords.put(Constants.RENTAL_FOUR_BR_TWO_BATH, Constants.RENTAL_KEYWORDS_FOUR_BR_TWO_BATH);
      keywords.put(Constants.RENTAL_FOUR_BR_THREE_BATH, Constants.RENTAL_KEYWORDS_FOUR_BR_THREE_BATH);
      keywords.put(Constants.RENTAL_FOUR_BR_THREE_HALF_BATH, Constants.RENTAL_KEYWORDS_FOUR_BR_THREE_HALF_BATH);
      keywords.put(Constants.RENTAL_FOUR_BR_FOUR_BATH, Constants.RENTAL_KEYWORDS_FOUR_BR_FOUR_BATH);
      keywords.put(Constants.RENTAL_STUDIO, Constants.RENTAL_KEYWORDS_STUDIO);
      keywords.put(Constants.RENTAL_TOWN_HOUSE, Constants.RENTAL_KEYWORDS_TOWN_HOUSE);
      keywords.put(Constants.RENTAL_CONDO, Constants.RENTAL_KEYWORDS_CONDO);
      keywords.put(Constants.RENTAL_OTHERS, Constants.RENTAL_KEYWORDS_OTHERS);
      

      ArrayList<String> roommateSubCatgries = new ArrayList<String>();
      roommateSubCatgries.add(Constants.ROOMMATE_ROOM_FOR_MALE);
      roommateSubCatgries.add(Constants.ROOMMATE_ROOM_FOR_FEMALE);
      roommateSubCatgries.add(Constants.ROOMMATE_ROOM);
      catgriesMap.put(Constants.ROOMMATE, roommateSubCatgries);
      keywords.put(Constants.ROOMMATE, Constants.ROOMMATE_KEYWORDS);
      keywords.put(Constants.ROOMMATE_ROOM_FOR_MALE, Constants.ROOMMATE_KEYWORDS_ROOM_FOR_MALE);
      keywords.put(Constants.ROOMMATE_ROOM_FOR_FEMALE, Constants.ROOMMATE_KEYWORDS_ROOM_FOR_FEMALE);
      keywords.put(Constants.ROOMMATE_ROOM, Constants.ROOMMATE_KEYWORDS_ROOM);


      ArrayList<String> carSubCatgries = new ArrayList<String>();
      carSubCatgries.add(Constants.CAR_ACURA);
      carSubCatgries.add(Constants.CAR_AUDI);
      carSubCatgries.add(Constants.CAR_BMW);
      carSubCatgries.add(Constants.CAR_BUICK);
      carSubCatgries.add(Constants.CAR_CADILLAC);
      carSubCatgries.add(Constants.CAR_CHEVROLET);
      carSubCatgries.add(Constants.CAR_CHRYSLER);
      carSubCatgries.add(Constants.CAR_DODGE);
      carSubCatgries.add(Constants.CAR_FORD);
      carSubCatgries.add(Constants.CAR_HONDA);
      carSubCatgries.add(Constants.CAR_HUMMER);
      carSubCatgries.add(Constants.CAR_HYUNDAI);
      carSubCatgries.add(Constants.CAR_INFINITY);
      carSubCatgries.add(Constants.CAR_JAGUAR);
      carSubCatgries.add(Constants.CAR_JEEP);
      carSubCatgries.add(Constants.CAR_KIA);
      carSubCatgries.add(Constants.CAR_LAND_ROVER);
      carSubCatgries.add(Constants.CAR_LEXUS);
      carSubCatgries.add(Constants.CAR_LINCOLN);
      carSubCatgries.add(Constants.CAR_MAZDA);
      carSubCatgries.add(Constants.CAR_MERCEDES);
      carSubCatgries.add(Constants.CAR_MITSUBHISHI);
      carSubCatgries.add(Constants.CAR_NISSAN);
      carSubCatgries.add(Constants.CAR_OTHERS);
      carSubCatgries.add(Constants.CAR_PONTIAC);
      carSubCatgries.add(Constants.CAR_PORCHE);
      carSubCatgries.add(Constants.CAR_SATURN);
      carSubCatgries.add(Constants.CAR_SUBARU);
      carSubCatgries.add(Constants.CAR_SUZUKI);
      carSubCatgries.add(Constants.CAR_TOYOTA);
      carSubCatgries.add(Constants.CAR_VOLKSWAGEN);
      carSubCatgries.add(Constants.CAR_VOLVO);
      catgriesMap.put(Constants.CAR, carSubCatgries);
      keywords.put(Constants.CAR, Constants.CAR_KEYWORDS);
      keywords.put(Constants.CAR_ACURA, Constants.CAR_KEYWORDS_ACURA);
      keywords.put(Constants.CAR_AUDI, Constants.CAR_KEYWORDS_AUDI);
      keywords.put(Constants.CAR_BMW, Constants.CAR_KEYWORDS_BMW);
      keywords.put(Constants.CAR_BUICK, Constants.CAR_KEYWORDS_BUICK);
      keywords.put(Constants.CAR_CADILLAC, Constants.CAR_KEYWORDS_CADILLAC);
      keywords.put(Constants.CAR_CHEVROLET, Constants.CAR_KEYWORDS_CHEVROLET);
      keywords.put(Constants.CAR_CHRYSLER, Constants.CAR_KEYWORDS_CHRYSLER);
      keywords.put(Constants.CAR_DODGE, Constants.CAR_KEYWORDS_DODGE);
      keywords.put(Constants.CAR_FORD, Constants.CAR_KEYWORDS_FORD);
      keywords.put(Constants.CAR_HONDA, Constants.CAR_KEYWORDS_HONDA);
      keywords.put(Constants.CAR_HUMMER, Constants.CAR_KEYWORDS_HUMMER);
      keywords.put(Constants.CAR_HYUNDAI, Constants.CAR_KEYWORDS_HYUNDAI);
      keywords.put(Constants.CAR_INFINITY, Constants.CAR_KEYWORDS_INFINITY);
      keywords.put(Constants.CAR_JAGUAR, Constants.CAR_KEYWORDS_JAGUAR);
      keywords.put(Constants.CAR_JEEP, Constants.CAR_KEYWORDS_JEEP);
      keywords.put(Constants.CAR_KIA, Constants.CAR_KEYWORDS_KIA);
      keywords.put(Constants.CAR_LAND_ROVER, Constants.CAR_KEYWORDS_LAND_ROVER);
      keywords.put(Constants.CAR_LEXUS, Constants.CAR_KEYWORDS_LEXUS);
      keywords.put(Constants.CAR_LINCOLN, Constants.CAR_KEYWORDS_LINCOLN);
      keywords.put(Constants.CAR_MAZDA, Constants.CAR_KEYWORDS_MAZDA);
      keywords.put(Constants.CAR_MERCEDES, Constants.CAR_KEYWORDS_MERCEDES);
      keywords.put(Constants.CAR_MITSUBHISHI, Constants.CAR_KEYWORDS_MITSUBHISHI);
      keywords.put(Constants.CAR_NISSAN, Constants.CAR_KEYWORDS_NISSAN);
      keywords.put(Constants.CAR_OTHERS, Constants.CAR_KEYWORDS_OTHERS);
      keywords.put(Constants.CAR_PONTIAC, Constants.CAR_KEYWORDS_PONTIAC);
      keywords.put(Constants.CAR_PORCHE, Constants.CAR_KEYWORDS_PORCHE);
      keywords.put(Constants.CAR_SATURN, Constants.CAR_KEYWORDS_SATURN);
      keywords.put(Constants.CAR_SUBARU, Constants.CAR_KEYWORDS_SUBARU);
      keywords.put(Constants.CAR_SUZUKI, Constants.CAR_KEYWORDS_SUZUKI);
      keywords.put(Constants.CAR_TOYOTA, Constants.CAR_KEYWORDS_TOYOTA);
      keywords.put(Constants.CAR_VOLKSWAGEN, Constants.CAR_KEYWORDS_VOLKSWAGEN);
      keywords.put(Constants.CAR_VOLVO, Constants.CAR_KEYWORDS_VOLVO);
      
      
      ArrayList<String> buysellSubCatgries = new ArrayList<String>();
      buysellSubCatgries.add(Constants.BUYSELL_APPLIANCES);
      buysellSubCatgries.add(Constants.BUYSELL_ANTIQUES);
      buysellSubCatgries.add(Constants.BUYSELL_ACCESSORIES);
      buysellSubCatgries.add(Constants.BUYSELL_AUTOPARTS);
      buysellSubCatgries.add(Constants.BUYSELL_BEAUTY);
      buysellSubCatgries.add(Constants.BUYSELL_BOOKS);
      buysellSubCatgries.add(Constants.BUYSELL_CAMERAS);
      buysellSubCatgries.add(Constants.BUYSELL_COMPUTERS);
      buysellSubCatgries.add(Constants.BUYSELL_ELECTRONICS);
      buysellSubCatgries.add(Constants.BUYSELL_FURNITURE);
      buysellSubCatgries.add(Constants.BUYSELL_JEWELRY);
      buysellSubCatgries.add(Constants.BUYSELL_SPORTING);
      buysellSubCatgries.add(Constants.BUYSELL_TICKETS);
      buysellSubCatgries.add(Constants.BUYSELL_OTHERS);
      catgriesMap.put(Constants.BUYSELL, buysellSubCatgries);
      keywords.put(Constants.BUYSELL, Constants.BUYSELL_KEYWORDS);
      keywords.put(Constants.BUYSELL_APPLIANCES, Constants.BUYSELL_KEYWORDS_APPLIANCES);
      keywords.put(Constants.BUYSELL_ANTIQUES, Constants.BUYSELL_KEYWORDS_ANTIQUES);
      keywords.put(Constants.BUYSELL_ACCESSORIES, Constants.BUYSELL_KEYWORDS_ACCESSORIES);
      keywords.put(Constants.BUYSELL_AUTOPARTS, Constants.BUYSELL_KEYWORDS_AUTOPARTS);
      keywords.put(Constants.BUYSELL_BEAUTY, Constants.BUYSELL_KEYWORDS_BEAUTY);
      keywords.put(Constants.BUYSELL_BOOKS, Constants.BUYSELL_KEYWORDS_BOOKS);
      keywords.put(Constants.BUYSELL_CAMERAS, Constants.BUYSELL_KEYWORDS_CAMERAS);
      keywords.put(Constants.BUYSELL_COMPUTERS, Constants.BUYSELL_KEYWORDS_COMPUTERS);
      keywords.put(Constants.BUYSELL_ELECTRONICS, Constants.BUYSELL_KEYWORDS_ELECTRONICS);
      keywords.put(Constants.BUYSELL_FURNITURE, Constants.BUYSELL_KEYWORDS_FURNITURE);
      keywords.put(Constants.BUYSELL_JEWELRY, Constants.BUYSELL_KEYWORDS_JEWELRY);
      keywords.put(Constants.BUYSELL_SPORTING, Constants.BUYSELL_KEYWORDS_SPORTING);
      keywords.put(Constants.BUYSELL_TICKETS, Constants.BUYSELL_KEYWORDS_TICKETS);
      keywords.put(Constants.BUYSELL_OTHERS, Constants.BUYSELL_KEYWORDS_OTHERS);

      
      ArrayList<String> jobSubCatgries = new ArrayList<String>();
      jobSubCatgries.add(Constants.JOB_ACCOUNTING);
      jobSubCatgries.add(Constants.JOB_ADMINISTRATIVE);
      jobSubCatgries.add(Constants.JOB_BIOTECH);
      jobSubCatgries.add(Constants.JOB_CUST_SERVICE);
      jobSubCatgries.add(Constants.JOB_EDUCATION);
      jobSubCatgries.add(Constants.JOB_GOVERNMENT);
      jobSubCatgries.add(Constants.JOB_HUMAN_RESOURCES);
      jobSubCatgries.add(Constants.JOB_LEGAL);
      jobSubCatgries.add(Constants.JOB_MANAGEMENT);
      jobSubCatgries.add(Constants.JOB_MANUFACTURING);
      jobSubCatgries.add(Constants.JOB_MEDIA);
      jobSubCatgries.add(Constants.JOB_OTHERS);
      jobSubCatgries.add(Constants.JOB_REAL_ESTATE);
      jobSubCatgries.add(Constants.JOB_SALES);
      jobSubCatgries.add(Constants.JOB_SOFTWARE);
      jobSubCatgries.add(Constants.JOB_SYSTEMS);
      jobSubCatgries.add(Constants.JOB_TECH_SUPPORT);
      jobSubCatgries.add(Constants.JOB_WEB_DESIGN);
      jobSubCatgries.add(Constants.JOB_WRITING);
      catgriesMap.put(Constants.JOB, jobSubCatgries);
      keywords.put(Constants.JOB, Constants.JOB_KEYWORDS);
      keywords.put(Constants.JOB_ACCOUNTING, Constants.JOB_KEYWORDS_ACCOUNTING);
      keywords.put(Constants.JOB_ADMINISTRATIVE, Constants.JOB_KEYWORDS_ADMINISTRATIVE);
      keywords.put(Constants.JOB_BIOTECH, Constants.JOB_KEYWORDS_BIOTECH);
      keywords.put(Constants.JOB_CUST_SERVICE, Constants.JOB_KEYWORDS_CUST_SERVICE);
      keywords.put(Constants.JOB_EDUCATION, Constants.JOB_KEYWORDS_EDUCATION);
      keywords.put(Constants.JOB_GOVERNMENT, Constants.JOB_KEYWORDS_GOVERNMENT);
      keywords.put(Constants.JOB_HUMAN_RESOURCES, Constants.JOB_KEYWORDS_HUMAN_RESOURCES);
      keywords.put(Constants.JOB_LEGAL, Constants.JOB_KEYWORDS_LEGAL);
      keywords.put(Constants.JOB_MANAGEMENT, Constants.JOB_KEYWORDS_MANAGEMENT);
      keywords.put(Constants.JOB_MANUFACTURING, Constants.JOB_KEYWORDS_MANUFACTURING);
      keywords.put(Constants.JOB_MEDIA, Constants.JOB_KEYWORDS_MEDIA);
      keywords.put(Constants.JOB_OTHERS, Constants.JOB_KEYWORDS_OTHERS);
      keywords.put(Constants.JOB_REAL_ESTATE, Constants.JOB_KEYWORDS_REAL_ESTATE);
      keywords.put(Constants.JOB_SALES, Constants.JOB_KEYWORDS_SALES);
      keywords.put(Constants.JOB_SOFTWARE, Constants.JOB_KEYWORDS_SOFTWARE);
      keywords.put(Constants.JOB_SYSTEMS, Constants.JOB_KEYWORDS_SYSTEMS);
      keywords.put(Constants.JOB_TECH_SUPPORT, Constants.JOB_KEYWORDS_TECH_SUPPORT);
      keywords.put(Constants.JOB_WEB_DESIGN, Constants.JOB_KEYWORDS_WEB_DESIGN);
      keywords.put(Constants.JOB_WRITING, Constants.JOB_KEYWORDS_WRITING);
      
      
      //Populate keywords tokens
      Iterator<Entry<String, String>> keywordsIter = keywords.entrySet().iterator();
      
      while(keywordsIter.hasNext()) {
         Entry<String, String> entry = keywordsIter.next();
         String key = entry.getKey();
         String value = entry.getValue();
         
         StringTokenizer valueTokens = new StringTokenizer(value);
         Set<String> tokens = new HashSet<String>();
         while(valueTokens.hasMoreTokens()) {
            tokens.add(valueTokens.nextToken().toLowerCase());
         }
         keywordsTokens.put(key, tokens);
      }
      
      ArrayList<String> allSubCatgries = new ArrayList<String>();
      catgriesMap.put(Constants.POSTING, allSubCatgries);
      
      catgriesDescMap.put(Constants.RENTAL, Constants.RENTAL_DESC);
      catgriesDescMap.put(Constants.ROOMMATE, Constants.ROOMMATE_DESC);
      catgriesDescMap.put(Constants.CAR, Constants.CAR_DESC);
      catgriesDescMap.put(Constants.BUYSELL, Constants.BUYSELL_DESC);
      catgriesDescMap.put(Constants.JOB, Constants.JOB_DESC);
      
      Set<Entry<String, String>> entrySet = catgriesDescMap.entrySet();
      for(Entry<String, String> entry:entrySet) {
         catgries.add(new CatgryVO(entry.getKey(), entry.getValue()));
      }
      
      Collections.sort(catgries, new CatgryVONameComparator());
      
      allMainCatgries.addAll(catgries);
      allMainCatgries.add(new CatgryVO(Constants.POSTING, Constants.POSTING_DESC));
      Collections.sort(allMainCatgries, new CatgryVONameComparator());
   }

   public static List<String> getSubCatgries(String catgry) {
      return catgriesMap.get(catgry);
   }
   
   public static List<CatgryVO> getMainCatgries() {
      Collections.sort(catgries, new CatgryNameComparator());
      return catgries;
   }
   
   public static List<CatgryVO> getAllMainCatgries() {
      return allMainCatgries;
   }
   
   public static boolean validCatgry(String catgry) {
      if (Constants.POSTING.equalsIgnoreCase(catgry)) {
         return true;
      }
      return catgriesMap.containsKey(catgry);
   }
   
   public static Class<? extends Posting> getPostingClass(String postingCatgry) {
      if (postingCatgry.equalsIgnoreCase(Constants.RENTAL)) {
         return Rental.class;
      } else if (postingCatgry.equalsIgnoreCase(Constants.ROOMMATE)) {
         return Roommate.class;
      } else if (postingCatgry.equalsIgnoreCase(Constants.CAR)) {
         return Car.class;
      } else if (postingCatgry.equalsIgnoreCase(Constants.BUYSELL)) {
         return Buysell.class;
      } else if (postingCatgry.equalsIgnoreCase(Constants.JOB)) {
         return Job.class;
      }
      return Posting.class;
   }
   
   public static <U extends Posting> String getPostingCatgry(U posting) {
      if (posting instanceof Rental) {
         return Constants.RENTAL;
      } else if (posting instanceof Roommate) {
         return Constants.ROOMMATE;
      } else if (posting instanceof Car) {
         return Constants.CAR;
      } else if (posting instanceof Buysell) {
         return Constants.BUYSELL;
      } else if (posting instanceof Job) {
         return Constants.JOB;
      }
      return Constants.POSTING;
   }
   
   public static String getPostingCatgry(Class<? extends Posting> posting) {
      if (posting == Rental.class) {
         return Constants.RENTAL;
      } else if (posting == Roommate.class) {
         return Constants.ROOMMATE;
      } else if (posting == Car.class) {
         return Constants.CAR;
      } else if (posting == Buysell.class) {
         return Constants.BUYSELL;
      } else if (posting == Job.class) {
         return Constants.JOB;
      }
      return Constants.POSTING;
   }
   
   public static Posting createNewPosting(String catgry) {
      Posting posting = null;
      if (catgry.equalsIgnoreCase(Constants.RENTAL)) {
         posting = new Rental();
      } else if (catgry.equalsIgnoreCase(Constants.ROOMMATE)) {
         posting = new Roommate();
      } else if (catgry.equalsIgnoreCase(Constants.CAR)){
         posting = new Car();
      } else if (catgry.equalsIgnoreCase(Constants.BUYSELL)) {
         posting = new Buysell();
      } else if (catgry.equalsIgnoreCase(Constants.JOB)) {
         posting = new Job();
      } else {
         posting = new Posting();
      }
      return posting;
   }
   
   public static String getCatgryDesc(String catgry) {
      return catgriesDescMap.get(catgry);
   }
   
   public static class CatgryNameComparator implements Comparator<CatgryVO> {
      public int compare(CatgryVO o1, CatgryVO o2) {
         return o1.getName().compareTo(o2.getName());
      }
   }
   
   public static class CatgryVONameComparator implements Comparator<CatgryVO> {
      public int compare(CatgryVO vo1, CatgryVO vo2) {
         return vo1.getName().compareTo(vo2.getName());
      }
   }
   
   public static String getContent(PostingVO postingVO) {
      StringBuffer content = new StringBuffer();
      if (postingVO.getCatgry() == null) {
         return null;
      }
      if (postingVO.getTitle() != null) {
         content.append(postingVO.getTitle().trim()+ " ");
      }
      if (postingVO.getDesc() != null) {
         content.append(postingVO.getDesc().trim() + " ");
      }
      if (postingVO.getType() != null) {
         content.append(postingVO.getType().trim() + " ");
      }
      
      if (postingVO.getSubCatgry() != null) {
         content.append(postingVO.getSubCatgry().trim() + " ");
      }
      
      if (postingVO.getCatgry() != null) {
         content.append(keywords.get(postingVO.getCatgry().toLowerCase()) + " ");
      }
      
      //Get subcatgry keywords
      ArrayList<String> subCatgries = catgriesMap.get(postingVO.getCatgry().toLowerCase());
      String subCatgryKeywords = getSubCatgryKeywords(content.toString(), subCatgries);
      
      if (subCatgryKeywords != null) {
         content.append(subCatgryKeywords);
      }
      
      return content.toString();
   }
   
   private static double getJaccardIndex(Set<String> content, Set<String> keywords) {
      if (content == null || keywords == null) {
         return 0;
      }
      Set<String> union = new HashSet<String>();
      union.addAll(content);
      union.addAll(keywords);
      
      Set<String> intersection = new HashSet<String>();
      
      Iterator<String> keywordsIter = keywords.iterator();
      while (keywordsIter.hasNext()) {
         String str = keywordsIter.next();
         if(content.contains(str)) {
            intersection.add(str);
         }
      }
      return (double)intersection.size() / content.size();
   }
   
   private static String getSubCatgryKeywords(String content, ArrayList<String> subCatgries) {
      if (content == null || subCatgries == null || subCatgries.size() == 0) {
         return null;
      }
      StringTokenizer contentTokens = new StringTokenizer(content);
      Set<String> contentSet = new HashSet<String>(); 
      while(contentTokens.hasMoreTokens()) {
         contentSet.add(contentTokens.nextToken().toLowerCase());
      }
      
      double maxJaccardIndex = 0; //If ZERO, return null
      String probSubCatgry = null; //Most matching sub catgry
      for(String subCatgry:subCatgries) {
         double jaccardIndex = getJaccardIndex(contentSet, keywordsTokens.get(subCatgry));
         if (jaccardIndex > maxJaccardIndex) {
            maxJaccardIndex = jaccardIndex;
            probSubCatgry = subCatgry;
         }
      }
      
      if (probSubCatgry == null) {
         return null;
      }
      return keywords.get(probSubCatgry);
   }
}
