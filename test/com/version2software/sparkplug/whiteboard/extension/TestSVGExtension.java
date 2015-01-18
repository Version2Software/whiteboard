/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 */

package com.version2software.sparkplug.whiteboard.extension;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import junit.framework.TestCase;

import org.jivesoftware.spark.util.Base64;
import com.version2software.sparkplug.whiteboard.SVGElement;
import com.version2software.sparkplug.whiteboard.command.ClearBackground;
import com.version2software.sparkplug.whiteboard.command.Remove;
import com.version2software.sparkplug.whiteboard.command.SetBackground;
import com.version2software.sparkplug.whiteboard.command.Configure;
import com.version2software.sparkplug.whiteboard.command.Attribute;
import com.version2software.sparkplug.whiteboard.shape.Circle;
import com.version2software.sparkplug.whiteboard.shape.Ellipse;
import com.version2software.sparkplug.whiteboard.shape.Line;
import com.version2software.sparkplug.whiteboard.shape.Path;
import com.version2software.sparkplug.whiteboard.shape.Rectangle;
import com.version2software.sparkplug.whiteboard.shape.Shape;
import com.version2software.sparkplug.whiteboard.shape.Text;

public class TestSVGExtension extends TestCase {

   public void testRectangle() throws Exception {
      String xml = "<rect id=\"100\" x=\"100.0\" y=\"200.0\" width=\"300.0\" height=\"400.0\" style=\"stroke:rgb(255,0,0);stroke-width:2;\" />";

      SVGExtension ext = new SVGExtension(xml);
      Shape shape = (Shape) ext.getSVGElement();

      assertTrue(shape instanceof Rectangle);

      Rectangle rec = (Rectangle) shape;
      assertEquals(100.0, rec.getX());
      assertEquals(200.0, rec.getY());
      assertEquals(300.0, rec.getWidth());
      assertEquals(400.0, rec.getHeight());
      assertEquals(new Color(255,0,0), rec.getColor());
      assertFalse(rec.isFill());
   }

   public void testRectangleFilled() throws Exception {
      String xml = "<rect id=\"100\" x=\"100.0\" y=\"200.0\" width=\"300.0\" height=\"400.0\" style=\"fill:rgb(255,0,0);\" />";

      SVGExtension ext = new SVGExtension(xml);
      Shape shape = (Shape) ext.getSVGElement();

      assertTrue(shape instanceof Rectangle);

      Rectangle rec = (Rectangle) shape;
      assertEquals(100.0, rec.getX());
      assertEquals(200.0, rec.getY());
      assertEquals(300.0, rec.getWidth());
      assertEquals(400.0, rec.getHeight());
      assertEquals(new Color(255,0,0), rec.getColor());
      assertTrue(rec.isFill());
   }

   public void testLine() throws Exception {
      String xml = "<line id=\"100\" x1=\"100.0\" y1=\"200.0\" x2=\"300.0\" y2=\"400.0\" style=\"stroke:rgb(255,200,0);stroke-width:2;\"/> ";

      SVGExtension ext = new SVGExtension(xml);
      Shape shape = (Shape) ext.getSVGElement();

      assertTrue(shape instanceof Line);

      Line line = (Line) shape;
      assertEquals(100.0, line.getX());
      assertEquals(200.0, line.getY());
      assertEquals(300.0, line.getxEnd());
      assertEquals(400.0, line.getyEnd());
      assertEquals(new Color(255,200,0), line.getColor());
   }

   public void testPath() throws Exception {
      String xml = "<path id=\"100\" d=\"M1 1 L3 3 L4 5  Z\" style=\"stroke:rgb(255,0,0);stroke-width:2;\"/>";

      SVGExtension ext = new SVGExtension(xml);
      Shape shape = (Shape) ext.getSVGElement();

      assertTrue(shape instanceof Path);

      Path path = (Path) shape;

      assertEquals(new Color(255,0,0), path.getColor());

      List<double []> points = path.getPoints();
      assertEquals(3, points.size());
      assertEquals(4.0, ((double[])points.get(2))[0]);
      assertEquals(5.0, ((double[])points.get(2))[1]);
   }

