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
import java.awt.*;
import java.awt.event.*;

/**
 * @author Quickstone Technologies Limited
 */

public class ChatFrame extends JFrame {
  private JTextField nameField = new JTextField();
  private JTextArea chatArea = new JTextArea();
  private JTextField messageField = new JTextField();

  public ChatFrame() {
    //layout and init components
    Container contentPane = this.getContentPane();
    Box vbox1 = Box.createVerticalBox();
    Box hbox1 = Box.createHorizontalBox();
    //vbox1.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
    vbox1.add(hbox1);
    hbox1.add(new JLabel("Username:"));
    hbox1.add(Box.createHorizontalStrut(2));
    hbox1.add(nameField);
    hbox1.add(Box.createGlue());
    hbox1.add(Box.createHorizontalStrut(2));
    vbox1.add(Box.createVerticalStrut(8));
    vbox1.add(chatArea);
    chatArea.setPreferredSize(new Dimension(400,400));
    chatArea.setEditable(false);
    chatArea.setBorder(BorderFactory.createLoweredBevelBorder());
    vbox1.add(Box.createVerticalStrut(8));
    vbox1.add(messageField);
    messageField.setPreferredSize(new Dimension(400,40));
    messageField.setBorder(BorderFactory.createLoweredBevelBorder());
    contentPane.add(vbox1);

  }
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }
  public JTextField getNameTextField() {
    return nameField;
  }
  public JTextField getMessageTextField() {
    return messageField;
  }
  public JTextArea getChatTextArea() {
    return chatArea;
  }
}
