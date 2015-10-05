
/* ******************************************************************************
 * Copyright (C) 2010 VMWare, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * JSONUtil.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

public abstract class JSONUtil
{
   private static Log _log = LogFactory.getLog(JSONUtil.class);
   private final static ObjectMapper mapper = new ObjectMapper();
   
   public static final String convertToJSONString(Object obj) {
      String json = "";
      try {
         json = mapper.writeValueAsString(obj);
         if(_log.isDebugEnabled()) {
            _log.debug(json);
         }
      } catch(Exception ex) {
         if(_log.isErrorEnabled()){
            _log.error("Error converting object to JSON.", ex);
         }
      }
      return json;
   }

}
