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
class Consume implements CSProcess {

  private int nLoops;
  private ChannelInputInt in;

  public Consume (int nLoops, ChannelInputInt in) {
    this.nLoops = nLoops;
    this.in = in;
  }

  public void run () {

    int x = -1;
    int warm_up = 1000;
    System.out.print ("warming up ... ");
    for (int i = 0; i < warm_up; i++) {
      x = in.read ();
    }
    System.out.println ("last number received = " + x);

    System.out.println ("1000 cycles completed ... timing now starting ...");

    while (true) {

      long t0 = System.currentTimeMillis ();
      for (int i = 0; i < nLoops; i++) {
        x = in.read ();
      }
      long t1 = System.currentTimeMillis ();

      System.out.println ("last number received = " + x);
      long microseconds   = (t1 - t0) * 1000;
      long timePerLoop_us = (microseconds / ((long) nLoops));
      System.out.println ("   " + timePerLoop_us + " microseconds / iteration");
      timePerLoop_us = (microseconds / ((long) (4*nLoops)));
      System.out.println ("   " + timePerLoop_us + " microseconds / communication");
      timePerLoop_us = (microseconds / ((long) (8*nLoops)));
      System.out.println ("   " + timePerLoop_us + " microseconds / context switch");

    }

  }

}
