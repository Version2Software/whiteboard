/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.images;

import javax.swing.ImageIcon;

public class ImageLoader {
   public static ImageIcon getImageIcon(String icon) {
      return new ImageIcon(ImageLoader.class.getResource(icon));
   }
}
