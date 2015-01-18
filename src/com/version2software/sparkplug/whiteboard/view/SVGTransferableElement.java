/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.view;

import com.version2software.sparkplug.whiteboard.SVGElement;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class SVGTransferableElement implements Transferable {
   private SVGElement element;
   
   public static DataFlavor svgElementFlavor = new DataFlavor(com.version2software.sparkplug.whiteboard.SVGElement.class, "V2 Whiteboard Image");

   private static DataFlavor[] supportedFlavors = { svgElementFlavor };

   public SVGTransferableElement(SVGElement element) {
      this.element = element;
   }

   public DataFlavor[] getTransferDataFlavors() {
      return supportedFlavors;
   }

   public boolean isDataFlavorSupported(DataFlavor flavor) {
      if (flavor.equals(svgElementFlavor)) {
         return true;
      }
      return false;
   }

   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
      if (flavor.equals(svgElementFlavor)) {
         return element;
      } else {
         throw new UnsupportedFlavorException(flavor);
      }
   }
}