   public void testText() throws Exception {
      String xml = "<text id=\"100\" x=\"100.0\" y=\"200.0\" style=\"font-size:18px;fill:rgb(255,200,0);\">xyz</text>";

      SVGExtension ext = new SVGExtension(xml);
      Shape shape = (Shape) ext.getSVGElement();

      assertTrue(shape instanceof Text);

      Text text = (Text) shape;
      assertEquals(100.0, text.getX());
      assertEquals(200.0, text.getY());
      assertEquals(new Color(255,200,0), text.getColor());
      assertEquals("xyz", text.getText());
   }

   public void testEllipse() throws Exception {
      String xml = "<ellipse id=\"100\" cx=\"100.0\" cy=\"200.0\" rx=\"10.0\" ry=\"5.0\" style=\"fill:rgb(255,0,0);\" />";

      SVGExtension ext = new SVGExtension(xml);
      Shape shape = (Shape) ext.getSVGElement();

      assertTrue(shape instanceof Ellipse);

      Ellipse ellipse = (Ellipse) shape;
      assertEquals(100.0, ellipse.getCx());
      assertEquals(200.0, ellipse.getCy());
      assertEquals(10.0, ellipse.getRx());
      assertEquals(5.0, ellipse.getRy());
      assertEquals(new Color(255,0,0), ellipse.getColor());
   }

   public void testCircle() throws Exception {
      String xml = "<circle id=\"100\" cx=\"100.0\" cy=\"200.0\" r=\"10\" style=\"fill:rgb(255,0,0);\" />";

      SVGExtension ext = new SVGExtension(xml);
      Shape shape = (Shape) ext.getSVGElement();

      assertTrue(shape instanceof Circle);

      Circle circle = (Circle) shape;
      assertEquals(100.0, circle.getCx());
      assertEquals(200.0, circle.getCy());
      assertEquals(10.0, circle.getR());
      assertEquals(new Color(255,0,0), circle.getColor());
   }
   
   public void testRemove() throws Exception {
      String xml = "<remove target=\"100\"/>";

      SVGExtension ext = new SVGExtension(xml);
      SVGElement e = ext.getSVGElement();

      assertTrue(e instanceof SVGElement);

      Remove remove = (Remove) e;
      assertEquals("100", remove.getTarget());
   }

   public void testClearBackground() throws Exception {
      String xml = "<clear-background/>";

      SVGExtension ext = new SVGExtension(xml);
      SVGElement e = ext.getSVGElement();

      assertTrue(e instanceof ClearBackground);

      ClearBackground clear = (ClearBackground) e;
      assertEquals("<clear-background/>", clear.asXML());
   }

   public void testSetBackground() throws Exception {
      File file = new File("classes/test.jpg");
      assertTrue(file.exists());

      FileInputStream fis = new FileInputStream(file);
      byte[] bytes = new byte[fis.available()];
      fis.read(bytes);
      String xml = "<set-background>"+ Base64.encodeBytes(bytes)+"</set-background>";
      //System.out.println(xml);

      SVGExtension ext = new SVGExtension(xml);
      SVGElement e = ext.getSVGElement();

      assertNotNull(e);
      assertTrue(e instanceof SetBackground);

      SetBackground set = (SetBackground) e;
      //assertEquals(xml, set.asXML());
   }

   public void testConfigure() throws Exception {
      String xml = "<configure target=\"100\" random=\"100\"><attribute name=\"font-size\">40</attribute><attribute name=\"x\">10</attribute></configure>";

      SVGExtension ext = new SVGExtension(xml);
      SVGElement e = ext.getSVGElement();

      assertTrue(e instanceof Configure);

      Configure c = (Configure) e;
      List<Attribute> list = c.getAttributes();
      assertEquals(xml, c.asXML());
      assertEquals(2, list.size());
      assertEquals("font-size", list.get(0).getName());
      assertEquals("40", list.get(0).getValue());
      assertEquals("x", list.get(1).getName());
      assertEquals("10", list.get(1).getValue());
   }
}
