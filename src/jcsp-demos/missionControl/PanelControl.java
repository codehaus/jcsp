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
import java.awt.*;
import java.awt.event.*;

/**
 * @author P.H. Welch
 */
public class PanelControl implements CSProcess {
 
  private final AltingChannelInput fromPanel;
  private final ChannelOutput toPanel;
  private final ChannelOutputInt hold;
 
  public PanelControl (final AltingChannelInput fromPanel,
                       final ChannelOutput toPanel,
                       final ChannelOutputInt hold) {
    this.fromPanel = fromPanel;
    this.toPanel = toPanel;
    this.hold = hold;
  }

  public void run () {

    final PanelColour panelOff = new PanelColour (Color.lightGray);
    final PanelColour panelOn = new PanelColour (Color.green);

    boolean mousePresent = false;
    toPanel.write (panelOff);

    while (true) {

      switch (((MouseEvent) fromPanel.read ()).getID ()) {
        case MouseEvent.MOUSE_PRESSED:
          if (! mousePresent) {
            mousePresent = true;
            hold.write (0);
            toPanel.write (panelOn);
          } else {
            mousePresent = false;
            hold.write (0);
            toPanel.write (panelOff);
          }
        break;
      }

    }

  }

}
