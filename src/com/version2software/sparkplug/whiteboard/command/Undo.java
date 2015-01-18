/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.command;

/**
 * Perform the undo action at the end of the Whiteboard.undoList.
 */
public class Undo extends Command {

   /**
    * @return an xml element of "<undo/>"
    */
   public String asXML() {
      return "<undo/>";
   }
}