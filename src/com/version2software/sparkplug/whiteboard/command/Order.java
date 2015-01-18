/**
 * Copyright (C) 2009 Version 2 Software, LLC. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard.command;

/**
 * A Command that indicates in what order (front/back) a shape should be in.
 */
public class Order extends Command {
   private String target;
   private String position;
   
   /**
    * Creates a new Order Command.
    * 
    * @param target the id of the item that will be reordered.
    * @param position the display order argument: back, back-one, front, front-one
    */
   public Order(String target, String position) {
      this.target = target;
      this.position = position;
   }
   
   /**
    * @return an xml element formatted as "<order target="123" position="back"/>" where the target
    * element is the id of the item to be reordered.
    */
   public String asXML() {
      String s = "<order target=\"#t\" position=\"#p\"/>";
      
      return s.replaceAll("#t", target).replaceAll("#p", position);
    }

   /**
    * @return target
    */
   public String getTarget() {
      return target;
   }
   
   /**
    * @return position
    */
   public String getPosition() {
      return position;
   }
}
