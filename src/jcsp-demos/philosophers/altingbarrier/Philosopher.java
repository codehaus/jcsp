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


import java.util.Random;
import org.jcsp.lang.*;
import org.jcsp.plugNplay.*;

/**
 * @author P.H. Welch
 */
class Philosopher implements CSProcess {

  // private attributes

  private final static int seconds = 1000;

  private final static int maxThink = 10*seconds;
  private final static int maxEat = 15*seconds;

  private final int id;
  private final AltingBarrier startEating, finishEating;
  private final ChannelOutput report;

  private final Random random;

  // constructors

  public Philosopher (int id, AltingBarrier startEating, AltingBarrier finishEating,
                      ChannelOutput report) {
    this.id = id;
    this.startEating = startEating;
    this.finishEating = finishEating;
    this.report = report;
    this.random = new Random (id + 1);
  }

  // public methods

  public void run () {

    final CSTimer tim = new CSTimer ();

    final PhilReport thinking = new PhilReport (id, PhilReport.THINKING);
    final PhilReport hungry = new PhilReport (id, PhilReport.HUNGRY);
    // final PhilReport sitting = new PhilReport (id, PhilReport.SITTING);
    final PhilReport eating= new PhilReport (id, PhilReport.EATING);
    final PhilReport leaving = new PhilReport (id, PhilReport.LEAVING);

    // The lines marked "OPTIONAL" below are to enforce a certain ordering
    // of the reports (that the Fork pickup/putdown reports occur before
    // these Philosopher eating/thinking reports).  This is not strictly
    // necessary and may all be removed (so long as, of course, similarly
    // marked lines in the Fork code are also removed!).

    while (true) {
      report.write (thinking);
      tim.sleep (random.nextInt (maxThink));    // thinking
      report.write (hungry);
      startEating.sync ();                      // sync with neighbouring forks
      startEating.sync ();                      // wait for forks to report picked up (OPTIONAL)
      report.write (eating);
      tim.sleep (random.nextInt (maxEat));      // eating
      report.write (leaving);
      finishEating.sync ();                     // sync with neighbouring forks
      finishEating.sync ();                     // wait for forks to report put down (OPTIONAL)
    }
  }

}
