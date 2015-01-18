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
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public abstract class Polyshape extends Shape {
   protected List<double[]> points;
   protected boolean fill = false;
   
   public Polyshape(String id, Color c, List<double[]> points, boolean fill) {
      super(id);
      setColor(c);

      // need to deep clone because passed by reference
      this.points = new ArrayList<double[]>();
      int size = points.size();
      for(int i = 0; i < size; i++) {
         double[] da = points.get(i);
         this.points.add(new double[] {da[0], da[1]});
      }
      this.fill = fill;
   }

   public Polyshape(String id, Color c, List<int[]> iPoints, boolean fill, AffineTransform v2w) {
      super(id);
      setColor(c);

      points = new ArrayList<double[]>();
      for (int[] p : iPoints) {
         Point2D w = v2w.transform(new Point2D.Double(p[0], p[1]), null);
         points.add(new double[] {w.getX(), w.getY()});
      }
      this.fill = fill;
   }

   protected abstract GeneralPath createPoly(AffineTransform t);
   protected abstract GeneralPath createPolyWorld();

   public boolean isFill() {
      return fill;
   }

   public void setFill(boolean fill) {
      this.fill = fill;
   }
   
   public void paintShape(Graphics2D g, AffineTransform t) {
      if (fill) {
         g.fill(createPoly(t));
      } else {
         g.draw(createPoly(t));
      }
   }

   public List<Point2D> getSelectionPoints() {
      List<Point2D> list = new ArrayList<Point2D>();
      for(double[] p : points) {
         list.add(new Point2D.Double(p[0], p[1]));
      }
      return list;
   }

   public boolean contains(Point2D p) {
      return createPolyWorld().contains(p);
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

   public String buildPointsAsXML() {
      StringBuilder sb = new StringBuilder();
      int size = points.size();
      for (int i = 0; i < size; i++) {
         double[] point = points.get(i);
         sb.append(point[0]);
         sb.append(",");
         sb.append(point[1]);
         sb.append(" ");
      }
      return sb.toString();
   }

   /**
    * replacePoints -- Used by the Configure process.
    */
   public void replacePoints(String xml) {
      points = SVGUtil.getPolylinePoints(xml);
   }
}
