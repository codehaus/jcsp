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
import java.util.*;

/**
 * @author P.H. Welch
 */
public class Worker implements CSProcess {

  private final int id;
  private final long seed;
  private final int maxWork;
  private final Bucket bucket;

  public Worker (int id, long seed, int maxWork, Bucket bucket) {
    this.id = id;
    this.seed = seed;
    this.maxWork = maxWork;
    this.bucket = bucket;
  }

  public void run () {

    final Random random = new Random (seed);        // each process gets a different seed

    final CSTimer tim = new CSTimer ();

    final String working = "\t... Worker " + id + " working ...";
    final String falling = "\t\t\t     ... Worker " + id + " falling ...";
    final String flushed = "\t\t\t\t\t\t  ... Worker " + id + " flushed ...";

    while (true) {
      System.out.println (working);               // these lines represent
      int sleepTime = (random.nextInt() & 0x7FFFFFFF) % maxWork;
      tim.sleep (sleepTime);       // one unit of work
      
      System.out.println (falling);
      bucket.fallInto ();
      System.out.println (flushed);
    }
  }

}
