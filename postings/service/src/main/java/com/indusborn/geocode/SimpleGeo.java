package com.indusborn.geocode;

import java.util.Map;

import com.indusborn.common.AppServerException;

public interface SimpleGeo {
   public Map<Long, Double> getPostingRecords(double lat, double lon,
         double radius, long startTime) throws AppServerException;

}
