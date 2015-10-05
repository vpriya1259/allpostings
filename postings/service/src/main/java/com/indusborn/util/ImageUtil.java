/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * ImageUtil.java --
 * <p>
 * TODOFile Description goes here
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import com.indusborn.domain.PostingImage.Status;
import com.indusborn.ui.domain.ImageVO;

public class ImageUtil {
   
   public static ImageVO getImage(byte[] content) {
      ImageVO imageVO = new ImageVO();
      imageVO.setData(content);
      return imageVO;
   }

   public static ImageVO getImage(MultipartFile file) {
      try {
         ImageVO imageVO = new ImageVO();
         if (file.getBytes().length == 0) {
            return null;
         }
         imageVO.setStatus(Status.NEW);
         imageVO.setData(file.getBytes());
         return imageVO;
      } catch (Exception ex) {
         return null;
      }

   }

   public static byte[] scaleImageToWidth(byte[] originalBytes, int scaledWidth)
         throws IOException {
      BufferedImage originalImage =
            ImageIO.read(new ByteArrayInputStream(originalBytes));
      int originalWidth = originalImage.getWidth();
      if (originalWidth <= scaledWidth) {
         return originalBytes;
      }
      int scaledHeight =
            (int) (originalImage.getHeight() * ((float) scaledWidth / (float) originalWidth));
      BufferedImage scaledImage =
            new BufferedImage(scaledWidth, scaledHeight,
                  BufferedImage.TYPE_INT_RGB);
      Graphics2D g = scaledImage.createGraphics();
      g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
      g.dispose();
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      ImageIO.write(scaledImage, "jpeg", byteStream);
      return byteStream.toByteArray();
   }

}
