/**
 * Copyright (C) 2006-2009 Version 2 Software, LLC. All rights reserved.
 * 
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package com.version2software.sparkplug.whiteboard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.PluginManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

import com.version2software.sparkplug.whiteboard.extension.SVGExtension;
import com.version2software.sparkplug.whiteboard.extension.SVGExtensionProvider;
import com.version2software.sparkplug.whiteboard.images.ImageLoader;
import com.version2software.sparkplug.whiteboard.query.WhiteboardInvitation;
import com.version2software.sparkplug.whiteboard.query.WhiteboardNotification;
import com.version2software.sparkplug.whiteboard.view.Whiteboard;

/**
 * Adds per-to-per whiteboard functionality to Spark. The sparkplug is based loosely on the following
 * specifications:
 * JEP-xxxx: An SVG Based Whiteboard Format (http://www.xmpp.org/extensions/inbox/whiteboard.html)
 * Scalable Vector Graphics (SVG) Tiny 1.2 Specification (http://www.w3.org/TR/SVGMobile12/index.html)
 * 
 * Icons are from the OpenOffice project (http://www.novell.com/coolsolutions/feature/1637.html)
 */
public class WhiteboardPlugin implements Plugin {
   private static Workspace workspace;
   private XMPPConnection connection;
   
   private Map<String, Whiteboard> whiteboardMap = new ConcurrentHashMap<String, Whiteboard>();
   
   private SVGPacketListener svgPacketListener = new SVGPacketListener();
   
   private WhiteboardInvitationListener invitationListener = new WhiteboardInvitationListener();
   private WhiteboardNotificationListener notificationListener = new WhiteboardNotificationListener();

