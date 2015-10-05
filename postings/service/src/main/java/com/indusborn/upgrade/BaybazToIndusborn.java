package com.indusborn.upgrade;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.indusborn.service.PostingService;
import com.indusborn.ui.domain.ImageVO;
import com.indusborn.ui.domain.PostingVO;

public class BaybazToIndusborn {
   private Log log = LogFactory.getLog(BaybazToIndusborn.class);

   public static void main(String[] args) {
      BaybazToIndusborn bb2ib = new BaybazToIndusborn();
      bb2ib.transferUserTable();
      bb2ib.transferPostings();
   }


   private static String[] paths = { "baybaz-business.xml",
         "indusborn-business.xml" };
   private ApplicationContext ctx;
   private JdbcTemplate bbJdbcTemplate;
   private JdbcTemplate ibJdbcTemplate;
   private PostingService postingService;

   public JdbcTemplate getBbJdbcTemplate() {
      return bbJdbcTemplate;
   }


   //Create baybaz schema
   //Upload baybaz data
   //Create indusborn schema
   //Transfer baybaz data to indusborn
   public BaybazToIndusborn() {
      ctx = new ClassPathXmlApplicationContext(paths);
      bbJdbcTemplate = (JdbcTemplate) ctx.getBean("bbJdbcTemplate");
      ibJdbcTemplate = (JdbcTemplate) ctx.getBean("ibJdbcTemplate");
      postingService = (PostingService) ctx.getBean("postingService");
   }


   public void transferUserTable() {
      log.info("Transferring user table...");
      List<BBUserVO> bbUsers =
            bbJdbcTemplate.query(BB_USER_QUERY, new RowMapper<BBUserVO>() {
               public BBUserVO mapRow(ResultSet rs, int rowNum)
                     throws SQLException {
                  BBUserVO user = new BBUserVO();
                  user.setId(rs.getInt("user_id"));
                  user.setCreated(rs.getTimestamp("user_created").getTime());
                  Timestamp updated = rs.getTimestamp("user_updated");
                  if (updated != null) {
                     user.setUpdated(updated.getTime());
                  }
                  user.setPassword(rs.getString("user_password"));
                  user.setEnabled(rs.getBoolean("user_enabled"));
                  user.setFname(rs.getString("user_fname"));
                  user.setLname(rs.getString("user_lname"));
                  user.setEmail(rs.getString("user_email"));
                  user.setAddress(rs.getString("user_address"));
                  user.setZip(rs.getString("user_zip"));
                  user.setPhone(rs.getString("user_phone"));
                  return user;
               }
            });

      if (bbUsers == null || bbUsers.size() == 0) {
         return;
      }

      for (BBUserVO user : bbUsers) {
         ibJdbcTemplate.update(IB_INSERT_USER_QUERY, user.getId(),
               user.getCreated(), user.getUpdated(), user.getPassword(),
               user.isEnabled(), user.getFname(), user.getLname(),
               user.getEmail(), user.getAddress(), user.getZip(),
               user.getPhone());
      }

      log.info("Transferred user table!");
   }

