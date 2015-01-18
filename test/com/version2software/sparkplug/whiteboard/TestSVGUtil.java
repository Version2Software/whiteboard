/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 */

package com.version2software.sparkplug.whiteboard;

import java.util.List;

import junit.framework.TestCase;

public class TestSVGUtil extends TestCase {

    public void testParseFontSize() throws Exception {
       assertEquals(40, SVGUtil.parseFontSize("style=font-size:40px;opacity:0.5;"));
    }

    public void testParseOpacity() throws Exception {
       assertEquals(.5F, SVGUtil.parseOpacity("style=font-size:40px;opacity:0.5;"));
    }
    
    public void testGetPolylinePoints() throws Exception {
       List<double[]> points = SVGUtil.getPolylinePoints("150,375 150,325 250,325 250,375");
       
       double[] actual = points.get(0);
       assertEquals(150.0, actual[0]);
       assertEquals(375.0, actual[1]);
       
       actual = points.get(1);
       assertEquals(150.0, actual[0]);
       assertEquals(325.0, actual[1]);
       
       actual = points.get(2);
       assertEquals(250.0, actual[0]);
       assertEquals(325.0, actual[1]);
       
       actual = points.get(3);
       assertEquals(250.0, actual[0]);
       assertEquals(375.0, actual[1]);
    }
    
    public void testGetPathPoints() throws Exception {
       List<double[]> points = SVGUtil.getPathPoints("M250.1 150 L150.0 350 L350.8 350.5 Z");
       
       double[] actual = points.get(0);
       assertEquals(250.1, actual[0]);
       assertEquals(150.0, actual[1]);
       
       actual = points.get(1);
       assertEquals(150.0, actual[0]);
       assertEquals(350.0, actual[1]);
       
       actual = points.get(2);
       assertEquals(350.8, actual[0]);
       assertEquals(350.5, actual[1]);
    }
}
