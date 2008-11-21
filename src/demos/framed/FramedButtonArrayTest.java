import jcsp.lang.*;
import jcsp.util.*;
import jcsp.plugNplay.*;

public class FramedButtonArrayTest {

  public static void main (String argv[]) {

    // labels for the array of buttons

    final String[] label = {"JCSP", "Rocket Science", "occam-pi", "Goodbye World"};

    final int nButtons = label.length;

    // row or column?

    final boolean horizontal = false;
  
    // initial pixel sizes for the frame for the button array
    
    final int pixDown = 20 + (horizontal ? 120 : nButtons*120);
    final int pixAcross = horizontal ? nButtons*120 : 120;
  
    // all button events are wired (for this example) to the same channel ...

    final Any2OneChannel allEvents =
      Any2OneChannel.create (new OverWriteOldestBuffer (10));

    final Any2OneChannel[] event = new Any2OneChannel[nButtons];
    
    for (int i = 0; i < nButtons; i++) {
      event[i] = allEvents;
    }

    // each button is given its own configuration channel ...

    final One2OneChannel[] configure = One2OneChannel.create (nButtons);

    // make the array of buttons ...

    FramedButtonArray f = null;

    final FramedButtonArray buttons =
      new FramedButtonArray (
        "FramedButtonArray Demo", nButtons,
	pixDown, pixAcross, horizontal,
	configure, event
      );

    // testrig ...

    new Parallel (
    
      new CSProcess[] {
      
        buttons,
        
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
