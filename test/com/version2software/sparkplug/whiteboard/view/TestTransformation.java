package com.version2software.sparkplug.whiteboard.view;

import junit.framework.TestCase;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/*
The purpose of these tests is to illustrated the behavoir of the AffineTransform.
 */
public class TestTransformation extends TestCase {

   public void testAffine() throws Exception {
      AffineTransform at = new AffineTransform();

      at.scale(2, 2);

      Point2D p0 = new Point2D.Double(1, 1);

      Point2D p1 = at.transform(p0, null);

      assertEquals(2, p1.getX(), .001);
      assertEquals(2, p1.getY(), .001);

      Point2D p2 = at.inverseTransform(p1, null);

      assertEquals(1, p2.getX(), .001);
      assertEquals(1, p2.getY(), .001);
   }

   public void testNoTransform() throws Exception {

      AffineTransform at = new AffineTransform();

      assertEquals(new Point2D.Double(0, 0), at.transform(new Point2D.Double(0, 0), null));
      assertEquals(new Point2D.Double(540, 400), at.transform(new Point2D.Double(540, 400), null));
   }

   public void testTranslate() throws Exception {

      Point2D s0 = new Point2D.Double(0, 0);
      Point2D sEnd = new Point2D.Double(540, 400);

      AffineTransform s2w = new AffineTransform();
      s2w.translate(100, 0);

      Point2D v0 = s2w.transform(s0, null);
      Point2D vEnd = s2w.transform(sEnd, null);

      assertEquals(new Point2D.Double(100, 0), v0);
      assertEquals(new Point2D.Double(640, 400), vEnd);

      s2w.translate(200, 0);

      v0 = s2w.transform(s0, null);
      vEnd = s2w.transform(sEnd, null);

      // transforms are additive
      assertEquals(new Point2D.Double(300, 0), v0);
      assertEquals(new Point2D.Double(840, 400), vEnd);      
   }

   public void testScale() throws Exception {

      Point2D s0 = new Point2D.Double(0, 0);
      Point2D sEnd = new Point2D.Double(540, 400);

      AffineTransform s2w = new AffineTransform();
      s2w.scale(10, 10);

      Point2D v0 = s2w.transform(s0, null);
      Point2D vEnd = s2w.transform(sEnd, null);
      
      assertEquals(new Point2D.Double(0, 0), v0);
      assertEquals(new Point2D.Double(5400, 4000), vEnd);

      // change the screen size

      sEnd = new Point2D.Double(540, 500);
      vEnd = s2w.transform(sEnd, null);

      // the viewport has increased
      assertEquals(new Point2D.Double(5400, 5000), vEnd);
   }


   public void testTranslateAndScale() throws Exception {

      Point2D s0 = new Point2D.Double(0, 0);
      Point2D sEnd = new Point2D.Double(540, 400);

      AffineTransform s2w = new AffineTransform();
      s2w.translate(100, 100);
      s2w.scale(10, 10);

      assertEquals(new Point2D.Double(100, 100), s2w.transform(s0, null));
      assertEquals(new Point2D.Double(5500, 4100), s2w.transform(sEnd, null));
   }
}
