/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 */

package com.version2software.sparkplug.whiteboard.shape;

import java.awt.Color;

import junit.framework.TestCase;

public class TestLine extends TestCase {
   
   public void testLineOne() throws Exception {
      String expected = "<line id=\"100\" x1=\"100.0\" y1=\"200.0\" x2=\"300.0\" y2=\"400.0\" style=\"stroke:rgb(255,200,0);stroke-width:2;\"/> ";
      
      Line line = new Line("100", new Color(255,200,0), 100, 200, 300, 400);
      
      assertEquals(expected, line.asXML());
   }
}
