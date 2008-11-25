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
import java.awt.*;

/**
 * @author P.H. Welch
 */
class BallsControl0 implements CSProcess {

  private static final int RED_SPEED = 7,
                           GREEN_SPEED = 13,
                           BLUE_SPEED = 19;

  private final ChannelOutput[] toBalls;
  private final DisplayList displayList;
  private final ChannelOutput toGraphics;
  private final ChannelInput fromGraphics;

  public BallsControl0 (final ChannelOutput[] toBalls,
                        final DisplayList displayList,
                        final ChannelOutput toGraphics,
                        final ChannelInput fromGraphics) {
    this.toBalls = toBalls;
    this.displayList = displayList;
    this.toGraphics = toGraphics;
    this.fromGraphics = fromGraphics;
  }

  private final static class Graphic implements GraphicsCommand.Graphic {
    public Color colour;
    public void doGraphic (java.awt.Graphics g, java.awt.Component c) {
      Dimension dim = c.getSize();
      g.setColor (colour);
      g.fillRect (0, 0, dim.width, dim.height);
    }
  }

  Graphic oldGraphic = new Graphic ();
  Graphic newGraphic = new Graphic ();

  GraphicsCommand oldCommand = new GraphicsCommand.General (oldGraphic);
  GraphicsCommand newCommand = new GraphicsCommand.General (newGraphic);

  public void run() {

    toGraphics.write (GraphicsProtocol.GET_DIMENSION);
    final Dimension graphicsDim = (Dimension) fromGraphics.read ();
    System.out.println ("BallsControl: graphics dimension = " + graphicsDim);

    final CSTimer tim = new CSTimer ();
    final long seed = tim.read ();

    // initialise data for background colour ...

    int colRed = 0, colGreen = 0, colBlue = 0,
        cvRed = RED_SPEED,  cvGreen = GREEN_SPEED,  cvBlue = BLUE_SPEED;

    newGraphic.colour = Color.black;

    displayList.set (newCommand);

    for (int i = 0; i < toBalls.length; i++) {
      toBalls[i].write (graphicsDim);
      toBalls[i].write (new Long (seed));
    }

    //final Thread me = Thread.currentThread ();
    //me.setPriority (Thread.MAX_PRIORITY);

    final long second = 1000;  // JCSP Timer units are milliseconds
    long interval = second / 20;

    long timeout;                         // timeouts will drift ... but never mind ...
    // long timeout = tim.read ();        // timeouts won't drift ... not wanted here ...

    while (true) {

        timeout = tim.read () + interval;   // timeouts will drift ... but never mind ...
        // timeout += interval;             // timeouts won't drift ... not wanted here ...

        final Graphic tmpA = oldGraphic;
        oldGraphic = newGraphic;
        newGraphic = tmpA;

        final GraphicsCommand tmpB = oldCommand;
        oldCommand = newCommand;
        newCommand = tmpB;

        colRed += cvRed;
        if (colRed > 255 - RED_SPEED) cvRed = -RED_SPEED; else if (colRed < RED_SPEED) cvRed = RED_SPEED;
        colGreen += cvGreen;
        if (colGreen > 255 - GREEN_SPEED) cvGreen = -GREEN_SPEED; else if (colGreen < GREEN_SPEED) cvGreen = GREEN_SPEED;
        colBlue += cvBlue;
        if (colBlue > 255 - BLUE_SPEED) cvBlue = -BLUE_SPEED; else if (colBlue < BLUE_SPEED) cvBlue = BLUE_SPEED;
        newGraphic.colour = new Color (colRed, colGreen, colBlue);

        tim.after (timeout);

        displayList.change (newCommand, 0);

     }

  }

}
