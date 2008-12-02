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


/**
 * @author P.H. Welch
 */
public class RandTest {

  private static void zap (int[] count) {
    for (int i = 0; i < count.length; i++ ) {
      count[i] = 0;
    }
  }

  private static void print (int[] count) {
    for (int i = 0; i < count.length; i++ ) {
      if ((i%8) == 0) System.out.println ();
      System.out.print (count[i] + "  ");
    }
    System.out.println ();
  }

  public static void main (String[] args) {
  
    final Rand random = new Rand ();
    
    final int[] count = new int[128];

    System.out.println ("\nStarting range (128) ...");
    zap (count);
    int n = 1;
    for (int i = 0; i < 25; i++) {
      final long t0 = System.currentTimeMillis ();
      for (int j = 0; j < n; j++) {
        count[random.range (128)]++;
      }
      final long t1 = System.currentTimeMillis ();
      final long span = 1000000*(t1 - t0);
      final long each = span/n;
      System.out.println (" range (128) : " + n + ", " + span + ", " + each);
      n *= 2;
    }
    print (count);

    System.out.println ("\nStarting bits (7) ...");
    zap (count);
    n = 1;
    for (int i = 0; i < 25; i++) {
      final long t0 = System.currentTimeMillis ();
      for (int j = 0; j < n; j++) {
        count[random.bits (7)]++;
      }
      final long t1 = System.currentTimeMillis ();
      final long span = 1000000*(t1 - t0);
      final long each = span/n;
      System.out.println (" bits (7) : " + n + ", " + span + ", " + each);
      n *= 2;
    }
    print (count);

    System.out.println ("\nStarting bits7 () ...");
    zap (count);
    n = 1;
    for (int i = 0; i < 25; i++) {
      final long t0 = System.currentTimeMillis ();
      for (int j = 0; j < n; j++) {
        count[random.bits7 ()]++;
      }
      final long t1 = System.currentTimeMillis ();
      final long span = 1000000*(t1 - t0);
      final long each = span/n;
      System.out.println (" bits7 () : " + n + ", " + span + ", " + each);
      n *= 2;
    }
    print (count);
    
  }

}
