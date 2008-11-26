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
import org.jcsp.plugNplay.ints.*;
import org.jcsp.demos.util.*;

/**
 * @author P.H. Welch
 */
public class CommsTime {

  public static final String TITLE = "CommsTime";
  public static final String DESCR =
  	"Test of communication speed between JCSP processes. Based on OCCAM CommsTime.occ by Peter Welch, " +
  	"University of Kent at Canterbury. Ported into Java by Oyvind Teig. Now using the JCSP library.\n" +
  	"\n" +
  	"A small network of four processes is created which will generate a sequence of numbers, measuring " +
  	"the time taken to generate each 10000. This time is then divided to calculate the time per iteration, " +
  	"the time per communication (one integer over a one-one channel) and the time for a context switch. " +
  	"There are four communications per iteration and two context switches per communication. This test " +
  	"forms a benchmark for the for the overheads involved.\n" +
  	"\n" +
	"This version uses a PARallel delta2 component, so includes the starting and finishing of one extra" +
	"process per loop.";

  public static void  main (String argv []) {

  	Ask.app (TITLE, DESCR);
  	Ask.show ();
  	Ask.blank ();

    int nLoops = 10000;
    System.out.println (nLoops + " loops ...\n");

    One2OneChannelInt a = Channel.one2oneInt ();
    One2OneChannelInt b = Channel.one2oneInt ();
    One2OneChannelInt c = Channel.one2oneInt ();
    One2OneChannelInt d = Channel.one2oneInt ();

    new Parallel (
      new CSProcess[] {
        new PrefixInt (0, c.in (), a.out ()),
        new Delta2Int (a.in (), d.out (), b.out ()),
        new SuccessorInt (b.in (), c.out ()),
        new Consume (nLoops, d.in ())
      }
    ).run ();

  }

}
