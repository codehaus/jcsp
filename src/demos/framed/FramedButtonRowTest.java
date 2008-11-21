import java.awt.*;
import jcsp.lang.*;
import jcsp.util.*;
import jcsp.awt.*;

public class FramedButtonRowTest {

  public static void main (String argv[]) {
  
    // initial pixel sizes for the row of buttons
    
    final int pixDown = 100;
    final int pixAcross = 500;
  
    // labels for the row of buttons

    final String[] label = {"JCSP", "Rocket Science", "CSP", "Goodbye World"};

    final int nButtons = label.length;

    // the allEvents channel is wired up to all buttons & reports all button presses ...

    final Any2OneChannel allEvents =
      Any2OneChannel.create (new OverWriteOldestBuffer (10));

    final Any2OneChannel[] event = new Any2OneChannel[nButtons];
    
    for (int i = 0; i < nButtons; i++) {
      event[i] = allEvents;
    }

    // make the row of buttons (each one separately configured) ...

    final One2OneChannel[] configure = One2OneChannel.create (nButtons);

    final FramedButtonRow row =
      new FramedButtonRow (
        "FramedButtonRow Demo", nButtons, pixDown, pixAcross, configure, event
      );

    // testrig ...

    new Parallel (
    
      new CSProcess[] {
      
        row,
        
        new CSProcess () {
        
          public void run () {
    
            for (int i = 0; i < nButtons; i++) {
              configure[i].write (label[i]);
            }
            
            boolean running = true;
            while (running) {
              final String s = (String) allEvents.read ();
              System.out.println ("Button `" + s + "' pressed ...");
              running = (s != label[nButtons - 1]);
            }
            
            System.exit (0);
            
          }
          
        }
        
      }
    ).run ();

  }

}
