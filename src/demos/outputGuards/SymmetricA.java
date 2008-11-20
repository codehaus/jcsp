
import org.jcsp.lang.*;
import java.util.Random;

class SymmetricA implements CSProcess {

  private final AltingChannelInputInt in;
  private final AltingChannelOutputInt out;

  public SymmetricA (AltingChannelInputInt in, AltingChannelOutputInt out) {
    this.in = in;
    this.out = out;
  }

  public void run () {
    
    final Alternative alt = new Alternative (new Guard[] {in , out});
    final int IN = 0, OUT = 1;

    final Random rand = new Random ();
    final CSTimer tim = new CSTimer ();
    
    int a = -1, b = -1;
    
    while (true) {
      int period = (rand.nextInt ()) % 16;
      tim.sleep (period);
      a++;
      switch (alt.fairSelect ()) {
        case IN:
          b = in.read ();
	  System.out.println (b);
        break;
        case OUT:
          out.write (a);
	  System.out.println ("\t\t" + a);
        break;
      }
    }
  }

}
