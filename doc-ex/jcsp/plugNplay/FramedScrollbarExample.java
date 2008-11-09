import org.jcsp.lang.*;
import org.jcsp.util.ints.*;
import org.jcsp.plugNplay.*;

public class FramedScrollbarExample {

  public static void main (String argv[]) {
  
    // initial pixel sizes for the scrollbar frame
    
    final boolean horizontal = true;
  
    final int pixDown = horizontal ? 300 : 400;
    final int pixAcross = horizontal ? 400 : 300;
  
    // the event channel is wired up to the scrollbar & reports all slider movements ...

    final One2OneChannelInt event =
      Channel.one2oneInt (new OverWriteOldestBufferInt (10));

    // the configure channel is wired up to the scrollbar  ...

    final One2OneChannel configure = Channel.one2one ();

    // make the framed scrollbar (connecting up its wires) ...

    final FramedScrollbar scrollbar =
      new FramedScrollbar (
        "FramedScrollbar Demo", pixDown, pixAcross,
        configure.in (), event.out (),
        horizontal, 0, 10, 0, 100
      );

    // testrig ...

    new Parallel (
    
      new CSProcess[] {
      
        scrollbar,
        
        new CSProcess () {        
          public void run () {            
            while (true) {
              final int n = event.in ().read ();
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
              configure.out ().write (Boolean.FALSE);
              for (int i = disabledCountdown; i > 0; i--) {
                System.out.println ("\t\t\t\tScrollbar disabled ... " + i);
                tim.sleep (second);
              }
              configure.out ().write (Boolean.TRUE);
              System.out.println ("\t\t\t\tScrollbar enabled ...");
            }            
          }          
        }
        
      }
    ).run ();

  }

}
