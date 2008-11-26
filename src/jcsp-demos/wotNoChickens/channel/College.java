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
import org.jcsp.demos.util.*;

/**
 * @author P.H. Welch
 */
public class College {

  public static final String TITLE = "Wot No Chickens";
  public static final String DESCR =
  	"Shows the JCSP solution to the 'Wot, No Chickens?' problem.\n\n" +

  	"The College consists of 5 Philosophers, a Chef and the Canteen. All are " +
    "\"active\" objects. The Canteen ALTs between a service Channel, shared by " +
    "all the Philosophers, and a supply Channel from the Chef.  Upon acceptance " +
    "of a service request, chickens are dispensed through a delivery Channel.\n\n" +

    "Despite the greedy behaviour of Philosopher 0, nobody starves. The Canteen " +
    "guards the service Channel so that Philosophers cannot blunder in when there " +
    "are no chickens, but are held waiting in the service queue.";

  public static void main (String argv[]) {

  	Ask.app (TITLE, DESCR);
  	Ask.show ();
  	Ask.blank ();

    int n_philosophers = 5;

    final Any2OneChannelInt service = Channel.any2oneInt ();
    final One2OneChannelInt deliver = Channel.one2oneInt ();
    final One2OneChannelInt supply = Channel.one2oneInt ();

    final Phil[] phil = new Phil[n_philosophers];
    for (int i = 0; i < n_philosophers; i++) {
      phil[i] = new Phil (i, service.out (), deliver.in ());
    }

    new Parallel (
      new CSProcess[] {
        new Clock (),
        new Canteen (service.in (), deliver.out (), supply.in ()),
        new Chef (supply.out ()),
        new Parallel (phil)
      }
    ).run ();

  }

}
