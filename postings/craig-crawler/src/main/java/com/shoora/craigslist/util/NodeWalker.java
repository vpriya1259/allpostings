package com.shoora.craigslist.util;

import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeWalker {
   private Node currNode;
   private NodeList currChildren;
   private Stack<Node> nodes;
   
   public NodeWalker(Node root) {
      nodes = new Stack<Node>();
      nodes.add(root);
   }
   
   public Node nextNode() {
      if (!hasNext()) {
         return null;
      }
      currNode = nodes.pop();
      currChildren = currNode.getChildNodes();
      if (currChildren != null && currChildren.getLength() > 0) {
         for (int i = currChildren.getLength() - 1; i >= 0; i--) {
            nodes.push(currChildren.item(i));
         }
      }
      return currNode;
   }
   
   
   public void skipChildren() {
      if (currChildren != null && currChildren.getLength() > 0) {
         for (int i=0; i<currChildren.getLength(); i++) {
            nodes.pop();
         }
      }
   }
   
   public boolean hasNext() {
      return nodes.size() > 0;
   }
   
   
/*   public boolean pathExists(String path) {
      if (path == null) {
         return false;
      }
      StringTokenizer tokenizer = new StringTokenizer(path, "/");
      ArrayList<String> pathElems = new ArrayList<String>();
      while (tokenizer.hasMoreTokens()) {
         pathElems.add(tokenizer.nextToken());
      }
      if (pathElems.isEmpty()) {
         return false;
      }
      
      NodeWalker nodeWalker = new NodeWalker(currNode);
      int currIndex = 0;
      String currPathElem = pathElems.get(currIndex);
      if (nodeWalker.hasNext()) {
         Node currNode = nodeWalker.nextNode();
         if (!currNode.getNodeName().equalsIgnoreCase(currPathElem)) {
            return false;
         }
         
      }
   }
*/
}
