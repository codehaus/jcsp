
import org.jcsp.lang.*;

import java.awt.Color;
import java.util.Random;

public class AltingBarrierGadget2 implements CSProcess {

  private final AltingBarrier[] barrier;
  private final AltingChannelInput leftIn, rightIn, click;
  private final ChannelOutput leftOut, rightOut, configure;
  private final Color offColour, standbyColour;
  private final int offInterval, standbyInterval, playInterval;

  public AltingBarrierGadget2 (
    AltingBarrier[] barrier,
    AltingChannelInput leftIn, ChannelOutput leftOut,
    AltingChannelInput rightIn, ChannelOutput rightOut,
    AltingChannelInput click, ChannelOutput configure,
    Color offColour, Color standbyColour,
    int offInterval, int standbyInterval, int playInterval
  ) {
    this.barrier = barrier;
    this.leftIn = leftIn;  this.leftOut = leftOut;
    this.rightIn = rightIn;  this.rightOut = rightOut;
    this.click = click;  this.configure = configure;
    this.offColour = offColour;  this.standbyColour = standbyColour;
    this.offInterval = offInterval;  this.standbyInterval = standbyInterval;
    this.playInterval = playInterval;
  }

  public void run () {

    CSTimer tim = new CSTimer ();

    final Random random = new Random ();

    final Guard[] standbyGuard = new Guard[barrier.length + 1];
    for (int i = 0; i < barrier.length; i++) {
      standbyGuard[i] = barrier[i];
    }
    standbyGuard[barrier.length] = tim;
    final int TIMEOUT = barrier.length;
    Alternative standbyAlt = new Alternative (standbyGuard);

    configure.write (Boolean.FALSE);           // disable mouse clicks

    while (true) {

      configure.write (offColour);
      tim.sleep (random.nextInt (offInterval));

      configure.write (standbyColour);
      tim.setAlarm (tim.read () + random.nextInt (standbyInterval));

      int choice = standbyAlt.fairSelect ();   // magic synchronisation

      if (choice != TIMEOUT) {
	play (choice, random, tim);
      }
      
    }

  }

  private void play (int choice, Random random, CSTimer tim) {

    final boolean RIGHTMOST = (choice == 0);
    final boolean LEFTMOST = (choice == (barrier.length - 1));

    Parcel parcel = null;
    if (RIGHTMOST) {
      parcel = new Parcel (new Color (random.nextInt ()), 0);
    } else {
      parcel = (Parcel) rightIn.read ();
    }

    configure.write (parcel.colour);
    configure.write (String.valueOf (parcel.count));

    while (click.pending ()) click.read ();    // clear any buffered mouse clicks
    configure.write (Boolean.TRUE);            // enable mouse clicks

    parcel.count++;
    if (LEFTMOST) {
      rightOut.write (parcel);                 // bounce
    } else {
      leftOut.write (parcel);                  // forward
    }
    parcel = null;
  
    final Guard[] playGuard = {click, tim, leftIn, rightIn, barrier[choice]};
    final int CLICK = 0, TIMEOUT = 1, LEFT = 2, RIGHT = 3, BARRIER = 4;

    final Alternative playAlt = new Alternative (playGuard);;
    final boolean[] playCondition = {true, RIGHTMOST, true, true, !RIGHTMOST};

    if (RIGHTMOST) tim.setAlarm (tim.read () + playInterval);

    boolean stopping = false;
    
    boolean playing = true;
    while (playing) {
      
      switch (playAlt.priSelect (playCondition)) {

        case CLICK:
	  click.read ();
	  stopping = true;
	break;

	case TIMEOUT:                          // only RIGHTMOST sets a timeout
	  stopping = true;
	  playCondition[TIMEOUT] = false;      // disable timeout (only taken once)
	break;

	case LEFT:
	  parcel = (Parcel) leftIn.read ();
	  if (parcel.poisoned) stopping = true;
	  if (stopping) {
	    if (RIGHTMOST) {
	      barrier[choice].sync ();         // make everyone stop
	      playing = false;                 // we have the parcel
	    } else {
	      parcel.poisoned = true;
	      rightOut.write (parcel);         // forward
	    }
	  } else {
	    configure.write (String.valueOf (parcel.count));
	    parcel.count++;
	    if (RIGHTMOST) {
	      leftOut.write (parcel);          // bounce
	    } else {
	      rightOut.write (parcel);         // forward
	    }
	    parcel = null;
	  }
	break;

	case RIGHT:
	  parcel = (Parcel) rightIn.read ();
	  if (stopping) {
	    parcel.poisoned = true;
	    rightOut.write (parcel);           // bounce
	  } else {
	    configure.write (String.valueOf (parcel.count));
	    parcel.count++;
	    if (LEFTMOST) {
	      rightOut.write (parcel);         // bounce
	    } else {
	      leftOut.write (parcel);          // forward
	    }
	  }
	  parcel = null;
	break;

	case BARRIER:                          // RIGHTMOST => not possible
	  playing = false;
	break;

      }
    }

    configure.write (Boolean.FALSE);           // disable mouse clicks
    configure.write ("");                      // clear button label

  }

}
