    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2008 Peter Welch and Paul Austin.            //
    //                2001-2004 Quickstone Technologies Limited.        //
    //                                                                  //
    //  This library is free software; you can redistribute it and/or   //
    //  modify it under the terms of the GNU Lesser General Public      //
    //  License as published by the Free Software Foundation; either    //
    //  version 2.1 of the License, or (at your option) any later       //
    //  version.                                                        //
    //                                                                  //
    //  This library is distributed in the hope that it will be         //
    //  useful, but WITHOUT ANY WARRANTY; without even the implied      //
    //  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR         //
    //  PURPOSE. See the GNU Lesser General Public License for more     //
    //  details.                                                        //
    //                                                                  //
    //  You should have received a copy of the GNU Lesser General       //
    //  Public License along with this library; if not, write to the    //
    //  Free Software Foundation, Inc., 59 Temple Place, Suite 330,     //
    //  Boston, MA 02111-1307, USA.                                     //
    //                                                                  //
    //  Author contact: P.H.Welch@kent.ac.uk                             //
    //                                                                  //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////


import org.jcsp.lang.*;
import org.jcsp.awt.*;
import java.awt.*;

/**
 * @author P.H. Welch
 */
public class BallsControl1 implements CSProcess {

  private final ChannelOutput[] toBalls;
  private final Barrier barrier;
  private final DisplayList displayList;
  private final ChannelOutput toGraphics;
  private final ChannelInput fromGraphics;
  private final int speed;

  public BallsControl1 (final ChannelOutput[] toBalls,
                        final Barrier barrier,
                        final DisplayList displayList,
                        final ChannelOutput toGraphics,
                        final ChannelInput fromGraphics,
                        final int speed) {
    this.toBalls = toBalls;
    this.barrier = barrier;
    this.displayList = displayList;
    this.toGraphics = toGraphics;
    this.fromGraphics = fromGraphics;
    this.speed = speed;
  }

  public void run() {

    toGraphics.write (GraphicsProtocol.GET_DIMENSION);
    final Dimension graphicsDim = (Dimension) fromGraphics.read ();
    System.out.println ("BallsControl: graphics dimension = " + graphicsDim);

    final GraphicsCommand baseCommand =
      new GraphicsCommand.ClearRect (0, 0, graphicsDim.width, graphicsDim.height);

    displayList.set (baseCommand);

    final CSTimer tim = new CSTimer ();
    final long seed = tim.read ();

    barrier.enroll ();                    // we do this before any of the balls

    for (int i = 0; i < toBalls.length; i++) {
      toBalls[i].write (graphicsDim);
      toBalls[i].write (new Long (seed));
    }

    final long second = 1000;              // JCSP Timer units are milliseconds
    long interval = (int) (((float) second)/((float) speed) + 0.5);
    System.out.println ("BallsControl1 : interval = " + interval);

    long timeout;                         // timeouts will drift ... but never mind ...
    // long timeout = tim.read ();        // timeouts won't drift ... not wanted here ...

    while (true) {

      timeout = tim.read () + interval;   // timeouts will drift ... but never mind ...
      // timeout += interval;             // timeouts won't drift ... not wanted here ...

      barrier.sync ();

      tim.after (timeout);

    }

  }

}
