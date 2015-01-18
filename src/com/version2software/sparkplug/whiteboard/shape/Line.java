/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;

/**
 * public Point2D calculateIntersect(Line2D line1, Line2D line2){
    float line1gradient = calculateGradient(line1);
    float line2gradient = calculateGradient(line2);
    float line1c = calculateC(line1.getP1(), line1gradient);
    float line2c = calculateC(line2.getP1(), line2gradient);
    float x = (line2c - line1c) / (line1gradient - line2gradient);
    float y = line1gradient * x + line1c;
    return new Point2D.Float(x, y);
}

private float calculateGradient(Line2D line){
    return (float) ( (line.getP2().getY() - line.getP1().getY()) / (line.getP2().getX() - line.getP1().getX()) );
}

private float calculateC(Point2D point, float gradient){
    return (float)( point.getY() - ( point.getX() * gradient ) );
}

algorithm to determine if a point is on a line

1. determine slope of line
 y2-y1/x2-x1

2. detemine y intercept using one of the end points of the line
b = y2 - m x2

3. substitute in the point to be tested (x3,y3) to see if equation is true
b =? y3 - m x3

 */
public class Line extends Shape {
   private double x;
   private double y;
   private double xEnd;
   private double yEnd;
   
   public Line(String id, Color c, double x, double y, double xEnd, double yEnd) {
      super(id);
      setColor(c);

      this.x = x;
      this.y = y;
      this.xEnd = xEnd;
      this.yEnd = yEnd;
   }

   public Line(String id, Color c, int ix, int iy, int iXend, int iYend, AffineTransform v2w) {
      super(id);
      setColor(c);

      Point2D v0 = new Point2D.Double(ix, iy);
      Point2D w0 = v2w.transform(v0, null);

      this.x = w0.getX();
      this.y = w0.getY();

      Point2D v1 = new Point2D.Double(iXend, iYend);
      Point2D w1 = v2w.transform(v1, null);

      this.xEnd = w1.getX();      
      this.yEnd = w1.getY();
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

   public double getxEnd() {
      return xEnd;
   }

   public void setxEnd(double xEnd) {
      this.xEnd = xEnd;
   }

   public double getyEnd() {
      return yEnd;
   }

   public void setyEnd(double yEnd) {
      this.yEnd = yEnd;
   }

   public void paintShape(Graphics2D g, AffineTransform w2v) {
      Point2D v0 = w2v.transform(new Point2D.Double(x, y), null);
      int ix = (int) v0.getX();
      int iy = (int) v0.getY();

      Point2D v1 = w2v.transform(new Point2D.Double(xEnd, yEnd), null);

      g.drawLine(ix, iy, (int)v1.getX(), (int)v1.getY());
   }

   public List<Point2D> getSelectionPoints() {
      List<Point2D> list = new ArrayList<Point2D>();
      list.add(new Point2D.Double(x, y));
      list.add(new Point2D.Double(xEnd, yEnd));
      return list;
   }

   public String asXML() {
      String s = "<line id=\"#i\" x1=\"#x1\" y1=\"#y1\" x2=\"#x2\" y2=\"#y2\" style=\"stroke:rgb(#r,#g,#b);stroke-width:2;#opacity\"/> ";

      s = s.replaceAll("#i", getId());

      s = s.replaceAll("#r", String.valueOf(red()));
      s = s.replaceAll("#g", String.valueOf(green()));
      s = s.replaceAll("#b", String.valueOf(blue()));

      s = s.replaceAll("#x1", String.valueOf(x));
      s = s.replaceAll("#y1", String.valueOf(y));
      s = s.replaceAll("#x2", String.valueOf(xEnd));
      s = s.replaceAll("#y2", String.valueOf(yEnd));

      s = s.replaceAll("#opacity", (getOpacity() != 1) ? "opacity:"+String.valueOf(getOpacity())+";" : "");
      return s;
   }
   
   public boolean contains(Point2D p) {
      Line2D line = new Line2D.Double(x, y, xEnd, yEnd);
      return line.intersects(p.getX(), p.getY(), 1, 1);
   }

   public void delta(double deltaX, double deltaY) {
      x += deltaX;
      xEnd += deltaX;
      y += deltaY;
      yEnd += deltaY;
   }

   public void movePoint(Point2D point, int handleIndex) {
      if (handleIndex == 0) {
         x = point.getX();
         y = point.getY();
      } else if (handleIndex == 1) {
         xEnd = point.getX();
         yEnd = point.getY();
      }
   }

   public Shape copy() {
      return new Line(getId(), getColor(), getX(), getY(), getxEnd(), getyEnd());
   }
}
