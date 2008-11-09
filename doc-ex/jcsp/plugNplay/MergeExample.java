import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.plugNplay.*;

public class MergeExample {

  public static void main (String[] argv) {

    final One2OneChannel[] a = Channel.one2oneArray (5);
    final One2OneChannel[] b = Channel.one2oneArray (4, new InfiniteBuffer ());
    final One2OneChannel c = Channel.one2one ();
    final One2OneChannel d = Channel.one2one ();

    new Parallel (
      new CSProcess[] {
        new Mult (2, a[0].in (), b[0].out ()),
        new Mult (3, a[1].in (), b[1].out ()),
        new Mult (5, a[2].in (), b[2].out ()),
        new Mult (7, a[3].in (), b[3].out ()),
        new Merge (Channel.getInputArray (b), c.out ()),
        new Prefix (1, c.in (), d.out ()),
        new Delta (d.in (), Channel.getOutputArray (a)),
        new Printer (a[4].in (), "--> ", "\n")
      }
    ).run ();

  }

}
