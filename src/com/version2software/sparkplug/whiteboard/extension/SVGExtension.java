/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.extension;

import com.version2software.sparkplug.whiteboard.SVGConstants;
import com.version2software.sparkplug.whiteboard.SVGUtil;
import com.version2software.sparkplug.whiteboard.SVGElement;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jivesoftware.smack.packet.PacketExtension;

import java.io.IOException;
import java.io.StringReader;

public class SVGExtension implements PacketExtension {
   private String shapeAsXML;

   public SVGExtension(String shapeAsXML) {
      this.shapeAsXML = shapeAsXML;
   }

   public String toXML() {
      return "<" + SVGConstants.ELEMENT_NAME + " xmlns=\"" + SVGConstants.NAMESPACE + "\">"+shapeAsXML+"</" + SVGConstants.ELEMENT_NAME + ">";
   }

   public String getElementName() {
      return SVGConstants.ELEMENT_NAME;
   }

   public String getNamespace() {
      return SVGConstants.NAMESPACE;
   }

   public SVGElement getSVGElement() throws JDOMException, IOException {
      Document doc = new SAXBuilder().build(new StringReader(shapeAsXML));
      return SVGUtil.parseSVGElement(doc.getRootElement());
   }
}
