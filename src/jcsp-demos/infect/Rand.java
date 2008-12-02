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
public class Rand {

  private int bits;
  
  private int bitsLeft = 0;

  private final static int[] mask = generateMask ();

  private final static int[] generateMask () {
    final int[] mask = new int[33];
    int ones = 0xFFFFFFFF;
    for (int i = mask.length - 1; i >= 0; i--) {
      mask[i] = ones;
      ones >>>= 1;
    }
    return mask;
  }

  private long mySeed;

  private final static long multiplier = 0x5DEECE66DL;
  private final static long addend = 0xBL;
  private final static long seedMask = (1L << 48) - 1;

  public Rand () {
    this (System.currentTimeMillis ());
  }

  public Rand (long seed) {
    setMySeed (seed);
  }

  public void setMySeed (long seed) {
    mySeed = (seed ^ multiplier) & seedMask;
  }

  private int next (int bits) {
    mySeed = ((mySeed*multiplier) + addend) & seedMask;
    return (int) (mySeed >>> (48 - bits));
  }

  private int next32 () {
    mySeed = ((mySeed*multiplier) + addend) & seedMask;
    return (int) (mySeed >>> 16);
  }

  public final int range (int n) {
    int i = next32 ();
    if (i < 0) {
      if (i == Integer.MIN_VALUE) {      // guard against minint !
        i = 42;
      } else {
        i = -i;
      }
    }
    return i % n;
  }

  public final int bits (int n) {        // assume : 0 < n <= 32
    if (n > bitsLeft) {
      bits = next32 ();
      bitsLeft = 32;
    }
    final int answer = bits & mask[n];
    bits >>>= n;
    bitsLeft -= n;
    return answer;
  }

  public final int bits7 () {
    if (7 > bitsLeft) {
      bits = next32 ();
      bitsLeft = 32;
    }
    final int answer = bits & 127;
    bits >>>= 7;
    bitsLeft -= 7;
    return answer;
  }

  public final int bits16 () {
    if (16 > bitsLeft) {
      bits = next32 ();
      bitsLeft = 32;
    }
    final int answer = bits & 65535;
    bits >>>= 16;
    bitsLeft -= 16;
    return answer;
  }

}
