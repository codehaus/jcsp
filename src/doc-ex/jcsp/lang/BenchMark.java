/*******************************************************************************
 *
 * $Archive: /jcsp/src/test/jcsp/BenchMark.java $
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
import org.jcsp.plugNplay.*;

import java.util.*;

/**
 * <H2>Process Diagram</H2>
 * <H3>External View</H3>
 * <PRE>
 *  _____________
 * |             |
 * |  BenchMark  |
 * |_____________|
 * </PRE>
 * <H3>Internal View</H3>
 * <PRE>
 *  _______________________________
 * |                               |
 * |  ___________       _________  |
 * | |           |     |         | |
 * | |  <A HREF="org.jcsp.PlugNplay.Numbers.html">Numbers</A>  |-->--| Consume | |
 * | |___________|     |_________| |
 * |                               |
 * |                     BenchMark |
 * |_______________________________|
 * </PRE>
 * <P>
 * <H2>Description</H2>
 * The BenchMark process is used to give an estimate of the synchronisation
 * overhead of Channel communication.
 * <P>
 * The CSProcess has two CSProcesses executing in Parallel: Numbers and Consume
 * The output of the Numbers process is connecting to the input of the Consume
 * process.
 * <P>
 * The Consume process reads 50 Objects from the input Channel to discard
 * any start-up overhead. It then times the time to read the next 5000 Objects
 * and then writes to the screen the time taken in total and per loop then
 * exits.
 *
 * @author P.D. Austin
 */

public class BenchMark implements CSProcess {
  /**
   * The main body of this process.
   */
  public void run() {
    One2OneChannel a = Channel.one2one();
    new Parallel(new CSProcess[] {
      new Numbers (a.out ()),
      new Consume (5000, a.in ())
    }).run();
  }

  /**
   * Main entry point for the application.
   */
  public static void main(String argv[]) {
    new BenchMark().run();
  }

  /**
   * The consume Process.
   */
  protected class Consume implements CSProcess {
    private ChannelInput in;
    private int nLoops;

    /**
     * Constructs a new Consume Process
     */
    public Consume(int nLoops, ChannelInput in) {
      this.in = in;
      this.nLoops = nLoops;
    }

    /**
     * The main body of this process.
     */
    public void run() {
      int warmUp = 50;
      for(int i = 0; i < warmUp; i++) {
        in.read();
      }

      Date date1 = new Date();
      for(int i = 0; i < nLoops; i++) {
        Object o = in.read();
      }

      Date date2 = new Date();

      long microSeconds = ((date2.getTime() - date1.getTime()) * 1000);
      System.out.println("  " + microSeconds + " microseconds");
      long timePerLoop = (microSeconds / ((long)nLoops));
      System.out.println("  " + timePerLoop + " microseconds / iteration");
      System.exit(0);
    }
  }
}
