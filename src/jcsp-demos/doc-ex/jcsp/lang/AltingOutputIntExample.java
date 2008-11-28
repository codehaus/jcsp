import org.jcsp.lang.*;
 
public class AltingOutputIntExample implements CSProcess {

  private final AltingChannelOutputInt out0, out1;
  
  public AltingOutputIntExample (final AltingChannelOutputInt out0,
                                 final AltingChannelOutputInt out1) {
    this.out0 = out0;
    this.out1 = out1;
  }

  public void run () {

    final Guard[] altChans = {out0, out1};
    final Alternative alt = new Alternative (altChans);

    while (true) {
      switch (alt.select ()) {
        case 0:
          out0.write (0);
          System.out.println ("out0 written");
        break;
        case 1:
          out1.write (1);
          System.out.println ("out1 written");
        break;
      }
    }

  }

}
