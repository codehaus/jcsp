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


import java.awt.*;
import javax.swing.*;
import org.jcsp.lang.*;
import java.util.*;
import org.jcsp.net.*;
import org.jcsp.net.tcpip.*;
import org.jcsp.net.cns.*;

/**
 * @author Quickstone Technologies Limited
 */

public class ChatPlusClientMain {

  private boolean cnsConnected = false;

  public ChatPlusClientMain() {
  }
  public static void main(String[] args) {

    ConnectDialog cd = new ConnectDialog();
    String cnsName = cd.getCNSName();

    try {
      Node.getInstance().init(new TCPIPNodeFactory(cnsName));
    }
    catch (Exception e) {
      System.out.println("Error - could not connect to server: " + e);
      System.exit(-1);
    }

    String channelName = cd.getChannelName();
    String username = cd.getUsername();
    String connectName = channelName + ".client2serverconnect";    

    //setup channels
    ChannelOutput messageOutChan;

    NetChannelOutput connectChan = CNS.createOne2Net(connectName);
    NetChannelInput messageInChan = NetChannelEnd.createNet2One();

    NetChannelOutput serverOutChan =
      NetChannelEnd.createOne2Net(messageInChan.getChannelLocation());
    One2OneChannel sorter2WhiteboardChan = Channel.one2one();
    One2OneChannel sorter2ChatChan = Channel.one2one();
    Any2OneChannel output2BufferChan = Channel.any2one();
    DrawingSettings ds =
      new DrawingSettings(Color.blue, Color.orange, true, 3, 1);
    ChatPlusFrame cpf = new ChatPlusFrame(ds);
    One2OneChannel buffer2IdChan = Channel.one2one();
    One2OneChannelInt id2BufferChan = Channel.one2oneInt();
    One2OneChannelInt localRedrawNotifyChan = Channel.one2oneInt();

    connectChan.write(new ConnectionBundle(username, serverOutChan, true));
    Object o = messageInChan.read();

    messageOutChan = null;
    if (o instanceof NetChannelLocation) {
      messageOutChan = NetChannelEnd.createAny2Net((NetChannelLocation) o);
    }
    else {
      Boolean b = (Boolean) o;
      if (b.booleanValue()) {
        System.out.println("client: received confirm");
      }
      else {
        JOptionPane.showMessageDialog(
          new JFrame(),
          "The username "
            + username
            + " is already in use.\n"
            + "Please try a different name");
        System.exit(0);
      }
    }

    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    cpf.setSize(d.width, d.height / 2);
    cpf.setVisible(true);

    cpf.initComponents();
    cpf.validate();
    ArrayList al = new ArrayList(2);
    al.add(
      new MessageObject(
        username,
        username + " connected.\n",
        MessageObject.CONNECT));

    messageOutChan.write(al);

    CSProcess wsp =
      new WhiteboardSendProcess(
        ds,
        cpf.getWhiteboard(),
        cpf.getWipeButton(),
        cpf.getWhiteboardScrollPane(),
        cpf.getToolMenu(),
        cpf,
        output2BufferChan.out(),
        localRedrawNotifyChan.in(),
        username,
        connectChan,
        serverOutChan);

    new Parallel(
      new CSProcess[] {
        new SorterProcess(
          messageInChan,
          sorter2WhiteboardChan.out(),
          sorter2ChatChan.out()),
        new JTextFieldProcessPlus(
          cpf.getMessageArea(),
          output2BufferChan.out(),
          username),
        new MessageReceiverProcess(
          username,
          sorter2ChatChan.in(),
          cpf.getUserArea(),
          cpf.getChatLog()),
        new CoalescingBuffer(
          output2BufferChan.in(),
          buffer2IdChan.out(),
          id2BufferChan.in()),
        new ReadyReportingIdentity(
          buffer2IdChan.in(),
          messageOutChan,
          id2BufferChan.out()),
        wsp,
        new WhiteboardReceiveProcess(
          cpf.getWhiteboard(),
          sorter2WhiteboardChan.in(),
          localRedrawNotifyChan.out(),
          username)})
      .run();

  }
}
