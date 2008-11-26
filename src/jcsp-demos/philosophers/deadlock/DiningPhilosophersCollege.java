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
  private final ChannelOutput report;

  public DiningPhilosophersCollege (int nPhilosophers, ChannelOutput report) {
    this.nPhilosophers = nPhilosophers;
    this.report = report;
  }

  public void run () {

    final One2OneChannel[] left = Channel.one2oneArray (nPhilosophers);
    final One2OneChannel[] right = Channel.one2oneArray (nPhilosophers);

    final Fork[] fork = new Fork[nPhilosophers];
    for (int i = 0; i < nPhilosophers; i++) {
      fork[i] = new Fork (nPhilosophers, i,
                          left[i].in (), right[(i + 1)%nPhilosophers].in (), report);
    }

    final Philosopher[] phil = new Philosopher[nPhilosophers];
    for (int i = 0; i < nPhilosophers; i++) {
      phil[i] = new Philosopher (i, left[i].out (), right[i].out (), report);
    }

    new Parallel (
      new CSProcess[] {
        new Parallel (phil),
        new Parallel (fork),
        new Clock (report)
      }
    ).run ();

  }

}
