import org.jcsp.lang.*;
import org.jcsp.plugNplay.*;

public class MultiplexExample {

  public static void main (String[] argv) {

    final One2OneChannel[] a = Channel.one2oneArray (3);
    final One2OneChannel b = Channel.one2one ();

    new Parallel (
      new CSProcess[] {
        new Numbers (a[0].out ()),
        new Fibonacci (a[1].out ()),
        new Squares (a[2].out ()),
        new Multiplex (Channel.getInputArray (a), b.out ()),
        new CSProcess () {
          public void run () {
            String[] key = {"Numbers ",
                            "            Fibonacci ",
                            "                          Squares "};
            while (true) {
              int channel = ((Integer) b.in ().read ()).intValue ();
              System.out.print (key[channel]);     // print channel source
              int n = ((Integer) b.in ().read ()).intValue ();
              System.out.println (n);              // print multiplexed data
            }
          }
        }
      }
    ).run ();

  }

}
