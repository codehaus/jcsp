import org.jcsp.lang.*;
  
// LaunchControl is a process to control the launch of a space rocket.

// It is configured with a countdown time in seconds -- this must be above
// a minimum threshold, MIN_COUNTDOWN, else the launch is immediately aborted.

// There are two control lines, abort and hold, that respectively abort
// or hold the launch if signalled.  The hold is released by a second
// signal on the same line.

// During countdown, the count is reported by outputting on the countdown channel.

// If not aborted, LaunchControl fires the rocket (by outputting on its fire
// channel) when the countdown reaches zero.  An ABORTED launch is also reported
// on this fire channel.

// After a successful or aborted launch, LaunchControl terminates.

public class LaunchControl implements CSProcess {
 
  public static final int MIN_COUNTDOWN = 10;

  public static final int FIRED = 0;
  public static final int HOLDING = -1;
  public static final int ABORTED = -2;
 
  private final int start;
  private final AltingChannelInputInt abort;
  private final AltingChannelInputInt hold;
  private final ChannelOutputInt countdown;
  private final ChannelOutputInt fire;

  private int status;
 
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
    return status;                                  // not to be used while running!
  }
 
  public void run () {
 
    final CSTimer tim = new CSTimer ();             // JCSP timers have
    final long oneSecond = 1000;                    // millisecond granularity

    long timeout = tim.read () + oneSecond;         // compute first timeout
 
    final Alternative alt =
      new Alternative (new Guard[] {abort, hold, tim});
    final int ABORT = 0;
    final int HOLD = 1;
    final int TICK = 2;
 
    int count = start;
    boolean counting = (start >= MIN_COUNTDOWN);    // abort if bad start
 
    while ((count > 0) && counting) {
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
          fire.write (HOLDING);                     // signal rocket
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
