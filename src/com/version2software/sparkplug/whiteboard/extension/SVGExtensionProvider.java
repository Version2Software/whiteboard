/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.extension;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import com.version2software.sparkplug.whiteboard.SVGConstants;

public class SVGExtensionProvider implements PacketExtensionProvider {
   
   public PacketExtension parseExtension(XmlPullParser parser) throws Exception {

      StringBuilder sb = new StringBuilder();
      while (true) {
         int eventType = parser.next();
         //System.out.println("eventType = " + eventType + ":" + parser.getName() + ":" + parser.getText());
         if (eventType == XmlPullParser.START_TAG && !parser.getName().equals(SVGConstants.ELEMENT_NAME)) {
            sb.append(parser.getText());
         } else if (eventType == XmlPullParser.TEXT) {
            sb.append(parser.getText());
         } else if (eventType == XmlPullParser.END_TAG && parser.getName().equals("set-background")) {
            sb.append(parser.getText());
         } else if (eventType == XmlPullParser.END_TAG && parser.getName().equals("image")) {
            sb.append(parser.getText());
         } else if (eventType == XmlPullParser.END_TAG && parser.getName().equals("text")) {
            sb.append(parser.getText());
         } else if (eventType == XmlPullParser.END_TAG && parser.getName().equals("attribute")) {
            sb.append(parser.getText());
         } else if (eventType == XmlPullParser.END_TAG && parser.getName().equals("configure")) {
            sb.append(parser.getText());
         } else if (eventType == XmlPullParser.END_TAG && parser.getName().equals(SVGConstants.ELEMENT_NAME)) {
            break;
         }
      }

      return new SVGExtension(sb.toString());
   }
}
