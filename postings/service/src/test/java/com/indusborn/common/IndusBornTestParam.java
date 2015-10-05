package com.indusborn.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.encrypt.PasswordEncoder;
import org.springframework.security.encrypt.SecureRandomStringKeyGenerator;
import org.springframework.security.encrypt.StandardPasswordEncoder;
import org.springframework.security.encrypt.StringKeyGenerator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import com.indusborn.geocode.GGcoder;
import com.indusborn.repository.MailDao;
import com.indusborn.repository.PostingDao;
import com.indusborn.repository.UserDao;
import com.indusborn.service.PostingService;

public class IndusBornTestParam {
   protected ApplicationContext ctx;
   protected PostingDao postingDao;
   protected UserDao userDao;
   protected MailDao mailDao;
   protected PostingService postingService;
   protected GGcoder geoCoder;
   protected PasswordEncoder passwordEncoder;
   protected int numOfAdds = 100;
   protected String desc = "IndusBorn - site for classifieds!";
   protected boolean online = false;
   protected Random generator;
   protected final String IMAGE_FOLDER = "C:/tmp/images/"; 
   protected String image1 = IMAGE_FOLDER + "image1.jpg";
   protected String image2 = IMAGE_FOLDER + "image1.jpg";
   protected double SUNNYVALE_LAT = 37.34195;
   protected double SUNNYVALE_LNG = -122.035798;
   protected StringKeyGenerator tokenGenerator = null;   
   
   private static String[] titles = {"Praveen", "Kumar", "yarlagadda"};
   private static String[] existingUserEmails = {"baybaztest1@gmail.com", "pra272veen@yahoo.com"};
   private static String[] newUserEmails = {"vpriya1259@vmware.com"};
   private static String[] regions = {"California", "Texas", "Florida", "Colorado"};
   private static String[] currencies = {"USD", "INR"};
   private static Map<String, ArrayList<String>> catgries = new HashMap<String, ArrayList<String>>();
   static {
      ArrayList<String> rentalSubCatgries = new ArrayList<String>();
      rentalSubCatgries.add("Single Bed Room");
      rentalSubCatgries.add("Double Bed Room");
      rentalSubCatgries.add("Three Bed Room");
      rentalSubCatgries.add("Town House");
      rentalSubCatgries.add("Condo");
      catgries.put("Rental", rentalSubCatgries);

      ArrayList<String> roommateSubCatgries = new ArrayList<String>();
      roommateSubCatgries.add("Room for a male");
      roommateSubCatgries.add("Room for a female");
      catgries.put("Roommate", roommateSubCatgries);

      ArrayList<String> carSubCatgries = new ArrayList<String>();
      carSubCatgries.add("Honda");
      carSubCatgries.add("Hummer");
      carSubCatgries.add("Ford");
      carSubCatgries.add("Dodge");
      carSubCatgries.add("Porche");
      catgries.put("Car", carSubCatgries);
      
   }
   private String[] addrs = {"1575 Tenaka Pl, Sunnyvale, CA",
                             "3400 Hillview Ave, Palo Alto, CA",
                             "1500 Crestview Dr, Mountain View, CA",
                             "650 Townsend st, San Francsico, CA",
                             "490 E Main Street Norwich CT 06360",
                             "50 Water Street Mystic CT 06355",
                             "233 River Road New London CT 06320",
                             "33 Paula Lane Uncasville CT 06385",
                             "574 New London Turnpike Norwich CT 06360",
                             "19 Quaker Ridge Rd Bethel CT 06801",
                             "2962 Dunedin Cv Germantown TN 38138 ",
                             "1000 Coney Island Ave Brooklyn NY 11230",
                             "1500 Vance Ave Memphis TN 38104"};
   private String[] types = {"Offered", "Wanted"};
   private double[] radii = {10000, 20000, 34000};
   private String[] mailSubjects = {"Crap on a cow", "holy shit", "William Wallace"};
   private String[] mailMessageBody = {"Kill Bill 1", "breaking bones", "deadlifts"};
   private String[] phoneNums = {"6312557026", "6312557027", "6312557028"};
   private String[] feedbackNames = {"abcdf", "adsfd", "asdf", "sdfkj"};
   private String[] feedbackEmails = {"asdf@gmail.com", "asdf@yahoo.com", "asdf@bing.com"};
   private String[] feedbackMssgs = {"asdf asdf asd sadf", "adf sdf sdf sdf sdf sdf", " asdf sf sadfsdf dsfvdf kjl sdf sdf"};
   private String[] feedbackIps = {"10.22.234.34", "10.20.34.23", "10.13.234.34", "10.12.34.23"};
   private String[] previewTypes = {"Rental", "Rommate", "Car", "Buy/Sell", "Job"};
   private long[] previewTypeIds = {234, 432, 89, 23, 234, 34, 23};

   

   @BeforeClass(alwaysRun=true)
   public void setUp() {
      String[] paths = {"indusborn-business.xml"};
      ctx = new ClassPathXmlApplicationContext(paths);
      postingDao = (PostingDao)ctx.getBean("postingDao");
      userDao = (UserDao)ctx.getBean("userDao");
      mailDao = (MailDao)ctx.getBean("mailDao");
      geoCoder = (GGcoder) ctx.getBean("geoCoder");
      postingService = (PostingService) ctx.getBean("postingService");
      passwordEncoder = (StandardPasswordEncoder)ctx.getBean("standardPasswordEncoder");
      generator = new Random();
      tokenGenerator = new SecureRandomStringKeyGenerator();
   }
   
   @DataProvider(name="addPostingsParams")
   public Object[][]
   addPostingsParams() {
      return new Object[][]{{catgries, titles, addrs, existingUserEmails, currencies}};
   }
   
   
   @DataProvider(name="usersParam")
   public Object[][]
   usersParam() {
      return new Object[][] {{existingUserEmails}};
   }
   
   @DataProvider(name="existingUserEmails")
   public Object[][] existingUserEmails() {
      return new Object[][] {{existingUserEmails}};
   }
   
   @DataProvider(name="newUserEmails")
   public Object[][] newUserEmails() {
      return new Object[][] {{newUserEmails}};
   }
   
   @DataProvider(name="postingParams")
   public Object[][] postingParams() {
      return new Object[][]{{catgries, titles, addrs, existingUserEmails, currencies, types}};
   }
   
                   
   
}
