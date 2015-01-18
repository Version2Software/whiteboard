/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 */

package com.version2software.sparkplug.whiteboard.command;

import junit.framework.TestCase;

public class TestOrder  extends TestCase {

   public void testOne() throws Exception {
      String expected = "<order target=\"100\" position=\"back\"/>";

      Order r = new Order("100", "back");

      assertEquals(expected, r.asXML());
      assertEquals("100", r.getTarget());
      assertEquals("back", r.getPosition());
   }
   
   public void testTwo() throws Exception {
      String expected = "<order target=\"43\" position=\"forward\"/>";

      Order r = new Order("43", "forward");

      assertEquals(expected, r.asXML());
      assertEquals("43", r.getTarget());
      assertEquals("forward", r.getPosition());
   }
}
