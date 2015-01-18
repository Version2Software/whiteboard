/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Text extends Shape {

   public static int DEFAULT_FONT_SIZE = 18;

   private double x;
   private double y;
   private int size;
   private String text;
   private int textWidth;
   private int textActualHeight;
   
   public Text(String id, Color c, double x, double y, int size, String text) {
      super(id);

      setColor(c);

      this.x = x;
      this.y = y;
      this.size = size;
      this.text = text;
   }

   public Text(String id, Color c, int ix, int iy, int size, String text, AffineTransform v2w) {
      super(id);

      setColor(c);

      Point2D v0 = new Point2D.Double(ix, iy);
      Point2D w0 = v2w.transform(v0, null);

      this.x = w0.getX();
      this.y = w0.getY();
      this.size = size;
      this.text = text;
   }

   public void paintShape(Graphics2D g, AffineTransform affineTransform) {
      g.setFont(getFont());
      //need to get the FontMetrics after the font has been set
      FontMetrics fontMetrics = g.getFontMetrics();
      
      int heightOfLines = 0;
      
      StringTokenizer tokenizer = new StringTokenizer(text, System.getProperty("line.separator"));
      while (tokenizer.hasMoreTokens()) {
         String line = tokenizer.nextToken();
         
         Point2D point = affineTransform.transform(new Point2D.Double(x, (y + heightOfLines)), null);
         g.drawString(line, (int) point.getX(), (int) point.getY());
         
         int lineWidth = fontMetrics.stringWidth(line);
         if (lineWidth > textWidth) {
            textWidth = lineWidth;
         }
         
         heightOfLines += fontMetrics.getHeight();//fontMetrics.getAscent() - fontMetrics.getDescent();
      }
      
      textActualHeight = heightOfLines;
   }

   public List<Point2D> getSelectionPoints() {
      List<Point2D> list = new ArrayList<Point2D>();
      list.add(new Point2D.Double(x-4, y-4));
      return list;
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

   public int getSize() {
      return size;
   }

   public void setSize(int size) {
      this.size = size;
   }

   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public String asXML() {
      String s = "<text id=\"#i\" x=\"#x\" y=\"#y\" style=\"font-size:#spx;fill:rgb(#r,#g,#b);#opacity\">#t</text>";

      s = s.replaceAll("#i", getId());

      s = s.replaceAll("#r", String.valueOf(red()));
      s = s.replaceAll("#g", String.valueOf(green()));
      s = s.replaceAll("#b", String.valueOf(blue()));

      s = s.replaceAll("#x", String.valueOf(x));
      s = s.replaceAll("#y", String.valueOf(y));
      s = s.replaceAll("#s", String.valueOf(size));
      s = s.replaceAll("#t", String.valueOf(text));

      s = s.replaceAll("#opacity", (getOpacity() != 1) ? "opacity:"+String.valueOf(getOpacity())+";" : "");

      return s;
   }
   
   public boolean contains(Point2D p) {
      //Since the baseline of the leftmost character is at position (x, y) in this graphics 
      //context's coordinate system we need to subtract the textActualHeight (ascent - descent) 
      //from the y position.
      //http://java.sun.com/developer/onlineTraining/Media/2DText/other.html
      // todo: convert world to view
      Rectangle2D rect = new Rectangle2D.Double(x, y - textActualHeight, textWidth, textActualHeight);
      return rect.contains(p);
   }

   public void delta(double deltaX, double deltaY) {
      x += deltaX;
      y += deltaY;
   }

   private Font getFont() {
      return new Font("Dialog", Font.BOLD, size);
   }

   public void movePoint(Point2D point, int handleIndex) {
      // no operation
   }

   public Shape copy() {
      return new Text(getId(), getColor(), getX(), getY(), getSize(), getText());
   }
}
