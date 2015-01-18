/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.command;

/**
 * A Command that indicates which item should be removed from the whiteboard.
 */
public class Remove extends Command {
   private String target;
   
   /**
    * Creates a new Remove Command.
    * 
    * @param target the id of the item that should be removed.
    */
   public Remove(String target) {
      this.target = target;
   }
   
   /**
    * @return an xml element formatted as "<remove target="123"/>" where the target
    * element is the id of the item to be removed.
    */
   public String asXML() {
      String s = "<remove target=\"#t\"/>";

      return s.replaceAll("#t", target);
    }

   /**
    * @return target
    */
   public String getTarget() {
      return target;
   }
}
