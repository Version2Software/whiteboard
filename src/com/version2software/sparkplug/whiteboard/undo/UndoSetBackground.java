/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.undo;

import com.version2software.sparkplug.whiteboard.view.Whiteboard;

public class UndoSetBackground extends UndoAction {

   public void execute(Whiteboard whiteboard) {
      whiteboard.setBackgroundImage(null);
   }
}
