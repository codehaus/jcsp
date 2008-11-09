import java.awt.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.awt.*;

public class ActiveLabelExample {

  public static void main (String argv[]) {

    final Frame root = new Frame ("ActiveLabel Example");

    final int nLabels = 8;
    final int countdown = 10;

    final One2OneChannel[] configureLabel = Channel.one2oneArray (nLabels);

    final ActiveLabel[] label = new ActiveLabel[nLabels];
    for (int i = 0; i < label.length; i++) {
      label[i] = new ActiveLabel (configureLabel[i].in (), "==>  " + countdown + "  <==");
      label[i].setAlignment (Label.CENTER);
    }

    final One2OneChannel configureButton = Channel.one2one ();
    final One2OneChannel event = Channel.one2one (new OverWriteOldestBuffer (10));

    final ActiveButton button = new ActiveButton (configureButton.in (), event.out (), "Start");

    root.setSize (300, 200);
    root.setLayout (new GridLayout (3, 3));
    for (int i = 0; i < nLabels + 1; i++) {
      if (i < 4) {
        root.add (label[i]);
      } else if (i == 4) {
        root.add (button);
      } else if (i > 4) {
        root.add (label[i - 1]);
      }
    }
    root.setVisible (true);

    new Parallel (
      new CSProcess[] {
        new Parallel (label),
        button,
        new CSProcess () {
          public void run () {
            final long second = 1000;
            CSTimer tim = new CSTimer ();
            Alternative alt = new Alternative (new Guard[] {event.in (), tim});
            event.in ().read ();              // wait for the start signal
            configureButton.out ().write ("Restart");
            int count = countdown;
            long timeout = tim.read () + second;
            while (count > 0) {
              tim.setAlarm (timeout);
              switch (alt.priSelect ()) {
                case 0:                 // reset signal
                  event.in ().read ();        // clear the reset
                  timeout = tim.read () + second;
                  count = countdown;
                break;
                case 1:                 // timeout signal
                  timeout += second;
                  count--;
                break;
              }
              final String newLabel = "==>  " + count + "  <==";
              for (int i = 0; i < nLabels; i++) {
                configureLabel[i].out ().write (newLabel);
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
