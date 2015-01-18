/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.undo;

import com.version2software.sparkplug.whiteboard.shape.Shape;
import com.version2software.sparkplug.whiteboard.view.Whiteboard;

import java.util.concurrent.CopyOnWriteArrayList;

public class UndoClearAll extends UndoAction {

   private CopyOnWriteArrayList<Shape> displayList;

   public UndoClearAll(CopyOnWriteArrayList<Shape> displayList) {
      this.displayList = displayList;
   }

   public void execute(Whiteboard whiteboard) {
      whiteboard.getDisplayList().addAll(displayList);
   }
}
