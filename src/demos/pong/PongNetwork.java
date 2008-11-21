import jcsp.lang.*;
import jcsp.util.*;
import jcsp.awt.*;

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

    final One2OneChannel mouseChannel =
      One2OneChannel.create (new OverWriteOldestBuffer (10));
    final One2OneChannel mouseMotionChannel =
      One2OneChannel.create (new OverWriteOldestBuffer (1));
    final One2OneChannel focusChannel =
      One2OneChannel.create (new OverWriteOldestBuffer (10));
    final One2OneChannel keyChannel =
      One2OneChannel.create (new OverWriteOldestBuffer (10));
    
    final One2OneChannel[] toBalls = One2OneChannel.create (nBalls);
    final Any2OneChannel fromBalls = new Any2OneChannel ();
    
    final One2OneChannel control2Flasher = new One2OneChannel ();
    final One2OneChannelInt mouse2Flasher = new One2OneChannelInt ();
    
    final One2OneChannel control2LeftPaddle = new One2OneChannel ();
    final One2OneChannel control2RightPaddle = new One2OneChannel ();
    
    final One2OneChannelInt leftMove = new One2OneChannelInt ();
    final One2OneChannelInt rightMove = new One2OneChannelInt ();

    final Any2OneChannel toGraphics = new Any2OneChannel ();
    final One2OneChannel fromGraphics = new One2OneChannel ();

    final Any2OneChannel balls2LeftPaddle = new Any2OneChannel ();
    final One2OneChannelInt leftPaddle2Balls = new One2OneChannelInt ();

    final Any2OneChannel balls2RightPaddle = new Any2OneChannel ();
    final One2OneChannelInt rightPaddle2Balls = new One2OneChannelInt ();
    
    final One2OneChannelInt[] toScorer = One2OneChannelInt.create (2);

    final Barrier dead = new Barrier (nBalls);

    // processes

    activeCanvas = new ActiveCanvas ();
    // activeCanvas.addFocusEventChannel (focusChannel);
    activeCanvas.addKeyEventChannel (keyChannel);
    activeCanvas.addMouseEventChannel (mouseChannel);
    activeCanvas.setBackground (Color.black);
    activeCanvas.setPaintable (displayList);
    activeCanvas.setGraphicsChannels (toGraphics, fromGraphics);
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

    final One2OneChannel startChannel =
      One2OneChannel.create (new OverWriteOldestBuffer (1));
    final One2OneChannel startConfigure = new One2OneChannel ();
    startButton = new ActiveButton (startConfigure, startChannel, "XXXXXXXXXXXXXXXXXXXXXX");
    startButton.setBackground (Color.white);
    startButton.setEnabled (false);
    south.add (startButton);

    south.add (new Label ("                      ", Label.CENTER));    // padding

    final One2OneChannel freezeChannel =
      One2OneChannel.create (new OverWriteOldestBuffer (1));
    final One2OneChannel freezeConfigure = new One2OneChannel ();
    freezeButton = new ActiveButton (freezeConfigure, freezeChannel, "XXXXXXXXXXXXXXXXXXXXXX");
    freezeButton.setBackground (Color.white);
    freezeButton.setEnabled (true);
    south.add (freezeButton);
   
    parent.add ("South", south);

    // labels

    final Panel north = new Panel ();
    north.setBackground (Color.green);

    final String[] infoTitle = {"Left", "Right"};
    final String[] infoWidth = {"XXXXXXXXXXXXXXXXXXXXXX",
                                "XXXXXXXXXXXXXXXXXXXXXX"};
    final One2OneChannel[] infoConfigure = One2OneChannel.create (infoTitle.length);
    infoLabel = new ActiveLabel[infoTitle.length];
    for (int i = 0; i < infoTitle.length; i++) {
      infoLabel[i] = new ActiveLabel (infoConfigure[i], infoWidth[i]);
      infoLabel[i].setAlignment (Label.CENTER);
      infoLabel[i].setBackground (Color.white);
      north.add (new Label (infoTitle[i], Label.CENTER));
      north.add (infoLabel[i]);
    }

    parent.add ("North", north);

    balls = new PongBall[nBalls];
    for (int i = 0; i < nBalls; i++) {
      balls[i] = new PongBall (i, ballSpeed, life, dead,
                               balls2LeftPaddle, leftPaddle2Balls,
                               balls2RightPaddle, rightPaddle2Balls,
                               toBalls[i], fromBalls, displayList);
    }
    
    keyControl= new PongKeyControl (keyChannel, leftMove, rightMove);

    leftPaddle = new PongPaddle (true, paddleSpeed, leftMove,
                                 balls2LeftPaddle, leftPaddle2Balls,
                                 toScorer[0], control2LeftPaddle, displayList);
    rightPaddle = new PongPaddle (false, paddleSpeed, rightMove,
                                  balls2RightPaddle, rightPaddle2Balls,
                                  toScorer[1], control2RightPaddle, displayList);

    // focusControl = new FocusControl (focusChannel, toGraphics, fromGraphics);
    // focusControl2 = new FocusControl2 (startChannel, toGraphics, fromGraphics);

    scorer = new PongScorer (toScorer, infoConfigure);

    flasher = new PongFlasher (control2Flasher, mouse2Flasher, displayList);

    mouseControl = new PongMouseControl (mouse2Flasher, toGraphics, fromGraphics, mouseChannel);

    control = new PongControl (toBalls, fromBalls,
                               control2Flasher, control2LeftPaddle, control2RightPaddle,
                               freezeConfigure, freezeChannel,
                               startConfigure, startChannel,
                               toGraphics, fromGraphics);

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

