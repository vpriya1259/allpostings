package com.shoora.craigslist.crawler;

import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.shoora.craigslist.dataobject.CImage;
import com.shoora.craigslist.dataobject.LeafPage;
import com.shoora.craigslist.dataobject.LeafPageRequest;
import com.shoora.craigslist.util.GeneralUtils;
import com.shoora.craigslist.util.NodeWalker;

public class LeafPageFetcher implements Callable<LeafPage> {
   private static final Log log = LogFactory.getLog(LeafPageFetcher.class);   
   private LeafPageRequest leafPageReq;

   public LeafPageFetcher(LeafPageRequest leafPageReq) {
      this.leafPageReq = leafPageReq;
   }

   /**
    * Algo: 1. URLUtils.getContent 2. Parse the content using Neko 3. Extract
    * title 4. Extract description 5. Extract price 6. Extract the images 7.
    * Spit out LeafPage
    */
   public LeafPage call() throws Exception {
      log.debug(String.format("Processing URL: %s", leafPageReq.getUrl()));
      byte[] content = GeneralUtils.getContent(new URL(leafPageReq.getUrl()));
      log.debug(String.format("URL %s content size is %d", leafPageReq.getUrl(), content.length));
      if (log.isTraceEnabled()) {
         log.trace(new String(content));
      }
      Node node = GeneralUtils.parseNeko(content);
      LeafPage leafPage = generateLeafPage(node);
      leafPage.setSrcUrl(leafPageReq.getUrl());
      return leafPage;
   }

   private LeafPage generateLeafPage(Node root) {
      LeafPage leafPage = new LeafPage();
      ArrayList<String> imageUrls = new ArrayList<String>();
      //traverse
      NodeWalker nodeWalker = new NodeWalker(root);
      while (nodeWalker.hasNext()) {
         Node currNode = nodeWalker.nextNode();

         if (!GeneralUtils.shouldProcess(currNode)) {
            nodeWalker.skipChildren();
            continue;
         }

         String name = currNode.getNodeName();
         
         //Title
         if ("title".equalsIgnoreCase(name)) {
            String title = GeneralUtils.getTitle(currNode);
            leafPage.setTitle(title);
         }
         
         //Price
         if ("h2".equalsIgnoreCase(name)) {
            String headerText = GeneralUtils.getText(currNode);
            Price price = getPrice(headerText);
            if (price != null) {
               leafPage.setPrice(price.getPrice());
               leafPage.setCurrency(price.getCurrency());
            }
            leafPage.setAddress(getAddress(headerText));
         }

         //Description
         if ("div".equalsIgnoreCase(name) && isUserBody(currNode)) {
            String desc = GeneralUtils.getText(currNode);
            leafPage.setDesc(desc);
         }

         //Image urls
         if ("img".equalsIgnoreCase(name)) {
            NamedNodeMap attrs = currNode.getAttributes();
            if (attrs != null) {
               Node srcNode = attrs.getNamedItem("src");
               if (srcNode != null) {
                  String url = srcNode.getNodeValue();
                  if (url.indexOf("thumb/") > 0) {
                     url = url.replaceAll("thumb/", "");
                  }
                  imageUrls.add(url);
               }
            }
         }
         
         //Created date
         if (currNode.getNodeType() == Node.TEXT_NODE) {
            String text = currNode.getNodeValue();
            text = text.trim();
            if (text.startsWith("Date:")) {
               long created = GeneralUtils.getTimeInMillis(text.substring(6));
               leafPage.setCreated(created);
            }
         }
         
         if ("a".equalsIgnoreCase(name)) {
            String href;
            if (currNode.getAttributes() != null) {
               Node attrNode = currNode.getAttributes().getNamedItem("href");
               href = attrNode.getNodeValue();
               if (href.startsWith("mailto:")) {
                  String email = GeneralUtils.getText(currNode);
                  leafPage.setEmail(email);
               }
            }

         }
      }
      
      for(String imageUrl:imageUrls) {
         try {
            log.debug(String.format("Fetching image url %s.", imageUrl));
            byte[] imageContent = GeneralUtils.getContent(new URL(imageUrl));
            if (imageContent.length == 0) {
               continue;
            }
            log.debug(String.format("Image %s size is %d.", imageUrl, imageContent.length));
            
            leafPage.getImages().add(new CImage(imageContent));
         } catch(Exception ex) {
            log.debug(String.format("Fetching image content %s failed!", imageUrl));
         }
      }
      leafPage.setCatgry(leafPageReq.getCatgry());
      leafPage.setSubCatgry(leafPageReq.getSubCatgry());
      return leafPage;
   }

   private Price getPrice(String text) {
      StringTokenizer tokenizer = new StringTokenizer(text, " /");
      while (tokenizer.hasMoreTokens()) {
         String price = tokenizer.nextToken();
         if (price == null || !(price.startsWith("$") || price.startsWith("INR"))) {
            continue;
         }
         if (price.startsWith("$")) {
            return new Price(price.substring(1), "USD");
         } else if (price.startsWith("INR")) {
            return new Price(price.substring(3), "INR");
         }
      }
      return null;
   }
   
   private String getAddress(String text) {
      if (text == null) {
         return null;
      }
      int first = text.lastIndexOf('(');
      int last = text.lastIndexOf(')');
      if (first >= last) {
         return null;
      }
      String loc = text.substring(first + 1, last);
      if (!"map".equalsIgnoreCase(loc)) {
         return loc;
      }
      
      text = text.substring(0, first - 1);
      first = text.lastIndexOf('(');
      last = text.lastIndexOf(')');
      return text.substring(first + 1, last);
   }

   private boolean isUserBody(Node node) {
      if (node == null) {
         return false;
      }
      NamedNodeMap attrs = node.getAttributes();
      if (attrs != null) {
         Node divIdNode = attrs.getNamedItem("id");
         if (divIdNode != null) {
            if ("userbody".equalsIgnoreCase(divIdNode.getNodeValue())) {
               return true;
            }
         }
      }
      return false;
   }
   
   private static class Price {
      private String price;
      private String currency;
      public Price(String price, String currency) {
         this.price = price;
         this.currency = currency;
      }
      public String getPrice() {
         return price;
      }
      public String getCurrency() {
         return currency;
      }
   }

}
