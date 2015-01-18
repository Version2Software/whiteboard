/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.command;

import java.util.List;
import java.util.ArrayList;

public class Configure extends Command {

   private String target;
   private String random;
   private List<Attribute> attributes = new ArrayList<Attribute>();

   public Configure(String target, String random) {
      this.target = target;
      this.random = random;
   }
   private static final String CONFIGURE_TEMPLATE = "<configure target=\"#t\" random=\"#r\">";
   private static final String ATTRIBUTE_TEMPLATE = "<attribute name=\"#n\">#v</attribute>";

   public String asXML() {
      StringBuilder sb = new StringBuilder();

      sb.append(CONFIGURE_TEMPLATE.replaceAll("#t", target).replaceAll("#r", random));
      for (Attribute a : attributes) {
         sb.append(ATTRIBUTE_TEMPLATE.replaceAll("#n", a.getName()).replaceAll("#v", a.getValue()));
      }
      sb.append("</configure>");

      return sb.toString();
    }

   public String getTarget() {
      return target;
   }

   public String getRandom() {
      return random;
   }

   public List<Attribute> getAttributes() {
      return attributes;
   }

   public void addAttribute(Attribute attribute) {
      attributes.add(attribute);
   }
}
