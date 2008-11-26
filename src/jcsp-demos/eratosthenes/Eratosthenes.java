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
public class Eratosthenes {

  public static final String TITLE = "Sieve of Eratosthenes";
  public static final String DESCR =
  	"Demonstrates the pipe-line approach to parallelisation, generating prime numbers by sieving. The " +
  	"sieve of Eratosthenes works by a feeder process pushing numbers into a pipeline of sieve processes. " +
  	"Each sieve process is allocated a prime number and discards any numbers in the pipeline divisible " +
  	"by that number. If a number reaches the end of the pipe it must be prime as no factors were found. " +
  	"It is printed and a new sieve process added to the pipeline to carry on searching for higher primes.\n" +
  	"\n" +
  	"This demonstration is a good way of stress testing the system for running a lot of processes.";

  public static void main (String[] args) {

  	Ask.app (TITLE, DESCR);
  	Ask.show ();
  	Ask.blank ();

    One2OneChannelInt c = Channel.one2oneInt ();
    new Parallel (
      new CSProcess[] {
        new Primes (c.out ()),
        new PrinterInt (c.in (), "--> ", "\n")
      }
    ).run ();
  }

}
