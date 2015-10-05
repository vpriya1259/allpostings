
/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * PaginationVO.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.ui.domain;

import java.util.ArrayList;

public class PaginationVO
{
   public ArrayList<PageVO> pageVOs;
   public ArrayList<PageVO> getPageVOs()
   {
      return pageVOs;
   }
   public void setPageVOs(ArrayList<PageVO> pageVOs)
   {
      this.pageVOs = pageVOs;
   }
}
