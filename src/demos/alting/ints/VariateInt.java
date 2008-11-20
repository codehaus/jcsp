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
public final class VariateInt implements CSProcess {

  private final ChannelOutputInt out;
  private final int start, stop, n;

  public VariateInt (final ChannelOutputInt out, final int start,
                     final int stop, final int n) {
    this.out = out;
    this.start = start;
    this.stop = stop;
    this.n = n;
  }

  public void run () {

    final int innerCycleTime = n*start;

    final CSTimer tim = new CSTimer ();
    long timeout = tim.read ();

    while (true) {
      int interval = start;
      while (interval >= stop) {
        final int innerCycles = innerCycleTime/interval;
        for (int i = 0; i < innerCycles; i++) {
          out.write (interval);
          timeout += (long) interval;
          tim.after (timeout);
        }
        interval /= 2;
      }
    }

  }

}
