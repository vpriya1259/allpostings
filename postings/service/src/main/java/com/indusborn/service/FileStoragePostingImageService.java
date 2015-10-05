/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indusborn.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.FileData;
import org.springframework.data.FileStorage;

import com.indusborn.util.ImageUtil;

public class FileStoragePostingImageService implements PostingImageService {

   private FileStorage storage;

   public void setStorage(FileStorage storage) {
      this.storage = storage;
   }

   public void savePostingImage(Long postingId, Long postingImageId,
         byte[] imageBytes) throws IOException {
      assertBytesLength(imageBytes);
      String contentType = guessContentType(imageBytes);
      assertContentType(contentType);
      storage.storeFile(new FileData("/" + postingId + "-"
            + postingImageId + "-small.jpg", ImageUtil.scaleImageToWidth(
            imageBytes, 50), contentType));
      storage.storeFile(new FileData("/" + postingId + "-"
            + postingImageId + "-normal.jpg", ImageUtil.scaleImageToWidth(
            imageBytes, 200), contentType));
      storage.storeFile(new FileData("/" + postingId + "-"
            + postingImageId + "-large.jpg", ImageUtil.scaleImageToWidth(
            imageBytes, 400), contentType));
   }

   public void delPostingImage(Long postingId, Long postingImageId) {
      storage.deleteFile("/" + postingId + "-" + postingImageId
            + "-small.jpg");
      storage.deleteFile("/" + postingId + "-" + postingImageId
            + "-normal.jpg");
      storage.deleteFile("/" + postingId + "-" + postingImageId
            + "-large.jpg");
   }

   public List<Long> getPostingImageIds(Long postingId) {
      return null;
   }

   // internal helpers

   private void assertBytesLength(byte[] imageBytes) {
      if (imageBytes.length == 0) {
         throw new IllegalArgumentException(
               "Cannot accept empty picture byte[] as a profile picture");
      }
   }

   private String guessContentType(byte[] imageBytes) throws IOException {
      return URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(
            imageBytes));
   }

   private void assertContentType(String contentType) {
      if (!IMAGE_TYPES.contains(contentType)) {
         throw new IllegalArgumentException("Cannot accept content type '"
               + contentType + "' as a profile picture.");
      }
   }

   private static final List<String> IMAGE_TYPES = Arrays.asList("image/jpeg",
         "image/gif", "image/png");

}
