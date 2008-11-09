import org.jcsp.lang.*;
import org.jcsp.plugNplay.ints.*;

public class TimesIntExample {

  public static void main (String[] argv) {

    final One2OneChannelInt a = Channel.one2oneInt ();
    final One2OneChannelInt b = Channel.one2oneInt ();
    final One2OneChannelInt c = Channel.one2oneInt ();

    new Parallel (
      new CSProcess[] {
        new NumbersInt (a.out ()),
        new NumbersInt (b.out ()),
        new TimesInt (a.in (), b.in (), c.out ()),
        new PrinterInt (c.in (), "--> ", "\n")
      }
    ).run ();

  }

}
