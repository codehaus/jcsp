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
public class Bounce implements CSProcess {

  private final Image[] images;
  private final Color background;

  private final Panel panel;

  private final ActiveCanvas activeCanvas;
  private final ActiveScrollbar activeScrollBar;

  private final BounceController bounceController;
  private final ImageAnimator imageAnimator;

  public Bounce (Image[] images, Color background, final Container parent, final int nSiblings) {

    this.images = images;
    this.background = background;

    // channels ...

    final One2OneChannel mouseEvent = Channel.one2one (new OverWriteOldestBuffer (10));
    final One2OneChannel toGraphics = Channel.one2one ();
    final One2OneChannel fromGraphics = Channel.one2one ();
    final One2OneChannelInt control = Channel.one2oneInt ();
    final One2OneChannelInt scroll = Channel.one2oneInt (new OverWriteOldestBufferInt (1));

    final DisplayList displayList = new DisplayList ();

    // awt processes ...

    activeCanvas = new ActiveCanvas ();
    activeCanvas.addMouseEventChannel (mouseEvent.out ());
    activeCanvas.setGraphicsChannels (toGraphics.in (), fromGraphics.out ());
    activeCanvas.setPaintable (displayList);
    activeCanvas.setBackground (background);
    final Dimension parentSize = parent.getSize ();
    activeCanvas.setSize (parentSize.width, parentSize.height/nSiblings);

    // If the parent is an applet, the above setSize has no effect and the activeCanvas
    // is fitted to the "Center" area (see below) of the applet's panel.
    // If the parent is a frame, the above *does* define the size of the activeCanvas
    // and the size of the parent is expanded to wrap around when it is packed.

    final int MAX_SCALE = 200;
    final int SLIDER = 30;
    activeScrollBar = new ActiveScrollbar (
      null, scroll.out (), Scrollbar.VERTICAL,
      MAX_SCALE, SLIDER, 0, MAX_SCALE + SLIDER
    );

    panel = new Panel ();
    panel.setLayout (new BorderLayout ());
    panel.add ("West", activeScrollBar);
    panel.add ("Center", activeCanvas);

    // application-specific processes ...

    bounceController = new BounceController (
      mouseEvent.in (), scroll.in (), control.out (), MAX_SCALE
    );

    imageAnimator = new ImageAnimator (
      control.in (), toGraphics.out (), fromGraphics.in (), displayList, images, 1, 1
    );

  }

  public Panel getPanel () {
    return panel;
  }


  public void run () {

    new Parallel (
      new CSProcess[] {
        activeScrollBar,
        activeCanvas,
        bounceController,
        imageAnimator
      }
    ).run ();

  }

}
