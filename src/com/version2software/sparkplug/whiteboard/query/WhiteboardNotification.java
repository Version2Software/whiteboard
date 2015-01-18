/**
 * Copyright (C) 2007 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.query;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import com.version2software.sparkplug.whiteboard.SVGConstants;

public class WhiteboardNotification extends IQ {
   public static final String ELEMENT_NAME = "notification";
   
   public enum Event { EXIT, NEW };
   private Event event;
   
   public WhiteboardNotification() {
      super();
   }
   
   public void setEvent(Event event) {
      this.event = event;
   }
   
   public Event getEvent() {
      return event;
   }

   public String getChildElementXML() {
      StringBuffer buf = new StringBuffer();
      buf.append("<" + ELEMENT_NAME + " xmlns=\"" + SVGConstants.NAMESPACE + "\">");
      buf.append("<event>" + getEvent() + "</event>");
      buf.append(getExtensionsXML());
      buf.append("</" + ELEMENT_NAME + ">");
      return buf.toString();
   }

   public static class Provider implements IQProvider {
      public Provider() {
         super();
      }

      public IQ parseIQ(XmlPullParser parser) throws Exception {
         WhiteboardNotification invite = new WhiteboardNotification();

         boolean done = false;
         while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
               if (parser.getName().equals("event")) {
                  invite.setEvent(WhiteboardNotification.Event.valueOf(parser.nextText()));
               }
            }
            else if (eventType == XmlPullParser.END_TAG) {
               if (parser.getName().equals(ELEMENT_NAME)) {
                  done = true;
               }
            }
         }

         return invite;
      }
   }
}
