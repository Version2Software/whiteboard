/**
 * Copyright (C) 2007 Version 2 Software, LLC. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */
package com.version2software.sparkplug.whiteboard.view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.version2software.sparkplug.whiteboard.command.Attribute;
import com.version2software.sparkplug.whiteboard.command.Configure;
import com.version2software.sparkplug.whiteboard.shape.Shape;
import com.version2software.sparkplug.whiteboard.undo.UndoColor;
import com.version2software.sparkplug.whiteboard.undo.UndoOpacity;
import com.version2software.sparkplug.whiteboard.SVGUtil;

public class PropertiesDialog extends JDialog {
   public static final String OPACITY_TAB = "Opactiy";
   public static final String COLOR_TAB = "Color";
   
   private Whiteboard whiteboard;
   private Shape selectedShape;
   private String selectedTab;
   
   private float initialOpacity;
   private Color initialColor;
   
   private JButton cancelButton = new JButton("Cancel");
   private JButton closeButton = new JButton("Close");
   
   public PropertiesDialog(Whiteboard whiteboard, Shape selectedShape, String selectedTab) {
      super(whiteboard, "Properties", true);
      
      this.whiteboard = whiteboard;
      this.selectedShape = selectedShape;
      this.selectedTab = selectedTab;
      
      initialOpacity = selectedShape.getOpacity();
      initialColor = selectedShape.getColor();
      
      initUI();
      initListeners();
   }
   
   private void initUI() {
      setSize(new Dimension(450, 350));
      setLocationRelativeTo(whiteboard);
      
      getContentPane().setLayout(new BorderLayout());
      
      JPanel center = new JPanel(new BorderLayout());
      
      JTabbedPane tabbedPane = new JTabbedPane();
      tabbedPane.addTab(OPACITY_TAB, getOpacityPanel());
      tabbedPane.addTab(COLOR_TAB, getColorPanel());
      
      for (int i = 0; i < tabbedPane.getTabCount(); i++) {
         if (tabbedPane.getTitleAt(i).equals(selectedTab)) {
            tabbedPane.setSelectedIndex(i);
         }
      }
      
      center.add(tabbedPane);
      
      JPanel south = new JPanel();
      south.setLayout(new FlowLayout(FlowLayout.RIGHT));
      south.add(cancelButton);
      south.add(closeButton);
      
      getContentPane().add(center, BorderLayout.CENTER);
      getContentPane().add(south, BorderLayout.SOUTH);
   }
   
   private void initListeners() {
      setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      
      cancelButton.addActionListener(new CancelActionListener());
      closeButton.addActionListener(new CloseActionListener());
   }
   
   private JPanel getOpacityPanel() {
      JPanel panel = new JPanel();
      panel.setLayout(new BorderLayout());
      
      int initOpacity = (int) (selectedShape.getOpacity() * 100);
      
      JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, initOpacity);
      slider.setMajorTickSpacing(10);
      slider.setPaintTicks(true);
      slider.setPaintLabels(true);
      slider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
      
      slider.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            if (source.getValueIsAdjusting()) {
               int opacity = (int) source.getValue();
               float fOpacity = opacity / 100f;
               selectedShape.setOpacity(fOpacity);
               
//               sendConfiguration(new Attribute("opacity", String.valueOf(fOpacity)));
            }
         }
      });

      panel.add(new JLabel("Select an opacity:"), BorderLayout.NORTH);
      panel.add(slider, BorderLayout.CENTER);
      
      return panel;
   }
   
   private JPanel getColorPanel() {
      JPanel panel = new JPanel();
      panel.setSize(new Dimension(300, 150));
      panel.setLayout(new BorderLayout());
      
      JColorChooser chooser = new JColorChooser();
      chooser.setColor(initialColor);
      
      ColorSelectionModel model = chooser.getSelectionModel();
      
      model.addChangeListener(new ChangeListener() {
          public void stateChanged(ChangeEvent evt) {
              ColorSelectionModel model = (ColorSelectionModel)evt.getSource();
              
              Color newColor = model.getSelectedColor();
              selectedShape.setColor(newColor);
              
//              sendConfiguration(new Attribute("color", SVGUtil.toRGB(selectedShape)));
          }
      }) ;
      
      panel.add(chooser, BorderLayout.CENTER);
      
      return panel;
   }

   private void sendConfiguration(Attribute a, String random) {
      Configure configure = new Configure(selectedShape.getId(), random);
      configure.addAttribute(a);
      whiteboard.processAndSend(configure);
   }
   
   private class CancelActionListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         selectedShape.setOpacity(initialOpacity);
         //sendConfiguration(new Attribute("opacity", String.valueOf(initialOpacity)));
         
         selectedShape.setColor(initialColor);
         //sendConfiguration(new Attribute("color", SVGUtil.toRGB(selectedShape)));
         
         dispose();
      }
   }
   
   private class CloseActionListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {

         String rand = String.valueOf(Math.random());
         String rand2 = String.valueOf(Math.random());

         whiteboard.appendUndo(new UndoColor(selectedShape, initialColor, rand));
         whiteboard.appendUndo(new UndoOpacity(selectedShape, initialOpacity, rand2));

         sendConfiguration(new Attribute("color", SVGUtil.toRGB(selectedShape)), rand);
         sendConfiguration(new Attribute("opacity", String.valueOf(selectedShape.getOpacity())), rand2);

         dispose();
      }
   }
}
