    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2008 Peter Welch and Paul Austin.            //
    //                2001-2004 Quickstone Technologies Limited.        //
    //                                                                  //
    //  This library is free software; you can redistribute it and/or   //
    //  modify it under the terms of the GNU Lesser General Public      //
    //  License as published by the Free Software Foundation; either    //
    //  version 2.1 of the License, or (at your option) any later       //
    //  version.                                                        //
    //                                                                  //
    //  This library is distributed in the hope that it will be         //
    //  useful, but WITHOUT ANY WARRANTY; without even the implied      //
    //  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR         //
    //  PURPOSE. See the GNU Lesser General Public License for more     //
    //  details.                                                        //
    //                                                                  //
    //  You should have received a copy of the GNU Lesser General       //
    //  Public License along with this library; if not, write to the    //
    //  Free Software Foundation, Inc., 59 Temple Place, Suite 330,     //
    //  Boston, MA 02111-1307, USA.                                     //
    //                                                                  //
    //  Author contact: P.H.Welch@kent.ac.uk                             //
    //                                                                  //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////


import javax.swing.*;
import org.jcsp.lang.*;

/**
 * @author Quickstone Technologies Limited
 */
public class MessageReceiverProcess implements CSProcess {
  private String user;
  private ChannelInput in;
  private JList userlist;
  private JTextArea chatlog;
  private DefaultListModel listmodel;

  public MessageReceiverProcess(String user, ChannelInput in, JList userlist, JTextArea chatlog) {
  this.user = user;
  this.in = in;
  this.userlist = userlist;
  this.chatlog = chatlog;
  listmodel = new DefaultListModel();
  userlist.setModel(listmodel);
  }

  public boolean updateRealtimeArea(String name, String message, boolean remove) {
    if (remove) {
      for (int x=0; x < listmodel.getSize(); x++ ) {
        UserInListObject uilo = (UserInListObject)listmodel.getElementAt(x);
        String s = uilo.getUserName();
        if (name.equals(s)) {
          listmodel.removeElementAt(x);
          return true;
        }
      }
    }
    for (int x= 0; x < listmodel.getSize(); x++) {
      UserInListObject uilo = (UserInListObject)listmodel.getElementAt(x);
      String s = uilo.getUserName();
      if (name.equals(s)) {
        uilo.setMessageText(message);
        userlist.update(userlist.getGraphics());
        return true;
      }
    }
    UserInListObject newUserObject = new UserInListObject(name,message);
    listmodel.addElement(newUserObject);
    return false;
  }

  public void run() {
    while (true) {
      MessageObject mess = (MessageObject)in.read();
      final String messageText = mess.message;
      final String messageUser = mess.user;
      final int sysCommand = mess.sysCommand;
      if (sysCommand != MessageObject.USERMESSAGE) {
        SwingUtilities.invokeLater(new Runnable() {
          final int command = sysCommand;
          public void run() {
            String s = chatlog.getText();
            if (s != null) {
              s = s + messageText.toUpperCase();
            }
            else {
              s = messageText.toUpperCase();
            }
            chatlog.setText(s);
            if (command == MessageObject.CONNECT) {
              if (!(user.equals(messageUser))) {
                MessageReceiverProcess.this.updateRealtimeArea(messageUser,"",false);
              }
            }
            else if (command == MessageObject.DISCONNECT) {

              if (!(user.equals(messageUser))) {
                MessageReceiverProcess.this.updateRealtimeArea(messageUser,"",true);
              }
            }

          }
        });
      }
      else {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            if (messageText.endsWith("\n")) {
              String s = chatlog.getText();
              if (s != null) {
                s = s + messageUser + ": " + messageText;
              }
              else {
                s = messageUser + ": " + messageText;
              }
              chatlog.setText(s);
              if (!(user.equals(messageUser))) {
                MessageReceiverProcess.this.updateRealtimeArea(messageUser,"",false);
              }
            }
            else {
              if (!(user.equals(messageUser))) {
                MessageReceiverProcess.this.updateRealtimeArea(messageUser,messageText,false);
              }
            }
          }
        });
      }
    }
  }
}
