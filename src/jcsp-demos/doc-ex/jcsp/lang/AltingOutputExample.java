import org.jcsp.lang.*;
 
public class AltingOutputExample implements CSProcess {

  private final AltingChannelOutput out0, out1;
  
  public AltingOutputExample (final AltingChannelOutput out0,
                              final AltingChannelOutput out1) {
    this.out0 = out0;
    this.out1 = out1;
  }

  public void run () {

    final Guard[] altChans = {out0, out1};
    final Alternative alt = new Alternative (altChans);

    while (true) {
      switch (alt.select ()) {
        case 0:
          out0.write (new Integer(0));
          System.out.println ("out0 written");
        break;
        case 1:
          out1.write (new Integer(1));
          System.out.println ("out1 written");
        break;
      }
    }

  }

}
