/*******************************************************************************
 *
 * $Archive: /jcsp/src/test/jcsp/AltTest.java $
 *
 * $Date: 1998/07/21 14:17:09 $
 *
 * $Revision: 1.1 $
 *
 * (C) Copyright 1997/8 Paul Austin <pda1@ukc.ac.uk>
 * University of Kent Canterbury
 ******************************************************************************/

import org.jcsp.lang.*;
import org.jcsp.plugNplay.*;

/**
 * <H2>Process Diagram</H2>
 * <H3>External View</H3>
 * <PRE>
 *  _________
 * |         |
 * | AltTest |
 * |_________|
 * </PRE>
 * <H3>Internal View</H3>
 * <PRE>
 *  ________________________________________________
 * |      _____           _____           _____     |
 * |     |     |         |     |         |     |    |
 * |     | <A HREF="org.jcsp.util.buildingblocks.Nos.html">Nos</A> |         | <A HREF="org.jcsp.util.buildingblocks.Nos.html">Nos</A> |         | <A HREF="org.jcsp.util.buildingblocks.Nos.html">Nos</A> |    |
 * |     |_____|         |_____|         |_____|    |
 * |        |               |               |       |
 * |      a v             b v             c v       |
 * |  ______|_____    ______|_____    ______|_____  |
 * | |            |  |            |  |            | |
 * | | <A HREF="org.jcsp.util.buildingblocks.FixedDelay.html">FixedDelay</A> |  | <A HREF="org.jcsp.util.buildingblocks.FixedDelay.html">FixedDelay</A> |  | <A HREF="org.jcsp.util.buildingblocks.FixedDelay.html">FixedDelay</A> | |
 * | |____________|  |____________|  |____________| |
 * |        |               |               |       |
 * |      d v             e v             f v       |
 * |        |               |               |       |
 * |        +----->-----+   |   +-----<-----+       |
 * |                  __|___|___|_       _________  |
 * |                 |            |  g  |         | |
 * |                 |            |-->--| <A HREF="org.jcsp.util.Printer.html">Printer</A> | |
 * |                 |____________|     |_________| |
 * |                                                |
 * |                                        AltTest |
 * |________________________________________________|
 * </PRE>
 * <P>
 * <H2>Description</H2>
 * The AltTest process is designed to test the Alternative class.
 * <P>
 * Each of the fixed delay processes have a delay time of 100, 200 or 300
 * microseconds.
 * <P>
 * The anonymous CSProcess has an infinite loop which will ALT on the
 * channels d, e & f. When one of these Channels becomes ready with data
 * the process will write the index of the Channel and the data value down
 * the g Channel.
 *
 * @author P.D. Austin
 */

public class AltTest implements CSProcess {

  /**
   * The main body of this process.
   */
  public void run () {
    One2OneChannel a = Channel.one2one ();
    One2OneChannel b = Channel.one2one ();
    One2OneChannel c = Channel.one2one ();
    final One2OneChannel d = Channel.one2one ();
    final One2OneChannel e = Channel.one2one ();
    final One2OneChannel f = Channel.one2one ();
    final One2OneChannel g = Channel.one2one ();

    new Parallel (
      new CSProcess[] {
        new Numbers (a.out ()),
        new Numbers (b.out ()),
        new Numbers (c.out ()),
        new FixedDelay (100, a.in (), d.out ()),
        new FixedDelay (200, b.in (), e.out ()),
        new FixedDelay (300, c.in (), f.out ()),
        new CSProcess () {
          public void run () {
            Guard[] chans = {d.in (), e.in (), f.in ()};
            Alternative alt = new Alternative (chans);
            while (true) {
              switch (alt.select ()) {
                case 0:
                  g.out ().write ("Channel 0 read " + d.in ().read () + "\n");
                break;
                case 1:
                  g.out ().write ("                    Channel 1 read " + e.in ().read () + "\n");
                break;
                case 2:
                  g.out ().write ("                                        Channel 2 read " + f.in ().read () + "\n");
                break;
              }
            }
          }
        },
        new Printer (g.in ())
      }
    ).run ();
  }

  /**
   * Main entry point for the application.
   */
  public static void main (String argv[]) {
    new AltTest ().run ();
  }
}
