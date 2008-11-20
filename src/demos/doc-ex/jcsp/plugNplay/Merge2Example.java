import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.plugNplay.*;

public class Merge2Example {

  public static void main (String[] argv) {

    final One2OneChannel[] a = Channel.one2oneArray (4);
    final One2OneChannel[] b = Channel.one2oneArray (3, new InfiniteBuffer ());
    final One2OneChannel c = Channel.one2one ();
    final One2OneChannel d = Channel.one2one ();
    final One2OneChannel e = Channel.one2one ();

    new Parallel (
      new CSProcess[] {
        new Mult (2, a[0].in (), b[0].out ()),
        new Mult (3, a[1].in (), b[1].out ()),
        new Mult (5, a[2].in (), b[2].out ()),
        new Merge2 (b[0].in (), b[1].in (), c.out ()),
        new Merge2 (c.in (), b[2].in (), d.out ()),
        new Prefix (1, d.in (), e.out ()),
        new Delta (e.in (), Channel.getOutputArray (a)),
        new Printer (a[3].in (), "--> ", "\n")
      }
    ).run ();

  }

}
