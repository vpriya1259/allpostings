package com.indusborn.ui.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import com.indusborn.domain.Posting;
import com.indusborn.util.CatgryUtil;

public class PostingsVO {
   public long total;
   public int userId;
   public int pageNum;
   public int pageSize;
   public String locationText;
   public String catgry;
   public String catgryDesc;
   public RegionVO regionVO;
   public String searchText;
   public int radius = 100;
   public String sortBy = "time";
   public List<PostingVO> postingVOs = new ArrayList<PostingVO>();

   public String getCatgryDesc() {
      return catgryDesc;
   }
   
   public String getCatgry() {
      return catgry;
   }

   public void setCatgry(String catgry) {
      this.catgry = catgry;
      this.catgryDesc = CatgryUtil.getCatgryDesc(catgry);
   }

   public List<PostingVO> getPostingVOs() {
      return postingVOs;
   }

   public void setPostingVOs(List<PostingVO> postingVOs) {
      this.postingVOs = postingVOs;
   }

   public String getSortBy() {
      return sortBy;
   }

   public void setSortBy(String sortBy) {
      this.sortBy = sortBy;
   }

   public int getRadius() {
      return radius;
   }

   public void setRadius(int radius) {
      this.radius = radius;
   }

   public long getTotal() {
      return total;
   }

   public void setTotal(long total) {
      this.total = total;
   }

   public int getUserId() {
      return userId;
   }

   public void setUserId(int userId) {
      this.userId = userId;
   }

   public int getPageNum() {
      return pageNum;
   }

   public void setPageNum(int pageNum) {
      this.pageNum = pageNum;
   }

   public int getPageSize() {
      return pageSize;
   }

   public void setPageSize(int pageSize) {
      this.pageSize = pageSize;
   }

   public String getSearchText() {
      return searchText;
   }

   public void setSearchText(String searchText) {
      this.searchText = searchText;
   }

   public String getLocationText() {
      return locationText;
   }

   public void setLocationText(String locationText) {
      this.locationText = locationText;
   }

   public RegionVO getRegionVO() {
      return regionVO;
   }

   public void setRegionVO(RegionVO regionVO) {
      this.regionVO = regionVO;
   }

}
