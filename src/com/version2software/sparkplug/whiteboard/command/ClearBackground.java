/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.command;

/**
 * Clears the background image from a whiteboard.
 */
public class ClearBackground extends Command {
   
   /**
    * @return an xml element of "<clear-background/>"
    */
   public String asXML() {
      return "<clear-background/>";
   }
}
