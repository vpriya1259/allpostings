package com.shoora.craigslist.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.html.dom.HTMLDocumentImpl;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.shoora.craigslist.dataobject.LeafPageRequest;

public class GeneralUtils {
   private static final Log log = LogFactory.getLog(GeneralUtils.class);
   private static final DOMFragmentParser parser = new DOMFragmentParser();
   private static String DATE_PATTERN = "yyyy-MM-dd,HH:mma";
   private static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(DATE_PATTERN);

   static {
      try {
         parser.setFeature("http://cyberneko.org/html/features/augmentations",
               true);
         parser.setProperty(
               "http://cyberneko.org/html/properties/default-encoding", "utf-8");
         parser.setFeature(
               "http://cyberneko.org/html/features/scanner/ignore-specified-charset",
               true);
         parser.setFeature(
               "http://cyberneko.org/html/features/balance-tags/ignore-outside-content",
               false);
         parser.setFeature(
               "http://cyberneko.org/html/features/balance-tags/document-fragment",
               true);
         parser.setFeature("http://cyberneko.org/html/features/report-errors",
               false);
      } catch (SAXException e) {
         log.error(e.getMessage());
      }
   }

   public static byte[] getContent(URL url) {
      byte content[] = null;
      try {
         URL urlObj = url;
         URLConnection urlConn = urlObj.openConnection();
         urlConn.setConnectTimeout(15000);
         urlConn.setReadTimeout(15000);
         PushbackInputStream in =
               new PushbackInputStream(new BufferedInputStream(
                     urlConn.getInputStream(), 4096), 4096);

         byte[] bytes = new byte[4096];
         ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
         int length = 0;
         for (int i = in.read(bytes); i != -1; i = in.read(bytes)) {
            out.write(bytes, 0, i);
            length += i;
         }
         content = out.toByteArray();
      } catch (Exception ex) {
         log.warn("Couldn't fetch the content for " + url.toString());
      }
      return content;
   }

   public static DocumentFragment parseNeko(byte[] content) {
      InputSource input = new InputSource(new ByteArrayInputStream(content));
      input.setEncoding("utf-8");
      DocumentFragment res = null;
      try {
         // convert Document to DocumentFragment
         HTMLDocumentImpl doc = new HTMLDocumentImpl();
         doc.setErrorChecking(false);
         res = doc.createDocumentFragment();
         DocumentFragment frag = doc.createDocumentFragment();
         parser.parse(input, frag);
         res.appendChild(frag);
         while (true) {
            frag = doc.createDocumentFragment();
            parser.parse(input, frag);
            if (!frag.hasChildNodes()) {
               break;
            }
            res.appendChild(frag);
         }
         //print(res, "");
      } catch (Exception ex) {
         log.error(ex.getMessage(), ex);
      }
      return res;
   }

   public static void print(Node node, String indent) {
      System.out.println(indent+node.getClass().getName());
      Node child = node.getFirstChild();
      while (child != null) {
          print(child, indent+" ");
          child = child.getNextSibling();
      }
  }
   
   public static String getTitle(Node node) {
      NodeWalker walker = new NodeWalker(node);

      while (walker.hasNext()) {
         Node currentNode = walker.nextNode();
         String nodeName = currentNode.getNodeName();
         short nodeType = currentNode.getNodeType();

         if ("body".equalsIgnoreCase(nodeName)) { // stop after HEAD
            return null;
         }

         if (nodeType == Node.ELEMENT_NODE) {
            if ("title".equalsIgnoreCase(nodeName)) {
               return getText(currentNode);
            }
         }
      }

      return null;
   }
   
