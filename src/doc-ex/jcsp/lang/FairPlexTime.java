import org.jcsp.lang.*;

public class FairPlexTime implements CSProcess {

  private final AltingChannelInput[] in;
  private final ChannelOutput out;
  private final long timeout;

  public FairPlexTime (final AltingChannelInput[] in, final ChannelOutput out,
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
    tim.setAlarm (tim.read () + timeout);
    while (running) {
      final int index = alt.fairSelect ();
      // final int index = alt.priSelect ();
      if (index == timerIndex) {
        running = false;
      } else {
        out.write (in[index].read ());
      }
    }
    System.out.println ("\n\r\tFairPlexTime: timed out ... poisoning all channels ...");
    for (int i = 0; i < in.length; i++) {
      in[i].poison (42);
    }
    out.poison (42);

  }

}
