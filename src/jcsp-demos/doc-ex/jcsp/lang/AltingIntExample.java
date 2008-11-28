import org.jcsp.lang.*;

public class AltingIntExample implements CSProcess {

  private final AltingChannelInputInt in0, in1;
  
  public AltingIntExample (final AltingChannelInputInt in0,
                           final AltingChannelInputInt in1) {
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
