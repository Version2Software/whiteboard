/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.view.listeners;

import com.version2software.sparkplug.whiteboard.shape.Shape;
import com.version2software.sparkplug.whiteboard.view.PropertiesDialog;
import com.version2software.sparkplug.whiteboard.view.Whiteboard;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

public class PropertiesMenuActionListener extends AbstractAction {
   private Whiteboard whiteboard;
   
   public PropertiesMenuActionListener(Whiteboard whiteboard) {
      this.whiteboard = whiteboard;
   }
   
   public void actionPerformed(ActionEvent ae) {
      Shape selectedShape = whiteboard.getSelectedShape();
      if (selectedShape == null) {
         JOptionPane.showMessageDialog(whiteboard, "A shape must be selected before its properites can be changed.",
               "No shaped selecteed", JOptionPane.INFORMATION_MESSAGE);
         
      } else {
         new PropertiesDialog(whiteboard, selectedShape, PropertiesDialog.OPACITY_TAB).setVisible(true);
      }
   }
}
