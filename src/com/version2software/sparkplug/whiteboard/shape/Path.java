/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.shape;

import com.version2software.sparkplug.whiteboard.SVGUtil;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * <path d="M250 150 L150 350 L350 350 Z" />
 */
public class Path extends Shape {
   private List<double[]> points;

   public Path(String id, Color c, List<double[]> points) {
      super(id);
      setColor(c);
      // need to clone because passed by reference
      this.points = new ArrayList<double[]>();
      int size = points.size();
      for(int i = 0; i < size; i++) {
         double[] da = points.get(i);
         this.points.add(new double[] {da[0], da[1]});
      }
   }

   public Path(String id, Color c, List<int[]> iPoints, AffineTransform v2w) {
      super(id);
      setColor(c);

      points = new ArrayList<double[]>();
      for (int[] p : iPoints) {
         Point2D w = v2w.transform(new Point2D.Double(p[0], p[1]), null);
         points.add(new double[] {w.getX(), w.getY()});
      }
   }

   public void paintShape(Graphics2D g, AffineTransform w2v) {
      double startX = -1;
      double startY = -1;
      int size = points.size();
      for (int i = 0; i < size; i++) {
         double[] point = (double[]) points.get(i);
         Point2D p0 = w2v.transform(new Point2D.Double(startX, startY), null);
         Point2D p1 = w2v.transform(new Point2D.Double(point[0], point[1]), null);

         int x0 = (int) p0.getX();
         int y0 = (int) p0.getY();
         int x1 = (int) p1.getX();
         int y1 = (int) p1.getY();

         if (i > 0) {
            g.drawLine(x0, y0, x1, y1);
            startX = point[0];
            startY = point[1];
         }
         startX = point[0];
         startY = point[1];
      }
   }

   public List<Point2D> getSelectionPoints() {
      List<Point2D> list = new ArrayList<Point2D>();
      for(double[] p : points) {
         list.add(new Point2D.Double(p[0], p[1]));
      }
      return list;
   }

   public String asXML() {
      String s = "<path id=\"#i\" d=\"#p Z\" style=\"stroke:rgb(#r,#g,#b);stroke-width:2;#opacity\"/>";

      s = s.replaceAll("#i", getId());

      s = s.replaceAll("#r", String.valueOf(red()));
      s = s.replaceAll("#g", String.valueOf(green()));
      s = s.replaceAll("#b", String.valueOf(blue()));

      s = s.replaceAll("#p", buildPathAsXML());
      s = s.replaceAll("#opacity", (getOpacity() != 1) ? "opacity:"+String.valueOf(getOpacity())+";" : "");

      return s;
   }

   public String buildPathAsXML() {
      StringBuilder sb = new StringBuilder();
      int size = points.size();
      for (int i = 0; i < size; i++) {
         double[] point = points.get(i);
         sb.append((i == 0) ? "M" : "L");
         sb.append(point[0]);
         sb.append(" ");
         sb.append(point[1]);
         sb.append(" ");
      }
      return sb.toString();
   }

   public boolean contains(Point2D p) {
      double startX = -1;
      double startY = -1;
      int size = points.size();
      for (int i = 0; i < size; i++) {
         double[] point = (double[]) points.get(i);

         if (i > 0) {
            Line2D line = new Line2D.Double(startX, startY, point[0], point[1]);
            if (line.intersects(p.getX(), p.getY(), 1, 1)) {
               return true;
            }
            startX = point[0];
            startY = point[1];
         }
         startX = point[0];
         startY = point[1];
      }
      return false;
   }

   public void delta(double deltaX, double deltaY) {
      for (double[] point : points) {
         point[0] += deltaX;
         point[1] += deltaY;
      }
   }

   public List<double[]> getPoints() {
      return points;
   }

   public void movePoint(Point2D point, int handleIndex) {
      getPoints().set(handleIndex, new double[] {point.getX(), point.getY()});
   }

   /**
    * replacePath -- Used by the Configure process. 
    */
   public void replacePath(String xml) {
      points = SVGUtil.getPathPoints(xml);
   }

   public Shape copy() {
      return new Path(getId(), getColor(), getPoints());
   }
}
