/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.undo;

import com.version2software.sparkplug.whiteboard.shape.Shape;
import com.version2software.sparkplug.whiteboard.view.Whiteboard;

import java.awt.Color;

public class UndoColor extends UndoAction {

   private Color color;
   private Shape shape;

   public UndoColor(Shape shape, Color color, String random) {
      this.shape = shape;
      this.color = color;
      this.random = random;
   }

   public void execute(Whiteboard whiteboard) {
      shape.setColor(color);
   }
}
