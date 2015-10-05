package com.indusborn.ui.util;

import java.util.ArrayList;

import com.indusborn.ui.domain.PageVO;
import com.indusborn.ui.domain.PaginationVO;

public class PaginationUtil
{

   public static final String PREV_BTTN_LABEL = "Previous";
   public static final String NEXT_BTTN_LABEL = "Next";

   public static  PaginationVO 
   createNavPages(int pageNum, int pageSize, long total, int navPagesSize) {
      ArrayList<PageVO> navPages = new ArrayList<PageVO>();
      int selectedIndex = -1;
      PageVO tmp;
      int totalPages = (int)Math.ceil((double)total/(double)pageSize);
      int currPage = pageNum;
      PaginationVO pagVO = new PaginationVO();
      int constPages = 2 * navPagesSize + 1;
      int i;
      int begin;
      int end;

      if(totalPages <= 1) {
         pagVO.pageVOs = navPages;
         return pagVO;
      }

      if(totalPages <= constPages) {
         if(currPage == 0) {
            for(i = 0; i < totalPages; i++) {
               tmp = new PageVO(String.valueOf(i+1), i);
               navPages.add(tmp);
            }
            tmp = new PageVO(NEXT_BTTN_LABEL, currPage + 1); 
            navPages.add(tmp);
            selectedIndex = 0;
         } else if(currPage == totalPages - 1) {
            tmp = new PageVO(PREV_BTTN_LABEL, currPage - 1);
            navPages.add(tmp);
            for(i = 0; i < totalPages; i++) {
               tmp = new PageVO(String.valueOf(i+1), i);
               navPages.add(tmp);
            }
            selectedIndex = currPage + 1; //+1 is there because of PREV button          
         } else {
            tmp = new PageVO(PREV_BTTN_LABEL, currPage - 1);
            navPages.add(tmp);
            for(i=0; i < totalPages; i++) {
               tmp = new PageVO(String.valueOf(i+1), i);
               navPages.add(tmp);
            }
            tmp = new PageVO(NEXT_BTTN_LABEL, currPage + 1); 
            navPages.add(tmp);
            selectedIndex = currPage + 1; //+1 is there because of PREV button
         }
      } else {
         if(currPage == 0) {
            for(i = 0; i < constPages; i++) {
               tmp = new PageVO(String.valueOf(i+1), i);
               navPages.add(tmp);
            }
            tmp = new PageVO(NEXT_BTTN_LABEL, currPage + 1);
            navPages.add(tmp);
            selectedIndex = 0;
         } else if (currPage == totalPages - 1) {
            tmp = new PageVO(PREV_BTTN_LABEL, currPage - 1); 
            navPages.add(tmp);
            begin = totalPages - constPages;
            end = totalPages;
            for(i = begin; i < end; i++) {
               tmp = new PageVO(String.valueOf(i+1), i);
               navPages.add(tmp);
            }
            selectedIndex = (constPages - 1) + 1; //+1 is there because of PREV button          
         } else {
            int firstPages = currPage;
            int lastPages = totalPages - 1 - currPage;

            if(firstPages < navPagesSize) {
               tmp = new PageVO(PREV_BTTN_LABEL, currPage - 1);
               navPages.add(tmp);
               for(i = 0; i < constPages; i++) {
                  tmp = new PageVO(String.valueOf(i+1), i); 
                  navPages.add(tmp);
               }
               tmp = new PageVO(NEXT_BTTN_LABEL, currPage + 1); 
               navPages.add(tmp);
               selectedIndex = (currPage % constPages) + 1; //+1 is there because of PREV button
            }
            else if(lastPages < navPagesSize) {
               //PREV [ | | | |*| |] NEXT
               tmp = new PageVO(PREV_BTTN_LABEL, currPage - 1);
               navPages.add(tmp);
               end = currPage + lastPages + 1;
               begin = end - constPages;
               for(i = begin; i < end; i++) {
                  tmp = new PageVO(String.valueOf(i+1), i);
                  navPages.add(tmp);
               }
               tmp = new PageVO(NEXT_BTTN_LABEL, currPage+1); 
               navPages.add(tmp);
               selectedIndex = (constPages - lastPages - 1) + 1; //+1 is there because of PREV button
            } else {
               tmp = new PageVO(PREV_BTTN_LABEL, currPage - 1); 
               navPages.add(tmp);
               begin = currPage - navPagesSize;
               for(i = begin; i < begin + constPages; i++) {
                  tmp = new PageVO(String.valueOf(i+1), i); 
                  navPages.add(tmp);
               }
               tmp = new PageVO(NEXT_BTTN_LABEL, currPage + 1); 
               navPages.add(tmp);
               selectedIndex = (navPagesSize % constPages) + 1; //+1 is there because of PREV button 
            }
         }
      }

      PageVO pageVO = navPages.get(selectedIndex);
      pageVO.setSelected(true);
      pagVO.pageVOs = navPages;
      return pagVO;           
   }


}
