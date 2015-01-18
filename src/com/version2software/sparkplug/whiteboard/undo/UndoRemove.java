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
 * Action: add the removed shape to the end of the displayList
 */
public class UndoRemove extends UndoAction {

   Shape shape;

   public UndoRemove(Shape shape) {
      this.shape = shape;
   }

   public void execute(Whiteboard whiteboard) {
      List<Shape> displayList = whiteboard.getDisplayList();
      displayList.add(shape);
   }
}