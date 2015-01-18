package com.version2software.sparkplug.whiteboard.view;

/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.log.Log;

import com.version2software.sparkplug.whiteboard.WhiteboardPlugin;

public class AboutDialog {
   public static void showDialog(Component parentComponent) {
      Box box = Box.createVerticalBox();
      
      JEditorPane editorPane = new JEditorPane();
      editorPane.setEditable(false);
      editorPane.setContentType("text/html");
      editorPane.setPreferredSize(new Dimension(400, 250));
      editorPane.addHyperlinkListener(new HyperlinkListener() {
         public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
               JEditorPane pane = (JEditorPane) e.getSource();
               if (e instanceof HTMLFrameHyperlinkEvent) {
                  HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                  HTMLDocument doc = (HTMLDocument) pane.getDocument();
                  doc.processHTMLFrameHyperlinkEvent(evt);
               } else {
                  try {
                     BrowserLauncher.openURL(e.getURL().toString());
                  } catch (Throwable t) {
                     Log.error(t);
                  }
               }
            }
         }
      });
      
      String msg = "<html>Whiteboard Sparkplug<br>" +
         "Version: " + WhiteboardPlugin.getVersion() +
         "<p>This Sparkplug is a work in progess for adding peer-to-peer whiteboarding to Spark.<br>" +
         "This sparkplug is based loosely on the following:<br>" +
         "* XEP-xxxx: An SVG Based Whiteboard Format (http://www.xmpp.org/extensions/inbox/whiteboard.html)<br>" +
         "* Scalable Vector Graphics (SVG) Tiny 1.2 Specification (http://www.w3.org/TR/SVGMobile12/index.html)</p>" +
         "<p>Icons are from the OpenOffice project (http://www.novell.com/coolsolutions/feature/1637.html)</p><br>" +
         "(c) Copyright Version 2 Software, LLC 2006-2009. All rights reserved.<br>" +
         "Visit <a href=\"http://www.version2software.com/\">http://www.version2software.com/</a><br></html>";
      
      editorPane.setText(msg);
      
      JScrollPane scrollPane = new JScrollPane(editorPane);
      box.add(scrollPane);
      
      JOptionPane.showMessageDialog(parentComponent, box, "About", JOptionPane.INFORMATION_MESSAGE);
   }
}
