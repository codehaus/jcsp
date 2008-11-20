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
public class StressedAlt {

  public static final String TITLE = "Stressed Alt";
  public static final String DESCR =
  		"Shows the fairness of an Alt at a high level of stress from the writing channels. Many writers " +
  		"will be writing to each of the channels (each is an Any-One channel) with no delay between writes. " +
  		"The ALT will be well behaved under such stress, still exhibiting fairness and no loss of data.\n" +
  		"\n" +
  		"Every 10000 cycles the reader will display the values read from each of the channels. If the Alt " +
  		"is serving the channels fairly the numbers will all be increasing together (though maybe wrapping around " +
  		"when the 2^31 limit for positive integers is reached). If the Alt is not serving them fairly then " +
  		"there will be an imbalance in the rate of increase between the channels.";

  public static void main (String [] args) {

  	Ask.app (TITLE, DESCR);
  	Ask.show ();
  	Ask.blank ();

    final int nChannels = 8;
    final int nWritersPerChannel = 8;

    //Any2OneChannel[] c = Channel.any2oneArray (nChannels, new OverWriteOldestBuffer (1));
    Any2OneChannel[] c = Channel.any2oneArray (nChannels);

    StressedWriter[] writers = new StressedWriter[nChannels*nWritersPerChannel];

    for (int channel = 0; channel < nChannels; channel++) {
      for (int i = 0; i < nWritersPerChannel; i++) {
        writers[(channel*nWritersPerChannel) + i] = new StressedWriter (c[channel].out(), channel, i);
      }
    }

    new Parallel (
      new CSProcess[] {
        new Parallel (writers),
        new StressedReader (Channel.getInputArray(c), nWritersPerChannel)
      }
    ).run ();

  }

}
