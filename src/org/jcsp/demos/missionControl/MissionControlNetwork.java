    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2006 Peter Welch and Paul Austin.            //
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
    //  Author contact: P.H.Welch@ukc.ac.uk                             //
    //                                                                  //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

package org.jcsp.demos.missionControl;

import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.util.ints.*;
import org.jcsp.awt.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * @author P.H.Welch
 */
public class MissionControlNetwork implements CSProcess {

  final private int interval;
  final private int start;
  final private ActiveFrame parent;

  class MyActiveButton extends ActiveButton {
    public MyActiveButton (String s) {
      super (s);
    }
    public Dimension getPreferredSize () {
      return new Dimension (100, 100);
    }
    public Dimension getMinimumSize () {
      return new Dimension (100, 100);
    }
    public Dimension getMaximumSize () {
      return new Dimension (100, 100);
    }
  }

  final private ActiveButton abortButton = new ActiveButton ("    abort    ");
  final private ActiveLabel countLabel = new ActiveLabel ("                   ");
  final private ActiveLabel rocketStatus = new ActiveLabel ("   Holding   ");

  public MissionControlNetwork (final int interval,
                                final int start,
                                final ActiveFrame parent) {

    this.interval = interval;
    this.start = start;
    this.parent = parent;

    // panelApplet.setLayout (new GridLayout (5, 3));
    parent.setLayout (new FlowLayout (FlowLayout.CENTER, 10, 75));

    countLabel.setAlignment (Label.CENTER);
    countLabel.setBackground (Color.white);
    rocketStatus.setAlignment (Label.CENTER);
    rocketStatus.setBackground (Color.lightGray);
    // abortButton.requestSize (100, 100);
    abortButton.setEnabled (false);
    abortButton.setBackground (Color.orange);

    parent.add (abortButton);
    parent.add (countLabel);
    parent.add (rocketStatus);

  }
    
  public void run () {

    final One2OneChannel mouseEvent = Channel.createOne2One ();           // applet panel
    final One2OneChannel panelConfigure = Channel.createOne2One ();       // channels

    final One2OneChannel abortEvent = Channel.createOne2One (new OverWriteOldestBuffer (1));           // abort button
    final One2OneChannel abortConfigure = Channel.createOne2One ();       // channels

    final One2OneChannelInt hold = ChannelInt.createOne2One ();           // launch control
    final One2OneChannelInt cancel = ChannelInt.createOne2One ();         // channels
    final One2OneChannelInt abort = ChannelInt.createOne2One (new OverWriteOldestBufferInt (1));
    final One2OneChannelInt countdown = ChannelInt.createOne2One ();
    final One2OneChannelInt fire = ChannelInt.createOne2One ();

    final One2OneChannel countdown2 = Channel.createOne2One ();
    final One2OneChannel fire2 = Channel.createOne2One ();

    parent.addMouseEventChannel (mouseEvent.out ());
    parent.setConfigureChannel (panelConfigure.in ());

    abortButton.addEventChannel (abortEvent.out ());
    abortButton.setConfigureChannel (abortConfigure.in ());

    countLabel.setConfigureChannel (countdown2.in ());
    rocketStatus.setConfigureChannel (fire2.in ());

    new Parallel (
      new CSProcess[] {
        parent,
        new PanelControl (mouseEvent.in(), panelConfigure.out (), hold.out ()),
        abortButton,
        new AbortControl (abortEvent.in (), abortConfigure.out (), cancel.in (), abort.out ()),
        countLabel,
        rocketStatus,
        new MultiLaunchControl (interval, start, abort.in (), cancel.out (), hold.in (), countdown.out (), fire.out ()),
        new CSProcess () {
          public void run () {
            final LabelColour white = new LabelColour (Color.white);
            while (true) {
              int n = countdown.in ().read ();
              countdown2.out ().write (white);
              countdown2.out ().write ((new Integer (n)).toString ());
            }
          }
        },
        new CSProcess () {
          public void run () {
            final LabelColour rocketGreen = new LabelColour (Color.green);
            final LabelColour rocketLightGray = new LabelColour (Color.lightGray);
            final LabelColour rocketOrange = new LabelColour (Color.orange);
            while (true) {
              switch (fire.in().read ()) {
                case LaunchControl.FIRED:
                  fire2.out().write (rocketGreen);
                  fire2.out().write ("Fired");
                break;
                case LaunchControl.COUNTING:
                  fire2.out().write (rocketGreen);
                  fire2.out().write ("Counting");
                break;
                case LaunchControl.HOLDING:
                  fire2.out().write (rocketLightGray);
                  fire2.out().write ("Holding");
                break;
                case LaunchControl.ABORTED:
                  fire2.out().write (rocketOrange);
                  fire2.out().write ("Aborted");
                break;
              }
            }
          }
        }
      }
    ).run ();

  }

}
