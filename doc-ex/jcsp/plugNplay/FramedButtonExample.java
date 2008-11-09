import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.plugNplay.*;

public class FramedButtonExample {

  public static void main (String argv[]) {
  
    // initial pixel sizes for the button frame
    
    final int pixDown = 100;
    final int pixAcross = 250;
  
    // labels for the button

    final String[] label = {"JCSP", "Rocket Science", "occam-pi", "Goodbye World"};

    // the event channel is wired up to the button & reports all button presses ...

    final One2OneChannel event = Channel.one2one (new OverWriteOldestBuffer (10));

    // the configure channel is wired up to the button  ...

    final One2OneChannel configure = Channel.one2one ();

    // make the framed button (connecting up its wires) ...

    final FramedButton button =
      new FramedButton (
        "FramedButton Demo", pixDown, pixAcross, configure.in (), event.out ()
      );

    // testrig ...

    new Parallel (
    
      new CSProcess[] {
      
        button,
        
        new CSProcess () {
        
          public void run () {
    
            int i = 0;
            
            while (true) {
              configure.out ().write (label[i]);
              i = (i + 1) % label.length;
              final String s = (String) event.in ().read ();
              System.out.println ("Button `" + s + "' pressed ...");
            }
            
          }
          
        }
        
      }
    ).run ();

  }

}
