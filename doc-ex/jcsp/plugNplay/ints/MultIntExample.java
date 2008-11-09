import org.jcsp.lang.*;
import org.jcsp.plugNplay.ints.*;

public class MultIntExample {

  public static void main (String[] argv) {

    final One2OneChannelInt a = Channel.one2oneInt ();
    final One2OneChannelInt b = Channel.one2oneInt ();

    new Parallel (
      new CSProcess[] {
        new NumbersInt (a.out ()),
        new MultInt (42, a.in (), b.out ()),
        new PrinterInt (b.in (), "--> ", "\n")
      }
    ).run ();

  }

}
