
import org.jcsp.lang.*;
import java.util.Random;

class SymmetricB implements CSProcess {

  private final AltingChannelInputInt in;
  private final AltingChannelOutputInt out;

  public SymmetricB (AltingChannelInputInt in, AltingChannelOutputInt out) {
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
      b++;
      switch (alt.fairSelect ()) {
        case IN:
          a = in.read ();
	  System.out.println ("\t\t\t" + a);
        break;
        case OUT:
          out.write (b);
	  System.out.println ("\t" + b);
        break;
      }
    }
  }

}
