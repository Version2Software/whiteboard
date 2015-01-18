/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.undo;

import com.version2software.sparkplug.whiteboard.shape.Shape;
import com.version2software.sparkplug.whiteboard.view.Whiteboard;

public class UndoOpacity extends UndoAction {

   private float opacity;
   private Shape shape;

   public UndoOpacity(Shape shape, float opacity, String random) {
      this.shape = shape;
      this.opacity = opacity;
      this.random = random;
   }

   public void execute(Whiteboard whiteboard) {
      shape.setOpacity(opacity);
   }

}
