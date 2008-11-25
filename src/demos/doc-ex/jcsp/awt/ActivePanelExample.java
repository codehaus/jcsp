import java.awt.*;
import java.awt.event.*;
import org.jcsp.util.*;
import org.jcsp.lang.*;
import org.jcsp.awt.*;

public class ActivePanelExample {

  public static void main (String argv[]) {

    final Frame root = new Frame ("ActivePanel Example");

    final One2OneChannel mouseEvent = Channel.one2one (new OverWriteOldestBuffer (10));

    final ActivePanel panel = new ActivePanel ();
    panel.addMouseEventChannel (mouseEvent.out ());

    root.add (panel);
    root.setSize (400, 400);
    root.setVisible (true);

    new Parallel (
      new CSProcess[] {
        panel,
        new CSProcess () {
          public void run () {
            boolean running = true;
            while (running) {
              final MouseEvent event = (MouseEvent) mouseEvent.in ().read ();
              switch (event.getID ()) {
                case MouseEvent.MOUSE_ENTERED:
                  System.out.println ("MOUSE_ENTERED");
                break;
                case MouseEvent.MOUSE_EXITED:
                  System.out.println ("MOUSE_EXITED");
                break;
                case MouseEvent.MOUSE_PRESSED:
                  System.out.println ("MOUSE_PRESSED");
                break;
                case MouseEvent.MOUSE_RELEASED:
                  System.out.println ("MOUSE_RELEASED");
                break;
                case MouseEvent.MOUSE_CLICKED:
                  if (event.getClickCount() > 1) {
                    System.out.println ("MOUSE_DOUBLE_CLICKED ... goodbye!");
                    running = false;
                  } else {
                    System.out.println ("MOUSE_CLICKED ... *double* click to quit!");
                  }
                break;
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
