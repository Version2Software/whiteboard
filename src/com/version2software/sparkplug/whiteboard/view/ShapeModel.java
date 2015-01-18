/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.view;

import com.version2software.sparkplug.whiteboard.SVGUtil;
import com.version2software.sparkplug.whiteboard.command.Attribute;
import com.version2software.sparkplug.whiteboard.command.Command;
import com.version2software.sparkplug.whiteboard.command.Configure;
import com.version2software.sparkplug.whiteboard.command.Order;
import com.version2software.sparkplug.whiteboard.command.Remove;
import com.version2software.sparkplug.whiteboard.command.SetBackground;
import com.version2software.sparkplug.whiteboard.shape.Circle;
import com.version2software.sparkplug.whiteboard.shape.Ellipse;
import com.version2software.sparkplug.whiteboard.shape.Line;
import com.version2software.sparkplug.whiteboard.shape.Path;
import com.version2software.sparkplug.whiteboard.shape.Polyshape;
import com.version2software.sparkplug.whiteboard.shape.Rectangle;
import com.version2software.sparkplug.whiteboard.shape.SVGImage;
import com.version2software.sparkplug.whiteboard.shape.Shape;
import com.version2software.sparkplug.whiteboard.shape.Text;
import com.version2software.sparkplug.whiteboard.undo.UndoAction;
import com.version2software.sparkplug.whiteboard.undo.UndoAdd;
import com.version2software.sparkplug.whiteboard.undo.UndoClearBackground;
import com.version2software.sparkplug.whiteboard.undo.UndoConfigure;
import com.version2software.sparkplug.whiteboard.undo.UndoOrder;
import com.version2software.sparkplug.whiteboard.undo.UndoRemove;
import com.version2software.sparkplug.whiteboard.undo.UndoSetBackground;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ShapeModel {

   private List<Shape> displayList = new CopyOnWriteArrayList<Shape>();
   private List<UndoAction> undoList = new CopyOnWriteArrayList<UndoAction>();
   private BufferedImage backgroundImage;

   public List<Shape> getDisplayList() {
      return displayList;
   }

   public BufferedImage getBackgroundImage() {
      return backgroundImage;
   }

   public void setBackgroundImage(BufferedImage backgroundImage) {
      this.backgroundImage = backgroundImage;
   }

   public int displayListSize() {
      return displayList.size();
   }

   public int undoListSize() {
      return undoList.size();
   }

   public Shape getShape(int i) {
      return displayList.get(i);
   }

   public boolean isDirty() {
      if (displayList.size() > 0) {
         return true;
      }

      if (backgroundImage != null) {
         return true;
      }

      //TODO what else do we need to check for?

      return false;
   }

   public void appendShape(Shape s) {
      appendUndo(new UndoAdd());
      displayList.add(s);
   }

   public void appendUndo(UndoAction undoAction) {
      undoList.add(undoAction);
   }

   public void appendUndoConfigure(Shape shape, String random) {
      appendUndo(new UndoConfigure(displayList.indexOf(shape), shape.copy(), random));
   }

   public void undo(Whiteboard whiteboard) {
      if (undoList.size() > 0) {
         int lastIndex = undoList.size() - 1;
         UndoAction undo = undoList.remove(lastIndex);
         undo.execute(whiteboard);
      }
   }

   public void processClearBackground() {
      appendUndo(new UndoClearBackground(backgroundImage));
      setBackgroundImage(null);
   }

   public void processSetBackground(SetBackground command) {
      appendUndo(new UndoSetBackground());
      setBackgroundImage(command.getImage());
   }

   public void processClearAll() {
      undoList.clear();
      displayList.clear();
      setBackgroundImage(null);
   }

   public void processConfigure(Command command) {

      Configure configure = (Configure) command;

      for(UndoAction ua : undoList) {
         if (configure.getRandom().equals(ua.getRandom())) {
            return;
         }
      }

      List<Attribute> list = configure.getAttributes();
      for (Shape s : displayList) {
         // find the target
         if (s.getId().equals(configure.getTarget())) {
            appendUndo(new UndoConfigure(displayList.indexOf(s), s.copy(), configure.getRandom()));
            for (Attribute a : list) {
               if (a.getName().equals("opacity")) {
                  s.setOpacity(Float.parseFloat(a.getValue()));
               } else if (a.getName().equals("color")) {
                  s.setColor(SVGUtil.getColor(a.getValue()));
               } else if (a.getName().equals("font-size")) {
                  Text text = (Text) s;
                  text.setSize(Integer.parseInt(a.getValue()));
               }

               if (s instanceof Rectangle) {
                  Rectangle r = (Rectangle) s;
                  if (a.getName().equals("x")) {
                     r.setX(Double.parseDouble(a.getValue()));
                  } else if (a.getName().equals("y")) {
                     r.setY(Double.parseDouble(a.getValue()));
                  } else if (a.getName().equals("width")) {
                     r.setWidth(Double.parseDouble(a.getValue()));
                  } else if (a.getName().equals("height")) {
                     r.setHeight(Double.parseDouble(a.getValue()));
                  }
               } else if (s instanceof Line) {
                  Line line = (Line) s;
                  if (a.getName().equals("x1")) {
                     line.setX(Double.parseDouble(a.getValue()));
                  } else if (a.getName().equals("y1")) {
                     line.setY(Double.parseDouble(a.getValue()));
                  } else if (a.getName().equals("x2")) {
                     line.setxEnd(Double.parseDouble(a.getValue()));
                  } else if (a.getName().equals("y2")) {
                     line.setyEnd(Double.parseDouble(a.getValue()));
                  }
               } else if (s instanceof Circle) {
                  Circle c = (Circle) s;
                  if (a.getName().equals("cx")) {
                     c.setCx(Double.parseDouble(a.getValue()));
                  } else if (a.getName().equals("cy")) {
                     c.setCy(Double.parseDouble(a.getValue()));
                  } else if (a.getName().equals("r")) {
                     c.setR(Double.parseDouble(a.getValue()));
                  }
               } else if (s instanceof Ellipse) {
                  Ellipse c = (Ellipse) s;
                  if (a.getName().equals("cx")) {
                     c.setCx(Double.parseDouble(a.getValue()));
                  } else if (a.getName().equals("cy")) {
                     c.setCy(Double.parseDouble(a.getValue()));
                  } else if (a.getName().equals("rx")) {
                     c.setRx(Double.parseDouble(a.getValue()));
                  } else if (a.getName().equals("ry")) {
                     c.setRy(Double.parseDouble(a.getValue()));
                  }
               } else if (s instanceof Text) {
                  Text text = (Text) s;
                  if (a.getName().equals("x")) {
                     text.setX(Double.parseDouble(a.getValue()));
                  } else if (a.getName().equals("y")) {
                     text.setY(Double.parseDouble(a.getValue()));
                  }
               } else if (s instanceof SVGImage) {
                  SVGImage image = (SVGImage) s;
                  if (a.getName().equals("x")) {
                     image.setX(Double.parseDouble(a.getValue()));
                  } else if (a.getName().equals("y")) {
                     image.setY(Double.parseDouble(a.getValue()));
                  } else if (a.getName().equals("width")) {
                     image.setWidth(Double.parseDouble(a.getValue()));
                  } else if (a.getName().equals("height")) {
                     image.setHeight(Double.parseDouble(a.getValue()));
                  }
               } else if (s instanceof Path) {
                  Path path = (Path) s;
                  if (a.getName().equals("d")) {
                     path.replacePath(a.getValue());
                  }
               } else if (s instanceof Polyshape) {
                  Polyshape poly = (Polyshape) s;
                  if (a.getName().equals("points")) {
                     poly.replacePoints(a.getValue());
                  }
               }
            }
            // finished with target
            break;
         }
      }
   }

   public boolean processOrder(Command command) {
      Order sb = (Order) command;

      for (Shape s : displayList) {
         if (s.getId().equals(sb.getTarget())) {

            if (sb.getPosition().equals("back")) {
               appendUndo(new UndoOrder(s, "front"));
               displayList.remove(s);
               displayList.add(0, s);
            } else if (sb.getPosition().equals("back-one")) {
               int pos = displayList.indexOf(s);
               if (pos > 0) {
                  appendUndo(new UndoOrder(s, "front-one"));
                  displayList.remove(s);
                  displayList.add(pos - 1, s);
               }
            } else if (sb.getPosition().equals("front")) {
               appendUndo(new UndoOrder(s, "back"));
               displayList.remove(s);
               displayList.add(s);
            } else if (sb.getPosition().equals("front-one")) {
               int pos = displayList.indexOf(s);
               if (pos < displayList.size() - 1) {
                  appendUndo(new UndoOrder(s, "back-one"));
                  displayList.remove(s);
                  displayList.add(pos + 1, s);
               }
            }

            return true;
         }
      }
      return false;
   }

   public void processRemove(Remove remove) {
      for (Shape s : displayList) {
         if (s.getId().equals(remove.getTarget())) {
            appendUndo(new UndoRemove(s));
            displayList.remove(s);
            break;
         }
      }
   }

   public void deselect() {
      for (Shape shape : displayList) {
         shape.setSelected(false);
      }
   }
}
