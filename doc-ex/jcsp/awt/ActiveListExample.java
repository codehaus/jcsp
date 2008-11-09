import java.awt.*;
import java.awt.event.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.awt.*;

public class ActiveListExample {

  public static void main (String argv[]) {

    final Frame root = new Frame ("ActiveList Example");

    final One2OneChannel configure = Channel.one2one ();

    final One2OneChannel event = Channel.one2one (new OverWriteOldestBuffer (10));
    final One2OneChannel itemEvent = Channel.one2one (new OverWriteOldestBuffer (10));

    final ActiveList list = new ActiveList (configure.in (), event.out (), 0, true);
    list.addItemEventChannel (itemEvent.out ());

    final String[] menu = {"Hello World", "Rocket Science", "CSP",
                          "Monitors", "Ignore Me", "Goodbye World"};

    for (int i = 0; i < menu.length; i++) {
      list.add (menu[i]);
    }

    root.setSize (300, 105);
    root.add (list);
    root.setVisible (true);

    new Parallel (
      new CSProcess[] {           // respond to the event channel
        list,
        new CSProcess () {
          public void run () {
            boolean running = true;
            while (running) {
              final String s = (String) event.in ().read ();
              System.out.println ("                         Action ==> `" + s + "'");
              running = (s != menu[menu.length - 1]);
            }
            root.setVisible (false);
            System.exit (0);
          }
        },
        new CSProcess () {        // respond to the itemEvent channel
          public void run () {
            while (true) {
              final ItemEvent e = (ItemEvent) itemEvent.in ().read ();
              final Integer item = (Integer) e.getItem ();
              if (e.getStateChange () == ItemEvent.SELECTED) {
                System.out.println ("Selected item " + item);
              } else {
                System.out.println ("Unselected item " + item);
              }
            }
          }
        },
        new CSProcess () {        // dynamically reconfigure the list
          public void run () {
            CSTimer tim = new CSTimer ();
            long timeout = tim.read ();
            while (true) {
              timeout += 10000;
              tim.after (timeout);
              System.out.println ("*** Removing last three items ...");
              for (int i = 3; i < menu.length; i++) {
                configure.out ().write ("-" + menu[i]);
              }
              timeout += 10000;
              tim.after (timeout);
              System.out.println ("*** Restoring last three items ...");
              for (int i = 3; i < menu.length; i++) {
                configure.out ().write (menu[i]);
              }
            }
          }
        }
      }
    ).run ();

  }

}
