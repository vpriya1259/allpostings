
/* ******************************************************************************
 * Copyright (C) 2010 VMWare, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * ImageVO.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.ui.domain;

import com.indusborn.domain.PostingImage.Status;

public class ImageVO
{
   private long id;
   private Status status = Status.NEW;
   private byte[] data;
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
   public byte[] getData() {
      return data;
   }
   public void setData(byte[] data) {
      this.data = data;
   }
}
