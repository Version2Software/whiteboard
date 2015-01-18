/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * A basic dialog that allows users to enter text to be placed on the whiteboard.
 */
public class TextDialog extends JDialog {
   private JComboBox sizeCombo;
   private JTextArea textArea = new JTextArea(5, 25);
   private String text;
   private Integer size;
   
   private JButton cancelButton = new JButton("Cancel");
   private JButton saveButton = new JButton("Save");
   
   /**
    * @param parent the non-null JFrame from which the dialog is displayed
    */
   public TextDialog(JFrame parent) {
      super(parent, "Enter text", true);
      
      setLocationRelativeTo(parent);
      
      initUI();
      initListeners();
   }
   
   private void initUI() {
      setSize(200, 200);
      
      DefaultComboBoxModel sizeComboModel = new DefaultComboBoxModel();
      for (int i = 1; i <= 72; i++) {
         sizeComboModel.addElement(i);
      }
      sizeCombo = new JComboBox(sizeComboModel);
      size = getFont().getSize();
      sizeCombo.setSelectedIndex(size);
      
      JPanel fontPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      fontPanel.add(new JLabel("Size:"));
      fontPanel.add(sizeCombo);
      
      JPanel textPanel = new JPanel(new BorderLayout());
      textPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      textPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
      
      JPanel buttonPanel = new JPanel(new FlowLayout());
      buttonPanel.add(cancelButton);
      buttonPanel.add(saveButton);
      
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(fontPanel, BorderLayout.NORTH);
      getContentPane().add(textPanel, BorderLayout.CENTER);
      getContentPane().add(buttonPanel, BorderLayout.SOUTH);

      pack();

      textArea.requestFocus();
   }
   
   private void initListeners() {
      setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

      sizeCombo.addItemListener(new SizeItemListener());
      
      cancelButton.addActionListener(new CancelActionListener());
      saveButton.addActionListener(new SaveActionListener());
   }
   
   /**
    * @param parent the non-null JFrame from which the dialog is displayed
    * @param text the text to be displayed on the dialogs textarea
    */
   public TextDialog(JFrame parent, String text) {
      this(parent);
      
      textArea.setText(text);
   }
   
   /**
    * @return the text entered in the dialog
    */
   public String getText() {
      return text;
   }
   
   /**
    * @return the selected font size
    */
   public int getTextSize() {
      return size;
   }
   
   private class SizeItemListener implements ItemListener {
      public void itemStateChanged(ItemEvent e) {
         size = (Integer) sizeCombo.getSelectedItem();
         
         Font currentFont = getFont();
         textArea.setFont(new Font(currentFont.getName(), currentFont.getStyle(), size));
      }
   }

   private class CancelActionListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         dispose();
      }
   }

   private class SaveActionListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         text = textArea.getText();
         dispose();
      }
   }
}
