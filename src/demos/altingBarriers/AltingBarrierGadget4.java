
import org.jcsp.lang.*;
import org.jcsp.util.*;

import java.awt.Color;
import java.util.*;

public class AltingBarrierGadget4 implements CSProcess {

  private final AltingChannelInput in;
  private final ChannelOutput[] out;
  private final Barrier bar;
  private final AltingChannelInput click;
  private final ChannelOutput configure;
  private final Color offColour, standbyColour;
  private final int offInterval, standbyInterval;
  private final int playInterval, countInterval;
  private final String[] label;

  public AltingBarrierGadget4 (
    AltingChannelInput in, ChannelOutput[] out, Barrier bar,
    AltingChannelInput click, ChannelOutput configure,
    Color offColour, Color standbyColour,
    int offInterval, int standbyInterval,
    int playInterval, int countInterval,
    String[] label
  ) {
    this.in = in;  this.out = out; this.bar = bar;
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
    final Any2OneChannel myChannel = Channel.any2one (new OverWritingBuffer (1));
    final Shared myShared = new Shared (myChannel.out());

    // parallel-I/O distribution of barriers and shared variables

    final One2OneChannel finished = Channel.one2one ();   // used only once

    new ProcessManager  (
      new CSProcess () {
        public void run () {
          for (int i = 0; i < out.length; i++) {
            synchronized (out[i]) {
              out[i].write (myAltingBarrier[i]);
              out[i].write (myShared);
            }
          }
          bar.sync ();               // wait for all gadgets to finish output
          finished.out().write (null);     // inform this gadget
        }
      }
    ).start ();

    ArrayList dump =                            // temporary storage
      new ArrayList (2*out.length);             // (guessed initial size)
      
    Alternative distributeAlt =
      new Alternative (new Guard[] {in, finished.in()});
    
    final int IN = 0, FINISHED = 1;
    
    int count = 0;
    boolean more = true;
    
    while (more) {
      switch (distributeAlt.priSelect ()) {
        case IN:
          dump.add (in.read ());                 // an AltingBarrier
          dump.add (in.read ());                 // Shared variables
          count++;
        break;
        case FINISHED:
          finished.in().read ();
          more = false;
        break;
      }
    }

    distributeAlt = null;                        // discard no longer needed item

    // make receiving arrays for the barrier and shared variables
    // from the other group leaders

    final Guard[] standbyGuard = new Guard[count + 2];

    final int MY_INDEX = count;    
    standbyGuard[MY_INDEX] = myAltingBarrier[out.length];

    final int TIMEOUT = count + 1;
    standbyGuard[TIMEOUT] = tim;
    
    final Shared[] shared = new Shared[count + 1];
    shared[MY_INDEX] = myShared;

    Iterator iterator = dump.iterator ();
    for (int i = 0; i < count; i++) {
      standbyGuard[i] = (AltingBarrier) iterator.next ();
      shared[i] = (Shared) iterator.next ();
    }

    iterator = null;                             // discard no longer needed item
    dump = null;                                 // discard no longer needed item

    // start real work ...
    
    Alternative standbyAlt = new Alternative (standbyGuard);

    configure.write (Boolean.FALSE);             // disable mouse clicks

    while (true) {

      configure.write (offColour);
      tim.sleep (random.nextInt (offInterval));

      configure.write (standbyColour);
      tim.setAlarm (tim.read () + random.nextInt (standbyInterval));

      int choice = standbyAlt.fairSelect ();     // magic synchronisation

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
    
    Alternative leaderAlt = new Alternative (new Guard[] {click, myChannel, tim});
    final int CLICK = 0, CANCEL = 1, TIM = 2;
    
    int count = (playInterval/countInterval) - 1;

    shared.ok = (count >= 0);                    // initialise shared shared
    shared.label = label[count];
    shared.colour = new Color (random.nextInt ());
    shared.brighter = shared.colour.brighter ();
        
    bar.sync ();                                 // allow inspection of shared variables

    boolean bright = true;

    long countTimeout = 0;
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