   private void transferPostings() {
      log.info("Transferring postings...");
      String[] catgries = { "Rental", "Rommate", "Car", "Buysell" };
      for (String catgry : catgries) {
         final String mainCatgry = catgry;
         String POSTING_QUERY = getPostingQuery(catgry);
         
         //Get baybaz postings
         List<BBPostingVO> bbPostings =
               bbJdbcTemplate.query(POSTING_QUERY,
                     new RowMapper<BBPostingVO>() {
                        public BBPostingVO mapRow(ResultSet rs, int rowNum)
                              throws SQLException {
                           BBPostingVO postingVO = new BBPostingVO();
                           postingVO.setId(rs.getLong(1));
                           postingVO.setCreated(rs.getTimestamp(2).getTime());
                           Timestamp updated = rs.getTimestamp(3);
                           if (updated != null) {
                              postingVO.setUpdated(updated.getTime());
                           }
                           postingVO.setType(rs.getString(4));
                           postingVO.setTitle(rs.getString(5));
                           postingVO.setDesc(rs.getString(6));
                           postingVO.setPrice(rs.getString(7));
                           postingVO.setEmail(rs.getString(8));
                           postingVO.setAddress(rs.getString(9));
                           postingVO.setLat(rs.getDouble(10));
                           postingVO.setLng(rs.getDouble(11));
                           postingVO.setCurrencyCode(rs.getString(12));
                           postingVO.setOwnerId(rs.getInt(13));
                           postingVO.setSubCatgry(rs.getString(14));
                           postingVO.setCatgry(mainCatgry);
                           return postingVO;
                        }
                     });

         //Store in indusborn
         for (BBPostingVO bbPosting : bbPostings) {
            String POSTING_IMAGE_QUERY = getPostingImgQuery(mainCatgry);
            List<BBPostingImgVO> bbPostingImages =
                  bbJdbcTemplate.query(POSTING_IMAGE_QUERY,
                        new Object[] { bbPosting.getId() },
                        new RowMapper<BBPostingImgVO>() {
                           public BBPostingImgVO mapRow(ResultSet rs, int rowNum)
                                 throws SQLException {
                              BBPostingImgVO postingImgVO =
                                    new BBPostingImgVO();
                              Blob blob = rs.getBlob(1);
                              postingImgVO.setContent(getByteArray(blob));
                              return postingImgVO;
                           }
                        });

            PostingVO postingVO = new PostingVO();
            postingVO.setCreated(bbPosting.getCreated());
            postingVO.setUpdated(bbPosting.getCreated());
            postingVO.setType(bbPosting.getType());
            postingVO.setTitle(bbPosting.getTitle());
            postingVO.setDesc(bbPosting.getDesc());
            postingVO.setPrice(bbPosting.getPrice());
            postingVO.setEmail(bbPosting.getEmail());
            postingVO.setAddress(bbPosting.getAddress());
            postingVO.setLat(bbPosting.getLat());
            postingVO.setLng(bbPosting.getLng());
            postingVO.setCurrency(bbPosting.getCurrencyCode());
            postingVO.setOwnerId(bbPosting.getOwnerId());
            postingVO.setSubCatgry(bbPosting.getSubCatgry());
            if("Rommate".equalsIgnoreCase(bbPosting.getCatgry())) {
               postingVO.setCatgry("Roommate");   
            } else {
               postingVO.setCatgry(bbPosting.getCatgry());
            }
            

            for (BBPostingImgVO image : bbPostingImages) {
               ImageVO imageVO = new ImageVO();
               imageVO.setData(image.getContent());
               postingVO.getImageVOs().add(imageVO);
            }
            postingService.addPosting(postingVO);
         }
      }
      log.info("Transferred postings!");
   }

