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


import org.jcsp.lang.*;
import org.jcsp.net.*;
import java.util.*;


/**
 * @author Quickstone Technologies Limited
 */

public class ConnectionAuthenticator implements CSProcess {
  private ChannelInput auth;
  private ChannelOutput out;
  private ChannelOutput back;
  private NetAltingChannelInput messageChan;
  private ArrayList users = new ArrayList();
 
  public ConnectionAuthenticator(NetAltingChannelInput auth, ChannelOutput out, NetAltingChannelInput messageChan) {
    this.auth = auth;
    this.out = out;
    this.messageChan = messageChan;

  }

  public void run() {
    while (true) {
      Object o = auth.read();
      System.out.println("connectAuth got "+ o);
      if (o instanceof ConnectionBundle) {
        String newUser = ((ConnectionBundle)o).getUser();
        if (((ConnectionBundle)o).connect()) { //connecting
          if (users.contains(newUser)) {
            ((ConnectionBundle)o).getReturnChan().write(Boolean.FALSE);
            //auth.close(Boolean.FALSE);
          }
          else {
            users.add(newUser);
            System.out.println("added user " + newUser);
            out.write(o);
            //auth.close(Boolean.TRUE);
						((ConnectionBundle)o).getReturnChan().write(messageChan.getChannelLocation());
          }
        }
        else { //disconnecting
          out.write(o);
        }        
      }
      else if (o instanceof ChannelOutput) {
        System.out.println("outputting to dynamic delta to disconnect");
        out.write(o);
      }
      else {
        System.out.println("uh-oh - not a string or ChannelOutput received by ConnectionAuthenticator");
      }
    }
  }
}
