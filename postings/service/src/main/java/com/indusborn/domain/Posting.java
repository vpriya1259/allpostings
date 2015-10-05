package com.indusborn.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Indexed
public class Posting implements Serializable {
   @DocumentId
   @Field(index = Index.UN_TOKENIZED, store = Store.YES)
   private long id;
   private long created;
   @Field(index = Index.UN_TOKENIZED, store = Store.YES)
   private long updated;
   private String subCatgry;
   private String type;
   private String title;
   private String desc;
   private String price;
   private String currency;
   private String email;
   private String address;
   private double lat;
   private double lng;
   private User owner;
   private String src;
   @Field(index = Index.TOKENIZED, store = Store.NO)
   private String content;
   private Set<PostingImage> images = new HashSet<PostingImage>();

   public Posting() {
   }

   public Posting(long created, long updated, String subCatgry, String type,
         String title, String desc, String price, String currency,
         String email, String address, double lat, double lng, String src, User owner) {
      this.created = created;
      this.updated = updated;
      this.subCatgry = subCatgry;
      this.type = type;
      this.title = title;
      this.desc = desc;
      this.price = price;
      this.currency = currency;
      this.email = email;
      this.address = address;
      this.lat = lat;
      this.lng = lng;
      this.src = src;
      this.owner = owner;
   }

   public String getSrc() {
      return src;
   }
   
   public void setSrc(String src) {
      this.src = src;
   }

   public long getCreated() {
      return created;
   }

   public void setCreated(long created) {
      this.created = created;
   }

   public long getUpdated() {
      return updated;
   }

   public void setUpdated(long updated) {
      this.updated = updated;
   }

   public String getCurrency() {
      return currency;
   }

   public void setCurrency(String currency) {
      this.currency = currency;
   }


   public Set<PostingImage> getImages() {
      return images;
   }

   public void setImages(Set<PostingImage> images) {
      this.images = images;
   }

   public void addPostingImage(PostingImage image) {
      images.add(image);
      image.setPosting(this);
   }


   public Double getLat() {
      return lat;
   }


   public void setLat(Double lat) {
      this.lat = lat;
   }


   public Double getLng() {
      return lng;
   }


   public void setLng(Double lng) {
      this.lng = lng;
   }


   public long getId() {
      return id;
   }


   public void setId(long id) {
      this.id = id;
   }


   public String getTitle() {
      return title;
   }


   public void setTitle(String title) {
      this.title = title;
   }


   public String getDesc() {
      return desc;
   }


   public void setDesc(String desc) {
      this.desc = desc;
   }

   public String getPrice() {
      return price;
   }

   public void setPrice(String price) {
      this.price = price;
   }

   public String getEmail() {
      return email;
   }


   public void setEmail(String email) {
      this.email = email;
   }


   public User getOwner() {
      return owner;
   }


   public void setOwner(User owner) {
      this.owner = owner;
   }


   @Override
   public String toString() {
      return "id " + id + " " + title + " owner id " + owner.getId()
            + " subcatgry " + subCatgry;
   }


   public String getAddress() {
      return address;
   }


   public void setAddress(String address) {
      this.address = address;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getSubCatgry() {
      return subCatgry;
   }

   public void setSubCatgry(String subCatgry) {
      this.subCatgry = subCatgry;
   }

   public void setLat(double lat) {
      this.lat = lat;
   }

   public void setLng(double lng) {
      this.lng = lng;
   }
   
   public void setContent(String content) {
      this.content = content;
   }

   public String getContent() {
      return content;
   }
}
