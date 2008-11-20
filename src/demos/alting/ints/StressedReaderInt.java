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
public class StressedReaderInt implements CSProcess {

  private AltingChannelInputInt[] c;
  private final int nWritersPerChannel;

  public StressedReaderInt (AltingChannelInputInt[] c,
                            final int nWritersPerChannel) {
    this.c = c;
    this.nWritersPerChannel = nWritersPerChannel;
  }

  public void run () {
    final int seconds = 1000;
    final int initialWait = 5;
    System.out.println ("\nWait (" + initialWait +
                        " seconds) for all the writers to get going ...");
    CSTimer tim = new CSTimer ();
    long timeout = tim.read () + (initialWait*seconds);
    tim.after (timeout);
    System.out.println ("OK - that should be long enough ...\n");
    int[][] n = new int[c.length][nWritersPerChannel];
    for (int channel = 0; channel < c.length; channel++) {
      for (int i = 0; i < nWritersPerChannel; i++) {
        int thing = c[channel].read ();
        n[channel][thing % nWritersPerChannel] = thing / nWritersPerChannel;
        for (int chan = 0; chan < channel; chan++) System.out.print ("  ");
        System.out.println ("channel " + channel +
                            " writer " + (thing % nWritersPerChannel) +
                            " read " + (thing / nWritersPerChannel));
      }
    }
    Alternative alt = new Alternative (c);
    int counter = 0, tock = 0;
    while (true) {
      if (counter == 0) {
        System.out.print ("Tock " + tock + " : ");
        int total = 0;
        for (int channel = 0; channel < n.length; channel++) {
          System.out.print (n[channel][tock % nWritersPerChannel] + " ");
          for (int i = 0; i < nWritersPerChannel; i++) {
            total += n[channel][i];
          }
        }
        System.out.println (": " + total);
        tock++;
        counter = 10000;
      }
      counter--;
      int channel = alt.fairSelect ();
      int packet = c[channel].read ();
      n[channel][packet % nWritersPerChannel] = packet / nWritersPerChannel;
      // for (int chan = 0; chan < channel; chan++) System.out.print ("  ");
      // System.out.println ("channel " + channel +
      //                     " writer " + (packet % nWritersPerChannel) +
      //                     " read " + (packet / nWritersPerChannel));
    }
  }

}
