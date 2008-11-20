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
public final class RegulateInt implements CSProcess {

  private final AltingChannelInputInt in, reset;
  private final ChannelOutputInt out;
  private final long initialInterval;

  public RegulateInt (final AltingChannelInputInt in, final AltingChannelInputInt reset,
                      final ChannelOutputInt out, final long initialInterval) {
    this.in = in;
    this.reset = reset;
    this.out = out;
    this.initialInterval = initialInterval;
  }

  public void run () {

    final CSTimer tim = new CSTimer ();

    final Guard[] guards = {reset, tim, in};              // prioritised order
    final int RESET = 0;                                  // index into guards
    final int TIM = 1;                                    // index into guards
    final int IN = 2;                                     // index into guards

    final Alternative alt = new Alternative (guards);

    int x = 0;                                      // holding object

    long interval = initialInterval;

    long timeout = tim.read () + interval;
    tim.setAlarm (timeout);

    while (true) {
      switch (alt.priSelect ()) {
        case RESET:
          interval = (long) reset.read ();
          timeout = tim.read ();                          // fall through
        case TIM:
          out.write (x);
          timeout += interval;
          tim.setAlarm (timeout);
        break;
        case IN:
          x = in.read ();
        break;
      }
    }

  }

}
