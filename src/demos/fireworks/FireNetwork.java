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
import org.jcsp.awt.*;
import java.awt.*;

/**
 * @author P.H. Welch
 */
public class FireNetwork implements CSProcess {

  private final ActiveCanvas activeCanvas;

  private final FireControl control;

  public FireNetwork (int maxParticles, int stillCount, int dragCount, int speed,
                      int accY, int maxDeltaY, int launchDeltaX, int launchDeltaY,
                      int scale, Container parent) {

    parent.setLayout (new BorderLayout ());
    
    final DisplayList displayList = new DisplayList ();
    // displayList.setMinRefreshInterval (10);
    // System.out.println ("FireNetwork: displayList.setMinRefreshInterval (10) ...");
    
    // final Any2OneChannel fromMouse = Channel.any2one (new OverWriteOldestBuffer (9));
    final One2OneChannel fromMouse = Channel.one2one (new OverWriteOldestBuffer (9));
    final One2OneChannel fromMouseMotion = Channel.one2one (new OverWriteOldestBuffer (9));
    final One2OneChannel fromKeyboard = Channel.one2one (new OverWriteOldestBuffer (9));
    final One2OneChannel fromCanvas = Channel.one2one (new OverWriteOldestBuffer (1));

    final One2OneChannel toGraphics = Channel.one2one ();
    final One2OneChannel fromGraphics = Channel.one2one ();

    activeCanvas = new ActiveCanvas ();
    activeCanvas.setBackground (Color.black);
    activeCanvas.setPaintable (displayList);
    activeCanvas.setGraphicsChannels (toGraphics.in (), fromGraphics.out ());
    activeCanvas.addMouseEventChannel (fromMouse.out ());
    activeCanvas.addMouseMotionEventChannel (fromMouseMotion.out ());
    activeCanvas.addKeyEventChannel (fromKeyboard.out ());
    activeCanvas.addComponentEventChannel(fromCanvas.out ());
    activeCanvas.setSize (parent.getSize ());

    // If the parent is an applet, the above setSize has no effect and the activeCanvas
    // is fitted to the "Center" area (see below) of the applet's panel.

    // If the parent is a frame, the above *does* define the size of the activeCanvas
    // and the size of the parent is expanded to wrap around when it is packed.

    System.out.println ("FireNetwork adding ActiveCanvas to the parent ...");
    parent.add ("Center", activeCanvas);

    control = new FireControl (
      fromMouse.in (), fromMouseMotion.in (), fromKeyboard.in (), fromCanvas.in (),
      displayList, toGraphics.out (), fromGraphics.in (),
      maxParticles, stillCount, dragCount, speed, accY, maxDeltaY,
      launchDeltaX, launchDeltaY, scale
    );

  }

  public void run () {

    System.out.println ("FireControl starting up ...");

    new Parallel (
      new CSProcess[] {
        activeCanvas,
        control
      }
    ).run ();

  }

}

