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
class PlasmaNetwork implements CSProcess {

  private final ActiveCanvas activeCanvas;
  private final PlasmaControl plasmaControl;
  private final ActiveChoice scaleChoice;
  private final ActiveChoice colourChoice;
  private final ActiveTextField codeTextField;
  private final ActiveButton freezeButton;
  private final ActiveLabel fpsLabel;

  public PlasmaNetwork (final Container parent) {

    // channels

    // final One2OneChannel mouseChannel =
    //   Channel.one2one (new OverWriteOldestBuffer (10));
    // final One2OneChannel mouseMotionChannel =
    //   Channel.one2one (new OverWriteOldestBuffer (1));
    // final One2OneChannel keyChannel =
    //   Channel.one2one (new OverWriteOldestBuffer (10));

    final One2OneChannel toGraphics = Channel.one2one ();
    final One2OneChannel fromGraphics = Channel.one2one ();
    final One2OneChannel canvasResize = Channel.one2one (new OverWritingBuffer (1));

    // processes

    parent.setLayout (new BorderLayout ());
    parent.setBackground (Color.black);

    activeCanvas = new ActiveCanvas ();
    // activeCanvas.addMouseEventChannel (mouseChannel);
    // activeCanvas.addMouseMotionEventChannel (mouseMotionChannel);
    // activeCanvas.addKeyEventChannel (keyChannel);
    activeCanvas.setGraphicsChannels (toGraphics.in (), fromGraphics.out ());
    activeCanvas.addComponentEventChannel (canvasResize.out ());
    activeCanvas.setSize (parent.getSize ());

    // If the parent is an applet, the above setSize has no effect and the activeCanvas
    // is fitted to the "Center" area (see below) of the applet's panel.

    // If the parent is a frame, the above *does* define the size of the activeCanvas
    // and the size of the parent is expanded to wrap around when it is packed.

    parent.add ("Center", activeCanvas);

    // south panel

    final Panel south = new Panel ();
    south.setBackground (Color.green);

    final One2OneChannel codeChannel = Channel.one2one (new OverWriteOldestBuffer (1));
    final One2OneChannel codeConfigure = Channel.one2one ();
    codeTextField = new ActiveTextField (codeConfigure.in (), codeChannel.out (), "XXXXXXXXXXXXXX", 24);
    codeTextField.setBackground (Color.white);
    codeTextField.setEnabled (true);
    south.add (new Label ("Genetic Code", Label.CENTER));
    south.add (codeTextField);

    final One2OneChannel scaleChannel = Channel.one2one (new OverWriteOldestBuffer (1));
    final One2OneChannel scaleConfigure = Channel.one2one ();
    scaleChoice = new ActiveChoice (scaleConfigure.in (), scaleChannel.out ());
    final String[] scaleMenu = {"1:1", "1:2", "1:4", "1:8", "1:16"};
    for (int i = 0; i < scaleMenu.length; i++) {
      scaleChoice.add (scaleMenu[i]);
    }
    south.add (new Label ("Scale", Label.CENTER));
    south.add (scaleChoice);

    final One2OneChannel colourChannel = Channel.one2one (new OverWriteOldestBuffer (1));
    final One2OneChannel colourConfigure = Channel.one2one ();
    colourChoice = new ActiveChoice (colourConfigure.in (), colourChannel.out ());
    final String[] colourMenu = {"Red/Green/Blue", "Red/Blue", "Red/Green", "Green/Blue", "Jumpy"};
    for (int i = 0; i < colourMenu.length; i++) {
      colourChoice.add (colourMenu[i]);
    }
    south.add (new Label ("Colours", Label.CENTER));
    south.add (colourChoice);

    parent.add ("South", south);

    // north panel

    final Panel north = new Panel ();
    north.setBackground (Color.green);

    final One2OneChannel freezeChannel = Channel.one2one (new OverWriteOldestBuffer (1));
    final One2OneChannel freezeConfigure = Channel.one2one ();
    freezeButton = new ActiveButton (freezeConfigure.in (), freezeChannel.out (), "XXXXXXXXXXXXXX");
    freezeButton.setBackground (Color.white);
    freezeButton.setEnabled (true);
    north.add (freezeButton);

    final One2OneChannel fpsConfigure = Channel.one2one ();
    fpsLabel = new ActiveLabel (fpsConfigure.in (), "XXXXXXXXXXXXXX");
    fpsLabel.setAlignment (Label.CENTER);
    fpsLabel.setBackground (Color.white);
    north.add (new Label ("Frames/Second", Label.CENTER));
    north.add (fpsLabel);

    parent.add ("North", north);

    plasmaControl=
      new PlasmaControl (
        codeConfigure.out (), codeChannel.in (),
        colourConfigure.out (), colourChannel.in (), colourMenu,
        scaleConfigure.out (), scaleChannel.in (), scaleMenu,
        freezeConfigure.out (), freezeChannel.in (),
        fpsConfigure.out (),
        toGraphics.out (), fromGraphics.in (),
        canvasResize.in ()
      );

  }

  public void run () {

    new Parallel (
      new CSProcess[] {
        activeCanvas,
        plasmaControl,
        codeTextField,
        scaleChoice,
        colourChoice,
        freezeButton,
        fpsLabel
      }
    ).run ();

  }

}
