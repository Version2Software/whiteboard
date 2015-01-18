/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.command;

/**
 * A simple class that represents a name/value pairing.
 */
public class Attribute {
   private String name;
   private String value;
   
   /**
    * Creates a new attribute with a name/value pairing.
    * 
    * @param name the name
    * @param value the value
    */
   public Attribute(String name, String value) {
      this.name = name;
      this.value = value;
   }

   /**
    * @return name
    */
   public String getName() {
      return name;
   }

   /**
    * @return value
    */
   public String getValue() {
      return value;
   }
}
