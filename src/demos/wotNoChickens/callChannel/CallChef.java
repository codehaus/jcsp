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
 * @author P.H. Welch
 */
class CallChef implements CSProcess {

  //The Chef is an active object.  He/she cooks chickens in batches of four --
  //taking around 2 seconds per batch -- and then sends them to the Canteen.
  //The Chef is delayed in the Canteen, waiting for an acknowledge that the
  //batch has been set down OK.
  //
  //This cycle continues indefinitely.

  private final String id;
  private final int batchSize;
  private final int batchTime;
  private final CallCanteen.Supply supply;

  public CallChef (String id, int batchSize, int batchTime, CallCanteen.Supply supply) {
    this.id = id;
    this.batchSize = batchSize;
    this.batchTime = batchTime;
    this.supply = supply;
  }

  public void run () {

    final CSTimer tim = new CSTimer ();

    int nReturned = 0;
    int nSupplied = 0;

    while (true) {
      // cook batchSize chickens
      System.out.println ("   Chef " + id + " : cooking ... " + (batchSize - nReturned) + " chickens");
      tim.sleep (batchTime);                    // this takes batchTime milliseconds to cook
      System.out.println ("   Chef " + id + " : " + batchSize + " chickens, ready-to-go ... ");
      nReturned = supply.freshChickens (id, batchSize);    // supply the chickens and wait till set down
      nSupplied += (batchSize - nReturned);
      System.out.println ("   Chef " + id + " : " + nReturned + " returned [" + nSupplied + " supplied]");
    }
  }

}
