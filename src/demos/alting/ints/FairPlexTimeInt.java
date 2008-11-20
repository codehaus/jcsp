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
public class FairPlexTimeInt implements CSProcess {

  private final AltingChannelInputInt[] in;
  private final ChannelOutputInt out;
  private final long timeout;

  public FairPlexTimeInt (final AltingChannelInputInt[] in, final ChannelOutputInt out,
                          final long timeout) {
    this.in = in;
    this.out = out;
    this.timeout = timeout;
  }

  public void run () {

    final Guard[] guards = new Guard[in.length + 1];
    System.arraycopy (in, 0, guards, 0, in.length);

    final CSTimer tim = new CSTimer ();
    final int timerIndex = in.length;
    guards[timerIndex] = tim;

    final Alternative alt = new Alternative (guards);

    boolean running = true;
    tim.setAlarm (System.currentTimeMillis () + timeout);
    while (running) {
      final int index = alt.fairSelect ();
      if (index == timerIndex) {
        running = false;
      } else {
        out.write (in[index].read ());
      }
    }
    System.out.println ("Goodbye from FairPlexTime ...");
    System.exit(0);
  }

}
