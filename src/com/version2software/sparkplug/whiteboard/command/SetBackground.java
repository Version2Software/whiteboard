/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.command;

import org.jivesoftware.spark.util.Base64;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SetBackground extends Command {

   private BufferedImage image;

   public SetBackground(BufferedImage image) {
      this.image = image;
   }

   public SetBackground(byte[] bytes) {
      ByteArrayInputStream in = new ByteArrayInputStream(Base64.decode(new String(bytes)));
      JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
      try {
         this.image = decoder.decodeAsBufferedImage();
      } catch (Exception e) {
         e.printStackTrace();
         image = null;
      }
   }

   public String asXML() {
      if (image == null) {
         return null;
      }
      byte[] bytes = new byte[0];
      try {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
         JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
         param.setQuality(10.f, false);
         encoder.setJPEGEncodeParam(param);
         encoder.encode(image);
         bytes = out.toByteArray();
      } catch (IOException e) {
         e.printStackTrace();
         bytes = new byte[0];
      }
      return "<set-background>"+Base64.encodeBytes(bytes)+"</set-background>";
   }

   public void setImage(BufferedImage image) {
      this.image = image;
   }

   public BufferedImage getImage() {
      return image;
   }
}
