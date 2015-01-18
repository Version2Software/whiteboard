/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.shape;

import org.jivesoftware.spark.util.Base64;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class SVGImage extends Shape {

   private double x;
   private double y;
   private double width;
   private double height;

   private BufferedImage image;

   // Used by author
   public SVGImage(String id, int ix, int iy, int iwidth, int iheight, BufferedImage image, AffineTransform v2w) {
      super(id);

      Point2D v0 = new Point2D.Double(ix, iy);
      Point2D w0 = v2w.transform(v0, null);

      this.x = w0.getX();
      this.y = w0.getY();

      Point2D v1 = new Point2D.Double(ix + iwidth, iy + iheight);
      Point2D w1 = v2w.transform(v1, null);

      this.width = w1.getX() - x;
      this.height = w1.getY() - y;

      this.image = image;
   }
   
   public SVGImage(String id, double x, double y, double width, double height, BufferedImage image) {
      super(id);
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.image = image;
   }

   // Used by receiver
   public SVGImage(String id, double x, double y, double width, double height, byte[] bytes) {
      super(id);
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;

      ByteArrayInputStream in = new ByteArrayInputStream(Base64.decode(new String(bytes)));
      try {
         this.image = ImageIO.read(in);
      } catch (Exception e) {
         e.printStackTrace();
         image = null;
      }
   }

   public double getX() {
      return x;
   }

   public void setX(double x) {
      this.x = x;
   }

   public double getY() {
      return y;
   }

   public void setY(double y) {
      this.y = y;
   }

   public double getWidth() {
      return width;
   }

   public void setWidth(double width) {
      this.width = width;
   }

   public double getHeight() {
      return height;
   }

   public void setHeight(double height) {
      this.height = height;
   }
   
   public BufferedImage getImage() {
      return image;
   }

   public void setImage(BufferedImage image) {
      this.image = image;
   }

   public void paintShape(Graphics2D g, AffineTransform t) {
      Point2D w0 = new Point2D.Double(x, y);
      Point2D v0 = t.transform(w0, null);
      int ix = (int) v0.getX();
      int iy = (int) v0.getY();

      Point2D w1 = new Point2D.Double(x+width, y+height);
      Point2D v1 = t.transform(w1, null);

      int iwidth = (int)v1.getX() - ix;
      int iheight = (int)v1.getY() - iy;

      if (image != null) {
         g.drawImage(image, ix, iy, iwidth, iheight, null);
      }
   }

   public List<Point2D> getSelectionPoints() {
      List<Point2D> list = new ArrayList<Point2D>();
      list.add(new Point2D.Double(x-4, y-4));
      list.add(new Point2D.Double(x+width+4, y-4));
      list.add(new Point2D.Double(x-4, y+height+4));
      list.add(new Point2D.Double(x+width+4, y+height+4));
      return list;
   }

   public String asXML() {
      if (image == null) {
         return "<image/>";
      }
      StringBuilder sb = new StringBuilder();

      String s = "<image id=\"#i\" x=\"#x\" y=\"#y\" width=\"#w\" height=\"#h\" #opacity>";
      s = s.replaceAll("#i", getId());

      s = s.replaceAll("#x", String.valueOf(x));
      s = s.replaceAll("#y", String.valueOf(y));
      s = s.replaceAll("#w", String.valueOf(width));
      s = s.replaceAll("#h", String.valueOf(height));

      s = s.replaceAll("#opacity", (getOpacity() != 1) ? "style=\"opacity:"+String.valueOf(getOpacity())+";\"" : "");

      sb.append(s);

      byte[] bytes = new byte[0];
      try {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         ImageIO.write(image, "jpg", out);
         bytes = out.toByteArray();

      } catch (IOException e) {
         e.printStackTrace();
         bytes = new byte[0];
      }
      sb.append(Base64.encodeBytes(bytes));
      sb.append("</image>");

      return sb.toString();
   }

   //TODO check to see if the rectangle is filled or not
   public boolean contains(Point2D p) {
      Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
      return rect.contains(p);
   }

   public void delta(double deltaX, double deltaY) {
      x += deltaX;
      y += deltaY;
   }

   public void movePoint(Point2D point, int handleIndex) {
      if (handleIndex == 0) {         // upper left
         width += x - point.getX();
         height += y - point.getY();
         x = point.getX();
         y = point.getY();
      } else if (handleIndex == 1) {  // upper right
         width = point.getX() - x;
         height += y - point.getY();
         y = point.getY();
      } else if (handleIndex == 2) {  // lower left
         width += x - point.getX();
         height = point.getY() - y;
         x = point.getX();
       } else if (handleIndex == 3) { // lower right
         width = point.getX() - x;
         height = point.getY() - y;
      }
   }

   public Shape copy() {
      return new SVGImage(getId(), getX(), getY(), getWidth(), getHeight(), getImage());
   }
}
