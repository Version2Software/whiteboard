/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.view;

import com.version2software.sparkplug.whiteboard.images.ImageLoader;
import com.version2software.sparkplug.whiteboard.shape.Shape;
import com.version2software.sparkplug.whiteboard.view.listeners.CopyMenuActionListener;
import com.version2software.sparkplug.whiteboard.view.listeners.DeleteMenuActionListener;
import com.version2software.sparkplug.whiteboard.view.listeners.DisplayOrderActionListener;
import com.version2software.sparkplug.whiteboard.view.listeners.PasteMenuActionListener;
import com.version2software.sparkplug.whiteboard.view.listeners.PropertiesMenuActionListener;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.RepaintManager;

public class WhiteboardPanel extends JPanel implements Printable {
   private Whiteboard whiteboard;
   
   public WhiteboardPanel(Whiteboard whiteboard) {
      super();
      
      this.whiteboard = whiteboard;
      
      setBackground(Color.white);
      initListeners();
   }
   
   private void initListeners() {
      final JPopupMenu popupMenu = new JPopupMenu();

      final JMenuItem copyPopupMenuItem = new JMenuItem("Copy", getImageIcon("stock_copy-16.png"));
      final JMenuItem pastePopupMenuItem = new JMenuItem("Paste", getImageIcon("stock_paste-16.png"));
      final JMenuItem propetiesPopupMenuItem = new JMenuItem("Properties");
      final JMenuItem deletePopupMenuItem = new JMenuItem("Delete", getImageIcon("stock_delete-16.png"));
      final JMenuItem bringtoFrontPopupMenuItem = new JMenuItem("Forward", getImageIcon("stock_bring-forward-16.png"));
      final JMenuItem sendToBackPopupMenuItem = new JMenuItem("Back", getImageIcon("stock_bring-backward-16.png"));

      popupMenu.add(copyPopupMenuItem);
      popupMenu.add(pastePopupMenuItem);
      popupMenu.addSeparator();
      popupMenu.add(propetiesPopupMenuItem);
      popupMenu.addSeparator();
      popupMenu.add(deletePopupMenuItem);
      popupMenu.addSeparator();
      popupMenu.add(bringtoFrontPopupMenuItem);
      popupMenu.add(sendToBackPopupMenuItem);

      copyPopupMenuItem.addActionListener(new CopyMenuActionListener(whiteboard));
      pastePopupMenuItem.addActionListener(new PasteMenuActionListener(whiteboard));
      propetiesPopupMenuItem.addActionListener(new PropertiesMenuActionListener(whiteboard));
      deletePopupMenuItem.addActionListener(new DeleteMenuActionListener(whiteboard));
      bringtoFrontPopupMenuItem.addActionListener(new DisplayOrderActionListener(whiteboard, "front"));
      sendToBackPopupMenuItem.addActionListener(new DisplayOrderActionListener(whiteboard, "back"));

      addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
               checkForTriggerEvent(e);
            }
         }

         public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
               checkForTriggerEvent(e);
            }
         }

         private void checkForTriggerEvent(MouseEvent e) {
            copyPopupMenuItem.setEnabled(false);
            pastePopupMenuItem.setEnabled(false);
            propetiesPopupMenuItem.setEnabled(false);
            deletePopupMenuItem.setEnabled(false);
            bringtoFrontPopupMenuItem.setEnabled(false);
            sendToBackPopupMenuItem.setEnabled(false);
            
            if (whiteboard.getSelectedShape() != null) {
               copyPopupMenuItem.setEnabled(true);
               propetiesPopupMenuItem.setEnabled(true);
               deletePopupMenuItem.setEnabled(true);
               bringtoFrontPopupMenuItem.setEnabled(true);
               sendToBackPopupMenuItem.setEnabled(true);
            }
            
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
         }
      });

   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      
      BufferedImage backgroundImage = whiteboard.getBackgroundImage();
      if (backgroundImage != null) {
         Graphics2D g2 = (Graphics2D) g;
         g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
      }

      if (whiteboard.isGridEnabled()) {
         int defaultGrid = whiteboard.getDefaultGrid();
         for (int x = 0; x < this.getWidth(); x += defaultGrid) {
            for (int y = 0; y < this.getHeight(); y += defaultGrid) {
               g.setColor(Color.GRAY);
               g.fillOval(x, y, 2, 2);
            }
         }
      }

      for (Shape shape : whiteboard.getDisplayList()) {
         shape.paint(g, whiteboard.getWorld2ScreenAffineTransform());
      }
   }

   public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
      if (pageIndex > 0) {
         return NO_SUCH_PAGE;

      } else {
         Graphics2D g2d = (Graphics2D) g;
         g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

         RepaintManager currentManager = RepaintManager.currentManager(this);
         currentManager.setDoubleBufferingEnabled(false);
         paint(g2d);
         currentManager.setDoubleBufferingEnabled(true);

         return PAGE_EXISTS;
      }
   }
   
   private ImageIcon getImageIcon(String icon) {
      return ImageLoader.getImageIcon(icon);
   }
}
