package com.shoora.craigslist.dataobject;

public class CImage {
   private final byte[] content;
   
   public CImage(byte[] content) {
      this.content = content;
   }
   
   public byte[] getContent() {
      return content;
   }
}
