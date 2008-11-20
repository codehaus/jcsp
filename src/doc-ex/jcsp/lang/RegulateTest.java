import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.plugNplay.*;

class RegulateTest {

  public static void main (String[] args) {

    final One2OneChannel a = Channel.one2one ();
    final One2OneChannel b = Channel.one2one ();
    final One2OneChannel c = Channel.one2one ();
    final One2OneChannel reset = Channel.one2one (new OverWriteOldestBuffer (1));

    new Parallel (
      new CSProcess[] {
        new Numbers (a.out ()),                               // generate numbers
        new FixedDelay (250, a.in (), b.out ()),              // let them through every quarter second
        new Regulate (b.in (), reset.in (), c.out (), 1000),  // initially sample every second
        new CSProcess () {
          public void run () {
            Long[] sample = {new Long (1000), new Long (250), new Long (100)};
            int[] count = {10, 40, 100};
            while (true) {
              for (int cycle = 0; cycle < sample.length; cycle++) {
                reset.out ().write (sample[cycle]);
                System.out.println ("\nSampling every " + sample[cycle] + " ms ...\n");
                for (int i = 0; i < count[cycle]; i++) {
                  Integer n = (Integer) c.in ().read ();
                  System.out.println ("\t==> " + n);
                }
              }
            }
          }
        }
      }
    ).run ();
  }

}
