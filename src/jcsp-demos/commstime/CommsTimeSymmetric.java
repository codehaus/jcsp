
import org.jcsp.lang.*;
import org.jcsp.plugNplay.ints.*;

class CommsTimeSymmetric {

  public static void  main (String argv []) {

    System.out.println ("");
    System.out.println ("Test of communication between JCSP processes");
    System.out.println ("Based on occam CommsTime.occ by Peter Welch, University of Kent at Canterbury");
    System.out.println ("Ported into Java by Oyvind Teig");
    System.out.println ("Now using the JCSP library (phw/pda1)");
    System.out.println ("This version uses *symmetric* channels");
    System.out.println ();

    SpuriousLog.start ();

    final int nIterations = 10000;
    System.out.println (nIterations + " iterations per timing ...\n");

    One2OneChannelSymmetricInt a = Channel.one2oneSymmetricInt ();
    One2OneChannelSymmetricInt b = Channel.one2oneSymmetricInt ();
    One2OneChannelSymmetricInt c = Channel.one2oneSymmetricInt ();
    One2OneChannelSymmetricInt d = Channel.one2oneSymmetricInt ();


      new Parallel (
        new CSProcess[] {
          new PrefixInt (0, c.in(), a.out()),
          new Delta2Int (a.in(), d.out(), b.out()),
          new SuccessorInt (b.in(), c.out()),
          new Consume (nIterations, d.in())
        }
      ).run ();


    System.out.println ("\n\n\nOnly gets here if all above parallel processes fail ...\n\n\n");

  }

}
