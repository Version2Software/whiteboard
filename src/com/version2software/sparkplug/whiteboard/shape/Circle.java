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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;

/* 
cx = "<coordinate>"
    The x-axis coordinate of the center of the circle.
    If the attribute is not specified, the effect is as if a value of "0" were specified.
    Animatable: yes.
cy = "<coordinate>"
    The y-axis coordinate of the center of the circle.
    If the attribute is not specified, the effect is as if a value of "0" were specified.
    Animatable: yes.
r = "<length>"
    The radius of the circle.
    A negative value is unsupported. A value of zero disables rendering of the element. If the attribute is not specified, the effect is as if a value of "0" were specified.
 */
public class Circle extends Shape {
   private double cx;
   private double cy;
   private double r;
   private boolean fill = false;

   public Circle(String id, Color c, double cx, double cy, double r, boolean fill) {
      super(id);
      setColor(c);
      this.cx = cx;
      this.cy = cy;
      this.r = r;
      this.fill = fill;
   }

   public Circle(String id, Color c, int icx, int icy, int ir, boolean fill, AffineTransform v2w) {
      super(id);
      setColor(c);

      Point2D v0 = new Point2D.Double(icx, icy);
      Point2D w0 = v2w.transform(v0, null);

      this.cx = w0.getX();
      this.cy = w0.getY();

      Point2D v1 = new Point2D.Double(icx + ir, icy);
      Point2D w1 = v2w.transform(v1, null);

      this.r = w1.getX() - cx;

      this.fill = fill;
   }

   public double getCx() {
      return cx;
   }

   public void setCx(double x) {
      this.cx = x;
   }

   public double getCy() {
      return cy;
   }

   public void setCy(double y) {
      this.cy = y;
   }

   public double getR() {
      return r;
   }

   public void setR(double width) {
      this.r = width;
   }

   public boolean isFill() {
      return fill;
   }

   public void setFill(boolean fill) {
      this.fill = fill;
   }

   protected int[] getViewRect(AffineTransform w2v) {
      Point2D wx0 = new Point2D.Double(cx - r, cy);
      Point2D wy0 = new Point2D.Double(cx, cy - r);
      Point2D wx1 = new Point2D.Double(cx + r, cy);
      Point2D wy1 = new Point2D.Double(cx, cy + r);

      Point2D vx0 = w2v.transform(wx0, null);
      Point2D vy0 = w2v.transform(wy0, null);
      Point2D vx1 = w2v.transform(wx1, null);
      Point2D vy1 = w2v.transform(wy1, null);

      int ix = (int) vx0.getX();
      int iy = (int) vy0.getY();
      int iwidth = (int) vx1.getX() - ix;
      int iheight = (int) vy1.getY() - iy ;

      return new int[] {ix, iy, iwidth, iheight};
   }

   public void paintShape(Graphics2D g, AffineTransform w2v) {

      int[] view = getViewRect(w2v);

      if (fill) {
         g.fillOval(view[0], view[1], view[2], view[3]);
      } else {
         g.drawOval(view[0], view[1], view[2], view[3]);
      }
   }

   public List<Point2D> getSelectionPoints() {
      List<Point2D> list = new ArrayList<Point2D>();
      list.add(new Point2D.Double(cx-r-4, cy));
      list.add(new Point2D.Double(cx, cy-r-4));
      list.add(new Point2D.Double(cx+r+4, cy));
      list.add(new Point2D.Double(cx, cy+r+4));
      return list;
   }

   public String asXML() {
      String s = "";
      if (fill) {
         s = "<circle id=\"#i\" cx=\"#cx\" cy=\"#cy\" r=\"#l\" style=\"fill:rgb(#r,#g,#b);#opacity\" />";
      } else {
         s = "<circle id=\"#i\" cx=\"#cx\" cy=\"#cy\" r=\"#l\" style=\"stroke:rgb(#r,#g,#b);stroke-width:2;#opacity\" />";
      }

      s = s.replaceAll("#i", getId());

      s = s.replaceAll("#r", String.valueOf(red()));
      s = s.replaceAll("#g", String.valueOf(green()));
      s = s.replaceAll("#b", String.valueOf(blue()));

      s = s.replaceAll("#cx", String.valueOf(cx));
      s = s.replaceAll("#cy", String.valueOf(cy));
      s = s.replaceAll("#l", String.valueOf(r));

     s = s.replaceAll("#opacity", (getOpacity() != 1) ? "opacity:"+String.valueOf(getOpacity())+";" : "");
      return s;
   }
   
   public boolean contains(Point2D p) {
      Ellipse2D ellipse = new Ellipse2D.Double(cx-r, cy-r, 2*r, 2*r);
      return ellipse.contains(p);
   }

   public void delta(double deltaX, double deltaY) {
      cx += deltaX;
      cy += deltaY;
   }

   public void movePoint(Point2D point, int handleIndex) {      
      r = point.distance(cx, cy);
   }

   public Shape copy() {
      return new Circle(getId(), getColor(), getCx(), getCy(), getR(), isFill());
   }
}
