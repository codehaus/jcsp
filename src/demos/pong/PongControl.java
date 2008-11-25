
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
    //////////////////////////////////////////////////////////////////////


import org.jcsp.lang.*;
import org.jcsp.awt.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class PongControl implements CSProcess {

  private final ChannelOutput[] toBalls;
  private final AltingChannelInput fromBalls;
  private final ChannelOutput toFlasher;
  private final ChannelOutput toLeftPaddle;
  private final ChannelOutput toRightPaddle;
  private final ChannelOutput freezeConfigure;
  private final AltingChannelInput freezeChannel;
  private final ChannelOutput startConfigure;
  private final AltingChannelInput startChannel;
  private final ChannelOutput toGraphics;
  private final ChannelInput fromGraphics;

  public PongControl (final ChannelOutput[] toBalls,
                      final AltingChannelInput fromBalls,
                      final ChannelOutput toFlasher,
                      final ChannelOutput toLeftPaddle,
                      final ChannelOutput toRightPaddle,
                      final ChannelOutput freezeConfigure,
                      final AltingChannelInput freezeChannel, 
                      final ChannelOutput startConfigure,
                      final AltingChannelInput startChannel, 
                      final ChannelOutput toGraphics,
                      final ChannelInput fromGraphics) {
    this.toBalls = toBalls;
    this.fromBalls = fromBalls;
    this.toFlasher = toFlasher;
    this.toLeftPaddle = toLeftPaddle;
    this.toRightPaddle = toRightPaddle;
    this.freezeConfigure = freezeConfigure;
    this.freezeChannel = freezeChannel;
    this.startConfigure = startConfigure;
    this.startChannel = startChannel;
    this.toGraphics = toGraphics;
    this.fromGraphics = fromGraphics;
  }

  public void run() {

    System.out.println ("PongControl running ...");

    toGraphics.write (GraphicsProtocol.GET_DIMENSION);
    final Dimension graphicsDim = (Dimension) fromGraphics.read ();
    System.out.println ("PongControl: graphics dimension = " + graphicsDim);
    
    toFlasher.write (null);       // wait for flasher to grab display slot 0 ...
    
    toLeftPaddle.write (graphicsDim);
    toLeftPaddle.write (null);
    
    toRightPaddle.write (graphicsDim);
    toRightPaddle.write (null);

    final CSTimer tim = new CSTimer ();
    final long seed = tim.read ();

    for (int i = 0; i < toBalls.length; i++) {
      toBalls[i].write (graphicsDim);
      toBalls[i].write (new Long (seed));
    }

    PriParallel.setPriority (Thread.MAX_PRIORITY);

    boolean running = true;
    boolean frozen = true;

    freezeConfigure.write ("Start the game");
    startConfigure.write ("New game");

    final Alternative alt = new Alternative (new Guard[] {freezeChannel, startChannel, fromBalls});
    final int FREEZE = 0;
    final int RESTART = 1;
    final int BALLS = 2;
    final boolean[] preCondition = {true, frozen, !frozen};

    while (running) {
    
      switch (alt.priSelect (preCondition)) {
      
        case FREEZE:
          freezeChannel.read ();
          if (frozen) {
            frozen = false;
            preCondition[BALLS] = true;
            preCondition[RESTART] = false;
            startConfigure.write (Boolean.FALSE);
            startConfigure.write ("New game");
            freezeConfigure.write ("FREEZE");
          } else {
            frozen = true;
            preCondition[BALLS] = false;
            preCondition[RESTART] = true;
            startConfigure.write ("NEW GAME");
            startConfigure.write (Boolean.TRUE);
            freezeConfigure.write ("RELEASE");
          }
          toGraphics.write (GraphicsProtocol.REQUEST_FOCUS);
          fromGraphics.read ();
        break;
        
        case RESTART:
          startChannel.read ();
          frozen = false;
          preCondition[BALLS] = true;
          preCondition[RESTART] = false;
          startConfigure.write (Boolean.FALSE);
          startConfigure.write ("New game");
          toLeftPaddle.write (null);
          toRightPaddle.write (null);
          freezeConfigure.write ("FREEZE");
          toGraphics.write (GraphicsProtocol.REQUEST_FOCUS);
          fromGraphics.read ();
        break;
        
        case BALLS:
          fromBalls.read ();    // just accept synchronisation from ball
        break;
        
      }
     
      
    }

  }

}
