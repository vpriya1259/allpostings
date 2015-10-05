
/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * Radius.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.ui.domain;
public class Radius
{
   private String label;
   private int distance;
   
   public Radius(String label, int distance) {
      this.label = label;
      this.distance = distance;
   }
   public String getLabel()
   {
      return label;
   }
   public void setLabel(String label)
   {
      this.label = label;
   }
   public int getDistance()
   {
      return distance;
   }
   public void setDistance(int distance)
   {
      this.distance = distance;
   }
}
