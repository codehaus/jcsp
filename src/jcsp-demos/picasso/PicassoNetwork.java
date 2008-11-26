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
public class PicassoNetwork implements CSProcess {

  private final ActiveCanvas activeCanvas;

  private final Picasso picasso;

  public PicassoNetwork (final Container parent) {

    final Dimension size = parent.getSize ();

    parent.setLayout (new BorderLayout ());
    parent.setBackground (Color.blue);

    final One2OneChannel mouseEvent = Channel.one2one (new OverWriteOldestBuffer (10));
    final One2OneChannel mouseMotionEvent = Channel.one2one (new OverWriteOldestBuffer (1));

    final DisplayList display = new DisplayList ();

    final One2OneChannel toGraphics = Channel.one2one ();
    final One2OneChannel fromGraphics = Channel.one2one ();

    activeCanvas = new ActiveCanvas ();
    activeCanvas.setBackground (Color.black);
    activeCanvas.addMouseEventChannel (mouseEvent.out ());
    activeCanvas.addMouseMotionEventChannel (mouseMotionEvent.out ());
    activeCanvas.setPaintable (display);
    activeCanvas.setGraphicsChannels (toGraphics.in (), fromGraphics.out ());
    activeCanvas.setSize (size);

    // If the parent is an applet, the above setSize has no effect and the activeCanvas
    // is fitted to the "Center" area (see below) of the applet's panel.

    // If the parent is a frame, the above *does* define the size of the activeCanvas
    // and the size of the parent is expanded to wrap around when it is packed.

    System.out.println ("PicassoNetwork adding ActiveCanvas to the parent ...");
    parent.add ("Center", activeCanvas);

    picasso = new Picasso (size, mouseEvent.in (), mouseMotionEvent.in (), display);
    // picasso = new Picasso (size, mouseEvent, mouseMotionEvent, toGraphics, fromGraphics);

  }

  public void run () {

    System.out.println ("PicassoNetwork starting up ...");

    new Parallel (
      new CSProcess[] {
        activeCanvas,
        picasso
      }
    ).run ();

  }

}

