import org.jcsp.lang.*;
import org.jcsp.plugNplay.*;

public class Plex2Example {

  public static void main (String[] argv) {

    final One2OneChannel[] a = Channel.one2oneArray (2);
    final One2OneChannel[] b = Channel.one2oneArray (2);
    final One2OneChannel c = Channel.one2one ();

    new Parallel (
      new CSProcess[] {
        new Fibonacci (a[0].out ()),
        new Squares (a[1].out ()),
        new Sign ("Fibonacci ", a[0].in (), b[0].out ()),
        new Sign ("              Squares ", a[1].in (), b[1].out ()),
        new Plex2 (b[0].in (), b[1].in (), c.out ()),
        new Printer (c.in (), "", "\n")
      }
    ).run ();

  }

}
