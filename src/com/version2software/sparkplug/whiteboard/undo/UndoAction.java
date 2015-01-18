/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.undo;

import com.version2software.sparkplug.whiteboard.view.Whiteboard;

public abstract class UndoAction {

   protected String random;

   public abstract void execute(Whiteboard whiteboard);

   public String getRandom() {
      return random;
   }
}