   public void initialize() {
      workspace = SparkManager.getWorkspace();
      connection = SparkManager.getConnection();

      ProviderManager manager = ProviderManager.getInstance();
      manager.addExtensionProvider(SVGConstants.ELEMENT_NAME, SVGConstants.NAMESPACE, new SVGExtensionProvider());

      connection.addPacketListener(svgPacketListener, new PacketExtensionFilter(SVGConstants.ELEMENT_NAME, SVGConstants.NAMESPACE));

      connection.addPacketListener(invitationListener, new PacketTypeFilter(WhiteboardInvitation.class));
      manager.addIQProvider(WhiteboardInvitation.ELEMENT_NAME, SVGConstants.NAMESPACE, new WhiteboardInvitation.Provider());
      
      connection.addPacketListener(notificationListener, new PacketTypeFilter(WhiteboardNotification.class));
      manager.addIQProvider(WhiteboardNotification.ELEMENT_NAME, SVGConstants.NAMESPACE, new WhiteboardNotification.Provider());

      addChatRoomWhiteboardButton();
      addContactListListener();
      
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            new CheckForNewVersion().start();
         }
      });
   }

   public void shutdown() {
      ProviderManager manager = ProviderManager.getInstance();
      manager.removeExtensionProvider(SVGConstants.ELEMENT_NAME, SVGConstants.NAMESPACE);
      manager.removeIQProvider(WhiteboardInvitation.ELEMENT_NAME, SVGConstants.NAMESPACE);
      manager.removeIQProvider(WhiteboardNotification.ELEMENT_NAME, SVGConstants.NAMESPACE);
      
      if (connection.isConnected()) {
         connection.removePacketListener(svgPacketListener);
         connection.removePacketListener(invitationListener);
         connection.removePacketListener(notificationListener);
      }
      
      svgPacketListener = null;
      invitationListener = null;
      notificationListener = null;
      
      whiteboardMap = null;
      workspace = null;
   }

   public boolean canShutDown() {
      return true;
   }

   public void uninstall() {
   }
   
   /**
    * Reads the plugin.xml file and returns the value of <version> element or null if the file cannot be found/read.
    * 
    * @return the version of the currently installed V2Whiteboard
    */
   public static String getVersion() {
      String version = null;
      
      try {
         File pluginXML;
         
         if (workspace == null) { //running in "standalone" mode
            pluginXML = new File("plugin.xml").getAbsoluteFile();
         }
         else {
            pluginXML = new File(PluginManager.PLUGINS_DIRECTORY, "whiteboard/plugin.xml").getAbsoluteFile();
         }
         
         SAXBuilder builder = new SAXBuilder(); 
         Document doc = builder.build(pluginXML);
         Element root = doc.getRootElement(); 
         version = root.getChild("version").getText();
      } catch (Exception e) {
         Log.error("Unable to get WhiteboardPlugin version", e);
      }
      
      return version;
   }
   
   private void startWorker(final String jid, final boolean isGroup) {
      SwingWorker worker = new SwingWorker() {
         public Object construct() {
            return doWork(jid, isGroup);
         }

         public void finished() {
         }
      };
      worker.start();
   }

   private Object doWork(final String jid, final boolean isGroup) {
      String from = isGroup ? StringUtils.parseBareAddress(jid) : jid;
      
      Whiteboard frame;
      if (whiteboardMap.containsKey(from)) {
         frame = whiteboardMap.get(from);
      }
      else {
         frame = new Whiteboard(from, isGroup);
         whiteboardMap.put(from, frame);
      }
      frame.setVisible(true);

      return this;
   }
   
   private void showWhiteboardInvite(final String invitee) {
      int selection = JOptionPane.showConfirmDialog(SparkManager.getFocusedComponent(), 
               "Would you like to start a whiteboard session with " + invitee + "?", 
               "Invitation", 
               JOptionPane.YES_NO_OPTION);

      if (selection == JOptionPane.YES_OPTION) {
         final WhiteboardInvitation invite = new WhiteboardInvitation();
         invite.setTo(invitee);

         connection.addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
               WhiteboardInvitation inviteReply = ((WhiteboardInvitation) packet);

               if (inviteReply.getType() == IQ.Type.RESULT) {
                  if (inviteReply.getAccept()) {
                     startWorker(invitee, false);
                  }
                  else {
                     JOptionPane.showMessageDialog(SparkManager.getFocusedComponent(), 
                              inviteReply.getFrom() + " has declined your offer to share a whiteboard.");
                  }
               }
            }
         }, new PacketIDFilter(invite.getPacketID()));

         connection.sendPacket(invite);
      }
   }

   /**
    * Listens for a PacketExtensions with an element name of SVGConstants.ELEMENT_NAME and a namespace of SVGConstants.NAMESPACE
    * and maps the packet to the appropriate Whiteboard based on the packet sender. In the case of a standard chat, a whiteboard 
    * invitation will need to be already sent and accepted for the packet to be processed. During a groupchat, if a whiteboard does 
    * not exist a new Whiteboard will be created and the packet will be processed. It is important to note that normal chats are
    * mapped to a full jid (including a resource) while groupchats do not include the resource.
    */
   private class SVGPacketListener implements PacketListener {
      public void processPacket(Packet packet) {
         SVGExtension extension = (SVGExtension) packet.getExtension(SVGConstants.ELEMENT_NAME, SVGConstants.NAMESPACE);

         Message m = (Message) packet;
         
         if (m.getType().equals(Message.Type.groupchat)) {
            String from = StringUtils.parseBareAddress(packet.getFrom());
            Whiteboard whiteboard = whiteboardMap.get(from);
            if (whiteboard == null) {
               whiteboard = new Whiteboard(from, true);
               whiteboardMap.put(from, whiteboard);
            }
         }
         
         Whiteboard whiteboard = m.getType().equals(Message.Type.groupchat) ? 
                  whiteboardMap.get(StringUtils.parseBareAddress(packet.getFrom())) : whiteboardMap.get(packet.getFrom());
         if (whiteboard != null) {
            try {
               whiteboard.processSVGElement(extension.getSVGElement());
            }
            catch (Exception e) {
               Log.error(e);
            }
         }
      }
   }
   
   private class WhiteboardInvitationListener implements PacketListener {
      public void processPacket(Packet packet) {
         if (packet == null) {
            return;
         }
         
         WhiteboardInvitation invite = (WhiteboardInvitation) packet;
         
         if (invite.getType() == IQ.Type.GET) {
            int selection = JOptionPane.showConfirmDialog(SparkManager.getFocusedComponent(), 
                     "Would you like to share a whiteboard with " + invite.getFrom() + "?", 
                     "Invitation", 
                     JOptionPane.YES_NO_OPTION);

            invite.setType(IQ.Type.RESULT);
            invite.setTo(invite.getFrom());
            
            if (selection == JOptionPane.YES_OPTION) {
               startWorker(invite.getFrom(), false);
               invite.setAccept(true);
            }
            
            connection.sendPacket(invite);
         }
      }
   }
   
   private class WhiteboardNotificationListener implements PacketListener {
      public void processPacket(Packet packet) {
         if (packet == null) {
            return;
         }
         
         WhiteboardNotification notification = (WhiteboardNotification) packet;
         Whiteboard whiteboard = whiteboardMap.get(notification.getFrom());
         if (whiteboard != null) {
            if (notification.getEvent() == WhiteboardNotification.Event.EXIT) {
               int selection = JOptionPane.showConfirmDialog(whiteboard, 
                        notification.getFrom() + " just closed the whiteboard you were working on.\n" +
                        "Would you like to continue to work in standalone mode?", 
                        "Continue working?", 
                        JOptionPane.YES_NO_OPTION);
               
               if (selection == JOptionPane.YES_OPTION) {
                  whiteboard.setPartcipant("Standalone");
               }
               
               if (selection == JOptionPane.NO_OPTION) {
                  whiteboard.dispose();
               }
               
               whiteboardMap.remove(notification.getFrom());
            }
            else {
               whiteboard.processNotification(notification);
            }
         }
      }
   }

   /**
    * Adds a whiteboard button to the chat room.
    */
   private void addChatRoomWhiteboardButton() {
      ChatManager chatManager = SparkManager.getChatManager();
      
      chatManager.addChatRoomListener(new ChatRoomListenerAdapter() {
         public void chatRoomOpened(final ChatRoom room) {
            ChatRoomButton button = new ChatRoomButton(ImageLoader.getImageIcon("stock_new-drawing-24.png"));
            button.setToolTipText("Share a whiteboard");
            
            room.getToolBar().addChatRoomButton(button);
            button.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  if (room.getChatType().equals(Message.Type.groupchat)) {
                     startWorker(room.getRoomname(), true);
                  }
                  
                  if (room.getChatType().equals(Message.Type.chat)) {
                     showWhiteboardInvite(((ChatRoomImpl) room).getJID());
                  }
               }
            });
         }

         public void chatRoomLeft(ChatRoom room) {
         }
      });
   }

