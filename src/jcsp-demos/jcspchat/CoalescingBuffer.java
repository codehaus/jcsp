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

public class CoalescingBuffer implements CSProcess {
  private ChannelOutput dataOut;
  private Guard[] inputArray;
  private ArrayList daList = new ArrayList();
  private int i;

  public CoalescingBuffer(AltingChannelInput dataIn, ChannelOutput dataOut,AltingChannelInputInt readyIn) {
    inputArray = new Guard[] {readyIn, dataIn};
    this.dataOut = dataOut;
  }

  public void run() {
    final Alternative alt = new Alternative(inputArray);
    boolean ready = false;
    while (true) {
      switch(alt.fairSelect()) {
        case 0:
          //System.out.println("buffer: chose report chan");
          i = ((AltingChannelInputInt)inputArray[0]).read();
          if (daList.size() > 0) {
            //System.out.println("buffer: stuff to send immediately - sending");
            daList.trimToSize();
            //System.out.println("buffer: list trimmed to " + daList.size());
            dataOut.write(daList);
            //System.out.println("buffer: sent data");
            daList = new ArrayList();
          }
          else {
            ready = true;
          }

          break;
        case 1:
          //System.out.println("buffer: chose message chan");
          Object o = ((AltingChannelInput)inputArray[1]).read();
          //System.out.println("buffer: received data - adding to list");
          daList.add(o);
          if (ready) {
            //System.out.println("buffer: ready - sending");
            daList.trimToSize();
            dataOut.write(daList);
            daList = new ArrayList();
            ready = false;
          }
          break;
      }
    }
  }
}
