/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 */

package com.version2software.sparkplug.whiteboard.shape;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class TestPolyline extends TestCase {
   
   public void testPathOne() throws Exception {
      String expected = "<polyline id=\"100\" points=\"150.0,375.0 150.0,325.0 250.0,325.0 \" style=\"stroke:rgb(255,0,0);stroke-width:2;\"/>";
      
      List<double[]> points = new ArrayList<double[]>();
      points.add(new double[]{150,375});
      points.add(new double[]{150,325});
      points.add(new double[]{250,325});
      
      Polyline line = new Polyline("100", new Color(255,0,0), points, false);
      
      assertEquals(expected, line.asXML());
   }
   
   public void testPathTwo() throws Exception {
      String expected = "<polyline id=\"100\" points=\"150.0,375.0 150.0,325.0 250.0,325.0 \" style=\"fill:rgb(255,0,0);\" />";
      
      List<double[]> points = new ArrayList<double[]>();
      points.add(new double[]{150,375});
      points.add(new double[]{150,325});
      points.add(new double[]{250,325});
      
      Polyline line = new Polyline("100", new Color(255,0,0), points, true);
      
      assertEquals(expected, line.asXML());
   }
}
