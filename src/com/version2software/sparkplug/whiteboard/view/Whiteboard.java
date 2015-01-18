/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.view;

import com.version2software.sparkplug.whiteboard.SVGElement;
import com.version2software.sparkplug.whiteboard.SVGUtil;
import com.version2software.sparkplug.whiteboard.WhiteboardPlugin;
import com.version2software.sparkplug.whiteboard.undo.UndoAction;
import com.version2software.sparkplug.whiteboard.undo.UndoColor;
import com.version2software.sparkplug.whiteboard.command.Attribute;
import com.version2software.sparkplug.whiteboard.command.ClearAll;
import com.version2software.sparkplug.whiteboard.command.ClearBackground;
import com.version2software.sparkplug.whiteboard.command.Command;
import com.version2software.sparkplug.whiteboard.command.Configure;
import com.version2software.sparkplug.whiteboard.command.Order;
import com.version2software.sparkplug.whiteboard.command.Remove;
import com.version2software.sparkplug.whiteboard.command.SetBackground;
import com.version2software.sparkplug.whiteboard.command.Undo;
import com.version2software.sparkplug.whiteboard.extension.SVGExtension;
import com.version2software.sparkplug.whiteboard.images.ImageLoader;
import com.version2software.sparkplug.whiteboard.query.WhiteboardNotification;
import com.version2software.sparkplug.whiteboard.shape.Circle;
import com.version2software.sparkplug.whiteboard.shape.Ellipse;
import com.version2software.sparkplug.whiteboard.shape.Line;
import com.version2software.sparkplug.whiteboard.shape.Path;
import com.version2software.sparkplug.whiteboard.shape.Polygon;
import com.version2software.sparkplug.whiteboard.shape.Polyline;
import com.version2software.sparkplug.whiteboard.shape.Polyshape;
import com.version2software.sparkplug.whiteboard.shape.Rectangle;
import com.version2software.sparkplug.whiteboard.shape.SVGImage;
import com.version2software.sparkplug.whiteboard.shape.Shape;
import com.version2software.sparkplug.whiteboard.shape.Text;
import com.version2software.sparkplug.whiteboard.view.ShapeModel;
import com.version2software.sparkplug.whiteboard.view.listeners.CopyMenuActionListener;
import com.version2software.sparkplug.whiteboard.view.listeners.DeleteMenuActionListener;
import com.version2software.sparkplug.whiteboard.view.listeners.DisplayOrderActionListener;
import com.version2software.sparkplug.whiteboard.view.listeners.PasteMenuActionListener;
import com.version2software.sparkplug.whiteboard.view.listeners.PropertiesMenuActionListener;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.log.Log;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

public class Whiteboard extends JFrame {
   private static final int PEN = 1;
   private static final int LINE = 2;
   private static final int RECTANGLE = 3;
   private static final int FILL_RECTANGLE = 4;
   private static final int ELLIPSE = 5;
   private static final int CIRCLE = 6;
   private static final int FILL_CIRCLE = 7;
   private static final int FILL_ELLIPSE = 8;
   private static final int TEXT = 9;
   private static final int SELECTION = 10;
   private static final int IMAGE = 11;
   private static final int POLYLINE = 12;
   private static final int FILL_POLYLINE = 13;
   private static final int POLYGON = 14;
   private static final int FILL_POLYGON = 15;
   private static final int PAN = 16;

   private static File lastDir;

   private static int currentTool = 0;

   private int mouseX = 0;
   private int mouseY = 0;
   private int previousMouseX = 0;
   private int previousMouseY = 0;
   private int defaultGrid = 25;
   private Point2D previousPoint;

   private Shape selectedShape = null;
   private Shape preselected = null;
   private int selectedHandle = -1;

   private boolean doneDrawing = true;

   private int originX = 0;
   private int originY = 0;
   private int originWidth = 0;
   private int originHeight = 0;
   private int drawX = 0;
   private int drawY = 0;

   private Color currentColor = Color.BLACK;
   private Color xorColor = Color.WHITE;

   private JMenu fileMenu = new JMenu("File");
   private JMenuItem newMenuItem = new JMenuItem("New", getImageIcon("stock_new-16.png"));
   private JMenuItem openMenuItem = new JMenuItem("Open", getImageIcon("stock_open-16.png"));
   private JMenuItem saveMenuItem = new JMenuItem("Save", getImageIcon("stock_save-16.png"));
   private JMenuItem exportMenuItem = new JMenuItem("Export As Image", getImageIcon("stock_export-16.png"));
   private JMenuItem sendMenuItem = new JMenuItem("Send");
   private JMenuItem printMenuItem = new JMenuItem("Print", getImageIcon("stock_print-16.png"));
   private JMenuItem exitMenuItem = new JMenuItem("Exit");

   private JMenu editMenu = new JMenu("Edit");
   private JMenuItem undoMenuItem = new JMenuItem("Undo", getImageIcon("arrow_undo.png"));
   private JMenuItem deselectMenuItem = new JMenuItem("Deselect");
   private JMenuItem copyMenuItem = new JMenuItem("Copy", getImageIcon("stock_copy-16.png"));
   private JMenuItem pasteMenuItem = new JMenuItem("Paste", getImageIcon("stock_paste-16.png"));
   private JMenuItem deleteMenuItem = new JMenuItem("Delete", getImageIcon("stock_delete-16.png"));
   private JMenuItem propertiesMenuItem = new JMenuItem("Properties");

   private JMenu orderMenu = new JMenu("Display Order");
   private JMenuItem forwardMenuItem = new JMenuItem("Forward", getImageIcon("stock_bring-forward-16.png"));
   private JMenuItem forwardOneMenuItem = new JMenuItem("Forward One", getImageIcon("stock_bring-forward-16.png"));
   private JMenuItem backMenuItem = new JMenuItem("Back", getImageIcon("stock_bring-backward-16.png"));
   private JMenuItem backOneMenuItem = new JMenuItem("Back One", getImageIcon("stock_bring-backward-16.png"));

   private JMenu viewMenu = new JMenu("View");
   private JMenuItem setBackgroundMenuItem = new JMenuItem("Set Background");
   private JMenuItem clearBackgroundMenuItem = new JMenuItem("Clear Background");
   private JMenuItem zoomInMenuItem = new JMenuItem("Zoom In", getImageIcon("stock_zoom-in-16.png"));
   private JMenuItem zoomOutMenuItem = new JMenuItem("Zoom Out", getImageIcon("stock_zoom-out-16.png"));
   private JMenuItem optionsMenuItem = new JMenuItem("Options");

   private JMenuItem panMenuItem = new JMenuItem("Pan");
   private JMenuItem initialPositionMenuItem = new JMenuItem("Initial Position");

   private JMenu helpMenu = new JMenu("Help");
   private JMenuItem helpMenuItem = new JMenuItem("Help");
   private JMenuItem aboutMenuItem = new JMenuItem("About");

   private JDialog colorChooserDialog;

