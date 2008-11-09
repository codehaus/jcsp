import java.awt.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.awt.*;

public class ActiveButtonExample {

  public static void main (String argv[]) {

    final Frame root = new Frame ("ActiveButton Example");

    final String[] label = {"Hello World", "Rocket Science", "CSP",
                            "Monitors", "Ignore Me", "Goodbye World"};

    final Any2OneChannel event = Channel.any2one (new OverWriteOldestBuffer (10));

    final ActiveButton[] button = new ActiveButton[label.length];
    for (int i = 0; i < label.length; i++) {
      button[i] = new ActiveButton (null, event.out (), label[i]);
      button[i].setBackground (Color.green);
    }

    root.setSize (300, 200);
    root.setLayout (new GridLayout (label.length/2, 2));
    for (int i = 0; i < label.length; i++) {
      root.add (button[i]);
    }
    root.setVisible (true);

    new Parallel (
      new CSProcess[] {
        new Parallel (button),
        new CSProcess () {
          public void run () {
            boolean running = true;
            while (running) {
              final String s = (String) event.in ().read ();
              System.out.println ("Button `" + s + "' pressed ...");
              running = (s != label[label.length - 1]);
            }
            root.setVisible (false);
            System.exit (0);
          }
        }
      }
    ).run ();
  }

}
