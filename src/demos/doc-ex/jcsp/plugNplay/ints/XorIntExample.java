import org.jcsp.lang.*;
import org.jcsp.plugNplay.ints.*;

public class XorIntExample {

  public static void main (String[] argv) {

    One2OneChannelInt a = Channel.one2oneInt ();
    One2OneChannelInt b = Channel.one2oneInt ();
    One2OneChannelInt c = Channel.one2oneInt ();

    new Parallel (
      new CSProcess[] {
        new NumbersInt (a.out ()),
        new GenerateInt (b.out (), Integer.MAX_VALUE),
        new XorInt (a.in (), b.in (), c.out ()),
        new PrinterInt (c.in (), "--> ", "\n")
      }
    ).run ();

  }

}
