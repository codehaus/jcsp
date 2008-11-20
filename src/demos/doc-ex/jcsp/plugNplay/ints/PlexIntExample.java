import org.jcsp.lang.*;
import org.jcsp.plugNplay.ints.*;

public class PlexIntExample {

  public static void main (String[] argv) {

    final One2OneChannelInt[] a = Channel.one2oneIntArray (3);
    final One2OneChannelInt b = Channel.one2oneInt ();

    new Parallel (
      new CSProcess[] {
        new NumbersInt (a[0].out ()),
        new FibonacciInt (a[1].out ()),
        new SquaresInt (a[2].out ()),
        new PlexInt (Channel.getInputArray (a), b.out ()),
        new PrinterInt (b.in (), "--> ", "\n")
      }
    ).run ();

  }

}
