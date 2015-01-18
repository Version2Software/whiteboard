/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *<rect x="0" y="0" width="100" style="fill:green; stroke:none;" height="100" />
 */
public class Rectangle extends Shape {
   private double x;
   private double y;
   private double width;
   private double height;
   private boolean fill = false;

   public Rectangle(String id, Color c, double x, double y, double width, double height, boolean fill) {
      super(id);
      setColor(c);
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.fill = fill;
   }

   public Rectangle(String id, Color c, int ix, int iy, int iwidth, int iheight, boolean fill, AffineTransform transform) {
      super(id);
      setColor(c);

      Point2D v0 = new Point2D.Double(ix, iy);
      Point2D w0 = transform.transform(v0, null);

      this.x = w0.getX();
      this.y = w0.getY();

      Point2D v1 = new Point2D.Double(ix + iwidth, iy + iheight);
      Point2D w1 = transform.transform(v1, null);

      this.width = w1.getX() - x;
      this.height = w1.getY() - y;
      this.fill = fill;
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

   public boolean isFill() {
      return fill;
   }

   public void setFill(boolean fill) {
      this.fill = fill;
   }

   public void paintShape(Graphics2D g, AffineTransform t) {

      Point2D w0 = new Point2D.Double(x, y);
      Point2D v0 = t.transform(w0, null);
      int x0 = (int) v0.getX();
      int y0 = (int) v0.getY();

      Point2D w1 = new Point2D.Double(x+width, y+height);
      Point2D v1 = t.transform(w1, null);

      int xWidth = (int)v1.getX() - x0;
      int yHeight = (int)v1.getY() - y0;

      if (fill) {
         g.fillRect(x0, y0, xWidth, yHeight);
      } else {
         g.drawRect(x0, y0, xWidth, yHeight);
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
      String s = "";
      if (fill) {
         s = "<rect id=\"#i\" x=\"#x\" y=\"#y\" width=\"#w\" height=\"#h\" style=\"fill:rgb(#r,#g,#b);#opacity\" />";
      } else {
         s = "<rect id=\"#i\" x=\"#x\" y=\"#y\" width=\"#w\" height=\"#h\" style=\"stroke:rgb(#r,#g,#b);stroke-width:2;#opacity\" />";
      }
      s = s.replaceAll("#i", getId());

      s = s.replaceAll("#r", String.valueOf(red()));
      s = s.replaceAll("#g", String.valueOf(green()));
      s = s.replaceAll("#b", String.valueOf(blue()));

      s = s.replaceAll("#x", String.valueOf(x));
      s = s.replaceAll("#y", String.valueOf(y));
      s = s.replaceAll("#w", String.valueOf(width));
      s = s.replaceAll("#h", String.valueOf(height));

      s = s.replaceAll("#opacity", (getOpacity() != 1) ? "opacity:"+String.valueOf(getOpacity())+";" : "");
      return s;
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
      if (handleIndex == 0) {           // upper left
         width += x - point.getX();
         height += y - point.getY();
         x = point.getX();
         y = point.getY();         
      } else if (handleIndex == 1) {    // upper right
         width = point.getX() - x;
         height += y - point.getY();
         y = point.getY();
      } else if (handleIndex == 2) {    // lower left
         width += x - point.getX();
         height = point.getY() - y;
         x = point.getX();
       } else if (handleIndex == 3) {   // lower right
         width = point.getX() - x;
         height = point.getY() - y;
      }
   }

   public Shape copy() {
      return new Rectangle(getId(), getColor(), getX(), getY(), getWidth(), getHeight(), isFill());
   }
}