   private JButton colorChooserButton = new JButton(getImageIcon("stock_filters-pop-art.png"));
   private ToolLabel selectionButton = new ToolLabel("stock_draw-selection.png", "Selection");
   private ToolLabel penButton = new ToolLabel("stock_draw-freeform-line.png", "Pen");
   private ToolLabel lineButton = new ToolLabel("stock_draw-line.png", "Line");
   private ToolLabel rectangleButton = new ToolLabel("stock_draw-rectangle-unfilled.png", "Rectangle");
   private ToolLabel fillRectangleButton = new ToolLabel("stock_draw-rectangle.png", "Filled rectangle");
   private ToolLabel ellipseButton = new ToolLabel("stock_draw-ellipse-unfilled.png", "Ellipse");
   private ToolLabel fillEllipseButton = new ToolLabel("stock_draw-ellipse.png", "Filled ellipse");
   private ToolLabel circleButton = new ToolLabel("stock_draw-circle-unfilled.png", "Circle");
   private ToolLabel fillCircleButton = new ToolLabel("stock_draw-circle.png", "Filled circle");
   private ToolLabel polylineButton = new ToolLabel("stock_draw-polygon.png", "Polyline");
   private ToolLabel fillPolylineButton = new ToolLabel("stock_draw-polygon-filled.png", "Filled polyline");
   private ToolLabel polygonButton = new ToolLabel("stock_draw-polygon-45.png", "Polygon");
   private ToolLabel fillPolygonButton = new ToolLabel("stock_draw-polygon-45-filled.png", "Filled polygon");
   private ToolLabel textButton = new ToolLabel("stock_directcursor.png", "Text");
   private ToolLabel imageButton = new ToolLabel("stock_insert_graphic.png", "Image");
   
   private CopyMenuActionListener copyListener = new CopyMenuActionListener(this);
   private PasteMenuActionListener pasteListener = new PasteMenuActionListener(this);
   private PropertiesMenuActionListener propertiesListener = new PropertiesMenuActionListener(this);
   private DeleteMenuActionListener deleteListener =  new DeleteMenuActionListener(this);
   private DisplayOrderActionListener forwardListener = new DisplayOrderActionListener(this, "front");
   private DisplayOrderActionListener forwardOneListener = new DisplayOrderActionListener(this, "front-one");
   private DisplayOrderActionListener backListener = new DisplayOrderActionListener(this, "back");
   private DisplayOrderActionListener backOneListener = new DisplayOrderActionListener(this, "back-one");

   ShapeModel model = new ShapeModel();

   private List<int []> pathList = new ArrayList<int []>();
   private WhiteboardPanel whiteboardPanel = new WhiteboardPanel(this);
   private JLabel labelCoord = new JLabel("");
   private JLabel labelTip = new JLabel("");
   private String participant;
   private boolean moving;
   private AffineTransform w2s;
   private AffineTransform s2w;
   private boolean isGroup;
   private boolean trackMouseEnabled = true;
   private boolean gridEnabled = false;
   private String random = null;

   public static void main(String[] args) {
      new Whiteboard("Standalone", false);
   }

   public Whiteboard(String participant, boolean isGroup) {
      super();

      this.participant = participant;
      this.isGroup = isGroup;

      setTitle("Whiteboarding with " + participant);
      initUI();
      initListeners();
      setVisible(true);
   }

   private void initUI() {
      initializeTransform();

      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
      
      buttonPanel.add(createButtonPair(colorChooserButton, selectionButton));
      buttonPanel.add(createButtonPair(penButton, lineButton));
      buttonPanel.add(createButtonPair(rectangleButton, fillRectangleButton));
      buttonPanel.add(createButtonPair(ellipseButton, fillEllipseButton));
      buttonPanel.add(createButtonPair(circleButton, fillCircleButton));
      buttonPanel.add(createButtonPair(polylineButton, fillPolylineButton));
      buttonPanel.add(createButtonPair(polygonButton, fillPolygonButton));
      buttonPanel.add(createButtonPair(imageButton, textButton));

      toggleButtons();

      setJMenuBar(initMenuBar());
      
      setLayout(new BorderLayout());
      add(buttonPanel, BorderLayout.WEST);
      add(whiteboardPanel, BorderLayout.CENTER);

      JPanel statusBar = new JPanel();
      statusBar.setLayout(new BorderLayout());
      statusBar.add(labelCoord, BorderLayout.WEST);
      statusBar.add(labelTip, BorderLayout.EAST);
      add(statusBar, BorderLayout.SOUTH);

      pack();
      setSize(540, 400);
   }

   private void initializeTransform() {
      w2s = new AffineTransform();
      w2s.setToScale(1, 1);

      try {
         s2w = w2s.createInverse();
      } catch (NoninvertibleTransformException e) {
         Log.error(e.getMessage());
      }
   }

   private JPanel createButtonPair(JComponent left, JComponent right) {
      Dimension componentSize = new Dimension(35, 35);
      left.setMaximumSize(componentSize);
      left.setMinimumSize(componentSize);
      left.setPreferredSize(componentSize);
      right.setMaximumSize(componentSize);
      right.setMinimumSize(componentSize);
      right.setPreferredSize(componentSize);
      
      JPanel panel = new JPanel(new FlowLayout());
      Dimension panelSize = new Dimension(80, 40);
      panel.setMaximumSize(panelSize);
      panel.setMinimumSize(panelSize);
      panel.setPreferredSize(panelSize);
      
      panel.add(left);
      panel.add(right);
      
      return panel;
   }

   private JMenuBar initMenuBar() {
      JMenuBar menuBar = new JMenuBar();
      
      menuBar.setBackground(Color.white);
      fileMenu.setBackground(Color.white);
      editMenu.setBackground(Color.white);
      viewMenu.setBackground(Color.white);
      helpMenu.setBackground(Color.white);

      fileMenu.add(newMenuItem);
      fileMenu.add(openMenuItem);
      fileMenu.addSeparator();

      /*
       * todo - figure out "send" semantics - must the master peer, if there is such a thing,
       * push it or must a non-master peer request it from the master?
       */

      // fileMenu.add(sendMenuItem);
      fileMenu.add(saveMenuItem);
      fileMenu.add(exportMenuItem);
      fileMenu.addSeparator();
      fileMenu.add(printMenuItem);
      fileMenu.addSeparator();
      fileMenu.add(exitMenuItem);
      menuBar.add(fileMenu);

      orderMenu.add(forwardMenuItem);
      orderMenu.add(forwardOneMenuItem);
      orderMenu.add(backMenuItem);
      orderMenu.add(backOneMenuItem);

      editMenu.add(undoMenuItem);
      editMenu.add(deselectMenuItem);
      editMenu.addSeparator();
      editMenu.add(copyMenuItem);
      editMenu.add(pasteMenuItem);
      editMenu.add(deleteMenuItem);
      editMenu.addSeparator();
      editMenu.add(propertiesMenuItem);
      editMenu.addSeparator();
      editMenu.add(orderMenu);
      menuBar.add(editMenu);
      
      viewMenu.add(setBackgroundMenuItem);
      viewMenu.add(clearBackgroundMenuItem);
      viewMenu.addSeparator();
      viewMenu.add(zoomInMenuItem);
      viewMenu.add(zoomOutMenuItem);
      viewMenu.add(panMenuItem);
      viewMenu.add(initialPositionMenuItem);
      viewMenu.addSeparator();
      viewMenu.add(optionsMenuItem);
      menuBar.add(viewMenu);

      helpMenu.add(helpMenuItem);
      helpMenu.add(aboutMenuItem);
      menuBar.add(helpMenu);

      return menuBar;
   }

