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
//|           service            |_________|    supply   |______|
//|
//|
//|
//| This time, although Philsopher 0 is just as greedy, no one starves.
//|

import org.jcsp.lang.*;

/**
 * This Canteen is an active object -- a pure SERVER process for its `service'
 * and `supply' CALL channels.  The service channel is any-1 since, hopefully,
 * there will be many customers.  The supply channel may be 1-1 (one chef) or
 * any-1 (many chefs).
 *
 * @author P.H. Welch
 */
class CallCanteen implements CSProcess {

  // call interfaces and channels

  public static interface Service {
    public int takeChicken (String philId);
  }

  public static class One2OneServiceChannel extends One2OneCallChannel implements Service {
    public int takeChicken (String philId) {
      join ();
      int n = ((Service) server).takeChicken (philId);
      fork ();
      return n;
    }
  }

  public static class Any2OneServiceChannel extends Any2OneCallChannel implements Service {
    public int takeChicken (String philId) {
      join ();
      int n = ((Service) server).takeChicken (philId);
      fork ();
      return n;
    }
  }

  public static interface Supply {
    public int freshChickens (String chefId, int value);
  }

  public static class One2OneSupplyChannel extends One2OneCallChannel implements Supply {
    public int freshChickens (String chefId, int value) {
      join ();
      int n = ((Supply) server).freshChickens (chefId, value);
      fork ();
      return n;
    }
  }

  public static class Any2OneSupplyChannel extends Any2OneCallChannel implements Supply {
    public int freshChickens (String chefId, int value) {
      join ();
      int n = ((Supply) server).freshChickens (chefId, value);
      fork ();
      return n;
    }
  }

  // fields and constructors

  private final AltingChannelAccept service;    // shared from all Philosphers (any-1)
  private final AltingChannelAccept supply;     // from the Chef (1-1)
  private final int serviceTime;                // how long a philosopher spends in the canteen
  private final int supplyTime;                 // how long the chef spends in the canteen
  private final int maxChickens;                // maximum number of chickens in the canteen

  public CallCanteen (Any2OneServiceChannel service, One2OneSupplyChannel supply,
                      int serviceTime, int supplyTime, int maxChickens) {
    this.service = service;
    this.supply = supply;
    this.serviceTime = serviceTime;
    this.supplyTime = supplyTime;
    this.maxChickens = maxChickens;
  }

  public CallCanteen (Any2OneServiceChannel service, Any2OneSupplyChannel supply,
                      int serviceTime, int supplyTime, int maxChickens) {
    this.service = service;
    this.supply = supply;
    this.serviceTime = serviceTime;
    this.supplyTime = supplyTime;
    this.maxChickens = maxChickens;
  }

  // inner process and run method

  private interface inner extends CSProcess, Service, Supply {};

  public void run () {

    new inner () {

      private int nChickens = 0;
      private int nSupplied = 0;

      private final CSTimer tim = new CSTimer ();

      public int takeChicken (String philId) {                   // precondition : nChickens > 0
        System.out.println ("   Canteen -> " + philId + " : one chicken ordered ... "
                                             + nChickens + " left");
        tim.sleep (serviceTime);         // this takes serviceTime to deliver
        nChickens--;
        nSupplied++;
        System.out.println ("   Canteen -> " + philId + " : one chicken coming down ... "
                                             + nChickens + " left [" + nSupplied + " supplied]");
        return 1;
      }

      public int freshChickens (String chefId, int value) {     // precondition : nChickens < maxChickens
        System.out.println ("   Canteen <- " + chefId
                                             + " : ouch ... make room ... this dish is very hot ...");
        tim.sleep (supplyTime);          // this takes supplyTime to put down
        nChickens += value;
        int sendBack = nChickens - maxChickens;
        if (sendBack > 0) {
          nChickens = maxChickens;
          System.out.println ("   Canteen <- " + chefId
                                               + " : full up ... sending back " + sendBack);
        } else {
          sendBack = 0;
        }
        System.out.println ("   Canteen <- " + chefId + " : more chickens ... "
                                             + nChickens + " now available");
        return sendBack;
      }

      public void run () {

        final Alternative alt = new Alternative (new Guard[] {supply, service});
        final boolean[] precondition = {true, false};
        final int SUPPLY = 0;
        final int SERVICE = 1;

        System.out.println ("   Canteen : starting ... ");
        while (true) {
          precondition[SERVICE] = (nChickens > 0);
          precondition[SUPPLY] = (nChickens < maxChickens);
          switch (alt.fairSelect (precondition)) {
            case SUPPLY:
              supply.accept (this);      // new batch of chickens from the Chef
            break;
            case SERVICE:
              service.accept (this);     // Philosopher wants a chicken
            break;
          }
        }

      }

    }.run ();

  }

}
