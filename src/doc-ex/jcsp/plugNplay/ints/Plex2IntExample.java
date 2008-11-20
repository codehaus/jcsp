import org.jcsp.lang.*;
import org.jcsp.plugNplay.ints.*;

public class Plex2IntExample {

  public static void main (String[] argv) {

    final One2OneChannelInt a = Channel.one2oneInt ();
    final One2OneChannelInt b = Channel.one2oneInt ();
    final One2OneChannelInt c = Channel.one2oneInt ();

    new Parallel (
      new CSProcess[] {
        new FibonacciInt (a.out ()),
        new SquaresInt (b.out ()),
        new Plex2Int (a.in (), b.in (), c.out ()),
        new PrinterInt (c.in (), "--> ", "\n")
      }
    ).run ();

  }

}
