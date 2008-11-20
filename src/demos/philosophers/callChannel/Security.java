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
class Security implements CSProcess {

  // protected attributes

  protected AltingChannelInputInt down, up;
  protected ChannelOutputInt report;

  protected final int maxSitting;

  // constructors

  public Security (AltingChannelInputInt down, AltingChannelInputInt up,
                   ChannelOutputInt report, int maxSitting) {
    this.down = down;
    this.up = up;
    this.report = report;
    this.maxSitting = maxSitting;
  }

  // public methods

  public void run () {

    final Alternative alt = new Alternative (new Guard[] {down, up});
    boolean[] precondition = {true, true};
    final int DOWN = 0;
    final int UP = 1;

    int nSitting = 0;

    while (true) {
      report.write (nSitting);
      precondition[DOWN] = (nSitting < maxSitting);
      switch (alt.fairSelect (precondition)) {
        case DOWN:
          down.read ();
          nSitting++;
        break;
        case UP:
          up.read ();
          nSitting--;
        break;
      }
    }
  }

}
