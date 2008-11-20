import java.awt.*;
import java.awt.event.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.awt.*;

public class ActiveFrameButtonExample {

  public static void main (String argv[]) {

    final Any2OneChannel windowEvent = Channel.any2one (new OverWriteOldestBuffer (10));

    final ActiveFrame frame =
      new ActiveFrame (null, windowEvent.out (), "ActiveButton Example");

    final String[] label = {"Hello World", "Rocket Science", "CSP",
                            "Monitors", "Ignore Me", "Goodbye World"};

    final Any2OneChannel buttonEvent = Channel.any2one (new OverWriteOldestBuffer (10));

    final ActiveButton[] button = new ActiveButton[label.length];
    for (int i = 0; i < label.length; i++) {
      button[i] = new ActiveButton (null, buttonEvent.out (), label[i]);
    }

    frame.setSize (300, 200);
    frame.setLayout (new GridLayout (label.length/2, 2));
    for (int i = 0; i < label.length; i++) {
      frame.add (button[i]);
    }
    frame.setVisible (true);

    new Parallel (
      new CSProcess[] {
        new Parallel (button),
        new CSProcess () {                 // respond to window events
          public void run () {
            boolean running = true;
            while (running) {
              final WindowEvent w = (WindowEvent) windowEvent.in ().read ();
              System.out.println ("Window event: " + w);
              running = (w.getID () != WindowEvent.WINDOW_CLOSING);
            }
            frame.setVisible (false);
            System.exit (0);
          }
        },
        new CSProcess () {                 // respond to button events
          public void run () {
            boolean running = true;
            while (running) {
              final String s = (String) buttonEvent.in ().read ();
              System.out.println ("Button `" + s + "' pressed ...");
              running = (s != label[label.length - 1]);
            }
            frame.setVisible (false);
            System.exit (0);
          }
        }
      }
    ).run ();

  }

}
