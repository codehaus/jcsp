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
import org.jcsp.plugNplay.Printer;
import org.jcsp.demos.util.*;

/**
 * @author P.H. Welch
 */
public class PriPlexTimeTest {

  public static final String TITLE = "Prioritised multiplexing (pri-Alt)";
  public static final String DESCR =
  		"Shows a pri-Alt in action. Five processes are created which generate numbers at 5ms intervals. " +
  		"A multiplexer will use 'priSelect' to serve lower numbered processes first. Contrast this with " +
  		"the fair multiplexor. Higher numbered processes will be starved and the timeout to stop the " +
  		"demonstration after 10 seconds may never be serviced.";

  public static void main (String[] args) {

  	Ask.app (TITLE, DESCR);
  	Ask.show ();
  	Ask.blank ();

    final One2OneChannel[] a = Channel.one2oneArray(5);
    final One2OneChannel b = Channel.one2one();

    new Parallel (                         // this won't see the higher
      new CSProcess[] {                    // indexed guards, including
        new Regular (a[0].out(), 0, 5),          // the timeout ... probably.
        new Regular (a[1].out(), 1, 5),
        new Regular (a[2].out(), 2, 5),
        new Regular (a[3].out(), 3, 5),
        new Regular (a[4].out(), 4, 5),
        new PriPlexTime (Channel.getInputArray(a), b.out(), 10000),
        new Printer (b.in(), "PriPlexTimeTest ==> ", "\n")
      }
    ).run ();

  }

}
