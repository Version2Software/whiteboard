/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.undo;

import com.version2software.sparkplug.whiteboard.view.Whiteboard;

import java.awt.image.BufferedImage;

public class UndoClearBackground extends UndoAction {

   private BufferedImage backgroundImage;

   public UndoClearBackground(BufferedImage backgroundImage) {
      this.backgroundImage = backgroundImage;
   }

   public void execute(Whiteboard whiteboard) {
      whiteboard.setBackgroundImage(backgroundImage);
   }
}
