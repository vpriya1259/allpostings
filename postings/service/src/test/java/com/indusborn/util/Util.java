package com.indusborn.util;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Blob;

import org.hibernate.Hibernate;

import com.indusborn.domain.PostingImage;
import com.indusborn.ui.domain.ImageVO;

public class Util {
   public static Blob getBlob(String fileName) throws Exception {
      File file = new File(fileName);
      int len = (int) file.length();
      byte[] bytes = new byte[(int) file.length()];
      FileInputStream fstream = new FileInputStream(file);
      fstream.read(bytes);
      fstream.close();
      return Hibernate.createBlob(bytes);
   }
   
   public static ImageVO
   getImageVO(String fileName) throws Exception {
      File file = new File(fileName);
      int len = (int)file.length();
      byte[] bytes = new byte[(int)file.length()];
      FileInputStream fstream = new FileInputStream(file);
      fstream.read(bytes);
      fstream.close();
      ImageVO imgVO = new ImageVO();
      imgVO.setData(bytes);
      return imgVO;
   }
}
