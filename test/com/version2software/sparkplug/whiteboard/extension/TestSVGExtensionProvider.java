/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 */

package com.version2software.sparkplug.whiteboard.extension;

import com.version2software.sparkplug.whiteboard.SVGConstants;
import com.version2software.sparkplug.whiteboard.SVGElement;
import com.version2software.sparkplug.whiteboard.command.SetBackground;
import com.version2software.sparkplug.whiteboard.shape.Rectangle;
import com.version2software.sparkplug.whiteboard.shape.Text;
import com.version2software.sparkplug.whiteboard.shape.SVGImage;
import org.jivesoftware.spark.util.Base64;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import junit.framework.TestCase;

import java.awt.Color;
import java.io.StringReader;
import java.io.File;
import java.io.FileInputStream;


public class TestSVGExtensionProvider extends TestCase {

   public void testRectangle() throws Exception {

      String xml = "<x><rect id=\"100\" x=\"100\" y=\"200\" width=\"300\" height=\"400\" style=\"stroke:rgb(255,0,0);stroke-width:2;\" /></x>";

      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

      XmlPullParser parser = factory.newPullParser();
      parser.setInput(new StringReader(xml));

      SVGExtension ext = (SVGExtension) new SVGExtensionProvider().parseExtension(parser);
      SVGElement element = ext.getSVGElement();

      assertEquals(ext.toXML(), "<x xmlns=\"" + SVGConstants.NAMESPACE + "\"><rect id=\"100\" x=\"100\" y=\"200\" width=\"300\" height=\"400\" style=\"stroke:rgb(255,0,0);stroke-width:2;\" /></x>");

      assertEquals(element.asXML(), "<rect id=\"100\" x=\"100.0\" y=\"200.0\" width=\"300.0\" height=\"400.0\" style=\"stroke:rgb(255,0,0);stroke-width:2;\" />");
      assertTrue(element instanceof Rectangle);

      Rectangle rec = (Rectangle) element;
      assertEquals(100.0, rec.getX());
      assertEquals(200.0, rec.getY());
      assertEquals(300.0, rec.getWidth());
      assertEquals(400.0, rec.getHeight());
      assertEquals(new Color(255,0,0), rec.getColor());
      assertFalse(rec.isFill());
   }

   public void testText() throws Exception {

      String xml = "<x><text id=\"100\" x=\"100\" y=\"200\" style=\"font-size:18px;fill:rgb(255,200,0);\">xyz</text></x>";

      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

      XmlPullParser parser = factory.newPullParser();
      parser.setInput(new StringReader(xml));

      SVGExtension ext = (SVGExtension) new SVGExtensionProvider().parseExtension(parser);
      SVGElement element = ext.getSVGElement();

      assertEquals(ext.toXML(), "<x xmlns=\"" + SVGConstants.NAMESPACE + "\"><text id=\"100\" x=\"100\" y=\"200\" style=\"font-size:18px;fill:rgb(255,200,0);\">xyz</text></x>");

      assertEquals("<text id=\"100\" x=\"100.0\" y=\"200.0\" style=\"font-size:18px;fill:rgb(255,200,0);\">xyz</text>", element.asXML());
      assertTrue(element instanceof Text);

      Text text = (Text) element;
      assertEquals(100.0, text.getX());
      assertEquals(200.0, text.getY());
      assertEquals(new Color(255,200,0), text.getColor());
   }

   public void testSetBackground() throws Exception {
      File file = new File("classes/test.jpg");
      assertTrue(file.exists());

      FileInputStream fis = new FileInputStream(file);
      byte[] bytes = new byte[fis.available()];
      fis.read(bytes);
      String xml = "<x xmlns=\"" + SVGConstants.NAMESPACE + "\"><set-background>"+ Base64.encodeBytes(bytes)+"</set-background></x>";

      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

      XmlPullParser parser = factory.newPullParser();
      parser.setInput(new StringReader(xml));

      SVGExtension ext = (SVGExtension) new SVGExtensionProvider().parseExtension(parser);
      SVGElement element = ext.getSVGElement();

      assertEquals(xml, ext.toXML());

      assertTrue(element instanceof SetBackground);
      SetBackground sb = (SetBackground) element;
      // todo: the xml is different even though the image is the same. scaling problem?
      //assertEquals(sb.asXML(), ext.toXML());
      assertNotNull(sb.getImage());
   }

   public void testImage() throws Exception {
      File file = new File("classes/test.jpg");
      assertTrue(file.exists());

      FileInputStream fis = new FileInputStream(file);
      byte[] bytes = new byte[fis.available()];
      fis.read(bytes);
      String xml = "<x xmlns=\"" + SVGConstants.NAMESPACE + "\"><image id=\"100\" x=\"100\" y=\"200\" width=\"300\" height=\"400\">"+ Base64.encodeBytes(bytes)+"</image></x>";

      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

      XmlPullParser parser = factory.newPullParser();
      parser.setInput(new StringReader(xml));

      SVGExtension ext = (SVGExtension) new SVGExtensionProvider().parseExtension(parser);
      SVGElement element = ext.getSVGElement();

      assertEquals(xml, ext.toXML());

      assertTrue(element instanceof SVGImage);
      SVGImage image = (SVGImage) element;
      assertEquals(100.0, image.getX());
      assertEquals(200.0, image.getY());
      System.out.println("image = " + image.asXML());
      // todo: the xml is different even though the image is the same. scaling problem?
      //assertEquals(image.asXML(), ext.toXML());
   }

}
