/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.shape;

import com.version2software.sparkplug.whiteboard.SVGElement;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public abstract class Shape extends SVGElement {

   private String id;
   private Color color;
   private boolean selected;
   private float opacity = 1F;

   public void paint(Graphics g, AffineTransform t) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setColor(getColor());
      Composite oldComposite = g2.getComposite();
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getOpacity()));

      paintShape(g2, t);

      if(isSelected()) {
         select(g2, t);
      }

      g2.setComposite(oldComposite);
   }

   public abstract void paintShape(Graphics2D g, AffineTransform t);
   
   public abstract boolean contains(Point2D p);

   public abstract void delta(double deltaX, double deltaY);

   public Shape() {
   }

   public Shape(String id) {
      this.id = id;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public Color getColor() {
      return color;
   }

   public void setColor(Color color) {
      this.color = color;
   }

   public float getOpacity() {
      return opacity;
   }

   public void setOpacity(float opacity) {
      this.opacity = opacity;
   }
   // (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue).

   public int red() {
      return (color.getRGB() >> 16) & 0xFF;
   }

   public int green() {
      return (color.getRGB() >> 8) & 0xFF;
   }

   public int blue() {
      return color.getRGB() & 0xFF;
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public void select(Graphics g, AffineTransform t) {
      drawSelectionPoints(g, t, Color.blue);
   }

   public void preselect(Graphics g, AffineTransform t) {
      drawSelectionPoints(g, t, Color.red);
   }

   private void drawSelectionPoints(Graphics g, AffineTransform t, Color color) {
      List<Point2D> list = getSelectionPoints();
      for(Point2D p : list) {
         drawSelectedPoint(g, t, p, color);
      }
   }

   public void drawSelectedPoint(Graphics g, AffineTransform t, Point2D point, Color color) {
      Point2D v0 = t.transform(point, null);
      int x = (int) v0.getX();
      int y = (int) v0.getY();
      g.setColor(color);
      g.fillRect(x-4, y-4, 8, 8);
   }

   public abstract List<Point2D> getSelectionPoints();

   public int getHandleIndex(AffineTransform w2s, Point2D viewPoint) {
      List<Point2D> list = getSelectionPoints();
      for (int i = 0; i < list.size(); i++) {
         Point2D v = w2s.transform(list.get(i), null);

         int x = (int) v.getX() - 4;
         int y = (int) v.getY() - 4;

         Rectangle2D rect = new Rectangle2D.Double(x, y, 8, 8);
         if (rect.contains(viewPoint)) {
            return i;
         }
      }
      return -1;
   }

   public abstract void movePoint(Point2D point, int handleIndex);
   public abstract Shape copy();
}