   private void initListeners() {
      setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      addWindowListener(new WhiteboardWindowAdapter());
      
      colorChooserButton.setToolTipText("Color picker");
      
      colorChooserButton.addActionListener(new ColorChooserActionListener());
      selectionButton.addMouseListener(new SelectionButtonListener());
      penButton.addMouseListener(new ToolButtonListener(PEN));
      lineButton.addMouseListener(new ToolButtonListener(LINE));
      rectangleButton.addMouseListener(new ToolButtonListener(RECTANGLE));
      fillRectangleButton.addMouseListener(new ToolButtonListener(FILL_RECTANGLE));
      ellipseButton.addMouseListener(new ToolButtonListener(ELLIPSE));
      fillEllipseButton.addMouseListener(new ToolButtonListener(FILL_ELLIPSE));
      circleButton.addMouseListener(new ToolButtonListener(CIRCLE));
      fillCircleButton.addMouseListener(new ToolButtonListener(FILL_CIRCLE));
      polylineButton.addMouseListener(new ToolButtonListener(POLYLINE));
      fillPolylineButton.addMouseListener(new ToolButtonListener(FILL_POLYLINE));
      polygonButton.addMouseListener(new ToolButtonListener(POLYGON));
      fillPolygonButton.addMouseListener(new ToolButtonListener(FILL_POLYGON));
      textButton.addMouseListener(new ToolButtonListener(TEXT));
      imageButton.addMouseListener(new ToolButtonListener(IMAGE));

      newMenuItem.addActionListener(new NewMenuActionListener());
      openMenuItem.addActionListener(new OpenMenuActionListener());
      saveMenuItem.addActionListener(new SaveMenuActionListener());
      exportMenuItem.addActionListener(new ExportMenuActionListener());
      sendMenuItem.addActionListener(new SendMenuActionListener());
      optionsMenuItem.addActionListener(new OptionsMenuActionListener());
      printMenuItem.addActionListener(new PrintMenuActionListener());
      exitMenuItem.addActionListener(new ExitMenuActionListener());

      undoMenuItem.addActionListener(new UndoMenuActionListener());
      undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

      deselectMenuItem.addActionListener(new DeselectMenuActionListener());

      copyMenuItem.addActionListener(copyListener);
      pasteMenuItem.addActionListener(pasteListener);
      deleteMenuItem.addActionListener(deleteListener);
      
      copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      
      propertiesMenuItem.addActionListener(propertiesListener);

      forwardMenuItem.addActionListener(forwardListener);
      forwardOneMenuItem.addActionListener(forwardOneListener);
      backMenuItem.addActionListener(backListener);
      backOneMenuItem.addActionListener(backOneListener);

      setBackgroundMenuItem.addActionListener(new SetBackgroundMenuActionListener());
      clearBackgroundMenuItem.addActionListener(new ClearBackgroundMenuActionListener());
      zoomInMenuItem.addActionListener(new ZoomInMenuActionListener());
      zoomOutMenuItem.addActionListener(new ZoomOutMenuActionListener());

      panMenuItem.addActionListener(new PanMenuActionListener());
      initialPositionMenuItem.addActionListener(new InitialPositionMenuActionListener());

      helpMenuItem.addActionListener(new HelpMenuActionListener());
      aboutMenuItem.addActionListener(new AboutMenuActionListener());

      whiteboardPanel.addMouseMotionListener(new DrawPanelMouseMotionListener());
      whiteboardPanel.addMouseListener(new DrawPanelMouseListener());
   }

   private void clearPanel() {
      processAndSend(new ClearAll());
   }

   private void penOperation(MouseEvent e) {
      Graphics g = whiteboardPanel.getGraphics();
      g.setColor(currentColor);

      if (doneDrawing) {
         setGraphicalDefaults(e);
         doneDrawing = false;
         g.drawLine(previousMouseX, previousMouseY, mouseX, mouseY);
      }

      if (hasMouseMoved(e)) {
         mouseX = snapToX(e.getX());
         mouseY = snapToY(e.getY());
         int[] point = {mouseX, mouseY};
         pathList.add(point);
         g.drawLine(previousMouseX, previousMouseY, mouseX, mouseY);

         previousMouseX = mouseX;
         previousMouseY = mouseY;
      }
   }

   private void lineOperation(MouseEvent e) {
      Graphics g = whiteboardPanel.getGraphics();
      g.setColor(currentColor);

      if (doneDrawing) {
         setGraphicalDefaults(e);
         g.setXORMode(xorColor);
         g.drawLine(originX, originY, mouseX, mouseY);
         doneDrawing = false;
      }

      if (hasMouseMoved(e)) {
         g.setXORMode(xorColor);
         g.drawLine(originX, originY, mouseX, mouseY);

         mouseX = snapToX(e.getX());
         mouseY = snapToX(e.getY());

         g.drawLine(originX, originY, mouseX, mouseY);
      }
   }

   private void rectangleOperation(MouseEvent e) {
      Graphics g = whiteboardPanel.getGraphics();
      g.setColor(currentColor);

      if (doneDrawing) {
         setGraphicalDefaults(e);
         doneDrawing = false;
      }

      if (hasMouseMoved(e)) {
         g.setXORMode(whiteboardPanel.getBackground());
         g.drawRect(drawX, drawY, originWidth, originHeight);

         mouseX = snapToX(e.getX());
         mouseY = snapToY(e.getY());

         setActualBoundry();

         g.drawRect(drawX, drawY, originWidth, originHeight);
      }
   }

   private void ellipseOperation(MouseEvent e) {
      Graphics g = whiteboardPanel.getGraphics();
      g.setColor(currentColor);

      if (doneDrawing) {
         setGraphicalDefaults(e);
         doneDrawing = false;
      }

      if (hasMouseMoved(e)) {
         g.setXORMode(xorColor);
         g.drawOval(drawX, drawY, originWidth, originHeight);

         mouseX = snapToX(e.getX());
         mouseY = snapToY(e.getY());

         setActualBoundry();

         g.drawOval(drawX, drawY, originWidth, originHeight);
      }
   }

   private void circleOperation(MouseEvent e) {
      Graphics g = whiteboardPanel.getGraphics();
      g.setColor(currentColor);

      if (doneDrawing) {
         setGraphicalDefaults(e);
         doneDrawing = false;
      }

      if (hasMouseMoved(e)) {
         g.setXORMode(xorColor);
         g.drawOval(drawX, drawY, originWidth, originWidth);

         mouseX = snapToX(e.getX());
         mouseY = snapToY(e.getY());

         setActualBoundry();

         g.drawOval(drawX, drawY, originWidth, originWidth);
      }
   }
   
