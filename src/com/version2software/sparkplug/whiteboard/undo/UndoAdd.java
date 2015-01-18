/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.undo;

import com.version2software.sparkplug.whiteboard.view.Whiteboard;
import com.version2software.sparkplug.whiteboard.shape.Shape;

import java.util.List;

/*
 * Action: remove the shape at the end of the displayList
 */
public class UndoAdd extends UndoAction {
   public void execute(Whiteboard whiteboard) {
      List<Shape> displayList = whiteboard.getDisplayList();
      if (displayList.size() > 0) {
         displayList.remove(displayList.size() - 1);
      }
   }
}
