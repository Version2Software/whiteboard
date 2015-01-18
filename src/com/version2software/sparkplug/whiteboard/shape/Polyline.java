/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.shape;

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * <polyline fill="none" stroke="blue" stroke-width="10" 
            points="50,375
                    150,375 150,325 250,325 250,375
                    350,375 350,250 450,250 450,375
                    550,375 550,175 650,175 650,375
                    750,375 750,100 850,100 850,375
                    950,375 950,25 1050,25 1050,375
                    1150,375" />
 */
public class Polyline extends Polyshape {

   public Polyline(String id, Color c, List<double[]> points, boolean fill) {
      super(id, c, points, fill);
   }

   public Polyline(String id, Color c, List<int[]> points, boolean fill, AffineTransform v2w) {
      super(id, c, points, fill, v2w);
   }

   public String asXML() {
      String s;
      if (fill) {
         s = "<polyline id=\"#i\" points=\"#p\" style=\"fill:rgb(#r,#g,#b);#opacity\" />";
      } else {
         s = "<polyline id=\"#i\" points=\"#p\" style=\"stroke:rgb(#r,#g,#b);stroke-width:2;#opacity\"/>";
      }

      s = s.replaceAll("#i", getId());

      s = s.replaceAll("#r", String.valueOf(red()));
      s = s.replaceAll("#g", String.valueOf(green()));
      s = s.replaceAll("#b", String.valueOf(blue()));

      s = s.replaceAll("#p",  buildPointsAsXML());
      s = s.replaceAll("#opacity", (getOpacity() != 1) ? "opacity:"+String.valueOf(getOpacity())+";" : "");

      return s;
   }
   
   protected GeneralPath createPoly(AffineTransform w2v) {
      GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());

      double[] start = points.get(0);
      Point2D w = new Point2D.Double(start[0], start[1]);
      Point2D v = w2v.transform(w, null);
      polyline.moveTo((int) v.getX(), (int) v.getY());

      for (double[] p : points) {
         w = new Point2D.Double(p[0], p[1]);
         v = w2v.transform(w, null);
         polyline.lineTo((int) v.getX(), (int) v.getY());
      }

      return polyline;
   }

   protected GeneralPath createPolyWorld() {
      GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());

      double[] start = points.get(0);
      polyline.moveTo((float) start[0], (float) start[1]);

      for (double[] p : points) {
         polyline.lineTo((float) p[0], (float) p[1]);
      }

      return polyline;
   }

   public Shape copy() {
      return new Polyline(getId(), getColor(), getPoints(), isFill());
   }
}
