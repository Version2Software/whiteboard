/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 */

package com.version2software.sparkplug.whiteboard.command;

import junit.framework.TestCase;

import java.util.List;

public class TestConfigure  extends TestCase {

   public void testOne() throws Exception {

      String expected = "<configure target=\"100\" random=\"1\"><attribute name=\"font-size\">40</attribute><attribute name=\"x\">10</attribute></configure>";

      Configure c = new Configure("100", "1");
      c.addAttribute(new Attribute("font-size", "40"));
      c.addAttribute(new Attribute("x", "10"));

      List<Attribute> list = c.getAttributes();

      assertEquals(expected, c.asXML());
      assertEquals(2, list.size());
      assertEquals("font-size", list.get(0).getName());
      assertEquals("40", list.get(0).getValue());
      assertEquals("x", list.get(1).getName());
      assertEquals("10", list.get(1).getValue());
   }
}
