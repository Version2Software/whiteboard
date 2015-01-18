/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 */

package com.version2software.sparkplug.whiteboard.shape;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import junit.framework.TestCase;

public class TestCircle extends TestCase {
   
   public void testCircleOne() throws Exception {
      String expected = "<circle id=\"100\" cx=\"100.0\" cy=\"200.0\" r=\"10.0\" style=\"stroke:rgb(255,0,0);stroke-width:2;\" />";
      
      Circle circle = new Circle("100", new Color(255,0,0), 100, 200, 10, false);
      
      assertEquals(expected, circle.asXML());
   }
   
   public void testCircleTwo() throws Exception {
      String expected = "<circle id=\"100\" cx=\"100.0\" cy=\"200.0\" r=\"10.0\" style=\"fill:rgb(255,0,0);\" />";
      
      Circle circle = new Circle("100", new Color(255,0,0), 100, 200, 10, true);

      assertEquals(expected, circle.asXML());
   }

   public void testThree() throws Exception {
      AffineTransform w2v = new AffineTransform();
      double[] world = {-540, -400, 540, 400};
      int[] view = {0, 0, 540, 400};
      double scaleX = (view[2] - view[0]) / (world[2] - world[0]);
      double scaleY = (view[3] - view[1]) / (world[3] - world[1]);

      w2v.translate(270, 200);
      w2v.scale(scaleX, scaleY);

      AffineTransform v2w = w2v.createInverse();

      Circle c = new Circle("100", new Color(255,0,0), 270, 200, 135, true, v2w);

      assertEquals(0., c.getCx());
      assertEquals(0., c.getCy());
      assertEquals(270., c.getR());

      int[] v = c.getViewRect(w2v);
      assertEquals(135, v[0]);    // left
      assertEquals(65, v[1]);     // top
      assertEquals(270, v[2]);    // width
      assertEquals(270, v[3]);    // height
   }
}
