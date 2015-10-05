package com.shoora.craigslist.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.shoora.craigslist.util.GeneralUtils;

public class MiscTest {
   private static String DATE_PATTERN = "yyyy-MM-dd,HH:mma";
   private static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(DATE_PATTERN).withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("CST")));   

   public static void main(String[] args) {
      long timeInMillis = 0;
      String createdTime = "2012-01-07,9:54PM";
      //String createdTime = "2011-12-30,12:22AM";
      DateTime time = dateFormatter.parseDateTime(createdTime);
      timeInMillis = time.getMillis();
      System.out.println(String.format("Unable to convert the time %s, %d", createdTime, timeInMillis));         

/*      String str = "$550 Big Room For Rent (pittsburg / antioch)";
      
      int first = str.lastIndexOf('(');
      int last = str.lastIndexOf(')');
      if (first > last) {
         return;
      }
      System.out.println(str.substring(first+1, last-1));
*/   }
   /*

      String imageUrl = "http://images.craigslist.org/5I25Lc5H33Ka3F53Jabcn93359143f9c31586.jpg";
      byte[] content = GeneralUtils.getContent(new URL(imageUrl));       
      File imgFile = new File("image.jpeg");
      imgFile.delete();
      imgFile.createNewFile();
      FileOutputStream os = new FileOutputStream(imgFile);
      os.write(content);
      os.close();
    *
    */
}
