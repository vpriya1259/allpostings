package com.shoora.craigslist.regions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.shoora.craigslist.util.GeneralUtils;
import com.shoora.craigslist.util.NodeWalker;

public class RegionsProcessor {
   private static Log log = LogFactory.getLog(RegionsProcessor.class);
   private static String template = "http://sfbay.craigslist.org/apa/$Rental$Others\nhttp://sfbay.craigslist.org/roo/$Roommate$Room\nhttp://sfbay.craigslist.org/cto/$Car$Others\nhttp://sfbay.craigslist.org/bks/$Buysell$Books\nhttp://sfbay.craigslist.org/fua/$Buysell$Furniture\nhttp://sfbay.craigslist.org/sys/$Buysell$Computers\nhttp://sfbay.craigslist.org/ele/$Buysell$Electronics\nhttp://sfbay.craigslist.org/ppa/$Buysell$Appliances\nhttp://sfbay.craigslist.org/atq/$Buysell$Antiques\nhttp://sfbay.craigslist.org/pta/$Buysell$Auto Parts\nhttp://sfbay.craigslist.org/hab/$Buysell$Beauty\nhttp://sfbay.craigslist.org/jwl/$Buysell$Jewelry\nhttp://sfbay.craigslist.org/spo/$Buysell$Sporting\nhttp://sfbay.craigslist.org/sof/$Job$Software\nhttp://sfbay.craigslist.org/acc/$Job$Accounting\nhttp://sfbay.craigslist.org/ofc/$Job$Administrative\nhttp://sfbay.craigslist.org/sci/$Job$Biotech\nhttp://sfbay.craigslist.org/bus/$Job$Management\nhttp://sfbay.craigslist.org/hum/$Job$Human Resources\nhttp://sfbay.craigslist.org/mar/$Job$Marketing\nhttp://sfbay.craigslist.org/sls/$Job$Sales\nhttp://sfbay.craigslist.org/sad/$Job$Systems\nhttp://sfbay.craigslist.org/web/$Job$Web design\n";
   private static String toBeReplaced = "http://sfbay.craigslist.org/";

   private static ArrayList<String> states = new ArrayList<String>();
   private static ArrayList<String> regions = new ArrayList<String>();
   
   private static List<String> getRegionUrls(Node pNode) {
      List<String> regionUrls = new ArrayList<String>();
      NodeWalker nodeWalker = new NodeWalker(pNode);
      String href = null;
      while (nodeWalker.hasNext()) {
         Node currNode = nodeWalker.nextNode();
         if ("a".equalsIgnoreCase(currNode.getNodeName())) {
            if (currNode.getAttributes() != null) {
               Node attrNode = currNode.getAttributes().getNamedItem("href");
               href = attrNode.getNodeValue();
               regionUrls.add(href);
            }
         }
      }
      return regionUrls;
   }
   
   private List<String> processState(Node root) {
      NodeWalker nodeWalker = new NodeWalker(root);
      while(nodeWalker.hasNext()) {
         Node currNode = nodeWalker.nextNode();
         if (!GeneralUtils.shouldProcess(currNode)) {
            nodeWalker.skipChildren();
            continue;
         }
         if("div".equalsIgnoreCase(currNode.getNodeName())) {
            NamedNodeMap attrs = currNode.getAttributes();
            if (attrs != null) {
               Node attr = attrs.getNamedItem("id");
               if (attr != null && "list".equalsIgnoreCase(attr.getNodeValue())) {
                  return getRegionUrls(currNode);
               }
            }
         }
      }
      return null;
   }
   
   private void loadRegions(String fileName) {
	   StringBuffer srcsContent = new  StringBuffer();
      try {
         BufferedReader fileBuff = new BufferedReader(new FileReader(fileName));
         String region;
         while ((region = fileBuff.readLine()) != null) {
            byte[] content = GeneralUtils.getContent(new URL(region));
            Node node = GeneralUtils.parseNeko(content);
            log.info(region);
            states.add(region);
            List<String> stateRegions = processState(node);
            if (stateRegions != null) {
               regions.addAll(stateRegions);
               for(String tmpRegion:stateRegions) {
            	   log.info(tmpRegion);
            	   srcsContent.append(template.replaceAll(toBeReplaced, tmpRegion));
               }
            }
            
         }
         log.info(String.format("States size: %d Regions size: %d", states.size(), regions.size()));
         BufferedWriter writer = new BufferedWriter(new FileWriter("regionsoutput.txt"));
         writer.write(srcsContent.toString());
         writer.close();
         log.info(srcsContent.toString());
      } catch (IOException ex) {
         log.error(ex);
         System.exit(1);
      }
   }

   public static void main(String[] args) {
      RegionsProcessor rp = new RegionsProcessor();
      rp.loadRegions("regions.txt");
   }

}
