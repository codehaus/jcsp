import org.jcsp.lang.*;
import org.jcsp.plugNplay.Printer;

class FairPlexTest {

  public static void main (String[] args) {

    final One2OneChannel[] a = Channel.one2oneArray (5);
    final One2OneChannel b = Channel.one2one ();

    new Parallel (
      new CSProcess[] {
        new Regular (a[0].out (), 0, 5),
        new Regular (a[1].out (), 1, 5),
        new Regular (a[2].out (), 2, 5),
        new Regular (a[3].out (), 3, 5),
        new Regular (a[4].out (), 4, 5),
        new FairPlex (Channel.getInputArray (a), b.out ()),
        new Printer (b.in (), "FairPlexTest ==> ", "\n")
      }
    ).run ();

  }

}
