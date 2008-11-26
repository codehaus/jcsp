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
import org.jcsp.demos.util.*;

/**
 * @author P.H. Welch
 */
public class BucketExample1 {

  public static final String TITLE = "Bucket Example 1";
  public static final String DESCR =
  	"Shows the use of a single bucket to control a group of worker processes. A worker process will take " +
  	"action for some time and then fall into the bucket. Another process will periodically flush the " +
  	"bucket, setting any workers in it working again. Such a system can be used to simulate actions which " +
  	"must start on a clock cycle (ie when the bucket is flushed). An individual action from a worker may " +
  	"take any length of time but the next action will not start until the next clock cycle.";

  public static void main (String[] args) {

  	Ask.app (TITLE, DESCR);
  	Ask.show ();
  	Ask.blank ();

    final int nWorkers = 10;

    final int second = 1000;                // JCSP timer units are milliseconds
    final int interval = 5*second;
    final int maxWork = 10*second;

    final long seed = new CSTimer ().read ();

    final Bucket bucket = new Bucket ();

    final Flusher flusher = new Flusher (interval, bucket);

    final Worker[] workers = new Worker[nWorkers];
    for (int i = 0; i < workers.length; i++) {
      workers[i] = new Worker (i, i + seed, maxWork, bucket);
    }

    System.out.println ("*** Flusher: interval = " + interval + " milliseconds");

    new Parallel (
      new CSProcess[] {
        flusher,
        new Parallel (workers)
      }
    ).run ();

  }

}
