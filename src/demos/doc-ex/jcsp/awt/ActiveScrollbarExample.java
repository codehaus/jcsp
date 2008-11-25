import java.awt.*;
import java.awt.event.*;
import org.jcsp.lang.*;
import org.jcsp.util.ints.*;
import org.jcsp.awt.*;

public class ActiveScrollbarExample {

  public static void main (String argv[]) {

    final ActiveClosingFrame activeFrame = new ActiveClosingFrame ("ActiveScrollbar Example");

    final One2OneChannel configure = Channel.one2one ();
    final One2OneChannelInt scrollEvent = Channel.one2oneInt (new OverWriteOldestBufferInt (10));

    final ActiveScrollbar scrollBar =
      new ActiveScrollbar (
        configure.in (), scrollEvent.out (), Scrollbar.HORIZONTAL, 0, 10, 0, 110
      );

    final Frame realFrame = activeFrame.getActiveFrame ();
    realFrame.setSize (400, 75);
    realFrame.add (scrollBar);
    realFrame.setVisible (true);

    new Parallel (
      new CSProcess[] {
        activeFrame,
        scrollBar,
        new CSProcess () {
          public void run () {
            final long FREE_TIME = 10000, BUSY_TIME = 250;
            CSTimer tim = new CSTimer ();
            Alternative alt = new Alternative (new Guard[] {tim, scrollEvent.in ()});
            final int TIM = 0, SCROLL = 1;
            long timeout = tim.read ();
            tim.setAlarm (timeout + FREE_TIME);
            boolean running = true;
            while (running) {
              switch (alt.priSelect ()) {
                case TIM:                                     // timeout happened
                  configure.out ().write (Boolean.FALSE);     // disable scrollbar
                  for (int i = 40; i > 0; i--) {
                    System.out.println ("*** busy busy busy ... " + i);
                    timeout = tim.read ();
                    tim.after (timeout + BUSY_TIME);
                  }
                  System.out.println ("*** free free free ... 0");
                  configure.out ().write (Boolean.TRUE);      // enable scrollbar
                  timeout = tim.read ();
                  tim.setAlarm (timeout + FREE_TIME);
                break;
                case SCROLL:                                  // scrollEvent happened
                  int position = scrollEvent.in ().read ();
                  System.out.println ("ScrollBar ==> " + position);
                  running = (position != 100);
                break;
              }
            }
            realFrame.setVisible (false);
            System.exit (0);
          }
        }
      }
    ).run ();
  }

}
