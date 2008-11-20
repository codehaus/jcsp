import org.jcsp.lang.*;
import org.jcsp.plugNplay.*;

public class AndExample {

  public static void main (String[] argv) {

    final One2OneChannel a = Channel.one2one ();
    final One2OneChannel b = Channel.one2one ();
    final One2OneChannel c = Channel.one2one ();

    new Parallel (
      new CSProcess[] {
        new Numbers (a.out ()),
        new Generate (b.out (), Integer.MAX_VALUE - 1),
        new And (a.in (), b.in (), c.out ()),
        new Printer (c.in (), "--> ", "\n")
      }
    ).run ();

  }

}
