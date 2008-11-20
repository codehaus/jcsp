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
public class BucketExample2 {

  public static final String TITLE = "Flying Dingbats";
  public static final String DESCR =
  	"Shows the use of a number of buckets to control the actions of a number of workers (dingbats). Each " +
  	"dingbat will perform one unit of work and then fall into one of the buckets. The bucket keeper will " +
  	"flush one bucket per cycle, possibly setting one or more dingbats to work again.";

  public static void main (String[] args) {

    final int minDingbat = 2;
    final int maxDingbat = 10;
    final int nDingbats = (maxDingbat - minDingbat) + 1;

    final int nBuckets = 2*maxDingbat;

    final Bucket[] bucket = Bucket.create (nBuckets);

    final int second = 1000;     // JCSP timer units are milliseconds
    final int tick = second;
    final BucketKeeper bucketKeeper = new BucketKeeper (tick, bucket);

    final Dingbat[] dingbats = new Dingbat[nDingbats];
    for (int i = 0; i < dingbats.length; i++) {
      dingbats[i] = new Dingbat (i + minDingbat, bucket);
    }

    new Parallel (
      new CSProcess[] {
        bucketKeeper,
        new Parallel (dingbats)
      }
    ).run ();

  }

}
