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
import java.awt.geom.Ellipse2D;
import java.util.List;
import java.util.ArrayList;

/* 
cx = "<coordinate>"
    The x-axis coordinate of the center of the ellipse.
    If the attribute is not specified, the effect is as if a value of "0" were specified.
    Animatable: yes.
cy = "<coordinate>"
    The y-axis coordinate of the center of the ellipse.
    If the attribute is not specified, the effect is as if a value of "0" were specified.
    Animatable: yes.
rx = "<length>"
    The x-axis radius of the ellipse.
    A negative value is an error (see Error processing). A value of zero disables rendering of the element.
    Animatable: yes.
ry = "<length>"
    The y-axis radius of the ellipse.
    A negative value is an error (see Error processing). A value of zero disables rendering of the element.
    Animatable: yes.
 */
public class Ellipse extends Shape {
   private double cx;
   private double cy;
   private double rx;
   private double ry;
   private boolean fill = false;

   public Ellipse(String id, Color c, double cx, double cy, double rx, double ry, boolean fill) {
      super(id);
      setColor(c);
      this.cx = cx;
      this.cy = cy;
      this.rx = rx;
      this.ry = ry;
      this.fill = fill;
   }

   public Ellipse(String id, Color c, int ix, int iy, int iRx, int iRy, boolean fill, AffineTransform v2w) {
      super(id);
      setColor(c);

      Point2D v0 = new Point2D.Double(ix, iy);
      Point2D w0 = v2w.transform(v0, null);

      this.cx = w0.getX();
      this.cy = w0.getY();

      Point2D vx = new Point2D.Double(ix + iRx, iy);
      Point2D wx = v2w.transform(vx, null);

      Point2D vy = new Point2D.Double(ix, iy + iRy);
      Point2D wy = v2w.transform(vy, null);

      this.rx = wx.getX() - cx;
      this.ry = wy.getY() - cy;

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

   public double getRx() {
      return rx;
   }

   public void setRx(double width) {
      this.rx = width;
   }

   public double getRy() {
      return ry;
   }

   public void setRy(double height) {
      this.ry = height;
   }

   public boolean isFill() {
      return fill;
   }

   public void setFill(boolean fill) {
      this.fill = fill;
   }

   protected int[] getViewRect(AffineTransform w2v) {
      Point2D wx0 = new Point2D.Double(cx - rx, cy);
      Point2D wy0 = new Point2D.Double(cx, cy - ry);
      Point2D wx1 = new Point2D.Double(cx + rx, cy);
      Point2D wy1 = new Point2D.Double(cx, cy + ry);

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
      list.add(new Point2D.Double(cx-rx-4, cy));
      list.add(new Point2D.Double(cx, cy-ry-4));
      list.add(new Point2D.Double(cx+rx+4, cy));
      list.add(new Point2D.Double(cx, cy+ry+4));
      return list;
   }

   /*
    * <ellipse rx="250" ry="100" fill="red"  />
    */
   
   public String asXML() {
      String s = "";
      if (fill) {
         s = "<ellipse id=\"#i\" cx=\"#cx\" cy=\"#cy\" rx=\"#_rx\" ry=\"#_ry\" style=\"fill:rgb(#r,#g,#b);#opacity\" />";
      } else {
         s = "<ellipse id=\"#i\" cx=\"#cx\" cy=\"#cy\" rx=\"#_rx\" ry=\"#_ry\" style=\"stroke:rgb(#r,#g,#b);stroke-width:2;#opacity\" />";
      }

      s = s.replaceAll("#i", getId());

      s = s.replaceAll("#r", String.valueOf(red()));
      s = s.replaceAll("#g", String.valueOf(green()));
      s = s.replaceAll("#b", String.valueOf(blue()));

      s = s.replaceAll("#cx", String.valueOf(cx));
      s = s.replaceAll("#cy", String.valueOf(cy));
      s = s.replaceAll("#_rx", String.valueOf(rx));
      s = s.replaceAll("#_ry", String.valueOf(ry));

      s = s.replaceAll("#opacity", (getOpacity() != 1) ? "opacity:"+String.valueOf(getOpacity())+";" : "");
      return s;
   }
   
   public boolean contains(Point2D p) {
      Ellipse2D ellipse = new Ellipse2D.Double(cx-rx, cy-ry, 2*rx, 2*ry);
      return ellipse.contains(p);
   }

   public void delta(double deltaX, double deltaY) {
      cx += deltaX;
      cy += deltaY;
   }

   public void movePoint(Point2D point, int handleIndex) {
      if (handleIndex == 0 || handleIndex == 2) {         // east or west
         rx = point.distance(cx, cy);
      } else if (handleIndex == 1 || handleIndex == 3) {  // north or south
         ry = point.distance(cx, cy);         
      }
   }

   public Shape copy() {
      return new Ellipse(getId(), getColor(), getCx(), getCy(), getRx(), getRy(), isFill());
   }
}
