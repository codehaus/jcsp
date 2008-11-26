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
import org.jcsp.util.ints.*;
import org.jcsp.plugNplay.ints.*;

/**
 * @author P.H. Welch
 */
public final class PrimeMultiples2 implements CSProcess {

  private final ChannelOutputInt trap;

  private final int[] primes;
  private final int minPrimes = 2;

  private final int howMany;

  public PrimeMultiples2 (final int[] primes, final int howMany,
                          final ChannelOutputInt trap) {
    // assume these are distinct primes (and at least minPrimes of them)
    this.primes = primes;
    if (howMany < minPrimes) {
      this.howMany = minPrimes;
    } else if (howMany > primes.length) {
      this.howMany = primes.length;
    } else {
      this.howMany = howMany;
    }
    this.trap = trap;
  }

  public void run () {

    final One2OneChannelInt[] a = Channel.one2oneIntArray (howMany + 1);
    final One2OneChannelInt[] b = Channel.one2oneIntArray (howMany, new InfiniteBufferInt ());
    final One2OneChannelInt c = Channel.one2oneInt ();
    final One2OneChannelInt d = Channel.one2oneInt ();
    final One2OneChannelInt e = Channel.one2oneInt ();

    final CSProcess[] Multipliers = new CSProcess[howMany];
    for (int i = 0; i < howMany; i++) {
      Multipliers[i] = new MultInt (primes[i], a[i].in (), b[i].out ());
    }

    new Parallel (
      new CSProcess[] {
        new Parallel (Multipliers),
        new MergeInt (Channel.getInputArray (b), c.out ()),
        new PrefixInt (1, c.in (), d.out ()),
        new DeltaInt (d.in (), Channel.getOutputArray (a)),
        new TrapNegative (a[howMany].in (), e.out (), trap),
        new PrinterInt (e.in (), "", " ")
      }
    ).run ();
  }

}
