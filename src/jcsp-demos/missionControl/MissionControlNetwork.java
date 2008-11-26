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
import org.jcsp.util.*;
import org.jcsp.util.ints.*;
import org.jcsp.awt.*;
import java.awt.*;

/**
 * @author P.H. Welch
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

    final One2OneChannel mouseEvent = Channel.one2one ();           // applet panel
    final One2OneChannel panelConfigure = Channel.one2one ();       // channels

    final One2OneChannel abortEvent = Channel.one2one (new OverWriteOldestBuffer (1));           // abort button
    final One2OneChannel abortConfigure = Channel.one2one ();       // channels

    final One2OneChannelInt hold = Channel.one2oneInt ();           // launch control
    final One2OneChannelInt cancel = Channel.one2oneInt ();         // channels
    final One2OneChannelInt abort = Channel.one2oneInt (new OverWriteOldestBufferInt (1));
    final One2OneChannelInt countdown = Channel.one2oneInt ();
    final One2OneChannelInt fire = Channel.one2oneInt ();

    final One2OneChannel countdown2 = Channel.one2one ();
    final One2OneChannel fire2 = Channel.one2one ();

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
