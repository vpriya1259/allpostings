package com.indusborn.geocode;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.indusborn.common.AppServerException;
import com.indusborn.common.MessageKeys;

public class MySQLGeoImpl extends HibernateDaoSupport implements SimpleGeo {
   private static Log _log = LogFactory.getLog(MySQLGeoImpl.class);

   public Map<Long, Double> getPostingRecords(double lat, double lon,
         double radius, long startTime) throws AppServerException {
      Session session = getSession();
      DecimalFormat twoDForm = new DecimalFormat("#.##");

      Map<Long, Double> idDistMap = new HashMap<Long, Double>();
      try {
         long started = System.currentTimeMillis();
         session.beginTransaction();
         String procQuery = "CALL GeoDistPosting(:lat, :lng, :radius, :startTime)";
         Query query;
         query =
               session.createSQLQuery(procQuery)
                     .addScalar("posting_id", Hibernate.LONG)
                     .addScalar("distance", Hibernate.STRING);
         query.setDouble("lat", lat);
         query.setDouble("lng", lon);
         query.setDouble("radius", radius);
         query.setLong("startTime", startTime);
         List list = query.list();

         if (list == null || list.size() == 0) {
            return idDistMap;
         }
         _log.info("GetRecords size " + list.size());
         for (Iterator<Object> it = list.iterator(); it.hasNext();) {
            Object[] record = (Object[]) it.next();
            Long id = Long.valueOf(record[0].toString());
            Double dist =
                  Double.valueOf(twoDForm.format(Double.valueOf(record[1]
                        .toString())));
            idDistMap.put(id, dist);
         }
         session.getTransaction().commit();
         long ended = System.currentTimeMillis();
         _log.info("MySQL geocoding lookup took " + (ended - started)
               + " mseconds for postings. " + lat + " " + lon);
      } catch (Exception ex) {
         session.getTransaction().rollback();
         _log.error("Stored procedure execution failed!", ex);
         throw new AppServerException(
               MessageKeys.GEO_CODING_GET_POSTING_RECORDS, ex.getCause());
      } finally {
         session.close();
      }
      return idDistMap;
   }
}
