/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 */

package com.version2software.sparkplug.whiteboard.shape;

import java.awt.Color;

import junit.framework.TestCase;

public class TestRectangle extends TestCase {
   
   public void testRectangleUnFilled() throws Exception {
      String expected = "<rect id=\"1\" x=\"100.0\" y=\"200.0\" width=\"300.0\" height=\"400.0\" style=\"stroke:rgb(255,0,0);stroke-width:2;\" />";
      
      Rectangle rect = new Rectangle("1", new Color(255,0,0), 100, 200, 300, 400, false);
      
      assertEquals(expected, rect.asXML());
   }
   
   public void testRectangleFilled() throws Exception {
      String expected = "<rect id=\"1\" x=\"100.0\" y=\"200.0\" width=\"300.0\" height=\"400.0\" style=\"fill:rgb(255,0,0);\" />";
      
      Rectangle rect = new Rectangle("1", new Color(255,0,0), 100, 200, 300, 400, true);
      rect.setOpacity(1F);      
      assertEquals(expected, rect.asXML());
   }

   public void testRectangleOpacity() throws Exception {
      String expected = "<rect id=\"1\" x=\"100.0\" y=\"200.0\" width=\"300.0\" height=\"400.0\" style=\"fill:rgb(255,0,0);opacity:0.4;\" />";

      Rectangle rect = new Rectangle("1", new Color(255,0,0), 100, 200, 300, 400, true);
      rect.setOpacity(.4F);
      assertEquals(expected, rect.asXML());
   }
}
