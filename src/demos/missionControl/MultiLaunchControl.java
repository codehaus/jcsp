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
public class MultiLaunchControl implements CSProcess {
 
  private final int interval;
  private final int start;
  private final AltingChannelInputInt abort;
  private final ChannelOutputInt cancel;
  private final AltingChannelInputInt hold;
  private final ChannelOutputInt countdown;
  private final ChannelOutputInt fire;
 
  public MultiLaunchControl (final int interval,
                             final int start,
                             final AltingChannelInputInt abort,
                             final ChannelOutputInt cancel,
                             final AltingChannelInputInt hold,
                             final ChannelOutputInt countdown,
                             final ChannelOutputInt fire) {
    this.interval = interval;
    this.start = start;
    this.abort = abort;
    this.cancel = cancel;
    this.hold = hold;
    this.countdown = countdown;
    this.fire = fire;
  }

  public void run () {

    final LaunchControl launchControl =
      new LaunchControl (start, abort, hold, countdown, fire);

    final CSTimer tim = new CSTimer ();
    final long seconds = 1000;

    final Alternative alt = new Alternative (new Guard[] {tim, hold});
    final int TIMEOUT = 0;
    final int HOLD = 1;

    hold.read ();                                         // start signal

    while (true) {

      cancel.write (0);                                   // enable abort

      launchControl.run ();
      int status = launchControl.getStatus ();

      cancel.write (status);                              // graceful
      if (status == LaunchControl.FIRED) abort.read ();   // reset

      boolean waiting = true;
      tim.setAlarm (tim.read () + (interval*seconds));

      while (waiting) {
        switch (alt.priSelect ()) {
          case TIMEOUT:
            waiting = false;
          break;
          case HOLD:
            hold.read ();
            fire.write (LaunchControl.HOLDING);
            hold.read ();
            fire.write (status);
          break;
        }
      }

    }

  }

}
