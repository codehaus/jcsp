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
class BucketKeeper implements CSProcess {

  private final long interval;
  private final Bucket[] bucket;

  public BucketKeeper (long interval, Bucket[] bucket) {
    this.interval = interval;
    this.bucket = bucket;
  }

  public void run () {

    String[] spacer = new String[bucket.length];
    spacer[0] = "";
    for (int i = 1; i < spacer.length; i++) spacer[i] = spacer[i - 1] + "  ";

    final CSTimer tim = new CSTimer ();
    long timeout = tim.read ();
    int index = 0;

    while (true) {
      final int n = bucket[index].flush ();
      if (n == 0) {
        System.out.println (spacer[index] + "*** bucket " + index + " was empty ...");
      }
      index = (index + 1) % bucket.length;
      timeout += interval;
      tim.after (timeout);
    }
  }

}
