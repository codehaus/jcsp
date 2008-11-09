import java.awt.*;
import java.awt.event.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.awt.*;

public class ActiveDialogExample {

  public static void main (String argv[]) {

    final Frame root = new Frame ();

    final One2OneChannel event = Channel.one2one (new OverWriteOldestBuffer (10));

    final ActiveDialog dialog = new ActiveDialog (null, event.out (), root, "ActiveDialog Example");

    // root.setSize (400, 400);
    // root.setVisible (true);
    dialog.setSize (300, 200);
    dialog.setVisible (true);

    new Parallel (
      new CSProcess[] {
        dialog,
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
