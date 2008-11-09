import java.awt.*;
import java.awt.event.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.awt.*;

public class ActiveClosingFrameButtonExample {

  public static void main (String argv[]) {

    final ActiveClosingFrame frame =
      new ActiveClosingFrame ("ActiveClosingFrameButton Example");

    final String[] label = {"Hello World", "Rocket Science", "CSP",
                            "Monitors", "Ignore Me", "Goodbye World"};

    final Any2OneChannel buttonEvent = Channel.any2one (new OverWriteOldestBuffer (10));

    final ActiveButton[] button = new ActiveButton[label.length];
    for (int i = 0; i < label.length; i++) {
      button[i] = new ActiveButton (null, buttonEvent.out (), label[i]);
    }

    final Frame realFrame = frame.getActiveFrame ();
    realFrame.setSize (300, 200);
    realFrame.setLayout (new GridLayout (label.length/2, 2));
    for (int i = 0; i < label.length; i++) {
      realFrame.add (button[i]);
    }
    realFrame.setVisible (true);

    new Parallel (
      new CSProcess[] {
        frame,
        new Parallel (button),
        new CSProcess () {
          public void run () {
            boolean running = true;
            while (running) {
              final String s = (String) buttonEvent.in ().read ();
              System.out.println ("Button `" + s + "' pressed ...");
              running = (s != label[label.length - 1]);
            }
            realFrame.setVisible (false);
            System.exit (0);
          }
        }
      }
    ).run ();

  }

}