   private void polyOperation(MouseEvent e) {
      Graphics g = whiteboardPanel.getGraphics();
      g.setColor(currentColor);

      if (doneDrawing) {
         setGraphicalDefaults(e);
         doneDrawing = false;
      }
      
      if (hasMouseMoved(e)) {
         mouseX = snapToX(e.getX());
         mouseY = snapToY(e.getY());
         int[] point = {mouseX, mouseY};
         pathList.add(point);
         
         int size = pathList.size();
         if (size <= 1) {
            pathList.add(0, new int[]{previousMouseX, previousMouseY});
            size = pathList.size();
         }
         
         int[] prev = pathList.get(size - 2);
         int[] last = pathList.get(size - 1);
         
         g.drawLine(prev[0], prev[1], last[0], last[1]);
         
         previousMouseX = mouseX;
         previousMouseY = mouseY;
      }
   }
   
   private void imageOperation(MouseEvent e) {
      Graphics g = whiteboardPanel.getGraphics();
      g.setColor(currentColor);

      if (doneDrawing) {
         setGraphicalDefaults(e);
         doneDrawing = false;
      }

      if (hasMouseMoved(e)) {
         g.setXORMode(whiteboardPanel.getBackground());
         g.drawRect(drawX, drawY, originWidth, originHeight);

         mouseX = snapToX(e.getX());
         mouseY = snapToY(e.getY());

         setActualBoundry();

         g.drawRect(drawX, drawY, originWidth, originHeight);
      }
   }

   private void moveOperation(MouseEvent e) {
      if (selectedShape == null) {
         return;
      }
      if (!moving) {
         random = String.valueOf(Math.random());
         model.appendUndoConfigure(selectedShape, random);

      }
      moving = true;

      // Don't remove this, it will cause the move operation to be squirely
      if (doneDrawing) {
         setGraphicalDefaults(e);
         doneDrawing = false;
      }
      int x = snapToX(e.getX());
      int y = snapToY(e.getY());

      if (hasMouseMoved(e)) {
         Point2D s0 = new Point2D.Double(mouseX, mouseY);
         Point2D s1 = new Point2D.Double(x, y);

         Point2D w0 = s2w.transform(s0, null);
         Point2D w1 = s2w.transform(s1, null);

         // Detemine if you are moving a handle or moving a shape.
         // A path is a special problem because the handles are so close together.
         boolean altKeyDown = (e.getModifiers() & MouseEvent.ALT_MASK) == MouseEvent.ALT_MASK ;
         if ((selectedShape instanceof Path) && selectedHandle > -1 && altKeyDown) {
            selectedShape.movePoint(w1, selectedHandle);
         } else if (!(selectedShape instanceof Path) && selectedHandle > -1) {
            selectedShape.movePoint(w1, selectedHandle);
         } else {
            selectedShape.delta(w1.getX() - w0.getX(), w1.getY() - w0.getY());
         }

         mouseX = x;
         mouseY = y;

         repaint();
      }
   }

   private void panOperation(MouseEvent e) {
      Point2D currentPoint = e.getPoint();

      Point2D wCurrentPoint = s2w.transform(currentPoint, null);
      Point2D wPreviousPoint = s2w.transform(previousPoint, null);

      double deltaX = wCurrentPoint.getX() - wPreviousPoint.getX();
      double deltaY = wCurrentPoint.getY() - wPreviousPoint.getY();

      translate(deltaX, deltaY);

      previousPoint = currentPoint;
   }

   public boolean isGridEnabled() {
      return gridEnabled;
   }

   public void setGridEnabled(boolean gridEnabled) {
      this.gridEnabled = gridEnabled;
      repaint();
   }
   
   public int getDefaultGrid() {
      return defaultGrid;
   }

   public boolean isTrackMouse() {
      return trackMouseEnabled;
   }

   public void setTrackMouse(boolean trackMouse) {
      this.trackMouseEnabled = trackMouse;
      if (!trackMouse) {
         labelCoord.setText(" ");
      }
   }
   
   public void setPartcipant(String partcipant) {
      this.participant = partcipant;
      setTitle(participant);
   }

   private int snapToX(int x) {
      return gridEnabled ? snap(x) : x;
   }

   private int snapToY(int y) {
      return gridEnabled ? snap(y) : y;
   }

   private int snap(int i) {
      int snap = i % defaultGrid;
      if (snap < ((double) defaultGrid / (double) 2)) {
         return (i - snap);
      }
      return (i + defaultGrid - snap);
   }

   private boolean hasMouseMoved(MouseEvent e) {
      return (mouseX != e.getX() || mouseY != e.getY());
   }

   /**
    * Calculate the new values for the global variables drawX and drawY
    * according to the new positions of the mouse cursor. This method
    * eleviates the possibility that a negative width or height can occur.
    */
   private void setActualBoundry() {
      /*
       * If any of the current mouse coordinates are smaller than the origin
       * coordinates, meaning if drag occurred in a negative manner, where either
       * the x-shift occurred from right and/or y-shift occurred from bottom to
       * top.
       */
      if (mouseX < originX || mouseY < originY) {

         /*
          * If the current mouse x coordinate is smaller than the origin x
          * coordinate, equate the drawX to be the difference between the
          * current width and the origin x coordinate.
          */
         if (mouseX < originX) {
            originWidth = originX - mouseX;
            drawX = originX - originWidth;
         } else {
            drawX = originX;
            originWidth = mouseX - originX;
         }

         /*
          * if the current mouse y coordinate is smaller than the origin y
          * coordinate, equate the drawY to be the difference between the
          * current height and the origin y coordinate.
          */
         if (mouseY < originY) {
            originHeight = originY - mouseY;
            drawY = originY - originHeight;
         } else {
            drawY = originY;
            originHeight = mouseY - originY;
         }
      }

      /*
       * Else if drag was done in a positive manner meaning x-shift occurred from
       * left to right and or y-shift occurred from top to bottom
       */
      else {
         drawX = originX;
         drawY = originY;
         originWidth = mouseX - originX;
         originHeight = mouseY - originY;
      }
   }

   /**
    * Sets all the drawing variables to the current position of the cursor.
    * Height and width variables are zeroed off.
    */
   private void setGraphicalDefaults(MouseEvent e) {
      int x = snapToX(e.getX());
      int y = snapToY(e.getY());
      mouseX = x;
      mouseY = y;
      previousMouseX = x;
      previousMouseY = y;
      originX = x;
      originY = y;
      drawX = x;
      drawY = y;
      originWidth = 0;
      originHeight = 0;
   }

   private void releasedPen() {
      doneDrawing = true;
      appendAndSend(new Path(id(), currentColor, pathList, s2w));
      pathList.clear();
   }

   private void releasedLine() {
      if ((Math.abs(originX - mouseX) + Math.abs(originY - mouseY)) != 0) {
         doneDrawing = true;
         appendAndSend(new Line(id(), currentColor, originX, originY, mouseX, mouseY, s2w));
      }
   }

   private void releasedRectangle(boolean fill) {
      doneDrawing = true;
      appendAndSend(new Rectangle(id(), currentColor, drawX, drawY, originWidth, originHeight, fill, s2w));
   }

   private void releasedEllipse(boolean fill) {
      doneDrawing = true;

      int rx = originWidth / 2;
      int ry = originHeight / 2;
      int cx = drawX + rx;
      int cy = drawY + ry;

      appendAndSend(new Ellipse(id(), currentColor, cx, cy, rx, ry, fill, s2w));
   }

