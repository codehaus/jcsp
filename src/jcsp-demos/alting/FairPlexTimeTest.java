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
public class FairPlexTimeTest {

  public static final String TITLE = "Fair multiplexing (fair-Alt)";
  public static final String DESCR =
  		"Shows a fair-Alt in action. Five processes are created which generate numbers at 5ms intervals. " +
  		"A multiplexer will use 'fairSelect' to ensure that each of the channels gets served. The output " +
  		"shows which data was accepted by the multiplexer. The fairness ensures that the higher numbered " +
  		"channels do not get starved. A timeout guard is also used to stop the demonstration after 10 " +
  		"seconds. Contrast this with the Pri-Alting demonstration.";

  public static void main (String[] args) {

  	Ask.app (TITLE, DESCR);
  	Ask.show ();
  	Ask.blank ();

	final One2OneChannel[] a = Channel.one2oneArray(5);
    final One2OneChannel b = Channel.one2one();

    new Parallel (
      new CSProcess[] {
        new Regular (a[0].out(), 0, 5),
        new Regular (a[1].out(), 1, 5),
        new Regular (a[2].out(), 2, 5),
        new Regular (a[3].out(), 3, 5),
        new Regular (a[4].out(), 4, 5),
        new FairPlexTime (Channel.getInputArray(a), b.out(), 10000),
        new Printer (b.in(), "FairPlexTimeTest ==> ", "\n")
      }
    ).run ();

  }

}
