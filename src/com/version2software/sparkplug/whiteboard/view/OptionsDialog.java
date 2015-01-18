/**
 * Copyright (C) 2007 Version 2 Software, LLC. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */
package com.version2software.sparkplug.whiteboard.view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JCheckBox;

public class OptionsDialog extends JDialog {

   private JCheckBox checkboxTrackMouse = new JCheckBox("Track Mouse");
   private JCheckBox checkboxGrid = new JCheckBox("Grid");
   /**
    * @param dialog the non-null Dialog from which the dialog is displayed
    */
   Whiteboard whiteboard = null;

   public OptionsDialog(Whiteboard parent, boolean gridEnabled, boolean mouseTrackingEnabled) {
      super(parent, "Options", true);
      whiteboard = parent;
      setPreferredSize(new Dimension(300, 100));
      setLocationRelativeTo(parent);
      setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

      JButton cancelButton = new JButton("Cancel");
      JButton saveButton = new JButton("Save");

      cancelButton.addActionListener(new CloseActionListener());
      saveButton.addActionListener(new SaveActionListener());

      getContentPane().setLayout(new BorderLayout());

      JPanel center = new JPanel();

      center.setLayout(new GridLayout(2,1));
      checkboxTrackMouse.setSelected(mouseTrackingEnabled);
      checkboxGrid.setSelected(gridEnabled);
      center.add(checkboxGrid);
      center.add(checkboxTrackMouse);

      getContentPane().add(center, BorderLayout.CENTER);

      JPanel south = new JPanel();
      south.setLayout(new FlowLayout());
      south.add(cancelButton);
      south.add(saveButton);
      getContentPane().add(south, BorderLayout.SOUTH);

      pack();
   }

   private class CloseActionListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         dispose();
      }
   }

   private class SaveActionListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         whiteboard.setTrackMouse(checkboxTrackMouse.isSelected());
         whiteboard.setGridEnabled(checkboxGrid.isSelected());
         dispose();
      }
   }
}
