import org.jcsp.lang.*;
import org.jcsp.plugNplay.*;

class ParaplexExample {

  public static void main (String[] args) {

    final One2OneChannel[] a = Channel.one2oneArray (3);
    final One2OneChannel b = Channel.one2one ();

    new Parallel (
      new CSProcess[] {
        new Numbers (a[0].out ()),
        new Squares (a[1].out ()),
        new Fibonacci (a[2].out ()),
        new Paraplex (Channel.getInputArray (a), b.out ()),
        new CSProcess () {
          public void run () {
            ChannelInput in = b.in ();
            System.out.println ("\n\t\tNumbers\t\tSquares\t\tFibonacci\n");
            while (true) {
              Object[] data = (Object[]) in.read ();
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
