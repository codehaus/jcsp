/*******************************************************************************
 *
 * $Archive: /jcsp/src/test/jcsp/BlocksTest.java $
 *
 * $Date: 1998/07/21 14:17:10 $
 *
 * $Revision: 1.1 $
 *
 * (C) Copyright 1997/8 Paul Austin <pda1@ukc.ac.uk>
 * University of Kent Canterbury
 ******************************************************************************/
// package test.jcsp;

import org.jcsp.lang.*;
import org.jcsp.plugNplay.ints.*;
import org.jcsp.util.ints.*;

/**
 * <H2>Process Diagram</H2>
 * <H3>External View</H3>
 * <PRE>
 *  ________________
 * |                |
 * | SquaresIntTest |
 * |________________|
 * </PRE>
 * <H3>Internal View</H3>
 * <PRE>
 *  ____________________________________________________
 * |                                                    |
 * |  ______________       ________       ____________  |
 * | |              |  a  |        |  b  |            | |
 * | |  <A HREF="org.jcsp.plugNplay.ints.SquaresInt.html">SquaresInt</A>  |-->--|  <I>anon</I>  |-->--| <A HREF="org.jcsp.plugNplay.ints.PrinterInt.html">PrinterInt</A> | |
 * | |______________|     |________|     |____________| |
 * |                                                    |
 * |                                         BlocksTest |
 * |____________________________________________________|
 * </PRE>
 * <P>
 * <H2>Description</H2>
 * The BlocksTest process is designed to test some of the plugNplay processes.
 * <P>
 * <H2><A HREF="SquaresIntTest.java.doc">Sourcecode</A></H2>
 *
 * @author P.D. Austin and P.H.Welch
 */

public class SquaresIntTest implements CSProcess {
  /**
   * The main body of this process.
   */
  public void run () {

    final One2OneChannelInt a = Channel.one2oneInt ();
    final One2OneChannelInt b = Channel.one2oneInt ();

    new Parallel (
      new CSProcess[] {
        new SquaresInt (a.out ()),
        new CSProcess () {
          public void run () {    // this anonymous process 
            int n = a.in ().read ();    // terminates when the numbers
            while (n > 0) {       // overflow, deadlocking
              b.out ().write (n);        // the pipeline of which
              n = a.in ().read ();      // it is a component.
            }
          }
        },
        new PrinterInt (b.in (), "Perfect squares ==> ", "\n")
      }
    ).run ();
  }

  /**
   * Main entry point for the application.
   */
  public static void main (String argv[]) {
    new SquaresIntTest ().run ();
  }

}
