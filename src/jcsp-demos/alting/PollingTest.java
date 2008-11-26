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
public class PollingTest {

  public static final String TITLE = "Polling Multiplexor";
  public static final String DESCR =
 		"Shows a pri-Alt with a skip guard being used to poll the inputs. Five processes generate numbers " +
  		"at 1s, 2s, 3s, 4s and 5s intervals. The number generated indicates the process generating it. If " +
  		"no data is available on a polling cycle the polling process will wait for 400ms before polling " +
  		"again. It could however be coded to perform some useful computation between polling cycles.\n" +
  		"\n" +
  		"The polling is unfair although this is not noticeable with these timings. If the interval at " +
  		"which the numbers are generated is shortened then the higher numbered processes may become starved.";

  public static void main (String[] args) {

  	Ask.app (TITLE, DESCR);
  	Ask.show ();
  	Ask.blank ();

    final One2OneChannel[] a = Channel.one2oneArray(5);
    final One2OneChannel b = Channel.one2one();

    new Parallel (
      new CSProcess[] {
        new Regular (a[0].out(), 1, 1000),
        new Regular (a[1].out(), 2, 2000),
        new Regular (a[2].out(), 3, 3000),
        new Regular (a[3].out(), 4, 4000),
        new Regular (a[4].out(), 5, 5000),
        new Polling (a[0].in(), a[1].in(), a[2].in(), a[3].in(), a[4].in(), b.out()),
        new Printer (b.in(), "PollingTest ==> ", "\n")
      }
    ).run ();

  }

}
