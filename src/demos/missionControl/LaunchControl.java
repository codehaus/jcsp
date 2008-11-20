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
  
// LaunchControl is a process to control the launch of a space rocket.

// It is configured with a countdown time in seconds -- this must be above
// a minimum threshold, MIN_COUNTDOWN, else the launch is immediately aborted.

// There are two control lines, abort and hold, that respectively abort
// or hold the launch if signalled.  The hold is released by a second
// signal on the same line.

// During countdown, the count is reported by outputting on the countdown
// channel.

// If not aborted, LaunchControl fires the rocket (by outputting on its fire
// channel) when the countdown reaches zero.  An ABORTED launch is also
// reported on this fire channel.

// After a successful or aborted launch, LaunchControl terminates.
// The status attribute records whether the launch was FIRED or ABORTED
// and may then be inspected.

/**
 * @author P.H. Welch
 */
public class LaunchControl implements CSProcess {
 
  public static final int MIN_COUNTDOWN = 10;

  public static final int FIRED = 0;
  public static final int HOLDING = 1;
  public static final int COUNTING = 2;
  public static final int ABORTED = 3;
  public static final int UNDEDFINED = 4;
 
  private final int start;
  private final AltingChannelInputInt abort;
  private final AltingChannelInputInt hold;
  private final ChannelOutputInt countdown;
  private final ChannelOutputInt fire;

  private int status = UNDEDFINED;
 
  public LaunchControl (final int start,
                        final AltingChannelInputInt abort,
                        final AltingChannelInputInt hold,
                        final ChannelOutputInt countdown,
                        final ChannelOutputInt fire) {
    this.start = start;
    this.abort = abort;
    this.hold = hold;
    this.countdown = countdown;
    this.fire = fire;
  }
 
  public int getStatus () {                         // inspection method
    return status;                                  // (can only be used
  }                                                 // in between runs)
 
  public void run () {
 
    final CSTimer tim = new CSTimer ();             // JCSP timers have
    final long oneSecond = 1000;                    // millisecond granularity

    long timeout = tim.read () + oneSecond;         // compute first timeout
 
    final Alternative alt =
      new Alternative (new Guard[] {abort, hold, tim});
    final int ABORT = 0;
    final int HOLD = 1;
    final int TICK = 2;
 
    fire.write (COUNTING);                          // signal rocket

    int count = start;
    boolean counting = (start >= MIN_COUNTDOWN);    // abort if bad start
 
    while ((count > 0) && counting) {
System.out.println ("********** LaunchControl: tick");
      countdown.write (count);                      // public address system
      tim.setAlarm (timeout);                       // set next timeout
      switch (alt.priSelect ()) {
        case ABORT:                                 // abort signalled
          abort.read ();                            // clear the signal
          counting = false;
        break;
        case HOLD:                                  // hold signalled
          long timeLeft = timeout - tim.read ();    // time till next tick
          hold.read ();                             // clear the signal
          fire.write (HOLDING);                     // signal rocket
          hold.read ();                             // wait for the release
          timeout = tim.read () + timeLeft;         // recompute next timeout
          fire.write (COUNTING);                    // signal rocket
        break;
        case TICK:                                  // timeout expired
          count--;
          timeout += oneSecond;                     // compute next timeout
        break;
      }
    }

    status = (counting) ? FIRED : ABORTED;          // set status attribute
    fire.write (status);                            // signal rocket (go/nogo)
    if (counting) countdown.write (0);              // complete countdown

  }
 
}
