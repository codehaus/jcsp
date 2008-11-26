import org.jcsp.lang.*;

public class Polling implements CSProcess {

  private final AltingChannelInput in0;
  private final AltingChannelInput in1;
  private final AltingChannelInput in2;
  private final ChannelOutput out;

  public Polling (final AltingChannelInput in0, final AltingChannelInput in1,
                  final AltingChannelInput in2, final ChannelOutput out) {
    this.in0 = in0;
    this.in1 = in1;
    this.in2 = in2;
    this.out = out;
  }

  public void run() {

    final Skip skip = new Skip ();
    final Guard[] guards = {in0, in1, in2, skip};
    final Alternative alt = new Alternative (guards);
    final CSTimer tim = new CSTimer ();

    Object o;

    while (true) {
      switch (alt.priSelect ()) {
        case 0:
          o = in0.read ();
          out.write ("\tin0 ==> " + o + "\n");
        break;
        case 1:
          o = in1.read ();
          out.write ("\t\tin1 ==> " + o + "\n");
        break;
        case 2:
          o = in2.read ();
          out.write ("\t\t\tin2 ==> " + o + "\n");
        break;
        case 3:
          out.write ("skip ...\n");
          tim.after (tim.read () + 50);
        break;
      }
    }

  }

}
