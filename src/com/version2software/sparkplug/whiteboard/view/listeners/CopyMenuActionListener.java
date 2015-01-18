/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.view.listeners;

import com.version2software.sparkplug.whiteboard.shape.Shape;
import com.version2software.sparkplug.whiteboard.view.SVGTransferableElement;
import com.version2software.sparkplug.whiteboard.view.Whiteboard;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class CopyMenuActionListener extends AbstractAction {
   private Whiteboard whiteboard;
   
   public CopyMenuActionListener(Whiteboard whiteboard) {
      this.whiteboard = whiteboard;
   }
   
   public void actionPerformed(ActionEvent ae) {
      Shape selectedShape = whiteboard.getSelectedShape();
      if (selectedShape != null) {
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new SVGTransferableElement(selectedShape), null);
      }
   }
}
