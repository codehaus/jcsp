
import org.jcsp.lang.Channel;
import org.jcsp.lang.One2OneChannelSymmetricInt;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Parallel;

public class PathologicalDemo {

  public static void main (String[] argv) {

    final One2OneChannelSymmetricInt c = Channel.one2oneSymmetricInt ();
    final One2OneChannelSymmetricInt d = Channel.one2oneSymmetricInt ();

    new Parallel (
      new CSProcess[] {
        new SymmetricA (c.in (), d.out ()),
        new SymmetricB (d.in (), c.out ())
      }
    ).run ();
  }

}
