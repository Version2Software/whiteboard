/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.view.listeners;

import com.version2software.sparkplug.whiteboard.SVGElement;
import com.version2software.sparkplug.whiteboard.WhiteboardUtil;
import com.version2software.sparkplug.whiteboard.shape.Circle;
import com.version2software.sparkplug.whiteboard.shape.Ellipse;
import com.version2software.sparkplug.whiteboard.shape.Line;
import com.version2software.sparkplug.whiteboard.shape.Path;
import com.version2software.sparkplug.whiteboard.shape.Polygon;
import com.version2software.sparkplug.whiteboard.shape.Polyline;
import com.version2software.sparkplug.whiteboard.shape.Rectangle;
import com.version2software.sparkplug.whiteboard.shape.SVGImage;
import com.version2software.sparkplug.whiteboard.shape.Shape;
import com.version2software.sparkplug.whiteboard.shape.Text;
import com.version2software.sparkplug.whiteboard.view.SVGTransferableElement;
import com.version2software.sparkplug.whiteboard.view.TextDialog;
import com.version2software.sparkplug.whiteboard.view.Whiteboard;

import org.jivesoftware.spark.util.log.Log;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public class PasteMenuActionListener extends AbstractAction {
   private Whiteboard whiteboard;
   
   public PasteMenuActionListener(Whiteboard whiteboard) {
      this.whiteboard = whiteboard;
   }
   
   public void actionPerformed(ActionEvent ae) {
      try {
         DataFlavor urlFlavor = new DataFlavor("application/x-java-url; class=java.net.URL");
         DataFlavor uriListFlavor = new DataFlavor("text/uri-list; class=java.lang.String");
         DataFlavor macPictStreamFlavor = new DataFlavor("image/x-pict; class=java.io.InputStream");
         
         Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
         if (transferable == null) {
            return;
         }
         
         if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            Image image = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
            pasteImage(transferable, image);
         }
         else if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            List<File> list = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
            for (File f : list) {
               ImageIcon icon = new ImageIcon(f.getAbsolutePath());
               pasteImage(transferable, icon.getImage());
            }
         }
         else if (transferable.isDataFlavorSupported(uriListFlavor)) {
            String uris = (String) transferable.getTransferData(uriListFlavor);

            // url-lists are defined by rfc 2483 as crlf-delimited 
            StringTokenizer izer = new StringTokenizer(uris, "\r\n");
            while (izer.hasMoreTokens()) {
               String uri = izer.nextToken();
               ImageIcon icon = new ImageIcon(uri);
               pasteImage(transferable, icon.getImage());
            }
         } 
         else if (transferable.isDataFlavorSupported(urlFlavor)) {
            URL url = (URL) transferable.getTransferData(urlFlavor);
            ImageIcon icon = new ImageIcon(url);
            pasteImage(transferable, icon.getImage());
         }
         else if (transferable.isDataFlavorSupported(macPictStreamFlavor)) {
            InputStream in = (InputStream) transferable.getTransferData(macPictStreamFlavor);
            // for the benefit of the non-mac crowd, this is 
            // done with reflection. directly, it would be: 
            // Image img = QTJPictHelper.pictStreamToJavaImage(in); 

            Class qtjphClass = Class.forName("com.version2software.sparkplug.whiteboard.view.listeners.QTJPictHelper");

            Class[] methodParamTypes = { java.io.InputStream.class };
            Method method = qtjphClass.getDeclaredMethod("pictStreamToJavaImage", methodParamTypes);
            InputStream[] methodParams = { in };
            Image img = (Image) method.invoke(null, methodParams);
            pasteImage(transferable, img);
         }
         else if (transferable.isDataFlavorSupported(SVGTransferableElement.svgElementFlavor)) {
            pasteSVGElement(transferable);
         }
         else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            pasteText(transferable);
         }
      } catch (Exception e) {
         e.printStackTrace();
         Log.error(e.getMessage());
      }
   }

   private void pasteSVGElement(Transferable transferable) throws UnsupportedFlavorException, IOException {
      SVGElement element = (SVGElement) transferable.getTransferData(SVGTransferableElement.svgElementFlavor);

      Shape shape = null;
      if (element instanceof Rectangle) {
         Rectangle rect = (Rectangle) element;
         shape = new Rectangle(whiteboard.id(), rect.getColor(), rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), rect.isFill());
         
      } else if (element instanceof Line) {
         Line line = (Line) element;
         shape = new Line(whiteboard.id(), line.getColor(), line.getX(), line.getY(), line.getxEnd(), line.getyEnd());
         
      } else if (element instanceof Text) {
         Text text = (Text) element;
         shape = new Text(whiteboard.id(), text.getColor(), text.getX(), text.getY(), text.getSize(), text.getText());
         
      } else if (element instanceof Circle) {
         Circle cir = (Circle) element;
         shape = new Circle(whiteboard.id(), cir.getColor(), cir.getCx(), cir.getCy(), cir.getR(), cir.isFill());
         
      } else if (element instanceof Ellipse) {
         Ellipse elli = (Ellipse) element;
         shape = new Ellipse(whiteboard.id(), elli.getColor(), elli.getCx(), elli.getCy(), elli.getRx(), elli.getRy(), elli.isFill());
         
      } else if (element instanceof SVGImage) {
         SVGImage img = (SVGImage) element;
         shape = new SVGImage(whiteboard.id(), img.getX(), img.getY(), img.getWidth(), img.getHeight(), img.getImage());
         
      } else if (element instanceof Path) {
         Path path = (Path) element;
         shape = new Path(whiteboard.id(), path.getColor(), path.getPoints());
         
      } else if (element instanceof Polyline) {
         Polyline poly = (Polyline) element;
         shape = new Polyline(whiteboard.id(), poly.getColor(), poly.getPoints(), poly.isFill());
         
      } else if (element instanceof Polygon) {
         Polygon poly = (Polygon) element;
         shape = new Polygon(whiteboard.id(), poly.getColor(), poly.getPoints(), poly.isFill());
      }
         
      if (shape != null) {
         shape.setSelected(false);
         whiteboard.appendAndSend(shape);
      }
   }
   
   private void pasteText(Transferable transferable) throws UnsupportedFlavorException, IOException {
      //TODO figure out a better way to determine where to put the text?
      String pastedText = (String) transferable.getTransferData(DataFlavor.stringFlavor);
      
      int x = 25;
      int y = 25;
      
      whiteboard.setDoneDrawing(true);

      Graphics g = whiteboard.getWhiteboardPanel().getGraphics();
      g.setColor(Color.BLACK);
      g.drawLine(x, y-10, x, y+10);

      TextDialog d = new TextDialog(whiteboard, pastedText);
      d.setVisible(true);
      String text = d.getText();
      int size = d.getTextSize();
      if (text != null) {
         whiteboard.appendAndSend(new Text(whiteboard.id(), whiteboard.getCurrentColor(), x, y, size, text, whiteboard.getWorld2ScreenAffineTransform()));
      }
   }
   
   private void pasteImage(Transferable transferable, Image image) throws UnsupportedFlavorException, IOException {
      //TODO figure out a better way to determine where to put the text?
      SVGImage svgImage = new SVGImage(whiteboard.id(), 25, 25, image.getWidth(null), image.getHeight(null), WhiteboardUtil.toBufferedImage(image), whiteboard.getWorld2ScreenAffineTransform());
      whiteboard.appendAndSend(svgImage);
   }
}
