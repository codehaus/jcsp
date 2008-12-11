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
import java.awt.image.*;

/**
 * @author P.H. Welch
 */
class InfectNetwork implements CSProcess {

  private final ActiveCanvas activeCanvas;

  // private final ActiveScrollbar renderRateBar;
  private final ActiveScrollbar infectRateBar;
  private final ActiveScrollbar convertRateBar;
  private final ActiveScrollbar recoverRateBar;

  // private final PseudoButton pseudoButton;
  private final ActiveButton resetButton;
  private final ActiveButton freezeButton;

  private final ActiveLabel fpsLabel;
  private final ActiveLabel infectedLabel;
  private final ActiveLabel deadLabel;
  
  private final ActiveChoice renderChoice;

  // private final ActiveLabel renderRateLabel;
  private final ActiveLabel infectRateLabel;
  private final ActiveLabel convertRateLabel;
  private final ActiveLabel recoverRateLabel;

  // private final InfectionControl control;
  private final Infection infection;

  public InfectNetwork (
    final int infectRate,
    final int convertRate,
    final int recoverRate,
    final int reinfectRate,
    final int renderChoiceIndex,
    final int sprayRadius,
    final Container parent
  ) {

    // System.out.println ("InfectNetwork: infectRate = " + infectRate);
    // System.out.println ("InfectNetwork: convertRate = " + convertRate);
    // System.out.println ("InfectNetwork: recoverRate = " + recoverRate);
    // System.out.println ("InfectNetwork: reinfectRate = " + reinfectRate);
    // System.out.println ("InfectNetwork: renderChoiceIndex = " + renderChoiceIndex);
    // System.out.println ("InfectNetwork: sprayRadius = " + sprayRadius);

    parent.setLayout (new BorderLayout ());
    parent.setBackground (Color.blue);

    System.out.println ("Infect creating channels ...");
    
    // final One2OneChannel[] event =
    //   Channel.one2oneArray  (InfectionControl.NUMBER + 1, new OverWriteOldestBuffer (1));
    // final One2OneChannel[] configure =
    //   Channel.one2oneArray  (InfectionControl.NUMBER + 1);

    final One2OneChannel resetEvent = Channel.one2one (new OverWriteOldestBuffer (1));
    final One2OneChannel resetConfigure = Channel.one2one ();
    
    final One2OneChannel freezeEvent = Channel.one2one  (new OverWriteOldestBuffer (1));
    final One2OneChannel freezeConfigure = Channel.one2one ();

    // final One2OneChannelInt renderRateBarEvent = Channel.one2oneInt (new OverWriteOldestBufferInt (1));
    // final One2OneChannel renderRateBarConfigure = Channel.one2one ();

    final One2OneChannelInt infectRateBarEvent = Channel.one2oneInt (new OverWriteOldestBufferInt (1));
    final One2OneChannel infectRateBarConfigure = Channel.one2one ();

    final One2OneChannelInt convertRateBarEvent = Channel.one2oneInt (new OverWriteOldestBufferInt (1));
    final One2OneChannel convertRateBarConfigure = Channel.one2one ();

    final One2OneChannelInt recoverRateBarEvent = Channel.one2oneInt (new OverWriteOldestBufferInt (1));
    final One2OneChannel recoverRateBarConfigure = Channel.one2one ();

    final One2OneChannel renderEvent = Channel.one2one (new OverWriteOldestBuffer (1));
    final One2OneChannel renderConfigure = Channel.one2one ();

    // final One2OneChannel report = Channel.one2one ();
    // final One2OneChannel generate = Channel.one2one ();

    final One2OneChannel toGraphics = Channel.one2one ();
    final One2OneChannel fromGraphics = Channel.one2one ();

    final One2OneChannel canvasResize = Channel.one2one (new OverWritingBuffer (1));

    // final One2OneChannel feedBack = Channel.one2one ();

    final One2OneChannel fpsConfigure = Channel.one2one ();
    final One2OneChannel infectedConfigure = Channel.one2one ();
    final One2OneChannel deadConfigure = Channel.one2one ();
    // final One2OneChannel renderRateLabelConfigure = Channel.one2one ();
    final One2OneChannel infectRateLabelConfigure = Channel.one2one ();
    final One2OneChannel convertRateLabelConfigure = Channel.one2one ();
    final One2OneChannel recoverRateLabelConfigure = Channel.one2one ();
    
    final One2OneChannel fromMouse = Channel.one2one  (new OverWriteOldestBuffer (9));
    final One2OneChannel fromMouseMotion = Channel.one2one  (new OverWriteOldestBuffer (9));

    System.out.println ("InfectNetwork created channels");
    System.out.println ("InfectNetwork creating ActiveButtons ...");

    // button = new ActiveButton[InfectionControl.NUMBER];
    // for (int i = 0; i < InfectionControl.NUMBER; i++) {
    //   button[i]
    //     = new ActiveButton (configure[i], event[i], "XXXXXXXXXXXXX");
    //   button[i].setBackground (Color.white);
    //   System.out.println ("  button " + i);
    // }

    resetButton = new ActiveButton (resetConfigure.in (), resetEvent.out (), "XXXXXXXXX");
    resetButton.setBackground (Color.white);
    
    freezeButton = new ActiveButton (freezeConfigure.in (), freezeEvent.out (), "XXXXXXXXX");
    freezeButton.setBackground (Color.white);

    final Label fpsText = new Label ("cycles/sec", Label.CENTER);
    fpsText.setForeground (Color.white);
    fpsLabel = new ActiveLabel (fpsConfigure.in (), "XXXXXXXXX");
    fpsLabel.setAlignment (Label.CENTER);
    fpsLabel.setBackground (Color.white);


    final Label infectedText = new Label ("infected", Label.CENTER);
    infectedText.setForeground (Color.white);
    infectedLabel = new ActiveLabel (infectedConfigure.in (), "XXXXXXXXX");
    infectedLabel.setAlignment (Label.CENTER);
    infectedLabel.setBackground (Color.white);

    final Label deadText = new Label ("dead", Label.CENTER);
    deadText.setForeground (Color.white);
    deadLabel = new ActiveLabel (deadConfigure.in (), "XXXXXXXXX");
    deadLabel.setAlignment (Label.CENTER);
    deadLabel.setBackground (Color.white);

    final Label renderRateText = new Label ("render", Label.CENTER);
    renderRateText.setForeground (Color.white);
    renderChoice = new ActiveChoice (renderConfigure.in (), renderEvent.out ());
    final String[] renderMenu = {"1:1", "1:2", "1:4", "1:8", "1:16", "1:32", "1:64", "1:128", "1:256", "none"};
    final int[] renderLookup = {1, 2, 4, 8, 16, 32, 64, 128, 256, Integer.MAX_VALUE};
    for (int i = 0; i < renderMenu.length; i++) {
      renderChoice.add (renderMenu[i]);
    }

    // renderRateLabel = new ActiveLabel (renderRateLabelConfigure.in (), "XXXXXXXXX");
    // renderRateLabel.setAlignment (Label.CENTER);
    // renderRateLabel.setBackground (Color.white);

    final Label InfectRateText = new Label ("infect", Label.CENTER);
    InfectRateText.setForeground (Color.white);
    infectRateLabel = new ActiveLabel (infectRateLabelConfigure.in (), "XXXXXXXXX");
    infectRateLabel.setAlignment (Label.CENTER);
    infectRateLabel.setBackground (Color.white);

    final Label convertRateText = new Label ("convert", Label.CENTER);
    convertRateText.setForeground (Color.white);
    convertRateLabel = new ActiveLabel (convertRateLabelConfigure.in (), "XXXXXXXXX");
    convertRateLabel.setAlignment (Label.CENTER);
    convertRateLabel.setBackground (Color.white);

    final Label recoverRateText = new Label ("recover", Label.CENTER);
    recoverRateText.setForeground (Color.white);
    recoverRateLabel = new ActiveLabel (recoverRateLabelConfigure.in (), "XXXXXXXXX");
    recoverRateLabel.setAlignment (Label.CENTER);
    recoverRateLabel.setBackground (Color.white);

    System.out.println ("InfectNetwork created ActiveButtons ... now adding them to the parent ...");
    
    final Panel north = new Panel ();
    north.setBackground (Color.darkGray);
    north.add (resetButton);
    north.add (fpsText);
    north.add (fpsLabel);
    north.add (infectedText);
    north.add (infectedLabel);
    north.add (deadText);
    north.add (deadLabel);
    north.add (freezeButton);
    parent.add ("North", north);

    final Panel south = new Panel ();
    south.setBackground (Color.darkGray);
    south.add (renderRateText);
    south.add (renderChoice);
    // south.add (renderRateLabel);
    south.add (InfectRateText);
    south.add (infectRateLabel);
    south.add (convertRateText);
    south.add (convertRateLabel);
    south.add (recoverRateText);
    south.add (recoverRateLabel);
    parent.add ("South", south);

    System.out.println ("InfectNetwork creating ActiveScrollbars ...");

    // renderRateBar = new ActiveScrollbar (renderRateBarConfigure.in (), renderRateBarEvent.out (),
                                         // Scrollbar.VERTICAL, 100 - 100, 25, 0, 125);
    // renderRateBar.setBackground (Color.white);

    infectRateBar = new ActiveScrollbar (infectRateBarConfigure.in (), infectRateBarEvent.out (),
                                         Scrollbar.VERTICAL, 100 - infectRate, 25, 0, 125);
    infectRateBar.setBackground (Color.white);

    convertRateBar = new ActiveScrollbar (convertRateBarConfigure.in (), convertRateBarEvent.out (),
                                         Scrollbar.VERTICAL, 100 - convertRate, 25, 0, 125);
    convertRateBar.setBackground (Color.white);

    recoverRateBar = new ActiveScrollbar (recoverRateBarConfigure.in (), recoverRateBarEvent.out (),
                                         Scrollbar.VERTICAL, 100 - recoverRate, 25, 0, 125);
    recoverRateBar.setBackground (Color.white);

    System.out.println ("InfectNetwork created ActiveScrollbars ... now adding it to the parent ...");

    // final Panel west = new Panel ();
    // west.setLayout (new GridLayout (2, 1));
    // west.add (renderRateBar);
    // west.add (infectRateBar);
    parent.add ("West", infectRateBar);

    final Panel east = new Panel ();
    east.setLayout (new GridLayout (2, 1));
    east.add (convertRateBar);
    east.add (recoverRateBar);
    parent.add ("East", east);

    System.out.println ("InfectNetwork now creating ActiveCanvas ...");
    activeCanvas = new ActiveCanvas ();
    activeCanvas.addMouseEventChannel (fromMouse.out ());
    activeCanvas.addMouseMotionEventChannel (fromMouseMotion.out ());
    activeCanvas.setGraphicsChannels (toGraphics.in (), fromGraphics.out ());
    activeCanvas.addComponentEventChannel (canvasResize.out ());
    activeCanvas.setSize (parent.getSize ());

    // If the parent is an applet, the above setSize has no effect and the activeCanvas
    // is fitted to the "Center" area (see below) of the applet's panel.

    // If the parent is a frame, the above *does* define the size of the activeCanvas
    // and the size of the parent is expanded to wrap around when it is packed.

    System.out.println ("InfectNetwork adding ActiveCanvas to the parent ...");
    parent.add ("Center", activeCanvas);

    // System.out.println ("InfectNetwork creating infectionControl ...");
    // control = new InfectionControl (event.out (), configure, report.out ());

    System.out.println ("InfectNetwork now creating infection ...");
    infection =
      new Infection (
        infectRate, convertRate, recoverRate, reinfectRate,
        renderChoiceIndex, sprayRadius,
        fromMouse.in (), fromMouseMotion.in (),
        resetEvent.in (), resetConfigure.out (),
        freezeEvent.in (), freezeConfigure.out (),
        // renderRateBarEvent.in (), renderRateBarConfigure.out (),
        infectRateBarEvent.in (), infectRateBarConfigure.out (),
        convertRateBarEvent.in (), convertRateBarConfigure.out (),
        recoverRateBarEvent.in (), recoverRateBarConfigure.out (),
        fpsConfigure.out (), infectedConfigure.out (), deadConfigure.out (),
        renderEvent.in (), renderConfigure.out (), renderMenu, renderLookup,
        // renderRateLabelConfigure.out (),
        infectRateLabelConfigure.out (),
        convertRateLabelConfigure.out (),
        recoverRateLabelConfigure.out (),
        toGraphics.out (), fromGraphics.in (),
        canvasResize.in ()
      );

    // System.out.println ("InfectNetwork creating pseudo button ...");
    // 
    // pseudoButton = new PseudoButton (configure[InfectionControl.NUMBER],
    //                                  event[InfectionControl.NUMBER],
    //                                  feedBack);

  }

  public void run () {

    System.out.println ("Infect starting up the network ...");

    new Parallel (
      new CSProcess[] {
        activeCanvas,
        resetButton,
        freezeButton,
        // renderRateBar,
        infectRateBar,
        convertRateBar,
        recoverRateBar,
        // pseudoButton,
        fpsLabel,
        infectedLabel,
        deadLabel,
        renderChoice,
        // renderRateLabel,
        infectRateLabel,
        convertRateLabel,
        recoverRateLabel,
        // control,
        infection
        // new Parallel (button)
      }
    ).run ();

  }

}
