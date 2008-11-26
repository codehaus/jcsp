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
import java.util.*;

/**
 * @author Quickstone Technologies Limited
 */
public class SorterProcess implements CSProcess{
  private ChannelInput in;
  private ChannelOutput whiteboardOut;
  private ChannelOutput chatOut;
  public SorterProcess(ChannelInput in, ChannelOutput whiteboardOut, ChannelOutput chatOut) {
    this.in = in;
    this.whiteboardOut = whiteboardOut;
    this.chatOut = chatOut;
  }
  public void run () {
    while (true) {
      Object o = in.read();
      //System.out.println("Sorter: received data");
      if (o instanceof ArrayList && ((ArrayList)o).size() != 0) {
        ArrayList al = (ArrayList)o;
        for (int x=0; x < al.size(); x++ ) {
          Object o2 = al.get(x);
          if (o2 instanceof WhiteboardDataBundle) {
            whiteboardOut.write(o2);
          }
          else {
            chatOut.write(o2);
          }
        }
      }
      else {
        System.out.println("uh-oh - somethings awry... :( ");
      }
    }
  }
}
