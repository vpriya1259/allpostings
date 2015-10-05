
/* ******************************************************************************
 * Copyright (C) 2009 VMware, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * XMLUtil.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.util;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.dom.DOMDocument;
import org.dom4j.io.DocumentSource;


public class XMLUtil
{
   public static String
   convertToString(DOMDocument domDoc) {
      StringWriter stringWriter = new StringWriter();
      try {
         Transformer serializer = TransformerFactory.newInstance().newTransformer();
         serializer.transform(new DocumentSource(domDoc.getDocument()),new StreamResult(stringWriter));
      } catch (TransformerException e) {
         e.printStackTrace();
      }
      return stringWriter.toString();
   }
}
