package com.version2software.sparkplug.whiteboard.view;

import com.version2software.sparkplug.whiteboard.shape.Rectangle;
import com.version2software.sparkplug.whiteboard.command.Configure;
import com.version2software.sparkplug.whiteboard.command.Attribute;

import junit.framework.TestCase;

import java.awt.Color;

public class TestShapeModel extends TestCase {

   Whiteboard wb;
   ShapeModel model;

   protected void setUp() {
      wb = new Whiteboard("steve", false);
      model = wb.model;
   }

   public void testOne() throws Exception {

      assertEquals(0, model.displayListSize());
      assertEquals(0, model.undoListSize());

      model.appendShape(new Rectangle("1", Color.red, 0, 0, 1, 1, true));

      assertEquals(1, model.displayListSize());
      assertEquals(1, model.undoListSize());

      model.undo(wb);

      assertEquals(0, model.displayListSize());
      assertEquals(0, model.undoListSize());
   }

   public void testTwo() throws Exception {

      assertEquals(0, model.displayListSize());
      assertEquals(0, model.undoListSize());

      Rectangle rect = new Rectangle("1", Color.red, 0, 0, 1, 1, true);
      model.appendShape(rect);

      assertEquals(1, model.displayListSize());
      assertEquals(1, model.undoListSize());

      model.appendUndoConfigure(rect, "123");

      assertEquals(1, model.displayListSize());
      assertEquals(2, model.undoListSize());

      Configure c = new Configure("1", "123");
      c.addAttribute(new Attribute("x", "10"));
      System.out.println("c.asXML() = " + c.asXML());

      model.processConfigure(c);

      assertEquals(1, model.displayListSize());
      assertEquals(2, model.undoListSize());

      Configure c2 = new Configure("1", "124");
      c2.addAttribute(new Attribute("x", "10"));

      model.processConfigure(c2);

      assertEquals(1, model.displayListSize());
      assertEquals(3, model.undoListSize());        
   }
}
