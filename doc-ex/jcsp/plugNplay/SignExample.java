import org.jcsp.lang.*;
import org.jcsp.plugNplay.*;

public class SignExample {

  public static void main (String[] argv) {

    final One2OneChannel[] a = Channel.one2oneArray (3);
    final One2OneChannel[] b = Channel.one2oneArray (3);
    final One2OneChannel c = Channel.one2one ();

    new Parallel (
      new CSProcess[] {
        new Numbers (a[0].out ()),
        new Fibonacci (a[1].out ()),
        new Squares (a[2].out ()),
        new Sign ("Numbers ", a[0].in (), b[0].out ()),
        new Sign ("            Fibonacci ", a[1].in (), b[1].out ()),
        new Sign ("                          Squares ", a[2].in (), b[2].out ()),
        new Plex (Channel.getInputArray (b), c.out ()),
        new Printer (c.in (), "", "\n")
      }
    ).run ();

  }

}
