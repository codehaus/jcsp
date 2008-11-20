
import org.jcsp.lang.*;

import java.awt.Color;
import java.util.Random;

public class AltingBarrierGadget1 implements CSProcess {

  private final AltingBarrier[] barrier;
  private final AltingChannelInput in, click;
  private final ChannelOutput out, configure;
  private final Color offColour, standbyColour;
  private final int offInterval, standbyInterval;
  private final int playInterval, flashInterval;

  public AltingBarrierGadget1 (
    AltingBarrier[] barrier,
    AltingChannelInput in, ChannelOutput out,
    AltingChannelInput click, ChannelOutput configure,
    Color offColour, Color standbyColour,
    int offInterval, int standbyInterval,
    int playInterval, int flashInterval
  ) {
    this.barrier = barrier;
    this.in = in;  this.out = out;
    this.click = click;  this.configure = configure;
    this.offColour = offColour;  this.standbyColour = standbyColour;
    this.offInterval = offInterval;  this.standbyInterval = standbyInterval;
    this.playInterval = playInterval;  this.flashInterval = flashInterval;
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

    configure.write (Boolean.FALSE);               // disable mouse clicks
                                                   // (not used by this gadget)
    while (true) {

      configure.write (offColour);
      tim.sleep (random.nextInt (offInterval));

      configure.write (standbyColour);
      tim.setAlarm (tim.read () + random.nextInt (standbyInterval));

      int choice = standbyAlt.fairSelect ();       // magic synchronisation

      if (choice != TIMEOUT) {
        play (choice, random, tim);
      }
      
    }

  }

  private void play (int choice, Random random, CSTimer tim) {
    
    final boolean RIGHTMOST = (choice == 0);
    final boolean LEFTMOST = (choice == (barrier.length - 1));

    Color colour = null;
    if (RIGHTMOST) {
      colour = new Color (random.nextInt ());
    } else {
      colour = (Color) in.read ();
    }
    Color brighter = colour.brighter ();

    if (!LEFTMOST) out.write (colour);             // pass it on

    final AltingBarrier focus = barrier[choice];

    final int count = playInterval/flashInterval;

    long timeout = tim.read () + flashInterval;
    
    boolean bright = true;

    for (int i = 0; i < count; i++) {
      configure.write (bright ? brighter : colour);
      bright = !bright;
      if (RIGHTMOST) {
        tim.after (timeout);
	timeout += flashInterval;
      }
      focus.sync ();
    }

  }

}
