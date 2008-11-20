import java.awt.*;
import java.awt.event.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.awt.*;

public class ActiveChoiceExample {

  public static void main (String argv[]) {

    final Frame root = new Frame ("ActiveChoice Example");

    final One2OneChannel close = Channel.one2one (new OverWriteOldestBuffer (1));

    final ActiveChoice choice = new ActiveChoice (null, close.out ());

    final String[] menu = {"Hello World", "Rocket Science", "CSP",
                           "Monitors", "Ignore Me", "Goodbye World"};

    for (int i = 0; i < menu.length; i++) {
      choice.add (menu[i]);
    }

    root.setSize (200, 100);
    root.add (choice);
    root.setVisible (true);

    new Parallel (
      new CSProcess[] {
        choice,
        new CSProcess () {
          public void run () {
            boolean running = true;
            while (running) {
              ItemEvent e = (ItemEvent) close.in ().read ();
              String item = (String) e.getItem ();
              System.out.println ("Selected ==> `" + item + "'");
              running = (item != menu[menu.length - 1]);
            }
            root.setVisible (false);
            System.exit (0);
          }
        }
      }
    ).run ();

  }

}
