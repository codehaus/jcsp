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
public class CallCollege implements CSProcess {

  public static final String TITLE = "Wot No Chickens [call channels]";
  public static final String DESCR =
  	"Shows the JCSP solution to the 'Wot, No Chickens?' problem using call channels.\n\n" +

  	"The College consists of 5 Philosophers, three Chef and the Canteen. All are " +
    "\"active\" objects. The Canteen ALTs between a service Channel, shared by " +
    "all the Philosophers, and a supply Channel from the Chef.  Upon acceptance " +
    "of a service request, chickens are dispensed through a delivery Channel.\n\n" +

    "Despite the greedy behaviour of Philosopher 0, nobody starves. The Canteen " +
    "guards the service Channel so that Philosophers cannot blunder in when there " +
    "are no chickens, but are held waiting in the service queue.";

  public void run () {

  	Ask.app (TITLE, DESCR);
  	Ask.show ();
  	Ask.blank ();

    final String[] philId = {"Bill", "Hilary", "Gennifer", "Paula", "Monica"};
    // final int nPhilosophers = 5;

    final int thinkTime = 3000;
    final int eatTime = 100;

    final int serviceTime = 0;
    final int supplyTime = 3000;
    final int maxChickens = 50;

    final CallCanteen.Any2OneServiceChannel service = new CallCanteen.Any2OneServiceChannel ();
    // final CallCanteen.One2OneSupplyChannel supply = new CallCanteen.One2OneSupplyChannel ();
    final CallCanteen.Any2OneSupplyChannel supply = new CallCanteen.Any2OneSupplyChannel ();

    // final CallCanteen canteen = new CallCanteen (serviceTime, supplyTime);

    final CallPhil[] phils = new CallPhil[philId.length];
    for (int i = 0; i < phils.length; i++) {
      // String philId = new Integer (i).toString ();
      phils[i] = new CallPhil (philId[i], service, thinkTime, eatTime, i == 0);
      // phils[i] = new CallPhil (i, canteen.service, thinkTime, eatTime);
    }

    new Parallel (
      new CSProcess[] {
        new CallClock (),
        new CallCanteen (service, supply, serviceTime, supplyTime, maxChickens),
        // canteen,
        new Parallel (phils),
        new CallChef ("Pierre", 4, 2000, supply),       // chefId, batchSize, batchTime
        new CallChef ("Henri", 10, 20000, supply),      // chefId, batchSize, batchTime
        new CallChef ("Sid", 100, 150000, supply)       // chefId, batchSize, batchTime
        // new CallChef (canteen.supply),
      }
    ).run ();

  }

  public static void main (String argv[]) {
    new CallCollege ().run ();
  }

}
