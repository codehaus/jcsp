
import org.jcsp.demos.util.*;
import org.jcsp.lang.*;
import org.jcsp.plugNplay.*;

public class AltingBarrierGadget0Demo0 {

  public static void main (String[] argv) {

    // final int nUnits = 8;

    final int nUnits = Ask.Int ("\nnUnits = ", 3, 10);
    
    // make the buttons

    final One2OneChannel[] event = Channel.one2oneArray(nUnits);
    
    final One2OneChannel[] configure = Channel.one2oneArray (nUnits);

    final boolean horizontal = true;

    final FramedButtonArray buttons =
      new FramedButtonArray (
        "AltingBarrier: Gadget 0, Demo 0", nUnits, 120, nUnits*100,
         horizontal, Channel.getInputArray(configure), Channel.getOutputArray(event)
      );

    // construct an array of front-ends to a single alting barrier
    
    final AltingBarrier[] group = AltingBarrier.create (nUnits);

    // make the gadgets

    final AltingBarrierGadget0[] gadgets = new AltingBarrierGadget0[nUnits];
    for (int i = 0; i < gadgets.length; i++) {
      gadgets[i] = new AltingBarrierGadget0 (event[i].in(), group[i], configure[i].out());
    }

    // run everything

    new Parallel (
      new CSProcess[] {
        buttons, new Parallel (gadgets)
      }
    ).run ();

  }

}
