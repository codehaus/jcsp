
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
import java.awt.event.*;
import java.util.*;

public class FlasherControl implements CSProcess {

  final private long period;
  final private AltingChannelInput mouseEvent;
  final private ChannelOutput appletConfigure;

  public FlasherControl (final long period,
                         final AltingChannelInput mouseEvent,
                         final ChannelOutput appletConfigure) {
    this.period = period;
    this.mouseEvent = mouseEvent;
    this.appletConfigure = appletConfigure;
  }

  private class AppletColour implements ActiveApplet.Configure {
    private Color colour = Color.lightGray;
    public void setColour (Color colour) {
      this.colour = colour;
    }
    public void configure (java.applet.Applet applet) {
      applet.setBackground (colour);
      applet.repaint ();
    }
  }
    
  public void run () {

    final Random random = new Random ();
    final CSTimer tim = new CSTimer ();

    final Alternative alt = new Alternative (new Guard[] {mouseEvent, tim});
    final boolean[] preCondition = {true, false};
    final int MOUSE = 0;
    final int TIMER = 1;

    final AppletColour[] appletColour = {new AppletColour (), new AppletColour ()};
    final AppletColour panelBlack = new AppletColour ();
    panelBlack.setColour (Color.black);

    appletConfigure.write (panelBlack);

    int index = 0;
    AppletColour appletCol = appletColour[index];
    appletCol.setColour (new Color (random.nextInt ()));

    long timeout = tim.read ();
    boolean mousePresent = false;
    boolean running = true;

    while (running) {

      switch (alt.priSelect (preCondition)) {

        case MOUSE:
          switch (((MouseEvent) mouseEvent.read ()).getID ()) {
            case MouseEvent.MOUSE_ENTERED:
              if (! mousePresent) {
                mousePresent = true;
                timeout = tim.read () + period;
                tim.setAlarm (timeout);
                appletConfigure.write (appletCol);
                preCondition[TIMER] = true;
              }
            break;
            case MouseEvent.MOUSE_EXITED:
              if (mousePresent) {
                mousePresent = false;
                appletConfigure.write (panelBlack);
                preCondition[TIMER] = false;
              }
            break;
          }
        break;

        case TIMER:
          // System.out.println ("FlasherControl: tick");
          timeout += period;
          tim.setAlarm (timeout);
          index = 1 - index;
          appletCol = appletColour[index];
          appletCol.setColour (new Color (random.nextInt ()));
          appletConfigure.write (appletCol);
        break;

      }

    }

  }

}
