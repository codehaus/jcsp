import org.jcsp.lang.*;
import org.jcsp.plugNplay.*;

class PollingTest {

  public static void main (String[] args) {

    final One2OneChannel[] a = Channel.one2oneArray (3);
    final One2OneChannel b = Channel.one2one ();

    new Parallel (
      new CSProcess[] {
        new Regular (a[0].out (), 100, 100),
        new Regular (a[1].out (), 250, 250),
        new Regular (a[2].out (), 1000, 1000),
        new Polling (a[0].in (), a[1].in (), a[2].in (), b.out ()),
        new Printer (b.in ())
      }
    ).run ();

  }

}
