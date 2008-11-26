
  /*************************************************************************
  *                                                                        *
  *  JCSP ("CSP for Java") libraries                                       *
  *  Copyright (C) 1996-2008 Peter Welch and Paul Austin.                  *
  *                                                                        *
  *  This library is free software; you can redistribute it and/or         *
  *  modify it under the terms of the GNU Lesser General Public            *
  *  License as published by the Free Software Foundation; either          *
  *  version 2.1 of the License, or (at your option) any later version.    *
  *                                                                        *
  *  This library is distributed in the hope that it will be useful,       *
  *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU     *
  *  Lesser General Public License for more details.                       *
  *                                                                        *
  *  You should have received a copy of the GNU Lesser General Public      *
  *  License along with this library; if not, write to the Free Software   *
  *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307,  *
  *  USA.                                                                  *
  *                                                                        *
  *  Author contact: P.H.Welch@ukc.ac.uk                                   *
  *                                                                        *
  *************************************************************************/

// package org.jcsp.plugNplay;
//
// This class *is* in the above package.  It is reproduced here because
// this source is quoted in the documentation of org.jcsp.lang.Alternative.
// The above package declaration is commented out since, otherwise,
// compiling the *java files in this directory would fail (because of
// an attempt to duplicate Regulate in org.jcsp.plugNplay).

//{{{  javadoc
/**
 * This process controls the flow of traffic from its in to out channels.
 * <H2>Description</H2>
 * <tt>Regulate</tt> produces a constant rate of output flow, regardless of
 * the rate of its input. At the end of each timeslice defined by the required
 * output rate, it outputs the last object input during that timeslice.
 * If nothing has come in during a timeslice, the previous output will be repeated
 * (note: this will be a null if nothing has ever arrived).
 * If the input flow is greater than the required output flow, data will be discarded.
 * <P>
 * The interval (in msecs) defining the output flow rate is given by a constructor
 * argument; but this can be changed at any time by sending a new interval (as a <tt>Long</tt>)
 * down its <tt>reset</tt> channel.
 * <H2>Description</H2>
 * See {@link Alternative#STFR here}.
 *
 * @see org.jcsp.plugNplay.FixedDelay
 * @see org.jcsp.plugNplay.Regular
 *
 * @author P.H.Welch
 *
 */
//}}}

import org.jcsp.lang.*;

public class Regulate implements CSProcess {

  private final AltingChannelInput in, reset;
  private final ChannelOutput out;
  private final long initialInterval;

  /**
    * Construct the process.
    * 
    * @param in the input channel
    * @param out the output channel
    * @param initialInterval the initial interval between outputs (in milliseconds)
    * @param reset send a <tt>Long</tt> down this to change the interval between outputs (in milliseconds)
    */
  public Regulate (final AltingChannelInput in, final AltingChannelInput reset,
                   final ChannelOutput out, final long initialInterval) {
    this.in = in;
    this.reset = reset;
    this.out = out;
    this.initialInterval = initialInterval;
  }

  /**
    * The main body of this process.
    */
  public void run () {

    final CSTimer tim = new CSTimer ();

    final Guard[] guards = {reset, tim, in};              // prioritised order
    final int RESET = 0;                                  // index into guards
    final int TIM = 1;                                    // index into guards
    final int IN = 2;                                     // index into guards

    final Alternative alt = new Alternative (guards);

    Object x = null;                                      // holding object

    long interval = initialInterval;

    long timeout = tim.read () + interval;
    tim.setAlarm (timeout);

    while (true) {
      switch (alt.priSelect ()) {
        case RESET:
          interval = ((Long) reset.read ()).longValue ();
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