   public static String getText(Node textNode) {
      NodeWalker nodeWalker = new NodeWalker(textNode);
      StringBuffer textBuff = new StringBuffer();
      String nodeName;
      while (nodeWalker.hasNext()) {
         Node currNode = nodeWalker.nextNode();
         nodeName = currNode.getNodeName();
         if (!shouldProcess(currNode)) {
            nodeWalker.skipChildren();
            continue;
         }
         if ((currNode.getNodeType() == Node.TEXT_NODE)) {
            if (isMapLinkText(currNode)) {
               continue;
            }
            String text = currNode.getNodeValue();
            text = text.replaceAll("\\s+", " ");
            text = text.trim();
            textBuff.append(text+"\n");
         }
         if ("br".equalsIgnoreCase(nodeName)) {
            textBuff.append("\n");
         }
         //Ignore Madison Ave <googlemap> <yahoomap>
         if ("small".equalsIgnoreCase(nodeName)) {
            nodeWalker.skipChildren();
            continue;
         }
         //Ignore catok, dogsok text
         if ("ul".equalsIgnoreCase(nodeName)) {
            NamedNodeMap attrs = currNode.getAttributes();
            if (attrs != null) {
               Node attr = attrs.getNamedItem("class");
               if (attr != null && "blurbs".equalsIgnoreCase(attr.getNodeValue())) {
                  nodeWalker.skipChildren();
               }
            }
         }
      }
      return textBuff.toString();
   }
   
   public static boolean isMapLinkText(Node currNode) {
      Node nextNode = currNode.getNextSibling();
      if (nextNode == null) {
         return false;
      }
      if ("small".equalsIgnoreCase(nextNode.getNodeName())) {
         return true;
      }
      return false;
   }

   public static String getLeafPageUrl(Node pNode) {
      NodeWalker nodeWalker = new NodeWalker(pNode);
      String href = null;
      String location = null;
      while (nodeWalker.hasNext()) {
         Node currNode = nodeWalker.nextNode();
         if ("a".equalsIgnoreCase(currNode.getNodeName())) {
            if (currNode.getAttributes() != null) {
               Node attrNode = currNode.getAttributes().getNamedItem("href");
               href = attrNode.getNodeValue();
            }
         }
         if ("font".equalsIgnoreCase(currNode.getNodeName())) {
            location = getText(currNode);
         }
      }
      if (href != null && location != null) {
         return href;
      }

      return null;
   }
   
   /**
    * Checks if the leaf page url has pics
    * @param pNode
    * @return
    */
   public static boolean hasPic(Node pNode) {
      NodeWalker nodeWalker = new NodeWalker(pNode);
      while (nodeWalker.hasNext()) {
         Node currNode = nodeWalker.nextNode();
         if ("span".equalsIgnoreCase(currNode.getNodeName())) {
            String text = getText(currNode);
            text = text.trim();
            if ("pic".equalsIgnoreCase(text)) {
               return true;
            }
         }
      }
      return false;
   }

   public static boolean shouldProcess(Node node) {
      if (node == null) {
         return false;
      }

      String name = node.getNodeName();
      if ("script".equalsIgnoreCase(name) || "style".equalsIgnoreCase(name)) {
         return false;
      }

      if (node.getNodeType() == Node.COMMENT_NODE) {
         return false;
      }
      return true;
   }


   public static byte[] scaleImageToWidth(byte[] originalBytes, int scaledWidth)
         throws IOException {
      BufferedImage originalImage =
            ImageIO.read(new ByteArrayInputStream(originalBytes));
      int originalWidth = originalImage.getWidth();
      if (originalWidth <= scaledWidth) {
         return originalBytes;
      }
      int scaledHeight =
            (int) (originalImage.getHeight() * ((float) scaledWidth / (float) originalWidth));
      BufferedImage scaledImage =
            new BufferedImage(scaledWidth, scaledHeight,
                  BufferedImage.TYPE_INT_RGB);
      Graphics2D g = scaledImage.createGraphics();
      g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
      g.dispose();
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      ImageIO.write(scaledImage, "jpeg", byteStream);
      return byteStream.toByteArray();
   }
   
   /**
    * Format "2011-12-30, 12:22AM PST"
    * @param createdTime
    * @return
    */
   public static long getTimeInMillis(String createdTime) {
      long timeInMillis = 0;
      StringTokenizer tokenizer = new StringTokenizer(createdTime);
      StringBuffer sb = new StringBuffer();
      int total = tokenizer.countTokens();
      int counter = 0;
      while(counter < total-1) {
         sb.append(tokenizer.nextToken());
         counter++;
      }
      String timezone = tokenizer.nextToken();
      
      try {
         dateFormatter.withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone(timezone)));
         DateTime time = dateFormatter.parseDateTime(sb.toString());
         timeInMillis = time.getMillis();
      } catch(Exception ex) {
         log.warn(String.format("Unable to convert the time %s", createdTime), ex);
      }
      return timeInMillis;
   }
   
}
