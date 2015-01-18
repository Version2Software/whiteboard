package com.version2software.sparkplug.whiteboard.view;

/**
 * Copyright (C) 2007 Version 2 Software, LLC. All rights reserved.
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

public class HelpDialog {
   public static void showDialog(Component parentComponent) {
      Box box = Box.createVerticalBox();
      
      JEditorPane editorPane = new JEditorPane();
      editorPane.setEditable(false);
      editorPane.setContentType("text/html");
      editorPane.setPreferredSize(new Dimension(320, 150));
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
         "<p>If you need additional help or information regarding the V2 Whiteboard, please visit: " +
         "<a href='http://www.version2software.com/v2whiteboard.html'>http://www.version2software.com/v2whiteboard.html</a><br><br>" +
         "To join the discussion group or read group messages, visit : " +
         "  <a href='http://groups.google.com/group/v2whiteboard'>The V2 Whiteboard Google Group</a>" +
          "</html>";
      
      editorPane.setText(msg);
      
      JScrollPane scrollPane = new JScrollPane(editorPane);
      box.add(scrollPane);
      
      JOptionPane.showMessageDialog(parentComponent, box, "Help", JOptionPane.QUESTION_MESSAGE);
   }
}
