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


//|
//| This program shows a use of the ALT mechanism.  It is a re-implementation
//| of the Starving Philosophers example but now has the Canteen programmed
//| properly as an active process.  Here is the College:
//|
//|
//|      0   1   2   3   4
//|      :)  :)  :)  :)  :)      ___________             ________
//|      |   |   |   |   |       |         |             |      |
//|    ---------------------<->--| Canteen |------<------| Cook |
//|       service/deliver        |_________|    supply   |______|
//|
//|
//|
//| This time, although Philsopher 0 is just as greedy, no one starves.
//|

import org.jcsp.lang.*;

/**
 * @author P.H. Welch
 */
class Canteen implements CSProcess {

  //The Canteen is an active object -- a pure SERVER process for its `supply'
  //and `service'/`deliver' Channels, giving priority to the former.
  //
  //Philosphers eat chickens.  They queue up at the Canteen on its `service'
  //Channel.  They only get served when chickens are available -- otherwise,
  //they just have to wait.  Once they have got `service', they are dispensed
  //a chicken down the `deliver' Channel.
  //
  //The Chef cooks chickens.  When a batch ready is ready, he/she queues up at
  //the Canteen on its `supply' Channel.  Setting down the batch takes around
  //3 seconds and the Chef is made to hang about this has happened.

  private final AltingChannelInputInt service;    // shared from all Philosphers (any-1)
  private final ChannelOutputInt deliver;         // shared to all Philosphers (but only used 1-1)
  private final AltingChannelInputInt supply;     // from the Chef (1-1)

  public Canteen (AltingChannelInputInt service, ChannelOutputInt deliver,
                  AltingChannelInputInt supply) {
    this.service = service;
    this.deliver = deliver;
    this.supply = supply;
  }

  public void run () {

    final Alternative alt = new Alternative (new Guard[] {supply, service});
    final boolean[] precondition = {true, false};
    final int SUPPLY = 0;
    final int SERVICE = 1;

    final CSTimer tim = new CSTimer ();

    int nChickens = 0;

    System.out.println ("            Canteen : starting ... ");
    while (true) {
      precondition[SERVICE] = (nChickens > 0);
      switch (alt.fairSelect (precondition)) {
        case SUPPLY:
          int value = supply.read ();        // new batch of chickens from the Chef
          System.out.println ("            Canteen : ouch ... make room ... this dish is very hot ... ");
          tim.after (tim.read () + 3000);   // this takes 3 seconds to put down
          nChickens += value;
          System.out.println ("            Canteen : more chickens ... " +
                               nChickens + " now available ... ");
          supply.read ();                   // let the Chef get back to cooking
        break;
        case SERVICE:
          service.read ();                  // Philosopher wants a chicken
          System.out.println ("      Canteen : one chicken coming down ... " +
                               (nChickens - 1) + " left ... ");
          deliver.write (1);             // serve one chicken
          nChickens--;
        break;
       }
    }
  }

}
