package com.shoora.craigslist.crawler;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import com.shoora.craigslist.dataobject.LeafPageRequest;
import com.shoora.craigslist.dataobject.SrcPageRequest;
import com.shoora.craigslist.util.GeneralUtils;
import com.shoora.craigslist.util.NodeWalker;

public class SrcPageFetcher implements Callable<List<LeafPageRequest>>{
   private Log log = LogFactory.getLog(SrcPageFetcher.class);
   private SrcPageRequest srcPageReq;
   
   public SrcPageFetcher(SrcPageRequest srcPageReq) {
      this.srcPageReq = srcPageReq;
   }

   public List<LeafPageRequest> call() throws Exception {
      log.debug(String.format("Processing %s...", srcPageReq.getUrl()));
      byte[] content = GeneralUtils.getContent(new URL(srcPageReq.getUrl()));
      Node node = GeneralUtils.parseNeko(content);
      List<LeafPageRequest> leafPageReqs = generateLeafPageReqs(node);
      return leafPageReqs;
   }
   
   public List<LeafPageRequest> generateLeafPageReqs(Node root) {
      List<LeafPageRequest> leafPageReqs = null;
      NodeWalker nodeWalker = new NodeWalker(root);
      while(nodeWalker.hasNext()) {
         Node currNode = nodeWalker.nextNode();
         
         if (!GeneralUtils.shouldProcess(currNode)) {
            nodeWalker.skipChildren();
            continue;
         }
         
         String name = currNode.getNodeName();
         if("p".equalsIgnoreCase(name)){
            String href = GeneralUtils.getLeafPageUrl(currNode);
            if(href != null) {
               if (leafPageReqs == null) {
                  leafPageReqs = new ArrayList<LeafPageRequest>();
               }
               leafPageReqs.add(new LeafPageRequest(href, srcPageReq.getCatgry(), srcPageReq.getSubCatgry(), GeneralUtils.hasPic(currNode)));
            }
         }
      }
      return leafPageReqs;
   }
   
}
