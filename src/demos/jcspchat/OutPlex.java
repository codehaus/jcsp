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

/**
 * @author Quickstone Technologies Limited
 */
public class OutPlex implements CSProcess {
  private AltingChannelInput usernameIn;
  private AltingChannelInput messageIn;
  private ChannelOutput out;
  private String username = "Anon";

  public OutPlex(AltingChannelInput usernameIn, AltingChannelInput messageIn, ChannelOutput out) {
    this.usernameIn = usernameIn;
    this.messageIn = messageIn;
    this.out = out;

  }
  public void run() {
    final AltingChannelInput[] altChans = { usernameIn, messageIn};
    final Alternative alt = new Alternative (altChans);
    String output;

    while (true) {
      switch (alt.select()) {
        case 0:
          username = (String)usernameIn.read();

        break;
        case 1:
          output = username + ": " + (String)messageIn.read() + "\n";
//          System.out.println("OutPlex: sending message = " + output);
          out.write(output);
        break;
      }
    }
  }
}
