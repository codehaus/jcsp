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
 *
 */

class StarveCanteen {

  // call interfaces and channels

  public static interface Service {
    public int takeChicken (String philId);
  }

  public static interface Supply {
    public int freshChickens (String chefId, int value);
  }

  // fields and constructors

  private final Service service;				// called by the philosophers
  private final Supply supply;					// called by the chefs
  private final int serviceTime;                // how long a philosopher spends in the canteen
  private final int supplyTime;                 // how long the chef spends in the canteen
  private final int maxChickens;                // maximum number of chickens in the canteen
  private final CSTimer tim;					// for delays

  public StarveCanteen (int serviceTime, int supplyTime, int maxChickens) {
    this.service = new Service () {
    	public int takeChicken (String philId) {
    		return doTakeChicken (philId);
    	}
    };
    this.supply = new Supply () {
    	public int freshChickens (String chefId, int value) {
    		return doFreshChickens (chefId, value);
    	}
    };
    this.serviceTime = serviceTime;
    this.supplyTime = supplyTime;
    this.maxChickens = maxChickens;
    this.tim = new CSTimer ();
  }

  public Service getService () {
  	return service;
  }

  public Supply getSupply () {
  	return supply;
  }

  private int nChickens = 0;
  private int nSupplied = 0;

  private synchronized int doTakeChicken (String philId) {
    System.out.println ("   Canteen -> " + philId + " : one chicken ordered ... "
                                         + nChickens + " left");
  	if (nChickens > 0) {
        tim.sleep (serviceTime);         // this takes serviceTime to deliver
        nChickens--;
        nSupplied++;
        System.out.println ("   Canteen -> " + philId + " : one chicken coming down ... "
                                             + nChickens + " left [" + nSupplied + " supplied]");
	    return 1;
  	} else {
  		return 0;
  	}
  }

  private synchronized int doFreshChickens (String chefId, int value) {
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

}
