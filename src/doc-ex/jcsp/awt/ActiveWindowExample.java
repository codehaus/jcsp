import java.awt.*;
import java.awt.event.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.awt.*;

public class ActiveWindowExample {

  public static void main (String argv[]) {

    final Frame root = new Frame ("ActiveWindow Example");

    final One2OneChannel event = Channel.one2one (new OverWriteOldestBuffer (10));

    final ActiveWindow window = new ActiveWindow (null, event.out (), root);

    root.setSize (400, 400);
    root.setVisible (true);
    window.setVisible (true);

    new Parallel (
      new CSProcess[] {
        window,
        new CSProcess () {
          public void run () {
            while (true) {
              WindowEvent w = (WindowEvent) event.in ().read ();
              System.out.println (w);
            }
          }
        }
      }
    ).run ();
  }

}
