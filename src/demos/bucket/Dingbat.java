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
public class Dingbat implements CSProcess {

  private final int id;
  private final Bucket[] bucket;

  public Dingbat (int id, Bucket[] bucket) {
    this.id = id;
    this.bucket = bucket;
  }

  public void run () {

    int logicalTime = 0;

    String[] spacer = new String[bucket.length];
    spacer[0] = "";
    for (int i = 1; i < spacer.length; i++) spacer[i] = spacer[i - 1] + "  ";

    String message = "Hello world from " + id + " ==> time = ";

    while (true) {
      logicalTime += id;
      final int slot = logicalTime % bucket.length;     // assume: id <= bucket.length
      bucket[slot].fallInto ();
      System.out.println (spacer[slot] + message + logicalTime);   // one unit of work
    }
  }

}