   private byte[] getByteArray(Blob blob) throws SQLException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buf = new byte[1024];
      InputStream in = blob.getBinaryStream();
      int n = 0;
      try {
         while ((n = in.read(buf)) >= 0) {
            baos.write(buf, 0, n);
         }
      } catch (IOException ex) {
         log.error("IOException while reading the stream", ex);
      } finally {
         try {
            in.close();
         } catch (IOException ex) {
            log.error("IOException while closing the input stream", ex);
         }
      }
      byte[] bytes = baos.toByteArray();
      return bytes;
   }

   private String getPostingQuery(String catgry) {
      String postingQuery = new String(BB_POSTING_QUERY);
      String newQuery =
            postingQuery.replaceAll("posting", catgry.toLowerCase());
      return newQuery;
   }

   private String getPostingImgQuery(String catgry) {
      String postingImageQuery = new String(BB_POSTING_IMAGE_QUERY);
      String newQuery =
            postingImageQuery.replaceAll("posting", catgry.toLowerCase());
      return newQuery;
   }


   private static final String BB_USER_QUERY =
         "select user_id, user_created, user_updated, user_password, user_enabled, user_fname, user_lname, "
               + "user_email, user_address, user_zip, user_phone from bb_user";

   private static final String IB_INSERT_USER_QUERY =
         "insert into ib_user(user_id, user_created, user_updated, user_password, user_enabled, user_fname, user_lname, user_email, user_address, user_zip, user_phone) "
               + "values (?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?)";

   private static final String BB_POSTING_QUERY =
         "select posting_id, posting_created, posting_updated, posting_type, posting_title, posting_desc, posting_price, posting_email, posting_address, "
               + "posting_lat, posting_lng, posting_currency_code, posting_owner_id, catgry_name from bb_posting, bb_catgry where bb_posting.posting_catgry_id = bb_catgry.catgry_id";

   private static final String IB_INSERT_POSTING_QUERY =
         "insert into ib_posting(posting_id, posting_created, posting_updated, posting_catgry, posting_subcatgry, posting_type, posting_title, posting_desc, posting_price, posting_email, posting_address, posting_lat, posting_lng, posting_currency, posting_owner_id) "
               + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

   private static final String BB_POSTING_IMAGE_QUERY =
         "select posting_image_image from bb_posting_image where posting_image_posting_id = ?";


   private static class BBPostingImgVO {
      private byte[] content;

      public byte[] getContent() {
         return content;
      }

      public void setContent(byte[] content) {
         this.content = content;
      }
   }

   private static class BBPostingVO {
      private long id;
      private long created;
      private long updated;
      private Date day;
      private String type;
      private String title;
      private String desc;
      private String price;
      private String email;
      private String address;
      private Double lat;
      private Double lng;
      private String currencyCode;
      private String subCatgry;
      private String catgry;
      private int ownerId;

      public String getCatgry() {
         return catgry;
      }

      public void setCatgry(String catgry) {
         this.catgry = catgry;
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

      public long getId() {
         return id;
      }

      public void setId(long id) {
         this.id = id;
      }

      public Date getDay() {
         return day;
      }

      public void setDay(Date day) {
         this.day = day;
      }

      public String getType() {
         return type;
      }

      public void setType(String type) {
         this.type = type;
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

      public String getAddress() {
         return address;
      }

      public void setAddress(String address) {
         this.address = address;
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

      public String getCurrencyCode() {
         return currencyCode;
      }

      public void setCurrencyCode(String currencyCode) {
         this.currencyCode = currencyCode;
      }

      public String getSubCatgry() {
         return subCatgry;
      }

      public void setSubCatgry(String subCatgry) {
         this.subCatgry = subCatgry;
      }

      public int getOwnerId() {
         return ownerId;
      }

      public void setOwnerId(int ownerId) {
         this.ownerId = ownerId;
      }
   }

   private static class BBUserVO {
      private int id;
      private String password;
      private long created;
      private long updated;
      private boolean enabled;
      private String fname;
      private String lname;
      private String email;
      private String address;
      private String zip;
      private String phone;

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

      public int getId() {
         return id;
      }

      public void setId(int id) {
         this.id = id;
      }

      public String getPassword() {
         return password;
      }

      public void setPassword(String password) {
         this.password = password;
      }

      public boolean isEnabled() {
         return enabled;
      }

      public void setEnabled(boolean enabled) {
         this.enabled = enabled;
      }

      public String getFname() {
         return fname;
      }

      public void setFname(String fname) {
         this.fname = fname;
      }

      public String getLname() {
         return lname;
      }

      public void setLname(String lname) {
         this.lname = lname;
      }

      public String getEmail() {
         return email;
      }

      public void setEmail(String email) {
         this.email = email;
      }

      public String getAddress() {
         return address;
      }

      public void setAddress(String address) {
         this.address = address;
      }

      public String getZip() {
         return zip;
      }

      public void setZip(String zip) {
         this.zip = zip;
      }

      public String getPhone() {
         return phone;
      }

      public void setPhone(String phone) {
         this.phone = phone;
      }
   }

}
