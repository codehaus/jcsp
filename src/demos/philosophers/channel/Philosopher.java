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

  // protected attributes

  protected final static int seconds = 1000;

  protected final static int maxThink = 10*seconds;
  protected final static int maxEat = 15*seconds;

  protected final static String[] space =
    {"  ", "    ", "      ", "        ", "          "};

  protected final int id;
  protected final ChannelOutput left, right, down, up;
  protected final ChannelOutput report;

  protected final Random random;

  // constructors

  public Philosopher (int id, ChannelOutput left, ChannelOutput right,
                      ChannelOutput down, ChannelOutput up,
                      ChannelOutput report) {
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

    final Integer Id = new Integer (id);
    final CSTimer tim = new CSTimer ();

    final PhilReport thinking = new PhilReport (id, PhilReport.THINKING);
    final PhilReport hungry = new PhilReport (id, PhilReport.HUNGRY);
    final PhilReport sitting = new PhilReport (id, PhilReport.SITTING);
    final PhilReport eating= new PhilReport (id, PhilReport.EATING);
    final PhilReport leaving = new PhilReport (id, PhilReport.LEAVING);

    final ProcessWrite signalLeft = new ProcessWrite (left);
    signalLeft.value = Id;

    final ProcessWrite signalRight = new ProcessWrite (right);
    signalRight.value = Id;

    final CSProcess signalForks = new Parallel (new CSProcess[] {signalLeft, signalRight});
    /*final CSProcess signalForks = new Sequence (
    	new CSProcess[] {
    		signalLeft,
    		new CSProcess () { public void run () { tim.sleep (seconds); } },
    		signalRight
    	}
    );*/

    while (true) {
      report.write (thinking);
      tim.sleep (range (maxThink));    // thinking
      report.write (hungry);
      down.write (Id);                 // get past the security guard
      report.write (sitting);
      signalForks.run ();              // pick up my forks (in parallel)
      report.write (eating);
      tim.sleep (range (maxEat));      // eating
      report.write (leaving);
      signalForks.run ();              // put down my forks (in parallel)
      up.write (Id);                   // get up from the table and go past the security guard
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
