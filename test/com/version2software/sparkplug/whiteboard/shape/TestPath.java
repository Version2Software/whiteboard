/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 */

package com.version2software.sparkplug.whiteboard.shape;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class TestPath extends TestCase {
   
   public void testPathOne() throws Exception {
      String expected = "<path id=\"100\" d=\"M1.0 1.0 L3.0 3.0 L4.0 4.0  Z\" style=\"stroke:rgb(255,0,0);stroke-width:2;\"/>";
      
      List<double[]> points = new ArrayList<double[]>();
      points.add(new double[]{1,1});
      points.add(new double[]{3,3});
      points.add(new double[]{4,4});
      
      Path line = new Path("100", new Color(255,0,0), points);
      
      assertEquals(expected, line.asXML());
   }
}
