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
class DiningPhilosophersCollege implements CSProcess {

  private final int nPhilosophers;
  private final int clockPeriod;
  private final PhilReport philReport;
  private final ForkReport forkReport;
  private final ChannelOutputInt securityReport;
  private final ChannelOutputInt clockReport;

  public DiningPhilosophersCollege (int nPhilosophers, int clockPeriod,
                                    PhilReport philReport,
                                    ForkReport forkReport,
                                    ChannelOutputInt securityReport,
                                    ChannelOutputInt clockReport) {
    this.nPhilosophers = nPhilosophers;
    this.clockPeriod = clockPeriod;
    this.philReport = philReport;
    this.forkReport = forkReport;
    this.securityReport = securityReport;
    this.clockReport = clockReport;
  }

  public void run () {

    final One2OneChannelInt[] left = Channel.one2oneIntArray (nPhilosophers);
    final One2OneChannelInt[] right = Channel.one2oneIntArray (nPhilosophers);

    final Any2OneChannelInt down = Channel.any2oneInt ();
    final Any2OneChannelInt up = Channel.any2oneInt ();

    final Fork[] fork = new Fork[nPhilosophers];
    for (int i = 0; i < nPhilosophers; i++) {
      fork[i] = new Fork (nPhilosophers, i,
                          left[i].in (), right[(i + 1)%nPhilosophers].in (), forkReport);
    }

    final Philosopher[] phil = new Philosopher[nPhilosophers];
    for (int i = 0; i < nPhilosophers; i++) {
      phil[i] = new Philosopher (i, left[i].out (), right[i].out (), down.out (), up.out (), philReport);
    }

    new Parallel (
      new CSProcess[] {
        new Parallel (phil),
        new Parallel (fork),
        new Security (down.in (), up.in (), securityReport, nPhilosophers - 1),
        new Clock (clockReport, clockPeriod)
      }
    ).run ();

  }

}
