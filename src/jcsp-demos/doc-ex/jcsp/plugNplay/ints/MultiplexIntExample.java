import org.jcsp.lang.*;
import org.jcsp.plugNplay.ints.*;

public class MultiplexIntExample {

  public static void main (String[] argv) {

    final One2OneChannelInt[] a = Channel.one2oneIntArray (3);
    final One2OneChannelInt b = Channel.one2oneInt ();

    new Parallel (
      new CSProcess[] {
        new NumbersInt (a[0].out ()),
        new FibonacciInt (a[1].out ()),
        new SquaresInt (a[2].out ()),
        new MultiplexInt (Channel.getInputArray (a), b.out ()),
        new CSProcess () {
          public void run () {
            String[] key = {"Numbers ",
                            "            Fibonacci ",
                            "                          Squares "};
            while (true) {
              System.out.print (key[b.in ().read ()]);   // print channel source
              System.out.println (b.in ().read ());      // print multiplexed data
            }
          }
        }
      }
    ).run ();

  }

}
