/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 */

package com.version2software.sparkplug.whiteboard.command;

import junit.framework.TestCase;

public class TestRemove  extends TestCase {

   public void testOne() throws Exception {

      String expected = "<remove target=\"100\"/>";

      Remove r = new Remove("100");

      assertEquals(expected, r.asXML());
      assertEquals("100", r.getTarget());
   }
}
