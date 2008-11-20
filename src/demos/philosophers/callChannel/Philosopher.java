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
import org.jcsp.plugNplay.ints.*;

/**
 * @author P.H. Welch
 */
class Philosopher implements CSProcess {

  // protected attributes

  protected final static int seconds = 1000;

  protected final static int maxThink = 10*seconds;
  protected final static int maxEat = 15*seconds;

  protected final int id;
  protected final ChannelOutputInt left, right, down, up;
  protected final PhilReport report;

  protected final Random random;

  // constructors

  public Philosopher (int id, ChannelOutputInt left, ChannelOutputInt right,
                      ChannelOutputInt down, ChannelOutputInt up,
                      PhilReport report) {
    this.id = id;
    this.left = left;
    this.right = right;
    this.down = down;
    this.up = up;
    this.report = report;
    this.random = new Random (id + 1);
  }

  // public methods

  public void run () {

    final CSTimer tim = new CSTimer ();

    final ProcessWriteInt signalLeft = new ProcessWriteInt (left);
    signalLeft.value = id;

    final ProcessWriteInt signalRight = new ProcessWriteInt (right);
    signalRight.value = id;

    final CSProcess signalForks = new Parallel (new CSProcess[] {signalLeft, signalRight});
    /*final CSProcess signalForks = new Sequence (
    	new CSProcess[] {
    		signalLeft,
    		new CSProcess () { public void run () { tim.sleep (seconds); } },
    		signalRight
    	}
    );*/

    while (true) {
      report.thinking (id);
      tim.sleep (range (maxThink));    // thinking
      report.hungry (id);
      down.write (id);                 // get past the security guard
      report.sitting (id);
      signalForks.run ();              // pick up my forks (in parallel)
      report.eating (id);
      tim.sleep (range (maxEat));      // eating
      report.leaving (id);
      signalForks.run ();              // put down my forks (in parallel)
      up.write (id);                   // get up from the table and go past the security guard
    }
  }

  // protected methods

  protected int range (int n) {
    // returns random int in the range 0 .. (n - 1)  [This is not needed in JDK 1.2.x]
    int i = random.nextInt ();
    if (i < 0) {
      if (i == Integer.MIN_VALUE) {
        i = 42;
      } else {
        i = -i;
      }
    }
    return i % n;
  }

}
