
/* ******************************************************************************
 * Copyright (C) 2010 VMWare, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * PostingImg.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.domain;

import java.io.Serializable;
import java.sql.Blob;

public class PostingImage implements Serializable
{
   public static enum Status {
      NEW, DELETE, KEEP
   }
   private long id;
   private Status status;
   private Posting posting;
   
   public Posting getPosting() {
      return posting;
   }
   public void setPosting(Posting posting) {
      this.posting = posting;
   }
   public long getId()
   {
      return id;
   }
   public void setId(long id)
   {
      this.id = id;
   }
   public Status getStatus() {
      return status;
   }
   public void setStatus(Status status) {
      this.status = status;
   }
}
