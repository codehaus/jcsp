
import org.jcsp.lang.*;
import org.jcsp.util.*;

import java.awt.Color;
import java.util.Random;

public class AltingBarrierGadget3 implements CSProcess {

  private final ChannelInput in;
  private final ChannelOutput[] out;
  private final AltingChannelInput click;
  private final ChannelOutput configure;
  private final Color offColour, standbyColour;
  private final int offInterval, standbyInterval;
  private final int playInterval, countInterval;
  private final String[] label;

  public AltingBarrierGadget3 (
    ChannelInput in,
    ChannelOutput[] out,
    AltingChannelInput click, ChannelOutput configure,
    Color offColour, Color standbyColour,
    int offInterval, int standbyInterval,
    int playInterval, int countInterval,
    String[] label
  ) {
    this.in = in;  this.out = out;
    this.click = click;  this.configure = configure;
    this.offColour = offColour;  this.standbyColour = standbyColour;
    this.offInterval = offInterval;  this.standbyInterval = standbyInterval;
    this.playInterval = playInterval;  this.countInterval = countInterval;
    this.label = label;
  }

  public void run () {

    CSTimer tim = new CSTimer ();

    final Random random = new Random ();

    // make alting barrier and shared variables
    // for the group this gadget is leading ...

    final AltingBarrier[] myAltingBarrier = AltingBarrier.create (out.length + 1);
    final Any2OneChannel myChannel = Channel.any2one(new OverWritingBuffer (1));
    final Shared myShared = new Shared (myChannel.out());

    // make receiving arrays for the barrier and shared variables
    // from the other group leaders

    final Guard[] standbyGuard = new Guard[out.length + 2];

    final int MY_INDEX = out.length;    
    standbyGuard[MY_INDEX] = myAltingBarrier[MY_INDEX];

    final int TIMEOUT = out.length + 1;
    standbyGuard[TIMEOUT] = tim;
    
    final Shared[] shared = new Shared[out.length + 1];
    shared[MY_INDEX] = myShared;

    // parallel-I/O distribution of barriers and shared variables

    new Parallel (
      new CSProcess[] {
      
        new CSProcess () {
          public void run () {
	    for (int i = 0; i < out.length; i++) {
	      synchronized (out[i]) {
	        out[i].write (myAltingBarrier[i]);
	        out[i].write (myShared);
	      }
	    }
	  }
	},
	
        new CSProcess () {
          public void run () {
	    for (int i = 0; i < out.length; i++) {
	      standbyGuard[i] = (AltingBarrier) in.read ();
	      shared[i] = (Shared) in.read ();
	    }
	  }
	}
	
      }
    ).run ();

    // start real work ...
    
    Alternative standbyAlt = new Alternative (standbyGuard);

    configure.write (Boolean.FALSE);           // disable mouse clicks

    while (true) {

      configure.write (offColour);
      tim.sleep (random.nextInt (offInterval));

      configure.write (standbyColour);
      tim.setAlarm (tim.read () + random.nextInt (standbyInterval));

      int choice = standbyAlt.fairSelect ();   // magic synchronisation

      if (choice == MY_INDEX) {
        playLeader (
          (AltingBarrier) standbyGuard[choice],
          shared[choice], myChannel.in(), random, tim
        );
      } 
      else if (choice != TIMEOUT) {
        play ((AltingBarrier) standbyGuard[choice], shared[choice]);
      }

      
    }

  }

  private void playLeader (
    AltingBarrier bar, Shared shared, AltingChannelInput myChannel,
    Random random, CSTimer tim
  ) {

    while (click.pending ()) click.read ();      // clear any buffered mouse clicks
    configure.write (Boolean.TRUE);              // enable mouse clicks
    
    while (myChannel.pending ()) myChannel.read ();   // clear any buffered cancels
    
    int count = 0;
    long countTimeout = 0;

    Alternative leaderAlt = new Alternative (new Guard[] {click, myChannel, tim});
    final int CLICK = 0, CANCEL = 1, TIM = 2;
    
    count = (playInterval/countInterval) - 1;
      
    shared.ok = (count >= 0);                    // initialise shared variables
    shared.label = label[count];
    shared.colour = new Color (random.nextInt ());
    shared.brighter = shared.colour.brighter ();
        
    bar.sync ();                                 // allow inspection of shared variables

    boolean bright = true;

    countTimeout = tim.read () + countInterval;  // set first timeout
    tim.setAlarm (countTimeout);

    while (shared.ok) {
      
      configure.write (bright ? shared.brighter : shared.colour);
      bright = !bright;

      configure.write (shared.label);

      bar.sync ();                               // allow update of shared variables

      count--;
      if (count < 0) {
        shared.ok = false;                       // game over
      } else {
        shared.label = label[count];
      }

      switch (leaderAlt.priSelect ()) {
        case CLICK:                              // our button clicked
          click.read ();
          shared.ok = false;                     // game over
        break;
        case CANCEL:                             // someone else's button clicked
          myChannel.read ();                     // (they will have set shared.ok)
        break;
        case TIM:                                // timeout - move on
          countTimeout += countInterval;
          tim.setAlarm (countTimeout);
        break;
      }

      bar.sync ();                               // allow inspection of shared variables

    }

    configure.write (Boolean.FALSE);             // disable mouse clicks
    configure.write ("");                        // clear button label

  }

  private void play (AltingBarrier bar, Shared shared) {

    while (click.pending ()) click.read ();      // clear any buffered mouse clicks
    configure.write (Boolean.TRUE);              // enable mouse clicks

    Alternative followerAlt = new Alternative (new Guard[] {click, bar});
    final int CLICK = 0, BAR = 1;
        
    bar.sync ();                                 // allow inspection of shared variables

    boolean bright = true;

    while (shared.ok) {
      
      configure.write (bright ? shared.brighter : shared.colour);
      bright = !bright;

      configure.write (shared.label);
    
      bar.sync ();                               // allow update of shared variables

      switch (followerAlt.priSelect ()) {
        case CLICK:                              // our button clicked
          click.read ();
          shared.toLeader.write (null);          // cancel leader timeout (if any)
          shared.ok = false;                     // game over (we must set this)
          bar.sync ();                           // allow inspection of shared variables
        break;
        case BAR:                                // allow inspection of shared variables
        break;
      }

    }

    configure.write (Boolean.FALSE);             // disable mouse clicks
    configure.write ("");                        // clear button label

  }

}
