import java.awt.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.awt.*;

public class ActiveFileDialogExample {

  public static void main (String argv[]) {

    final Frame root = new Frame ();

    final One2OneChannel configure = Channel.one2one ();

    final One2OneChannel event = Channel.one2one (new OverWriteOldestBuffer (10));

    final ActiveFileDialog fileDialog =
      new ActiveFileDialog (configure.in (), event.out (), root, "ActiveFileDialog Example");

    new Parallel (
      new CSProcess[] {
        fileDialog,
        new CSProcess () {
          public void run () {
            String dir = ".";           // start directory for the file dialogue
            String file = "";
            while (file != null) {
              configure.out ().write (dir);
              configure.out ().write (Boolean.TRUE);
              dir = (String) event.in ().read ();
              file = (String) event.in ().read ();
              if (file != null)
                System.out.println ("Chosen file = `" + dir + file + "'");
            }
          System.exit (0);
          }
        }
      }
    ).run ();
  }

}
