/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard;

import com.version2software.sparkplug.whiteboard.command.Attribute;
import com.version2software.sparkplug.whiteboard.command.ClearAll;
import com.version2software.sparkplug.whiteboard.command.ClearBackground;
import com.version2software.sparkplug.whiteboard.command.Configure;
import com.version2software.sparkplug.whiteboard.command.Order;
import com.version2software.sparkplug.whiteboard.command.Remove;
import com.version2software.sparkplug.whiteboard.command.SetBackground;
import com.version2software.sparkplug.whiteboard.command.Undo;
import com.version2software.sparkplug.whiteboard.shape.Circle;
import com.version2software.sparkplug.whiteboard.shape.Ellipse;
import com.version2software.sparkplug.whiteboard.shape.Line;
import com.version2software.sparkplug.whiteboard.shape.Path;
import com.version2software.sparkplug.whiteboard.shape.Polygon;
import com.version2software.sparkplug.whiteboard.shape.Polyline;
import com.version2software.sparkplug.whiteboard.shape.Rectangle;
import com.version2software.sparkplug.whiteboard.shape.SVGImage;
import com.version2software.sparkplug.whiteboard.shape.Text;
import com.version2software.sparkplug.whiteboard.shape.Shape;

import org.jdom.Element;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SVGUtil {

   /**
    * Converts a jdom element into a SVGElement. If the elements name is not of a recognized
    * SVGElement a null object will be returned.
    * 
    * @param e the jdom element to be converted into a SVGElement 
    * @return the SVGElement of the parsed jdom element
    */
   public static SVGElement parseSVGElement(Element e) {
      SVGElement svgElement = null;
      String elementName = e.getName();
      if (elementName.equals("rect")) {
         String id = e.getAttributeValue("id");
         String x = e.getAttributeValue("x");
         String y = e.getAttributeValue("y");
         String width = e.getAttributeValue("width");
         String height = e.getAttributeValue("height");
         String style = e.getAttributeValue("style");
         Rectangle rect = new Rectangle(id, SVGUtil.getColor(style), Double.valueOf(x), Double.valueOf(y),
               Double.valueOf(width), Double.valueOf(height), (style.indexOf("fill") >= 0));
         rect.setOpacity(parseOpacity(style));
         svgElement = rect;

      } else if (elementName.equals("line")) {
         String id = e.getAttributeValue("id");
         String x1 = e.getAttributeValue("x1");
         String y1 = e.getAttributeValue("y1");
         String x2 = e.getAttributeValue("x2");
         String y2 = e.getAttributeValue("y2");
         String style = e.getAttributeValue("style");

         Line line = new Line(id, SVGUtil.getColor(style), Double.valueOf(x1), Double.valueOf(y1), Double.valueOf(x2), Double.valueOf(y2));
         line.setOpacity(parseOpacity(style));
         svgElement = line;

      } else if (elementName.equals("path")) {
         String id = e.getAttributeValue("id");
         String d = e.getAttributeValue("d");
         String style = e.getAttributeValue("style");

         Path path = new Path(id, SVGUtil.getColor(style), SVGUtil.getPathPoints(d));
         path.setOpacity(parseOpacity(style));
         svgElement = path;

      } else if (elementName.equals("text")) {
         String id = e.getAttributeValue("id");
         String x1 = e.getAttributeValue("x");
         String y1 = e.getAttributeValue("y");
         String style = e.getAttributeValue("style");
         String text = e.getText();
         int fontSize = parseFontSize(style);
         Text t = new Text(id, SVGUtil.getColor(style), Double.valueOf(x1), Double.valueOf(y1), fontSize, text);
         t.setOpacity(parseOpacity(style));
         svgElement = t;

      } else if (elementName.equals("ellipse")) {
         String id = e.getAttributeValue("id");
         String cx = e.getAttributeValue("cx");
         String cy = e.getAttributeValue("cy");
         String rx = e.getAttributeValue("rx");
         String ry = e.getAttributeValue("ry");
         String style = e.getAttributeValue("style");
         Ellipse ellipse = new Ellipse(id, SVGUtil.getColor(style), Double.valueOf(cx), Double.valueOf(cy),
               Double.valueOf(rx), Double.valueOf(ry), (style.indexOf("fill") >= 0));
         ellipse.setOpacity(parseOpacity(style));
         svgElement = ellipse;

      } else if (elementName.equals("circle")) {
         String id = e.getAttributeValue("id");
         String cx = e.getAttributeValue("cx");
         String cy = e.getAttributeValue("cy");
         String r = e.getAttributeValue("r");
         String style = e.getAttributeValue("style");
         Circle circle = new Circle(id, SVGUtil.getColor(style), Double.valueOf(cx), Double.valueOf(cy),
               Double.valueOf(r), (style.indexOf("fill") >= 0));
         circle.setOpacity(parseOpacity(style));
         svgElement = circle;

      } else if (elementName.equals("polyline")) {
         String id = e.getAttributeValue("id");
         String points = e.getAttributeValue("points");
         String style = e.getAttributeValue("style");
         Polyline polyline = new Polyline(id, SVGUtil.getColor(style), SVGUtil.getPolylinePoints(points), 
               (style.indexOf("fill") >= 0));
         polyline.setOpacity(parseOpacity(style));
         svgElement = polyline;

      } else if (elementName.equals("polygon")) {
         String id = e.getAttributeValue("id");
         String points = e.getAttributeValue("points");
         String style = e.getAttributeValue("style");
         Polygon polyline = new Polygon(id, SVGUtil.getColor(style), SVGUtil.getPolylinePoints(points), 
               (style.indexOf("fill") >= 0));
         polyline.setOpacity(parseOpacity(style));
         svgElement = polyline;

      } else if (elementName.equals("image")) {
         String id = e.getAttributeValue("id");
         String x = e.getAttributeValue("x");
         String y = e.getAttributeValue("y");
         String width = e.getAttributeValue("width");
         String height = e.getAttributeValue("height");
         String style = e.getAttributeValue("style");
         SVGImage image = new SVGImage(id, Double.valueOf(x), Double.valueOf(y),
         Double.valueOf(width), Double.valueOf(height), e.getText().getBytes());
         image.setOpacity(parseOpacity(style));
         svgElement = image;

      } else if (elementName.equals("remove")) {
         String target = e.getAttributeValue("target");
         svgElement = new Remove(target);

      } else if (elementName.equals("configure")) {
         String target = e.getAttributeValue("target");
         String random = e.getAttributeValue("random");
         Configure configure = new Configure(target, random);

         List elements = e.getChildren();
         for (Object o : elements) {
            Element attribute = (Element) o;
            String name = attribute.getAttributeValue("name");
            String value = attribute.getText();
            configure.addAttribute(new Attribute(name, value));
         }
         svgElement = configure;

      } else if (elementName.equals("clear-all")) {
         svgElement = new ClearAll();

      } else if (elementName.equals("clear-background")) {
         svgElement = new ClearBackground();
         
      } else if (elementName.equals("set-background")) {
         svgElement = new SetBackground(e.getText().getBytes());

      } else if (elementName.equals("undo")) {
         svgElement = new Undo();
      }

      else if (elementName.equals("order")) {
         String target = e.getAttributeValue("target");
         String position = e.getAttributeValue("position");
         svgElement = new Order(target, position);
      }

      return svgElement;
   }

   //style="stroke:rgb(0,0,0); stroke-width:2"
   /**
    * Converts a String in a "rgb(0,0,0)" format to a Java Color. If the String is null
    * or the String cannot be parsed a Color.BLACK will be returned.
    *
    * @param s the String to be converted to a Color
    * @return the Color of the String s parameter
    */
   public static Color getColor(String s) {
      if (s == null) {
         return Color.BLACK;
      }

      String patternStr = "rgb\\(\\d+,\\d+,\\d+\\)";

      Pattern pattern = Pattern.compile(patternStr);
      Matcher matcher = pattern.matcher(s);

      if (matcher.find()) {
         String pre = matcher.group(0);
         String post = pre.replaceAll("rgb\\(", "").replaceAll("\\)", "");

         String[] colors = post.split(",");

         return new Color(Integer.parseInt(colors[0]),
               Integer.parseInt(colors[1]),
               Integer.parseInt(colors[2]));
      }

      return Color.BLACK;
   }

   /**
    * Converts a String in a "opacity:0.5" format to a floarting point value between 0.0 and 1.0. 
    * If the String is null or the String cannot be parsed a floating point value of 1F will be returned.
    *
    * @param style the String to be converted to a float
    * @return the float value of the String style parameter
    */
   public static float parseOpacity(String style) {
      if (style == null) {
         return 1F;
      }

      Pattern pattern = Pattern.compile("opacity:(0.)?\\d;");
      Matcher matcher = pattern.matcher(style);

      if (matcher.find()) {
         String s = matcher.group(0);
         s = s.replaceAll("opacity:", "");
         s = s.replaceAll(";", "");

         return Float.parseFloat(s);
      }

      return 1F;
   }

   /**
    * Converts a String in a "font-size:40px" format to a int value. If the String is null or the String 
    * cannot be parsed an int of Text.DEFAULT_FONT_SIZE will be returned.
    *
    * @param style the String to be converted to a int
    * @return the int value of the String style parameter
    */
   public static int parseFontSize(String style) {
      if (style == null) {
         return Text.DEFAULT_FONT_SIZE;
      }

      Pattern pattern = Pattern.compile("font-size:\\d+px;");
      Matcher matcher = pattern.matcher(style);

      if (matcher.find()) {
         String s = matcher.group(0);
         s = s.replaceAll("font-size:", "");
         s = s.replaceAll("px;", "");

         return Integer.parseInt(s);
      }

      return Text.DEFAULT_FONT_SIZE;
   }

   /**
    * Converts a String in a "M250 150 L150 350 L350 350 Z" format into List<double[]> of points. If no points
    * are found an empty List will be returned.
    *
    * @param points the String to be converted to a List<double[]> of points.
    * @return a List<double[]> of the String points parameter
    */
   public static List<double[]> getPathPoints(String points) {
      List<double[]> list = new ArrayList<double[]>();
      if (points == null) {
         return list;
      }
      String patternStr = "[ML]\\S+ \\S+ ";

      Pattern pattern = Pattern.compile(patternStr);
      Matcher matcher = pattern.matcher(points);
      while (matcher.find()) {
         String[] coords = matcher.group(0).substring(1).split(" ");
         double[] point = {Double.parseDouble(coords[0]), Double.parseDouble(coords[1])};
         list.add(point);
      }
      
      return list;
   }
   
   /**
    * Converts a String in a "150,375 150,325 250,325 250,375" format into List<double[]> of points. If no points
    * are found an empty List will be returned.
    *
    * @param points the String to be converted to a List<double[]> of points.
    * @return a List<double[]> of the String points parameter
    */
   public static List<double[]> getPolylinePoints(String points) {
      List<double[]> list = new ArrayList<double[]>();
      if (points == null) {
         return list;
      }
      
      StringTokenizer tokenizer = new StringTokenizer(points);
      while (tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken();
         
         String[] coords = token.split(",");
         double[] point = {Double.parseDouble(coords[0]), Double.parseDouble(coords[1])};
         list.add(point);
      }
      
      return list;
   }

   public static String toRGB(Shape shape) {
      String s = "rgb(#r,#g,#b)";

      s = s.replaceAll("#r", String.valueOf(shape.red()));
      s = s.replaceAll("#g", String.valueOf(shape.green()));
      s = s.replaceAll("#b", String.valueOf(shape.blue()));

      return s;
   }
}
