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
public class StarveCollege implements CSProcess {

  public static final String TITLE = "Wot No Chickens [starving]";
  public static final String DESCR =
  	"Shows the 'Wot, No Chickens?' problem with process starvation.\n\n" +

  	"The College consists of 5 Philosophers, a Chef and the Canteen. The chef and the philosphers " +
  	"are active processes that call methods on the passive canteen. The JCSP solution to this problem " +
  	"makes the canteen active and able to hold the chef and philosophers in a queue. This one tells " +
  	"the processes to wait and try again later if it cannot deal with them leading to possible starvation " +
  	"because the place in the queue is lost.";

  public void run () {

  	Ask.app (TITLE, DESCR);
  	Ask.show ();
  	Ask.blank ();

    final String[] philId = {"Bill", "Hilary", "Gennifer", "Paula", "Monica"};
    // final int nPhilosophers = 5;

    final int thinkTime = 3000;
    final int eatTime = 100;
    final int waitTime = 3000;

    final int serviceTime = 0;
    final int supplyTime = 3000;
    final int maxChickens = 50;

    final StarveCanteen canteen = new StarveCanteen (serviceTime, supplyTime, maxChickens);

    final StarvePhil[] phils = new StarvePhil[philId.length];
    for (int i = 0; i < phils.length; i++) {
      // String philId = new Integer (i).toString ();
      phils[i] = new StarvePhil (philId[i], canteen.getService (), thinkTime, eatTime, waitTime, i == 0);
      // phils[i] = new CallPhil (i, canteen.service, thinkTime, eatTime);
    }

    new Parallel (
      new CSProcess[] {
        new StarveClock (),
        // canteen,
        new Parallel (phils),
        new StarveChef ("Pierre", 4, 2000, canteen.getSupply ()),  // chefId, batchSize, batchTime
        //new StarveChef ("Henri", 10, 20000, canteen.getSupply ()), // chefId, batchSize, batchTime
        //new StarveChef ("Sid", 100, 150000, canteen.getSupply ())  // chefId, batchSize, batchTime
        // new CallChef (canteen.supply),
      }
    ).run ();

  }

  public static void main (String argv[]) {
    new StarveCollege ().run ();
  }

}
