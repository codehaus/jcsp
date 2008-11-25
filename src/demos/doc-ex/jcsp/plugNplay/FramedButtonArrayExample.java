import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.plugNplay.*;

public class FramedButtonArrayExample {

  public static void main (String argv[]) {

    // labels for the array of buttons

    final String[] label = {"JCSP", "Rocket Science", "occam-pi", "Goodbye World"};

    final int nButtons = label.length;

    // row or column?

    final boolean horizontal = true;
  
    // initial pixel sizes for the frame for the button array
    
    final int pixDown = 20 + (horizontal ? 120 : nButtons*120);
    final int pixAcross = horizontal ? nButtons*120 : 120;
  
    // all button events are wired (for this example) to the same channel ...

    final Any2OneChannel allEvents =
      Channel.any2one (new OverWriteOldestBuffer (10));

    final Any2OneChannel[] event = new Any2OneChannel[nButtons];
    
    for (int i = 0; i < nButtons; i++) {
      event[i] = allEvents;
    }

    // each button is given its own configuration channel ...

    final One2OneChannel[] configure = Channel.one2oneArray (nButtons);

    // make the array of buttons ...

    final FramedButtonArray buttons =
      new FramedButtonArray (
        "FramedButtonArray Demo", nButtons,
        pixDown, pixAcross, horizontal,
        Channel.getInputArray (configure), Channel.getOutputArray (event)
      );

    // testrig ...

    new Parallel (
    
      new CSProcess[] {
      
        buttons,
        
        new CSProcess () {
        
          public void run () {
    
            for (int i = 0; i < nButtons; i++) {
              configure[i].out ().write (label[i]);
            }
            
            boolean running = true;
            while (running) {
              final String s = (String) allEvents.in ().read ();
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
