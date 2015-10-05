
/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * PageVO.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.ui.domain;
public class PageVO
{
   private String label;
   private int data;
   private boolean selected = false;

   public boolean isSelected()
   {
      return selected;
   }
   public void setSelected(boolean selected)
   {
      this.selected = selected;
   }
   public int getData()
   {
      return data;
   }
   public void setData(int data)
   {
      this.data = data;
   }
   public PageVO(String label, int data) {
      this.label = label;
      this.data = data;
   }
   public String getLabel()
   {
      return label;
   }
   public void setLabel(String label)
   {
      this.label = label;
   }
}
