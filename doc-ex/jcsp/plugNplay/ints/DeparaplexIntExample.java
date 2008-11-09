import org.jcsp.lang.*;
import org.jcsp.plugNplay.ints.*;

public class DeparaplexIntExample {

  public static void main (String[] args) {

    final One2OneChannelInt[] a = Channel.one2oneIntArray (3);
    final One2OneChannel b = Channel.one2one ();
    final One2OneChannelInt[] c = Channel.one2oneIntArray (3);
    final One2OneChannel d = Channel.one2one ();

    new Parallel (
      new CSProcess[] {
        new NumbersInt (a[0].out ()),
        new SquaresInt (a[1].out ()),
        new FibonacciInt (a[2].out ()),
        new ParaplexInt (Channel.getInputArray (a), b.out ()),
        new DeparaplexInt (b.in (), Channel.getOutputArray (c)),
        new ParaplexInt (Channel.getInputArray (c), d.out ()),
        new CSProcess () {
          public void run () {
            System.out.println ("\n\t\tNumbers\t\tSquares\t\tFibonacci\n");
            while (true) {
              int[] data = (int[]) d.in ().read ();
              for (int i = 0; i < data.length; i++) {
                System.out.print ("\t\t" + data[i]);
              }
              System.out.println ();
            }
          }
        }
      }
    ).run ();
  }

}
