import org.jcsp.lang.*;
import org.jcsp.plugNplay.ints.*;
import org.jcsp.plugNplay.*;

public class SignIntExample {

  public static void main (String[] argv) {

    final One2OneChannelInt[] a = Channel.one2oneIntArray (3);
    final One2OneChannel[] b = Channel.one2oneArray (3);
    final One2OneChannel c = Channel.one2one ();

    new Parallel (
      new CSProcess[] {
        new NumbersInt (a[0].out ()),
        new FibonacciInt (a[1].out ()),
        new SquaresInt (a[2].out ()),
        new SignInt ("Numbers ", a[0].in (), b[0].out ()),
        new SignInt ("            Fibonacci ", a[1].in (), b[1].out ()),
        new SignInt ("                          Squares ", a[2].in (), b[2].out ()),
        new Plex (Channel.getInputArray (b), c.out ()),
        new Printer (c.in (), "", "\n")
      }
    ).run ();

  }

}
