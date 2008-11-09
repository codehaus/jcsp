import java.awt.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.awt.*;

public class ActiveButtonControlExample {

  public static void main (String argv[]) {

    final Frame root = new Frame ("ActiveButtonControl Example");

    final String[][] labels = {
      new String[] {"Hello World", "JCSP", "Restart"},
      new String[] {"Rocket Science", "JCSP", "occam"},
      new String[] {"Deadlock", "JCSP", "occam"},
      new String[] {"Race Hazard", "JCSP", "occam"},
      new String[] {"Starvation", "JCSP", "Quit", "Back"},
      new String[] {"Threads", "JCSP", "occam"},
      new String[] {"Livelock", "JCSP", "occam"},
      new String[] {"Monitors", "JCSP", "occam"},
      new String[] {"Alchemy", "JCSP", "Smile"}
    };

    final int nButtons = labels.length;

    final One2OneChannel[] fromButton = Channel.one2oneArray (nButtons, new OverWriteOldestBuffer (1));
    final Any2OneChannel[] toButton = Channel.any2oneArray (nButtons);

    final One2OneChannel report = Channel.one2one ();

    final ActiveButton[] button = new ActiveButton[nButtons];
    for (int i = 0; i < nButtons; i++) {
      button[i] = new ActiveButton (toButton[i].in (), fromButton[i].out (), "XXXXXXXXX");
      button[i].setBackground (Color.green);
    }

    root.setSize (450, 200);
    root.setLayout (new GridLayout (nButtons/3, 3));
    for (int i = 0; i < nButtons; i++) {
      root.add (button[i]);
    }
    root.setVisible (true);

    final int initial   = 0;                    // state names
    final int diagonal  = 1;
    final int opposite  = 2;
    final int centre    = 3;
    final int full      = 4;
    final int terminal  = 5;

    final String[] stateName = {
      "initial", "diagonal", "opposite", "centre", "full", "terminal"
    };

    final ActiveButtonState[] state = new ActiveButtonState[stateName.length];

    try {

      state[initial] =
        new ActiveButtonState (
          new int[] {
            0, 0, 1,
            0, 1, 1,                              // label index
            1, 1, 1
          },
          new boolean[] {
            true,  true,  false,
            true,  false, false,                  // enable/disable
            false, false, false
          },
          new int[] {
            diagonal, initial,  initial,
            initial,  initial,  initial,          // next state
            initial,  initial,  initial
          }
        );

      state[diagonal] =
        new ActiveButtonState (
          new int[] {
            1, 1, 0,
            1, 0, 1,                              // label index
            0, 1, 1
         },
          new boolean[] {
            false, false, true,
            false, true,  false,                  // enable/disable
            true,  false, false
          },
          new int[] {
            diagonal, diagonal, centre,
            diagonal, opposite, diagonal,         // next state
            full,     diagonal, diagonal
          }
        );

      state[opposite] =
        new ActiveButtonState (
          new int[] {
            1, 1, 1,
            1, 1, 0,                              // label index
            1, 0, 0
          },
          new boolean[] {
            false, false, false,
            false, false, true,                   // enable/disable
            false, true,  true
          },
          new int[] {
            opposite, opposite, opposite,
            opposite, opposite, opposite,         // next state
            opposite, opposite, diagonal
          }
        );

      state[centre] =
        new ActiveButtonState (
          new int[] {
            1, 1, 1,
            1, 3, 1,                              // label index
            1, 1, 1
          },
          new boolean[] {
            false, false, false,
            false, true,  false,                  // enable/disable
            false, false, false
          },
          new int[] {
            centre,   centre,   centre,
            centre,   diagonal, centre,           // next state
            centre,   centre,   centre
          }
        );

      state[full] =
        new ActiveButtonState (
          new int[] {
            2, 2, 2,
            2, 2, 2,                              // label index
            2, 2, 2
          },
          new boolean[] {
            true,  true,  true,
            true,  true,  true,                   // enable/disable
            true,  true,  true
          },
          new int[] {
            initial,  diagonal, diagonal,
            diagonal, terminal, diagonal,         // next state
            diagonal, diagonal, opposite
          }
        );

      state[terminal] =
        new ActiveButtonState (
          new int[] {
            1, 1, 1,
            1, 1, 1,                              // label index
            1, 1, 1
          },
          new boolean[] {
            false, false, false,
            false, false, false,                  // enable/disable
            false, false, false
          },
          new int[] {
            terminal, terminal, terminal,
            terminal, terminal, terminal,         // next state
            terminal, terminal, terminal
          }
        );

    } catch (ActiveButtonState.BadArguments e) {

     System.out.println (e);
     System.exit (0);

    };

    new Parallel (
      new CSProcess[] {
        new Parallel (button),
        new CSProcess () {
          public void run () {
            final ActiveButtonControl control;
            try {
              control = new ActiveButtonControl (
                Channel.getInputArray (fromButton),
		Channel.getOutputArray (toButton),
		report.out (), labels, state, initial
              );
              control.setReportButtonIndex (true);
              control.setReportButtonLabel (true);
              control.run ();
            } catch (ActiveButtonControl.BadArguments e) {
              System.out.println (e);
              System.exit (0);
            }
          }
        },
        new CSProcess () {
          public void run () {
            for (int j = 0; j < nButtons; j++) {
              toButton[j].out ().write (Color.RED);
            }
            boolean running = true;
            while (running) {
              final int newState = ((Integer) report.in ().read ()).intValue ();
              final int buttonIndex = ((Integer) report.in ().read ()).intValue ();
              final String buttonString = (String) report.in ().read ();
              System.out.println (
                "Button " + buttonIndex +
                " (" + buttonString + ") pressed ==> " + stateName[newState]
              );
              running = (newState != terminal);
            }
            final CSTimer tim = new CSTimer ();        // countdown to exit
            final long interval = 1000;                // one second
            long timeout = tim.read ();
            for (int i = 10; i >= 0; i--) {
              timeout += interval;
              tim.after (timeout);
              final String iString = (new Integer (i)).toString ();
              for (int j = 0; j < nButtons; j++) {
                toButton[j].out ().write (iString);
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
