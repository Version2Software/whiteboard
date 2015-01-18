/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.shape;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * <polygon fill="red" stroke="blue" stroke-width="10" 
            points="350,75  379,161 469,161 397,215
                    423,301 350,250 277,301 303,215
                    231,161 321,161" />
 */
public class Polygon extends Polyshape {

   public Polygon(String id, Color c, List<double[]> points, boolean fill) {
      super(id, c, points, fill);
   }

   public Polygon(String id, Color c, List<int[]> points, boolean fill, AffineTransform v2w) {
      super(id, c, points, fill, v2w);
   }

   public String asXML() {
      String s;
      if (fill) {
         s = "<polygon id=\"#i\" points=\"#p\" style=\"fill:rgb(#r,#g,#b);#opacity\" />";
      } else {
         s = "<polygon id=\"#i\" points=\"#p\" style=\"stroke:rgb(#r,#g,#b);stroke-width:2;#opacity\"/>";
      }

      s = s.replaceAll("#i", getId());

      s = s.replaceAll("#r", String.valueOf(red()));
      s = s.replaceAll("#g", String.valueOf(green()));
      s = s.replaceAll("#b", String.valueOf(blue()));

      s = s.replaceAll("#p", buildPointsAsXML());
      s = s.replaceAll("#opacity", (getOpacity() != 1) ? "opacity:"+String.valueOf(getOpacity())+";" : "");

      return s;
   }

   protected GeneralPath createPoly(AffineTransform w2v) {
      GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());
      
      double[] start = points.get(0);
      Point2D w = new Point2D.Double(start[0], start[1]);
      Point2D v = w2v.transform(w, null);
      polygon.moveTo((int) v.getX(), (int) v.getY());

      for (double[] p : points) {
         w = new Point2D.Double(p[0], p[1]);
         v = w2v.transform(w, null);
         polygon.lineTo((int) v.getX(), (int) v.getY());
      }

      polygon.closePath();
      return polygon;
   }

   protected GeneralPath createPolyWorld() {
      GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());

      double[] start = points.get(0);
      polygon.moveTo((float) start[0], (float) start[1]);

      for (double[] p : points) {
         polygon.lineTo((float) p[0], (float) p[1]);
      }

      polygon.closePath();
      return polygon;
   }
   
   public Shape copy() {
      return new Polygon(getId(), getColor(), getPoints(), isFill());
   }
}
