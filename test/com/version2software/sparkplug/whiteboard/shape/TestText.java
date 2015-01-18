/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 */

package com.version2software.sparkplug.whiteboard.shape;

import junit.framework.TestCase;

import java.awt.*;

public class TestText extends TestCase {

   public void testTextOne() throws Exception {
      String expected = "<text id=\"100\" x=\"100.0\" y=\"200.0\" style=\"font-size:18px;fill:rgb(255,200,0);\">xyz</text>";

      Text text = new Text("100", new Color(255,200,0), 100, 200, 18, "xyz");

      assertEquals(expected, text.asXML());
   }
}
