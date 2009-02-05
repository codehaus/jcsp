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
class Fork implements CSProcess {

  // private attributes

  private final int nPhilosophers, id;
  private final AltingBarrier leftStartEating, leftFinishEating, rightStartEating, rightFinishEating;
  private final ChannelOutput report;

  // constructors

  public Fork (int nPhilosophers, int id,
               AltingBarrier leftStartEating, AltingBarrier leftFinishEating,
	       AltingBarrier rightStartEating, AltingBarrier rightFinishEating,
               ChannelOutput report) {
    this.nPhilosophers = nPhilosophers;
    this.id = id;
    this.leftStartEating = leftStartEating;
    this.leftFinishEating = leftFinishEating;
    this.rightStartEating = rightStartEating;
    this.rightFinishEating = rightFinishEating;
    this.report = report;
  }

  // public methods

  public void run () {

    ForkReport leftUp = new ForkReport (id, id, ForkReport.UP);
    ForkReport leftDown = new ForkReport (id, id, ForkReport.DOWN);
    ForkReport rightUp = new ForkReport ((id + 1) % nPhilosophers, id, ForkReport.UP);
    ForkReport rightDown = new ForkReport ((id + 1) % nPhilosophers, id, ForkReport.DOWN);

    Alternative alt = new Alternative (new Guard[] {leftStartEating, rightStartEating});
    final int LEFT = 0;
    final int RIGHT = 1;

    // The lines marked "OPTIONAL" below are to enforce a certain ordering
    // of the reports (that these Fork pickup/putdown reports occur before
    // the Philosopher eating/thinking reports).  This is not strictly
    // necessary and may all be removed (so long as, of course, similarly
    // marked lines in the Philospher code are also removed!).

    while (true) {
      switch (alt.fairSelect ()) {
        case LEFT:
          report.write (leftUp);            // report philospher has picked us up
          leftStartEating.sync ();          // let philospher report eating (OPTIONAL)
          leftFinishEating.sync ();
          report.write (leftDown);          // report philospher has put us down
          leftFinishEating.sync ();         // let philospher report thinking (OPTIONAL)
        break;
        case RIGHT:
          report.write (rightUp);           // report philospher has picked us up
          rightStartEating.sync ();         // let philospher report eating (OPTIONAL)
          rightFinishEating.sync ();
          report.write (rightDown);         // report philospher has put us down
          rightFinishEating.sync ();        // let philospher report thinking (OPTIONAL)
        break;
      }
    }
  }

}
