
package com.indusborn.common;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class MessageObject
{
   private String              messageKey;
   private Map<String, Object>     parameters;

   public MessageObject() {}

   public MessageObject(String key, Map<String, Object> params) {
      messageKey = key;
      parameters = params;
   }


   public void addParameter(String key, Object value) {
      if (parameters == null) {
         parameters =  new java.util.HashMap<String, Object>();
      }

      parameters.put(key, value);
   }

   // JavaBean getters/setters
   public Map<String, Object> getParameters() {
      return parameters;
   }

   public void setParameters(Map<String, Object> _parameters) {
      this.parameters = _parameters;
   }

   public String getMessageKey() {
      return messageKey;
   }

   public void setMessageKey(String msg) {
      messageKey = msg;
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      if (messageKey != null) {
         s.append("Message key = " + messageKey);
      }

      if (parameters != null) {
         java.util.Set<String> keys = parameters.keySet();
         boolean first = true;
         for (String name : keys) {
            if (first) {
               s.append(", Parameters: ");
               first = false;
            } else {
               s.append(", ");
            }
            s.append("(" + name + " = " + parameters.get(name) + ")");
         }
      }

      return s.toString();
   }

   public String toLocalizedString(Set<java.util.Properties> resProperties) {
      String msg = null;
      if (resProperties  == null) {
         // no resource files
         return toString();
      }

      for (Properties res : resProperties) {
         msg = res.getProperty(messageKey);
         if (msg != null) {
            break;
         }
      }
      if (msg == null) {
         // key is not found in any resource properties, use key itself for return string
         msg = messageKey;
      }

      if (parameters != null && !parameters.isEmpty()) {
         for (Map.Entry<String, Object> param : parameters.entrySet()) {
            String paramName = "\\{" + param.getKey() + "\\}";
            String paramValue = param.getValue().toString();
            msg = msg.replaceAll(paramName, paramValue);
         }
      }
      return msg;
   }

}
