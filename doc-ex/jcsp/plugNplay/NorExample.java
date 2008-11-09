import org.jcsp.lang.*;
import org.jcsp.plugNplay.*;

public class NorExample {

  public static void main (String[] argv) {

    final One2OneChannel a = Channel.one2one ();
    final One2OneChannel b = Channel.one2one ();
    final One2OneChannel c = Channel.one2one ();
    final One2OneChannel d = Channel.one2one ();

    new Parallel (
      new CSProcess[] {
        new Numbers (a.out ()),
        new Generate (b.out (), 0),
        new Nor (a.in (), b.in (), c.out ()),
        new Successor (c.in (), d.out ()),
        new Printer (d.in (), "--> ", "\n")
      }
    ).run ();

  }

}
