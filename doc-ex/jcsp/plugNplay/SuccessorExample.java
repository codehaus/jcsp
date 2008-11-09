import org.jcsp.lang.*;
import org.jcsp.plugNplay.*;

public class SuccessorExample {

  public static void main (String[] argv) {

    final One2OneChannel a = Channel.one2one ();
    final One2OneChannel b = Channel.one2one ();

    new Parallel (
      new CSProcess[] {
        new Numbers (a.out ()),
        new Successor (a.in (), b.out ()),
        new Printer (b.in (), "--> ", "\n")
      }
    ).run ();

  }

}
