import org.jcsp.lang.*;

public class AltingExample implements CSProcess {

  private final AltingChannelInput in0, in1;
  
  public AltingExample (final AltingChannelInput in0,
                        final AltingChannelInput in1) {
    this.in0 = in0;
    this.in1 = in1;
  }

  public void run () {

    final Guard[] altChans = {in0, in1};
    final Alternative alt = new Alternative (altChans);

    while (true) {
      switch (alt.select ()) {
        case 0:
          System.out.println ("in0 read " + in0.read ());
        break;
        case 1:
          System.out.println ("in1 read " + in1.read ());
        break;
      }
    }

  }

}
