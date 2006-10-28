    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2001 Peter Welch and Paul Austin.            //
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
    //  Author contact: P.H.Welch@ukc.ac.uk                             //
    //                  mailbox@quickstone.com                          //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

package org.jcsp.demos.hamming;

import org.jcsp.lang.*;
import org.jcsp.demos.util.*;

/**
 * @author P.H.Welch
 */
public final class Hamming2 {

  public static final String TITLE = "Hamming Codes";
  public static final String DESCR =
  	"Uses a pipeline of processes to generate integers with given prime factors. Demonstrates the ability " +
  	"of a system to cope with large numbers of processes running.";

  public static void main (String[] args) {

  	Ask.app (TITLE, DESCR);
  	Ask.show ();
  	Ask.blank ();

    final One2OneChannelInt trap = ChannelInt.createOne2One ();

    final int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};

    Parallel.setUncaughtErrorDisplay (false);

    while (true) {

      for (int i = 2; i <= primes.length; i++)  {

        System.out.println ("\nAll positive ints whose prime factors contain only:\n");
        System.out.print ("  " + primes[0]);
        for (int j = 1; j < i; j++)  {
          System.out.print (", ");
          System.out.print (primes[j]);
        }
        System.out.println ("\n");

        final ProcessManager manager =
          new ProcessManager (new PrimeMultiples2 (primes, i, trap.out ()));

        manager.start ();                // start up the managed process

        final int count = trap.in().read ();  // wait for hamming numbers to overflow

        manager.stop ();                 // stop the managed process

        System.out.println ("\n\nThere were " + count + " of them ...");

      }
    }

  }

}
