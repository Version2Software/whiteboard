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

public class UndoOrder extends UndoAction {

   private Shape shape;
   private String position;

   public UndoOrder(Shape shape, String position) {
      this.shape = shape;
      this.position = position;
   }

   public void execute(Whiteboard whiteboard) {
      List<Shape> displayList = whiteboard.getDisplayList();
      int pos = displayList.indexOf(shape);

      if (position.equals("back")) {
         displayList.remove(shape);
         displayList.add(0, shape);
      } else if (position.equals("back-one")) {
         if (pos > 0) {
            displayList.remove(shape);
            displayList.add(pos - 1, shape);
         }
      } else if (position.equals("front")) {
         displayList.remove(shape);
         displayList.add(shape);
      } else if (position.equals("front-one")) {
         if (pos < displayList.size() - 1) {
            displayList.remove(shape);
            displayList.add(pos + 1, shape);
         }
      }
   }
}
