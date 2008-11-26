
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
import org.jcsp.util.*;
import org.jcsp.awt.*;

import java.awt.*;

public class PongNetwork implements CSProcess {

  private final ActiveCanvas activeCanvas;

  private final PongControl control;

  private final PongBall[] balls;

  private final PongKeyControl keyControl;
  private final PongPaddle leftPaddle;
  private final PongPaddle rightPaddle;

  private final PongFlasher flasher;
  private final PongMouseControl mouseControl;

  // private final FocusControl focusControl;
  // private final FocusControl2 focusControl2;
  
  private final PongScorer scorer;

  private final ActiveButton startButton;
  private final ActiveButton freezeButton;

  private final ActiveLabel[] infoLabel;

  public PongNetwork (final int nBalls, final int ballSpeed, final int paddleSpeed,
                      final int life, final Container parent) {

    parent.setLayout (new BorderLayout ());
    
    final DisplayList displayList = new DisplayList ();
    // displayList.setMinRefreshInterval (10);
    // System.out.println ("PongNetwork: displayList.setMinRefreshInterval (10) ...");

    // channels

    final One2OneChannel mouseChannel = Channel.one2one (new OverWriteOldestBuffer (10));
    final One2OneChannel mouseMotionChannel = Channel.one2one (new OverWriteOldestBuffer (1));
    final One2OneChannel focusChannel = Channel.one2one (new OverWriteOldestBuffer (10));
    final One2OneChannel keyChannel = Channel.one2one (new OverWriteOldestBuffer (10));
    
    final One2OneChannel[] toBalls = Channel.one2oneArray (nBalls);
    final Any2OneChannel fromBalls = Channel.any2one ();
    
    final One2OneChannel control2Flasher = Channel.one2one ();
    final One2OneChannelInt mouse2Flasher = Channel.one2oneInt ();
    
    final One2OneChannel control2LeftPaddle = Channel.one2one ();
    final One2OneChannel control2RightPaddle = Channel.one2one ();
    
    final One2OneChannelInt leftMove = Channel.one2oneInt ();
    final One2OneChannelInt rightMove = Channel.one2oneInt ();

    final Any2OneChannel toGraphics = Channel.any2one ();
    final One2OneChannel fromGraphics = Channel.one2one ();

    final Any2OneChannel balls2LeftPaddle = Channel.any2one ();
    final One2OneChannelInt leftPaddle2Balls = Channel.one2oneInt ();

    final Any2OneChannel balls2RightPaddle = Channel.any2one ();
    final One2OneChannelInt rightPaddle2Balls = Channel.one2oneInt ();
    
    final One2OneChannelInt[] toScorer = Channel.one2oneIntArray (2);

    final Barrier dead = new Barrier (nBalls);

    // processes

    activeCanvas = new ActiveCanvas ();
    // activeCanvas.addFocusEventChannel (focusChannel);
    activeCanvas.addKeyEventChannel (keyChannel.out());
    activeCanvas.addMouseEventChannel (mouseChannel.out());
    activeCanvas.setBackground (Color.black);
    activeCanvas.setPaintable (displayList);
    activeCanvas.setGraphicsChannels (toGraphics.in(), fromGraphics.out());
    activeCanvas.setSize (parent.getSize ());

    // If the parent is an applet, the above setSize has no effect and the activeCanvas
    // is fitted to the "Center" area (see below) of the applet's panel.

    // If the parent is a frame, the above *does* define the size of the activeCanvas
    // and the size of the parent is expanded to wrap around when it is packed.

    System.out.println ("PongNetwork adding ActiveCanvas to the parent ...");
    parent.add ("Center", activeCanvas);

    // buttons and menus

    final Panel south = new Panel ();
    south.setBackground (Color.green);

    final One2OneChannel startChannel = Channel.one2one (new OverWriteOldestBuffer (1));
    final One2OneChannel startConfigure = Channel.one2one ();
    startButton = new ActiveButton (startConfigure.in(), startChannel.out(), "XXXXXXXXXXXXXXXXXXXXXX");
    startButton.setBackground (Color.white);
    startButton.setEnabled (false);
    south.add (startButton);

    south.add (new Label ("                      ", Label.CENTER));    // padding

    final One2OneChannel freezeChannel = Channel.one2one (new OverWriteOldestBuffer (1));
    final One2OneChannel freezeConfigure = Channel.one2one ();
    freezeButton = new ActiveButton (freezeConfigure.in(), freezeChannel.out(), "XXXXXXXXXXXXXXXXXXXXXX");
    freezeButton.setBackground (Color.white);
    freezeButton.setEnabled (true);
    south.add (freezeButton);
   
    parent.add ("South", south);

    // labels

    final Panel north = new Panel ();
    north.setBackground (Color.green);

    final String[] infoTitle = {"Left", "Right"};
    final String[] infoWidth = {"XXXXXXXXXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXXXXXXXXX"};
    final One2OneChannel[] infoConfigure = Channel.one2oneArray (infoTitle.length);
    infoLabel = new ActiveLabel[infoTitle.length];
    for (int i = 0; i < infoTitle.length; i++) {
      infoLabel[i] = new ActiveLabel (infoConfigure[i].in(), infoWidth[i]);
      infoLabel[i].setAlignment (Label.CENTER);
      infoLabel[i].setBackground (Color.white);
      north.add (new Label (infoTitle[i], Label.CENTER));
      north.add (infoLabel[i]);
    }

    parent.add ("North", north);

    // balls

    balls = new PongBall[nBalls];
    for (int i = 0; i < nBalls; i++) {
      balls[i] = new PongBall (
        i, ballSpeed, life, dead,
        balls2LeftPaddle.out(), leftPaddle2Balls.in(),
        balls2RightPaddle.out(), rightPaddle2Balls.in(),
        toBalls[i].in(), fromBalls.out(), displayList
      );
    }

    // keystrokes manager
    
    keyControl= new PongKeyControl (keyChannel.in(), leftMove.out(), rightMove.out());

    // paddles

    leftPaddle = new PongPaddle (
      true, paddleSpeed, leftMove.in(),
      balls2LeftPaddle.in(), leftPaddle2Balls.out(),
      toScorer[0].out(), control2LeftPaddle.in(), displayList
    );

    rightPaddle = new PongPaddle (
      false, paddleSpeed, rightMove.in(),
      balls2RightPaddle.in(), rightPaddle2Balls.out(),
      toScorer[1].out(), control2RightPaddle.in(), displayList
    );

    // focusControl = new FocusControl (focusChannel, toGraphics, fromGraphics);
    // focusControl2 = new FocusControl2 (startChannel, toGraphics, fromGraphics);
    
    // scorer

    scorer = new PongScorer (Channel.getInputArray (toScorer), Channel.getOutputArray (infoConfigure));

    // background flasher and its (mouse) controller

    flasher = new PongFlasher (control2Flasher.in(), mouse2Flasher.in(), displayList);

    mouseControl = new PongMouseControl (mouse2Flasher.out(), toGraphics.out(), fromGraphics.in(), mouseChannel.in());

    // main game controller

    control = new PongControl (
      Channel.getOutputArray (toBalls), fromBalls.in(),
      control2Flasher.out(), control2LeftPaddle.out(), control2RightPaddle.out(),
      freezeConfigure.out(), freezeChannel.in(),
      startConfigure.out(), startChannel.in(),
      toGraphics.out(), fromGraphics.in()
    );

  }

  public void run () {

    System.out.println ("PongNetwork starting up ...");

    new Parallel (
      new CSProcess[] {
        activeCanvas,
        control,
        new Parallel (balls),
        keyControl,
        leftPaddle,
        rightPaddle,
        scorer,
        flasher,
        mouseControl,
        freezeButton,
        startButton,
        new Parallel (infoLabel),
      }
    ).run ();

  }

}
