import org.jcsp.lang.*;
import org.jcsp.plugNplay.*;

class FairPlexTimeTest {

  public static void main (String[] args) {

    final long timeout = 5000;                                  // 5 seconds

    final One2OneChannel[] a = Channel.one2oneArray (5, 0);     // poisonable channels  (zero immunity)
    final One2OneChannel b = Channel.one2one (0);               // poisonable channels  (zero immunity)

    new Parallel (
      new CSProcess[] {
        new Generate (a[0].out (), 0),
        new Generate (a[1].out (), 1),
        new Generate (a[2].out (), 2),
        new Generate (a[3].out (), 3),
        new Generate (a[4].out (), 4),
        new FairPlexTime (Channel.getInputArray (a), b.out (), timeout),
        new Printer (b.in (), "FairPlexTimeTest ==> ", "\n")
      }
    ).run ();

  }

}
