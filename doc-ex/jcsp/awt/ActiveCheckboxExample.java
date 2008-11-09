import java.awt.*;
import java.awt.event.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.awt.*;

public class ActiveCheckboxExample {

  public static void main (String argv[]) {

    final Frame root = new Frame ("ActiveCheckbox Example");

    final String[] box = {"Hello World", "Rocket Science", "CSP",
                          "Monitors", "Ignore Me", "Goodbye World"};

    final Any2OneChannel event = Channel.any2one (new OverWriteOldestBuffer (10));

    final ActiveCheckbox[] check = new ActiveCheckbox[box.length];
    for (int i = 0; i < box.length; i++) {
      check[i] = new ActiveCheckbox (null, event.out (), box[i]);
    }

    root.setSize (300, 200);
    root.setLayout (new GridLayout (box.length, 1));
    for (int i = 0; i < box.length; i++) {
     root.add (check[i]);
    }
    root.setVisible (true);

    new Parallel (
      new CSProcess[] {
        new Parallel (check),
        new CSProcess () {
          public void run () {
            boolean running = true;
            while (running) {
              final ItemEvent e = (ItemEvent) event.in ().read ();
              final String item = (String) e.getItem ();
              if (e.getStateChange () == ItemEvent.SELECTED) {
                System.out.println ("Checked ==> `" + item + "'");
                running = (item != box[box.length - 1]);
              } else {
                System.out.println ("Unchecked ==> `" + item + "'");
              }
            }
            root.setVisible (false);
            System.exit (0);
          }
        }
      }
    ).run ();

  }

}
