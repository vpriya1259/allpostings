
/* ******************************************************************************
 * Copyright (C) 2009 VMware, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * TimeUtil.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.indusborn.common.Constants;



public class TimeUtil
{
   public static long getPostingStartTime() {
      DateTime currTime = new DateTime(DateTimeZone.UTC);
      DateTime startTime = currTime.minusDays(Constants.POSTING_MAX_AGE);
      return startTime.getMillis();
   }
   
   public static long getCurrTimeInUTC() {
      DateTime dt = new DateTime(DateTimeZone.UTC);
      return dt.getMillis();
   }

   public static Date 
   strToDate(String date)
   throws ParseException
   {
      DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
      Date day = df.parse(date);
      return day;
   }
   
   
   public static Timestamp
   strToTimestamp(String date)
   throws ParseException
   {
      Date day = strToDate(date);
      return new Timestamp(day.getTime());
   }
   
   public static Timestamp
   mySQLStrToTimestamp(String timestamp)
   throws ParseException
   {
	   DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   Date day = df.parse(timestamp);
	   return new Timestamp(day.getTime());
   }
   
   public static Date
   mySQLStrToDate(String dayStr)
   throws ParseException
   {
	   DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	   Date day = df.parse(dayStr);
	   return day;
   }
}
