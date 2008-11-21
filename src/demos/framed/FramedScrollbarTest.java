import jcsp.lang.*;
import jcsp.util.ints.*;
import jcsp.plugNplay.*;

public class FramedScrollbarTest {

  public static void main (String argv[]) {
  
    // initial pixel sizes for the scrollbar frame
    
    final int pixDown = 400;
    final int pixAcross = 120;

    // the event channel is wired up to the scrollbar & reports all slider movements ...

    final One2OneChannelInt event =
      One2OneChannelInt.create (new OverWriteOldestBufferInt (10));

    // the configure channel is wired up to the scrollbar  ...

    final One2OneChannel configure = new One2OneChannel ();

    // make the framed scrollbar (connecting up its wires) ...

    final FramedScrollbar scrollbar =
      new FramedScrollbar (
        "FramedScrollbar Demo", pixDown, pixAcross, configure, event,
        false, 0, 10, 0, 100
      );

    // testrig ...

    new Parallel (
    
      new CSProcess[] {
      
        scrollbar,
        
        new CSProcess () {        
          public void run () {            
            while (true) {
              final int n = event.read ();
              System.out.println ("FramedScrollbar ==> " + n);
            }            
          }          
        },
        
        new CSProcess () {        
          public void run () {
            final int second = 1000;                // time is in millisecs
            final int enabledTime = 10*second;
            final int disabledCountdown = 5;
            final CSTimer tim = new CSTimer ();
            while (true) {
              tim.sleep (enabledTime);
              configure.write (Boolean.FALSE);
              for (int i = disabledCountdown; i > 0; i--) {
                System.out.println ("\t\t\t\tScrollbar disabled ... " + i);
                tim.sleep (second);
              }
              configure.write (Boolean.TRUE);
              System.out.println ("\t\t\t\tScrollbar enabled ...");
            }            
          }          
        }
        
      }
    ).run ();

  }

}
