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

  // protected attributes

  protected int id;
  protected Integer Id;
  protected AltingChannelInput left, right;
  protected ChannelOutput report;

  protected ForkReport leftUp, leftDown, rightUp, rightDown;

  // constructors

  public Fork (int nPhilosophers, int id,
               AltingChannelInput left, AltingChannelInput right,
               ChannelOutput report) {
    this.id = id;
    Id = new Integer(id);
    this.left = left;
    this.right = right;
    this.report = report;
    leftUp = new ForkReport (id, id, ForkReport.UP);
    leftDown = new ForkReport (id, id, ForkReport.DOWN);
    rightUp = new ForkReport ((id + 1) % nPhilosophers, id, ForkReport.UP);
    rightDown = new ForkReport ((id + 1) % nPhilosophers, id, ForkReport.DOWN);
  }

  // public methods

  public void run () {
    Alternative alt = new Alternative (new Guard[] {left, right});
    final int LEFT = 0;
    final int RIGHT = 1;
    while (true) {
      switch (alt.fairSelect ()) {
        case LEFT:
          left.read ();
          report.write (leftUp);
          left.read ();
          report.write (leftDown);
        break;
        case RIGHT:
          right.read ();
          report.write (rightUp);
          right.read ();
          report.write (rightDown);
        break;
      }
    }
  }

}
