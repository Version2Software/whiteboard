/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 */

package com.version2software.sparkplug.whiteboard.shape;

import java.awt.Color;

import junit.framework.TestCase;

public class TestEllipse extends TestCase {
   
   public void testEllipseOne() throws Exception {
      String expected = "<ellipse id=\"100\" cx=\"100.0\" cy=\"200.0\" rx=\"10.0\" ry=\"5.0\" style=\"stroke:rgb(255,0,0);stroke-width:2;\" />";
      
      Ellipse ellipse = new Ellipse("100", new Color(255,0,0), 100, 200, 10, 5, false);
      
      assertEquals(expected, ellipse.asXML());
   }
   
   public void testEllipseTwo() throws Exception {
      String expected = "<ellipse id=\"100\" cx=\"100.0\" cy=\"200.0\" rx=\"10.0\" ry=\"5.0\" style=\"fill:rgb(255,0,0);\" />";
      
      Ellipse ellipse = new Ellipse("100", new Color(255,0,0), 100, 200, 10, 5, true);

      assertEquals(expected, ellipse.asXML());
   }
}
