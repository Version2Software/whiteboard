/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.view;

import com.version2software.sparkplug.whiteboard.images.ImageLoader;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * A help class that simulates a button that visually remains in a "selected" or "pressed" after
 * being clicked on.
 */
public class ToolLabel extends JLabel {
   private static Border selectedBorder = BorderFactory.createLoweredBevelBorder();
   private static Border unselectedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
   
   private static Color selectedColor = Color.LIGHT_GRAY;
   private static Color unselectedColor = Color.WHITE;

   /**
    * @param iconName the name of the icon to be added to the label
    * @param tooltip the tooltip text to be added to the label
    */
   public ToolLabel(String iconName, String tooltip) {
      super();
      
      setIcon(ImageLoader.getImageIcon(iconName));
      setToolTipText(tooltip);
      
      setOpaque(true);
      setBackground(Color.white);
      setHorizontalAlignment(JLabel.CENTER);
   }
   
   /**
    * Sets the label to appear to selected (depressed)
    * 
    * @param true to set the label as selected, otherwise false
    */
   public void setSelected(boolean selected) {
      if (selected) {
         setBorder(selectedBorder);
         setBackground(selectedColor);
      } else {
         setBorder(unselectedBorder);
         setBackground(unselectedColor);
      }
   }
}
