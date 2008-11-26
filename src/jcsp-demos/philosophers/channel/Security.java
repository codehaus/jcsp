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

  protected AltingChannelInput down, up;
  protected ChannelOutput report;

  protected int maxSitting;

  protected SecurityReport[] seated;

  // constructors

  public Security (AltingChannelInput down, AltingChannelInput up,
                   ChannelOutput report, int nMaxSitting) {
    this.down = down;
    this.up = up;
    this.report = report;
    this.maxSitting = nMaxSitting;
    this.seated = new SecurityReport [nMaxSitting + 1];
    for (int i = 0; i < (maxSitting + 1); i++) {
      seated[i] = new SecurityReport (i);
    }
  }

  // public methods

  public void run () {

    Alternative alt = new Alternative (new Guard[] {down, up});
    boolean[] precondition = {true, true};
    final int DOWN = 0;
    final int UP = 1;

    int nSitting = 0;

    while (true) {
      precondition[DOWN] = (nSitting < maxSitting);
      switch (alt.fairSelect (precondition)) {
        case DOWN:
          down.read ();
          nSitting++;
          report.write (seated[nSitting]);
        break;
        case UP:
          up.read ();
          nSitting--;
          report.write (seated[nSitting]);
        break;
      }
    }
  }

}
