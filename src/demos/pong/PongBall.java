
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

public class PongBall implements CSProcess {

  private final int id;
  private final int speed;
  private final int life;
  private final Barrier dead;
  private final ChannelOutput toLeftPaddle;
  private final ChannelInputInt fromLeftPaddle;
  private final ChannelOutput toRightPaddle;
  private final ChannelInputInt fromRightPaddle;
  private final ChannelInput fromControl;
  private final ChannelOutput toControl;
  private final DisplayList displayList;

  private Random random;

  public PongBall (final int id, final int speed, final int life, final Barrier dead,
                   final ChannelOutput toLeftPaddle, final ChannelInputInt fromLeftPaddle,
                   final ChannelOutput toRightPaddle, final ChannelInputInt fromRightPaddle,
                   final ChannelInput fromControl, final ChannelOutput toControl,
                   final DisplayList displayList) {
    this.id = id;
    this.speed = speed;
    this.life = life*speed;
    this.dead = dead;
    this.toLeftPaddle = toLeftPaddle;
    this.fromLeftPaddle = fromLeftPaddle;
    this.toRightPaddle = toRightPaddle;
    this.fromRightPaddle = fromRightPaddle;
    this.fromControl = fromControl;
    this.toControl = toControl;
    this.displayList = displayList;
  }

  public final static class Info {
    public int zing, y;
  }

  private final static class Graphic implements GraphicsCommand.Graphic {
    public Color colour;
    public int x, y, width, height;
    public void doGraphic (java.awt.Graphics g, java.awt.Component c) {
      g.setColor (colour);
      g.fillOval (x, y, width, height);
    }
  }

  private final int range (int n) {      // returns a random integer in the range [0, n - 1]
    int i = random.nextInt ();           // needed since random.nextInt (<int>) not in JDK1.1 :-(
    if (i < 0) {
      if (i == Integer.MIN_VALUE) {      // guard against minint !
        i = 42;
      } else {
        i = -i;
      }
    }
    return i % n;
  }

  private final int delta (int n) {      // returns a random integer in the range [-n, n] excluding 0
    int i = range (2*n) - n;
    if (i == 0) i = n;
    return i;
  }

  final CSTimer tim = new CSTimer ();

  private final void countdown (final long seconds) {
    final long interval = 250;  // milli-seconds
    for (int t = 0; t < seconds; t += interval) {
      tim.sleep (interval);
      toControl.write (null);             // get permission to continue ...
    }
  }

  public void run () {

// System.out.println ("Ball " + id + " running ...");

    final Dimension graphicsDim = (Dimension) fromControl.read ();
    final long seed = id + ((Long) fromControl.read ()).longValue ();
    this.random = new Random (seed);
    // System.out.println ("Ball " + id + ": " + graphicsDim);
    // System.out.println ("Ball " + id + ": seed = " + seed);

    final int displaySlot = displayList.extend (GraphicsCommand.NULL);
    // System.out.println ("Ball " + id + ": displaySlot = " + displaySlot);

    Graphic newGraphic = new Graphic ();
    Graphic oldGraphic = new Graphic ();

    GraphicsCommand newCommand = new GraphicsCommand.General (newGraphic);
    GraphicsCommand oldCommand = new GraphicsCommand.General (oldGraphic);

    final Info info = new Info ();

// System.out.println ("PongBall " + id + ": priority = " + PriParallel.getPriority ());
    PriParallel.setPriority (Thread.MAX_PRIORITY);
// System.out.println ("PongBall " + id + ": priority = " + PriParallel.getPriority ());

    final long second = 1000;  // JCSP Timer units are milliseconds
    long interval = (long) (((float) second)/((float) speed) + 0.5);
// System.out.println ("Ball " + id + ": interval = " + interval);

    final long blackout = 5*second;

    while (true) {

      // initialise data for new ball ...

      int deltaX = delta (10);
      int deltaY = delta (10);

      info.zing = (deltaX*deltaX) + (deltaY*deltaY);

      newGraphic.colour = new Color (random.nextInt ());
      newGraphic.width = range (30) + 10;
      newGraphic.height = range (30) + 10;

      final int min_x = PongPaddle.WIDTH;
      final int max_x = graphicsDim.width - (PongPaddle.WIDTH + newGraphic.width);

      final int min_y = 0;
      final int max_y = graphicsDim.height - newGraphic.height;
      
      // newGraphic.x = range (max_x - min_x) + min_x;        // serve from anywhere
      newGraphic.x = (deltaX > 0) ? min_x : max_x;          // serve from the baseline
      newGraphic.y = range (max_y);

      oldGraphic.colour = newGraphic.colour;
      oldGraphic.width = newGraphic.width;
      oldGraphic.height = newGraphic.height;

// System.out.println ("Ball " + id + ": initialX,Y = " + newGraphic.x + ", " + newGraphic.y);
// System.out.println ("Ball " + id + ": initialW,H = " + newGraphic.width + ", " + newGraphic.height);
// System.out.println ("Ball " + id + ": (deltaX, deltaY) = (" + deltaX + ", " + deltaY + ")");

      countdown (id*second);                // bring the balls up one by one

      // System.out.println ("Ball " + id + ": alive");

      int countdown = life;

      long timeout;                         // timeouts will drift ... but never mind ...
      // long timeout = tim.read ();        // timeouts won't drift ... not wanted here ...

      while (countdown > 0) {

        timeout = tim.read () + interval;   // timeouts will drift ... but never mind ...
        // timeout += interval;             // timeouts won't drift ... not wanted here ...

        toControl.write (null);             // get permission to continue (i.e. we're not frozen)

        displayList.change (newCommand, displaySlot);

        final Graphic tmpA = oldGraphic;
        oldGraphic = newGraphic;
        newGraphic = tmpA;

        final GraphicsCommand tmpB = oldCommand;
        oldCommand = newCommand;
        newCommand = tmpB;

        int x = oldGraphic.x;
        int y = oldGraphic.y;

        if ((y < 0) || (y > max_y)) {
          deltaY = -deltaY;
        }

        if (x < min_x) {
          info.y = y + oldGraphic.height/2;
          toLeftPaddle.write (info);
          if (fromLeftPaddle.read () == PongPaddle.HIT) {
            deltaX = -deltaX;
          } else {
            break;
          }
        } else if (x > max_x) {
          info.y = y + oldGraphic.height/2;
          toRightPaddle.write (info);
          if (fromRightPaddle.read () == PongPaddle.HIT) {
            deltaX = -deltaX;
          } else {
            break;
          }
        }
        
        newGraphic.x = x + deltaX;
        newGraphic.y = y + deltaY;

        countdown--;

        tim.after (timeout);

      }

      // System.out.println ("Ball " + id + ": dead");

      displayList.change (GraphicsCommand.NULL, displaySlot);

      dead.sync ();           // wait for all the other balls to die

      countdown (blackout);   // blackout for 5 seconds ...

    }

  }

}
