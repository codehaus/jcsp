import org.jcsp.lang.*;
import org.jcsp.plugNplay.*;

public class XorExample {

  public static void main (String[] argv) {

    One2OneChannel a = Channel.one2one ();
    One2OneChannel b = Channel.one2one ();
    One2OneChannel c = Channel.one2one ();

    new Parallel (
      new CSProcess[] {
        new Numbers (a.out ()),
        new Generate (b.out (), Integer.MAX_VALUE),
        new Xor (a.in (), b.in (), c.out ()),
        new Printer (c.in (), "--> ", "\n")
      }
    ).run ();

  }

}
