
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

public class PongFlasher implements CSProcess {

  private final ChannelInput fromControl;
  private final AltingChannelInputInt trigger;
  private final DisplayList displayList;

  public PongFlasher (final ChannelInput fromControl,
                      final AltingChannelInputInt trigger,
                      final DisplayList displayList) {
    this.fromControl = fromControl;
    this.trigger = trigger;
    this.displayList = displayList;
  }

  private final static class Graphic implements GraphicsCommand.Graphic {
    public Color colour= Color.black;
    public void doGraphic (java.awt.Graphics g, java.awt.Component c) {
      Dimension dim = c.getSize();
      g.setColor (colour);
      g.fillRect (0, 0, dim.width, dim.height);
    }
  }

  public void run() {

    Graphic oldGraphic = new Graphic ();
    Graphic newGraphic = new Graphic ();

    GraphicsCommand oldCommand = new GraphicsCommand.General (oldGraphic);
    GraphicsCommand newCommand = new GraphicsCommand.General (newGraphic);

    displayList.set (newCommand);
    
    final Random random = new Random ();
  
    System.out.println ("Flasher running ...");

    fromControl.read ();    // let control process continue (and let the balls pick displaylist slots)

    final CSTimer tim = new CSTimer ();

    final Thread me = Thread.currentThread ();
    me.setPriority (Thread.MAX_PRIORITY);

    final long second = 1000;  // JCSP Timer units are milliseconds
    long interval = -1;        // negative ==> not flashing

    final Alternative alt = new Alternative (new Guard[] {trigger, tim});
    final boolean[] preCondition = {true, interval >= 0};
    final int TRIGGER = 0;
    final int TIMER = 1;

    long timeout = 0;
    
    boolean mousePresent = false;
    boolean running = true;

    while (running) {
    
      final Graphic tmpGraphic = oldGraphic;
      oldGraphic = newGraphic;
      newGraphic = tmpGraphic;
      
      final GraphicsCommand tmpCommand = oldCommand;
      oldCommand = newCommand;
      newCommand = tmpCommand;

      switch (alt.priSelect (preCondition)) {

        case TRIGGER:
          interval = trigger.read ();
          if (interval >= 0) {
            timeout = tim.read () + interval;
            tim.setAlarm (timeout);
            newGraphic.colour = new Color (random.nextInt ());
            preCondition[TIMER] = true;
          } else {
            newGraphic.colour = Color.black;
            preCondition[TIMER] = false;
          }
        break;

        case TIMER:
          timeout += interval;
          tim.setAlarm (timeout);
          newGraphic.colour = new Color (random.nextInt ());
        break;

      }

      displayList.change (newCommand, 0);

    }

  }

}
