import jcsp.lang.*;
import java.awt.event.*;

public class PongKeyControl implements CSProcess {

  private final ChannelInput keyboard;
  private final ChannelOutputInt leftMove;
  private final ChannelOutputInt rightMove;

  public PongKeyControl (final ChannelInput keyboard,
                         final ChannelOutputInt leftMove,
                         final ChannelOutputInt rightMove) {
    this.keyboard = keyboard;
    this.leftMove = leftMove;
    this.rightMove = rightMove;
  }

  public void run () {
System.out.println ("PongKeyControl starting ...");
    while (true) {
      final KeyEvent keyEvent = (KeyEvent) keyboard.read ();
      if (keyEvent.getID () == KeyEvent.KEY_PRESSED) {
        switch (keyEvent.getKeyCode ()) {
          case KeyEvent.VK_A:
            leftMove.write (PongPaddle.UP);
          break;
          case KeyEvent.VK_Z:
            leftMove.write (PongPaddle.DOWN);
          break;
          case KeyEvent.VK_K:
            rightMove.write (PongPaddle.UP);
          break;
          case KeyEvent.VK_M:
            rightMove.write (PongPaddle.DOWN);
          break;
        }
      }
    }
  }

}
