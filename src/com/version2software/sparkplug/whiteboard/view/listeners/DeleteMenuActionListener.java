/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.view.listeners;

import com.version2software.sparkplug.whiteboard.command.Remove;
import com.version2software.sparkplug.whiteboard.shape.Shape;
import com.version2software.sparkplug.whiteboard.view.Whiteboard;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class DeleteMenuActionListener extends AbstractAction {
   private Whiteboard whiteboard;

   public DeleteMenuActionListener(Whiteboard whiteboard) {
      this.whiteboard = whiteboard;
   }
   
   public void actionPerformed(ActionEvent ae) {
      for (Shape shape : whiteboard.getDisplayList()) {
         if (shape.isSelected()) {
            whiteboard.processAndSend(new Remove(shape.getId()));
         }
      }
   }
}