//sample of how to add a Component next to the chat transcript
//ChatManager chatManager = SparkManager.getChatManager();
//chatManager.addChatRoomListener(new ChatRoomListenerAdapter() {
//    public void chatRoomOpened(final ChatRoom room) {
//        room.getChatPanel().add(new JTextField("Hello"), new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
//    }
//});

   /**
    * Adds a whiteboard context menuitem to each contact listed in the roster.
    */
   private void addContactListListener() {
      final ContactList contactList = workspace.getContactList();

      final Action whiteboardAction = new AbstractAction() {
         public void actionPerformed(ActionEvent actionEvent) {
            Iterator contactListIter = contactList.getSelectedUsers().iterator();
            if (contactListIter.hasNext()) {
               ContactItem contactItem = (ContactItem) contactListIter.next();
               //we need to get the contact's jid that includes their resource so we can send them the invitation iq packet
               showWhiteboardInvite(PresenceManager.getPresence(contactItem.getJID()).getFrom());
            }
         }
      };

      whiteboardAction.putValue(Action.NAME, "Whiteboard");
      whiteboardAction.putValue("SmallIcon", ImageLoader.getImageIcon("stock_new-drawing-16.png")); 

      contactList.addContextMenuListener(new ContextMenuListener() {
         public void poppingUp(Object object, JPopupMenu popupMenu) {
            if (object instanceof ContactItem) {
               whiteboardAction.setEnabled(((ContactItem) object).isAvailable());
               popupMenu.add(whiteboardAction);
            }
         }

         public void poppingDown(JPopupMenu popup) {
            //not used
         }

         public boolean handleDefaultAction(MouseEvent e) {
            return false;
         }
      });
   }
   
   private class CheckForNewVersion extends Thread {
      public void run() {
         try {
            SAXBuilder builder = new SAXBuilder(); 
            Document doc = builder.build(new URL("http://www.version2software.com/downloads/whiteboard/version.xml"));
            Element root = doc.getRootElement(); 
            String currentVersion = root.getChild("currentVersion").getText();
            String pluginVersion = getVersion();
            
            if (pluginVersion != null && pluginVersion.compareTo(currentVersion) < 0) {
               String msg = "<html>A new version of V2Whiteboard is available. You can download it from<br>" +
                     "<a href=\"http://www.version2software.com/v2whiteboard.html\">http://www.version2software.com/v2whiteboard.html</a></html>";
               JOptionPane.showMessageDialog(SparkManager.getFocusedComponent(), msg, "New Version", JOptionPane.INFORMATION_MESSAGE);
            }
         }
         catch (Exception e) {
            Log.error("Unable to get latest WhiteboardPlugin version", e);
         }
      }
   }
}
