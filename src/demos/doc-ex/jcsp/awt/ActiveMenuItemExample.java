import java.awt.*;
import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.awt.*;

public class ActiveMenuItemExample {

  public static void main (String argv[]) {

    final ActiveClosingFrame activeClosingFrame =
      new ActiveClosingFrame ("ActiveMenuItem Example");

    final ActiveFrame frame = activeClosingFrame.getActiveFrame ();

    final MenuBar menuBar = new MenuBar ();
    frame.setMenuBar (menuBar);

    final Menu fileMenu = new Menu ("File");
    final Menu langMenu = new Menu ("Language");
    menuBar.add (fileMenu);
    menuBar.add (langMenu);

    final String[] fileOptions = {"Hello World", "Rocket Science", "CSP",
                                  "Monitors", "Ignore Me", "Goodbye World"};
    final String[] langOptions = {"occam-pi", "Java", "Smalltalk", "Algol-60",
                                  "Pascal", "Haskell", "SML", "Lisp"};

    final Any2OneChannel event[] = Channel.any2oneArray (2, new OverWriteOldestBuffer (10));

    final ActiveMenuItem[] fileMenuItem = new ActiveMenuItem[fileOptions.length];
    for (int i = 0; i < fileOptions.length; i++) {
      fileMenuItem[i] = new ActiveMenuItem (null, event[0].out (), fileOptions[i]);
      fileMenu.add (fileMenuItem[i]);
    }

    final ActiveMenuItem[] langMenuItem = new ActiveMenuItem[langOptions.length];
    for (int i = 0; i < langOptions.length; i++) {
      langMenuItem[i] = new ActiveMenuItem (null, event[1].out (), langOptions[i]);
      langMenu.add (langMenuItem[i]);
    }

    frame.setSize (300, 200);
    frame.setBackground (Color.green);
    frame.setVisible (true);

    new Parallel (
      new CSProcess[] {
        activeClosingFrame,
        new Parallel (fileMenuItem),
        new Parallel (langMenuItem),
        new CSProcess () {
          public void run () {
            boolean running = true;
            while (running) {
              final String s = (String) event[0].in ().read ();
              System.out.println ("File ==> `" + s + "' selected ...");
              running = (s != fileOptions[fileOptions.length - 1]);
            }
            frame.setVisible (false);
            System.exit (0);
          }
        },
        new CSProcess () {
          public void run () {
            while (true) {
              final String s = (String) event[1].in ().read ();
              System.out.println ("Language ==> `" + s + "' selected ...");
            }
          }
        }
      }
    ).run ();

  }

}