   private void releasedCircle(boolean fill) {
      doneDrawing = true;

      int r = originWidth / 2;
      int cx = drawX + r;
      int cy = drawY + r;

      appendAndSend(new Circle(id(), currentColor, cx, cy, r, fill, s2w));
   }
   
   private void releasedPolyline(boolean fill) {
      doneDrawing = true;
      appendAndSend(new Polyline(id(), currentColor, pathList, fill, s2w));
      pathList.clear();
   }
   
   private void releasedPolygon(boolean fill) {
      doneDrawing = true;
      appendAndSend(new Polygon(id(), currentColor, pathList, fill, s2w));
      pathList.clear();
   }

   private void releasedText(int x, int y) {
      doneDrawing = true;

      Graphics g = whiteboardPanel.getGraphics();
      g.setColor(Color.BLACK);
      g.drawLine(x, y-10, x, y+10);

      TextDialog d = new TextDialog(Whiteboard.this);
      d.setVisible(true);
      String text = d.getText();
      int size =  d.getTextSize();
      if (text != null) {
         appendAndSend(new Text(id(), currentColor, x, y, size, text, s2w));
      }
   }

   private void releasedImage() {
      doneDrawing = true;

      try {
         JFileChooser chooser = getImageFileChooser(ImageIO.getReaderFormatNames());

         int returnVal = chooser.showOpenDialog(Whiteboard.this);
         if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = validateFileName(chooser.getSelectedFile(), chooser.getFileFilter());
            lastDir = file.getParentFile();
            SVGImage image = new SVGImage(id(), drawX, drawY, originWidth, originHeight, ImageIO.read(file), s2w);
            appendAndSend(image);
         }
      } catch (Exception e) {
         Log.error(e.getMessage());
      }
   }

   private JFileChooser getImageFileChooser(String[] formatNames) {
      JFileChooser chooser = new JFileChooser(lastDir);
      chooser.setAcceptAllFileFilterUsed(false);
      List<String> names = new ArrayList<String>();
      for (String name : formatNames) {
         if ("bmp jpeg jpg tif tiff gif png".indexOf(name) > -1) {
            names.add(name);
         }
      }
      Collections.sort(names);
      for (String name : names) {
         chooser.addChoosableFileFilter(new WhiteboardFileFilter(name, "*." + name));
      }
      // set the first one in the list as the default
      if (names.size() > 0) {
         chooser.setFileFilter(chooser.getChoosableFileFilters()[0]);
      }
      return chooser;
   }

   /**
    * When a shape is moved we send a message with the new position rather than deleting the shape
    * and then inserting it.
    */
   private void releasedMove() {
      if (moving) {
         doneDrawing = true;
         if (selectedShape instanceof Rectangle) {
            Rectangle rect = (Rectangle) selectedShape;
            Configure configure = new Configure(selectedShape.getId(), random);
            configure.addAttribute(new Attribute("x", String.valueOf(rect.getX())));
            configure.addAttribute(new Attribute("y", String.valueOf(rect.getY())));
            configure.addAttribute(new Attribute("width", String.valueOf(rect.getWidth())));
            configure.addAttribute(new Attribute("height", String.valueOf(rect.getHeight())));
            sendMessage(configure);
         } else if (selectedShape instanceof Line) {
            Line line = (Line) selectedShape;
            Configure configure = new Configure(selectedShape.getId(), random);
            configure.addAttribute(new Attribute("x1", String.valueOf(line.getX())));
            configure.addAttribute(new Attribute("y1", String.valueOf(line.getY())));
            configure.addAttribute(new Attribute("x2", String.valueOf(line.getxEnd())));
            configure.addAttribute(new Attribute("y2", String.valueOf(line.getyEnd())));
            sendMessage(configure);
         } else if (selectedShape instanceof Text) {
            Text text = (Text) selectedShape;
            Configure configure = new Configure(selectedShape.getId(), random);
            configure.addAttribute(new Attribute("x", String.valueOf(text.getX())));
            configure.addAttribute(new Attribute("y", String.valueOf(text.getY())));
            sendMessage(configure);
         } else if (selectedShape instanceof Circle) {
            Circle cir = (Circle) selectedShape;
            Configure configure = new Configure(selectedShape.getId(), random);
            configure.addAttribute(new Attribute("r", String.valueOf(cir.getR())));
            configure.addAttribute(new Attribute("cx", String.valueOf(cir.getCx())));
            configure.addAttribute(new Attribute("cy", String.valueOf(cir.getCy())));
            sendMessage(configure);
         } else if (selectedShape instanceof Ellipse) {
            Ellipse elli = (Ellipse) selectedShape;
            Configure configure = new Configure(selectedShape.getId(), random);
            configure.addAttribute(new Attribute("rx", String.valueOf(elli.getRx())));
            configure.addAttribute(new Attribute("ry", String.valueOf(elli.getRy())));
            configure.addAttribute(new Attribute("cx", String.valueOf(elli.getCx())));
            configure.addAttribute(new Attribute("cy", String.valueOf(elli.getCy())));
            sendMessage(configure);
         } else if (selectedShape instanceof SVGImage) {
            SVGImage img = (SVGImage) selectedShape;
            Configure configure = new Configure(selectedShape.getId(), random);
            configure.addAttribute(new Attribute("x", String.valueOf(img.getX())));
            configure.addAttribute(new Attribute("y", String.valueOf(img.getY())));
            configure.addAttribute(new Attribute("width", String.valueOf(img.getWidth())));
            configure.addAttribute(new Attribute("height", String.valueOf(img.getHeight())));
            sendMessage(configure);
         } else if (selectedShape instanceof Path) {
            Path path = (Path) selectedShape;
            Configure configure = new Configure(selectedShape.getId(), random);
            configure.addAttribute(new Attribute("d", path.buildPathAsXML()));
            sendMessage(configure);
         }  else if (selectedShape instanceof Polyshape) {
            Polyshape poly = (Polyshape) selectedShape;
            Configure configure = new Configure(selectedShape.getId(), random);
            configure.addAttribute(new Attribute("points", poly.buildPointsAsXML()));
            sendMessage(configure);
         } else {
            sendMessage(new Remove(selectedShape.getId()));
            selectedShape.setSelected(false);
            sendMessage(selectedShape);
         }
         selectedShape = null;
         moving = false;
         random = null;
         repaint();
      }
   }

   private ImageIcon getImageIcon(String icon) {
      return ImageLoader.getImageIcon(icon);
   }

   /**
    * Sets all the buttons (ToolLabels) to their unselected state
    * except for the currently selected tool.
    */
   private void toggleButtons() {
      selectionButton.setSelected(false);
      penButton.setSelected(false);
      lineButton.setSelected(false);
      rectangleButton.setSelected(false);
      fillRectangleButton.setSelected(false);
      ellipseButton.setSelected(false);
      fillEllipseButton.setSelected(false);
      circleButton.setSelected(false);
      fillCircleButton.setSelected(false);
      polylineButton.setSelected(false);
      fillPolylineButton.setSelected(false);
      polygonButton.setSelected(false);
      fillPolygonButton.setSelected(false);
      textButton.setSelected(false);
      imageButton.setSelected(false);

      switch (currentTool) {
      case SELECTION:
         selectionButton.setSelected(true);
         break;

      case PEN:
         penButton.setSelected(true);
         break;

      case LINE:
         lineButton.setSelected(true);
         break;

      case RECTANGLE:
         rectangleButton.setSelected(true);
         break;

      case FILL_RECTANGLE:
         fillRectangleButton.setSelected(true);
         break;

      case ELLIPSE:
         ellipseButton.setSelected(true);
         break;

      case FILL_ELLIPSE:
         fillEllipseButton.setSelected(true);
         break;

      case CIRCLE:
         circleButton.setSelected(true);
         break;

      case FILL_CIRCLE:
         fillCircleButton.setSelected(true);
         break;
         
      case POLYLINE:
         polylineButton.setSelected(true);
         break;
         
      case FILL_POLYLINE:
         fillPolylineButton.setSelected(true);
         break;
         
      case POLYGON:
         polygonButton.setSelected(true);
         break;
         
      case FILL_POLYGON:
         fillPolygonButton.setSelected(true);
         break;

      case TEXT:
         textButton.setSelected(true);
         break;

      case IMAGE:
         imageButton.setSelected(true);
         break;
      }
   }

   public void processCommand(Command command) {

      if (command instanceof Remove) {
         model.processRemove((Remove) command);
         repaint();
      } else if (command instanceof Undo) {
         model.undo(Whiteboard.this);
         repaint();         
      } else if (command instanceof ClearAll) {
         model.processClearAll();
         selectedShape = null;
         repaint();
      } else if (command instanceof ClearBackground) {
         model.processClearBackground();
         repaint();
      } else if (command instanceof SetBackground) {
         model.processSetBackground((SetBackground) command);
         repaint();
      } else if (command instanceof Order) {
         if (model.processOrder(command)) {
            repaint();
         }
      } else if (command instanceof Configure) {
         model.processConfigure(command);
         repaint();
      }
   }

   /**
     * Adds an element to the displayList or performs a command. If an element is not
     * recognized (e.g., the drawing was created by an svg editing tool that has included elements
     * that we do not recognize), then that element is thrown away.
     *
     * @param element - A Shape or a Command
     */
   public void processSVGElement(SVGElement element) {
      if (element instanceof Shape) {
         model.appendShape((Shape) element);
         repaint();
      } else if (element instanceof Command) {
         processCommand((Command) element);
      }
   }

   public void appendUndo(UndoAction undoAction) {
      model.appendUndo(undoAction);
   }

   private class DrawPanelMouseMotionListener implements MouseMotionListener {
      public void mouseDragged(MouseEvent e) {

         trackMouse(e);

         switch (currentTool) {
         case SELECTION:
            moveOperation(e);
            break;

         case PEN:
            penOperation(e);
            break;

         case LINE:
            lineOperation(e);
            break;

         case RECTANGLE:
            rectangleOperation(e);
            break;

         case FILL_RECTANGLE:
            rectangleOperation(e);
            break;

         case ELLIPSE:
            ellipseOperation(e);
            break;

         case FILL_ELLIPSE:
            ellipseOperation(e);
            break;

         case CIRCLE:
            circleOperation(e);
            break;

         case FILL_CIRCLE:
            circleOperation(e);
            break;

         case IMAGE:
            imageOperation(e);
            break;

         case PAN:
            panOperation(e);
            break;
         }
      }

      public void mouseMoved(MouseEvent e) {
         trackMouse(e);
         Point2D point = s2w.transform(e.getPoint(), null);
         for (Shape shape : model.getDisplayList()) {
            if (shape.contains(point) || shape.getHandleIndex(w2s, e.getPoint()) > -1) {
               setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

               if (currentTool == SELECTION) {
                  if (preselected != null && !preselected.equals(shape)) {
                     repaint();       // Unpaint the previous preselection
                  }
                  Graphics g = whiteboardPanel.getGraphics();
                  shape.preselect(g, w2s);
                  preselected = shape;
                  if (shape instanceof Path) {
                     labelTip.setText("Alt-Drag moves a handle      ");
                  }
               }
               return;
            }
         }
         // Nothing has been selected, unpaint any preselected shape
         if (preselected != null) {
            preselected = null;
            labelTip.setText("");
            repaint();
         }
         toggleCursor();
      }

      private void trackMouse(MouseEvent e) {
         if (trackMouseEnabled) {
            Point2D worldPoint = s2w.transform(e.getPoint(), null);
            labelCoord.setText(String.format(" Screen: (%3d, %4d)  World: (%1.2f, %2.2f)",
                  e.getX(), e.getY(), worldPoint.getX(), worldPoint.getY()));
         }
      }
   }

      /**
    * Sets the appropriate cursor depending on the current tool.
    */
   private void toggleCursor() {
      switch (currentTool) {
      case SELECTION:
         setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
         break;

      case PAN:
         setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
         break;

      case TEXT:
         setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
         break;
         
      case PEN:
      case LINE:
      case RECTANGLE:
      case FILL_RECTANGLE:
      case ELLIPSE:
      case FILL_ELLIPSE:
      case CIRCLE:
      case FILL_CIRCLE:
      case POLYLINE:
      case FILL_POLYLINE:
      case POLYGON:
      case FILL_POLYGON:
      case IMAGE:
         setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
         break;

      default:
         setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
   }

   private void confirmExit() {
      if (!model.isDirty()) {
         dispose();
         sendEventNotification(WhiteboardNotification.Event.EXIT) ;
         return;
      }
      
      int selection = JOptionPane.showConfirmDialog(Whiteboard.this, 
               "Would you like to save your whiteboard before exiting?", 
               "Save?", 
               JOptionPane.YES_NO_CANCEL_OPTION);
      
      switch (selection) {
      case JOptionPane.YES_OPTION:
         save();
         dispose();
         sendEventNotification(WhiteboardNotification.Event.EXIT) ;
         break;
         
      case JOptionPane.NO_OPTION:
         dispose();
         sendEventNotification(WhiteboardNotification.Event.EXIT) ;
         break;

      default:
         break;
      }
   }
   
   public void processNotification(WhiteboardNotification notification) {
      if (notification.getEvent() == WhiteboardNotification.Event.NEW) {
         JOptionPane.showMessageDialog(this, notification.getFrom() + " just created a new whiteboard.");
      }
   }
   
   public void save() {
      JFileChooser chooser = new JFileChooser(lastDir);
      WhiteboardFileFilter filter = new WhiteboardFileFilter("svg", "SVG Files (*.svg)");
      chooser.setFileFilter(filter);
      int returnVal = chooser.showSaveDialog(Whiteboard.this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         File file = validateFileName(chooser.getSelectedFile(), chooser.getFileFilter() );
         lastDir = file.getParentFile();
         try {
            StringBuilder sb = new StringBuilder();
            sb.append("<svg v2-whiteboard-version=");
            sb.append("\"");
            sb.append(WhiteboardPlugin.getVersion());
            sb.append("\"");
            sb.append(">");

            if (model.getBackgroundImage() != null) {
               sb.append(new SetBackground(model.getBackgroundImage()).asXML());
            }
            for (Shape s: model.getDisplayList()) {
               sb.append(s.asXML());
            }
            sb.append("</svg>");
            
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            writer.write(sb.toString());
            writer.close();
         } catch (IOException e) {
            Log.error(e.getMessage());
         }
      }
   }

   private class DrawPanelMouseListener implements MouseListener {

      public void mouseClicked(MouseEvent e) {
         switch (currentTool) {
         //Polyline and polygon are so similar it seems a shame to have all this duplicate code
         case POLYLINE:
            if (e.getClickCount() == 1) {
               polyOperation(e);
            }
            
            if (e.getClickCount() == 2) {
               releasedPolyline(false);
            }
            break;
         
         case FILL_POLYLINE:
            if (e.getClickCount() == 1) {
               polyOperation(e);
            }

            if (e.getClickCount() == 2) {
               releasedPolyline(true);
            }
            break;
         
         case POLYGON:
            if (e.getClickCount() == 1) {
               polyOperation(e);
            }
            
            if (e.getClickCount() == 2) {
               releasedPolygon(false);
            }
            break;
         
         case FILL_POLYGON:
            if (e.getClickCount() == 1) {
               polyOperation(e);
            }
   
            if (e.getClickCount() == 2) {
               releasedPolygon(true);
            }
            break;
         }
      }

      public void mousePressed(MouseEvent e) {
         selectedShape = null;
         selectedHandle = -1;

         if (currentTool == SELECTION) {

             model.deselect();

            /* You can only select one item at a time. If two items overlap, then
             * the most recently added item (i.e., at the end of the display list)
             * gets selected.
             */
            for (int i = model.displayListSize() - 1; i >= 0; i--) {

               Shape shape = model.getShape(i);
               int handle = shape.getHandleIndex(w2s, e.getPoint()); // -1 if no handle selected

               if (shape.contains(s2w.transform(e.getPoint(), null)) || handle > -1) {
                  shape.setSelected(true);
                  selectedShape = shape;
                  selectedHandle = handle;
                  break;
               }
            }
            repaint();
         } else if (currentTool == PAN) {
            previousPoint = e.getPoint();
         }
      }

      public void mouseReleased(MouseEvent e) {
         switch (currentTool) {
            case SELECTION:
               releasedMove();
               break;

            case PEN:
               releasedPen();
               break;

            case LINE:
               releasedLine();
               break;

            case RECTANGLE:
               releasedRectangle(false);
               break;

            case FILL_RECTANGLE:
               releasedRectangle(true);
               break;

            case ELLIPSE:
               releasedEllipse(false);
               break;

            case FILL_ELLIPSE:
               releasedEllipse(true);
               break;

            case CIRCLE:
               releasedCircle(false);
               break;

            case FILL_CIRCLE:
               releasedCircle(true);
               break;

            case TEXT:
               releasedText(e.getX(), e.getY());
               break;

            case IMAGE:
               releasedImage();
               break;
         }
      }

      public void mouseEntered(MouseEvent e) {
         toggleCursor();
      }

      public void mouseExited(MouseEvent e) {
         setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
   }

   private class ColorChooserActionListener implements ActionListener {
      private final JColorChooser colorChooser = new JColorChooser();
      
      public void actionPerformed(ActionEvent ae) {
         if (selectedShape != null) {
            //use the rgb values since getColor() will always return grey because the shape is selected 
            colorChooser.setColor(new Color(selectedShape.red(), selectedShape.green(), selectedShape.blue()));
         }
         
         if (colorChooserDialog == null) {
            ActionListener okListener = new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  currentColor = colorChooser.getColor();
                  colorChooserButton.setForeground(currentColor);
                  colorChooserDialog.setVisible(false);

                  if (selectedShape != null) {
                     String rand = String.valueOf(Math.random());
                     model.appendUndo(new UndoColor(selectedShape, selectedShape.getColor(), rand));
                     selectedShape.setColor(currentColor);
                     selectedShape.setSelected(false);
                     Configure configure = new Configure(selectedShape.getId(), rand);
                     configure.addAttribute(new Attribute("color", SVGUtil.toRGB(selectedShape)));
                     sendMessage(configure);
                     selectedShape = null;
                     repaint();
                  }
               }
            };

            ActionListener cancelListener = new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  colorChooserDialog.setVisible(false);
               }
            };

            colorChooserDialog = JColorChooser.createDialog(Whiteboard.this, "Choose a color", false, colorChooser, okListener, cancelListener);
         }

         colorChooserDialog.setVisible(true);
      }
   }

   private class SelectionButtonListener extends MouseAdapter {
      public void mouseClicked(MouseEvent e) {
         currentTool = SELECTION;
         model.deselect();
         repaint();
         toggleButtons();
      }
   }

   private class ToolButtonListener extends MouseAdapter {

      private int tool;

      public ToolButtonListener(int tool) {
         this.tool = tool;
      }

      public void mouseClicked(MouseEvent e) {
         currentTool = tool;
         toggleButtons();
      }
   }

   private class PanMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         currentTool = PAN;
         toggleButtons();
         model.deselect();
         repaint();
      }
   }
   
   private class NewMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         if (!model.isDirty() ) {
            newWhiteboard();
            return;
         }
         
         int selection = JOptionPane.showConfirmDialog(Whiteboard.this, 
                  "Would you like to save your existing whiteboard before creating a new one?", 
                  "Save?", 
                  JOptionPane.YES_NO_CANCEL_OPTION);
         
         switch (selection) {
         case JOptionPane.YES_OPTION:
            save();
            newWhiteboard();
            break;
            
         case JOptionPane.NO_OPTION:
            newWhiteboard();
            break;

         default:
            break;
         }
      }
      
      private void newWhiteboard() {
         clearPanel();
         toggleButtons();
         initializeTransform();
         sendEventNotification(WhiteboardNotification.Event.NEW);
      }
   }

   private class OpenMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         JFileChooser chooser = new JFileChooser(lastDir);
         WhiteboardFileFilter filter = new WhiteboardFileFilter("svg", "SVG Files (*.svg)");
         chooser.setFileFilter(filter);

         int returnVal = chooser.showOpenDialog(Whiteboard.this);
         if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            lastDir = file.getParentFile();
            clearPanel();
            pathList.clear();
            try {
               InputStreamReader is = new InputStreamReader(new FileInputStream(file), "UTF-8");
               SAXBuilder parser = new SAXBuilder();
               Document doc = parser.build(is);
               Element root = doc.getRootElement();

               // todo: use the following line to detect if an old file needs to be converted to a new version
               //org.jdom.Attribute version = root.getAttribute("v2-whiteboard-version");

               for (Object e : root.getChildren()) {
                  if (e instanceof Element) {
                     SVGElement svgElement = SVGUtil.parseSVGElement((Element) e);
                     processSVGElement(svgElement);
                     sendMessage(svgElement);
                  }
               }
               is.close();
            } catch (Exception e) {
               StringWriter sw = new StringWriter();
               e.printStackTrace(new PrintWriter(sw));
               Log.error(sw.toString());
               JOptionPane.showMessageDialog(null, sw.toString());
            }
         }
      }
   }

   private class WhiteboardFileFilter extends FileFilter {

      private String ext;
      private String description;

      public WhiteboardFileFilter(String ext, String description) {
         this.ext = ext;
         this.description = description;
      }

      public boolean accept(File f) {
         if (f != null) {
            if (f.isDirectory()) {
               return true;
            }
            String e = getExtension(f);
            if (e != null && e.equals(ext)) {
               return true;
            }
         }
         return false;
      }

      public String getDescription() {
         return description;
      }

      public String getExtension(File f) {
         if (f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < filename.length() - 1) {
               return filename.substring(i + 1).toLowerCase();
            }
         }
         return null;
      }

      public String toString() {
         return ext;
      }
   }

   private class OptionsMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         new OptionsDialog(Whiteboard.this, gridEnabled, trackMouseEnabled).setVisible(true);
      }
   }

   private class PrintMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         PrinterJob printJob = PrinterJob.getPrinterJob();
         printJob.setPrintable(whiteboardPanel);
         if (printJob.printDialog()) {
            try {
               printJob.print();
            } catch (PrinterException pe) {
               Log.error(pe.getMessage());
            }
         }
      }
   }

   private class SaveMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         save();
      }
   }

   private class ExportMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         JFileChooser chooser = getImageFileChooser(ImageIO.getWriterFormatNames());
         int returnVal = chooser.showSaveDialog(Whiteboard.this);
         if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = validateFileName(chooser.getSelectedFile(), chooser.getFileFilter());
            lastDir = file.getParentFile();
            saveImage(chooser.getFileFilter().toString(), file);
         }
      }

      public void saveImage(String type, File file) {
         try {
            int width = whiteboardPanel.getWidth();
            int height = whiteboardPanel.getHeight();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();
            whiteboardPanel.paint(g2);
            g2.dispose();
            ImageIO.write(image, type, file);
         } catch (IOException e) {
            Log.error(e.getMessage());
         }
      }
   }

   private class SendMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         if (model.getBackgroundImage() != null) {
            sendMessage(new SetBackground(model.getBackgroundImage()));
         }
         for (Shape s : model.getDisplayList()) {
            sendMessage(s);
         }
      }
   }

   private class ExitMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         confirmExit();
      }
   }
   
   private class WhiteboardWindowAdapter extends WindowAdapter {
      public void windowClosing(WindowEvent we) {
         confirmExit();
      }
   }

   private class DeselectMenuActionListener extends AbstractAction {
      public void actionPerformed(ActionEvent ae) {
         model.deselect();
         repaint();
      }
   }

   private class UndoMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         processAndSend(new Undo());
      }
   }

   private class SetBackgroundMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         try {
            JFileChooser chooser = getImageFileChooser(ImageIO.getReaderFormatNames());
            int returnVal = chooser.showOpenDialog(Whiteboard.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               File file = chooser.getSelectedFile();
               lastDir = file.getParentFile();
               processAndSend(new SetBackground(ImageIO.read(file)));
            }
         } catch (Exception e) {
            Log.error(e.getMessage());
         }
      }
   }

   private class ClearBackgroundMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         processAndSend(new ClearBackground());
      }
   }

   public void appendAndSend(Shape s) {
      if (!isGroup) {
         model.appendShape(s);
         repaint();
      }
      sendMessage(s);
   }

   public void processAndSend(Command c) {
      if (!isGroup) {
         processCommand(c);
      }
      sendMessage(c);
   }
   
   public Shape getSelectedShape() {
      return selectedShape;
   }

   public Color getCurrentColor() {
      return currentColor;
   }
   
   public void setDoneDrawing(boolean doneDrawing) {
      this.doneDrawing = doneDrawing;
   }
   
   public List<Shape> getDisplayList() {
      return model.getDisplayList();
   }
   
   public AffineTransform getWorld2ScreenAffineTransform() {
      return w2s;
   }
   
   public WhiteboardPanel getWhiteboardPanel() {
      return whiteboardPanel;
   }
   
   public BufferedImage getBackgroundImage() {
      return model.getBackgroundImage();
   }

   public void setBackgroundImage(BufferedImage backgroundImage) {
      model.setBackgroundImage(backgroundImage);
      repaint();
   }

   private void changeScale(double xScale, double yScale) {
      if (w2s.getDeterminant() != 0) {   // is it invertible?
         w2s.scale(xScale, yScale);
         try {
            s2w = w2s.createInverse();
         } catch (NoninvertibleTransformException e) {
            Log.error(e.getMessage());
         }
         repaint();
      }
   }

   private void translate(double xTrans, double yTrans) {
      if (w2s.getDeterminant() != 0) {   // is it invertible?
         w2s.translate(xTrans, yTrans);
         try {
            s2w = w2s.createInverse();
         } catch (NoninvertibleTransformException e) {
            Log.error(e.getMessage());
         }
         repaint();
      }
   }

   private class ZoomInMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         changeScale(2.5, 2.5);
      }
   }

   private class ZoomOutMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         changeScale(.4, .4);
      }
   }

   private class InitialPositionMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         initializeTransform();
         repaint();
      }
   }
   
   private class HelpMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         HelpDialog.showDialog(Whiteboard.this);
      }
   }

   private class AboutMenuActionListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         AboutDialog.showDialog(Whiteboard.this);
      }
   }

   private void sendMessage(final SVGElement s) {
      if (participant.equals("Standalone")) {
         return;
      }
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            new MessageThread(s).start();
         }
      });
   }

   private class MessageThread extends Thread {
      private SVGElement element;

      public MessageThread(SVGElement element) {
         this.element = element;
      }

      public void run() {
         XMPPConnection con = SparkManager.getConnection();
         Message message = new Message();
         message.setFrom(con.getUser());
         message.setTo(participant);
         if (isGroup) {
            message.setType(Message.Type.groupchat);
         }
         message.addExtension(new SVGExtension(element.asXML()));
         con.sendPacket(message);
      }
   }
   
   private void sendEventNotification(final WhiteboardNotification.Event event) {
      if (participant.equals("Standalone")) {
         return;
      }
      
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            new EventNotificationThread(event).start();
         }
      });
   }

   private class EventNotificationThread extends Thread {
      private WhiteboardNotification.Event event;

      public EventNotificationThread(WhiteboardNotification.Event event) {
         this.event = event;
      }

      public void run() {
         XMPPConnection con = SparkManager.getConnection();
         WhiteboardNotification notification = new WhiteboardNotification();
         notification.setType(IQ.Type.SET);
         notification.setFrom(con.getUser());
         notification.setTo(participant);
         notification.setEvent(event);
         con.sendPacket(notification);
      }
   }

   public String id() {
      return (String.valueOf(System.currentTimeMillis()) + "/" + participant);
   }

   /**
    * Checks if the file name ends with the extension of the given filter
    * @param file
    * @param filter
    * @return
    */
   private File validateFileName(File file, FileFilter filter) {
      if (filter.accept(file)) {
         return file;
      }

      String fileName = file.getAbsolutePath();
      int index = fileName.lastIndexOf(".");
      if (index > 0) {
         fileName = fileName.substring(0, index);
      }

      String extension = filter.toString();
      return new File(fileName + "." + extension);
   }

}
