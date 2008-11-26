import org.jcsp.lang.*;

public class LaunchControlTest {

  public static void main (String[] args) {

    final int start = 50;

    final long seconds = 1000;
    final long abortTime = 30000;
    final long holdEvery = 11654;
    final long holdTime = 8651;

    final One2OneChannelInt countdown = Channel.one2oneInt ();
    final One2OneChannelInt abort = Channel.one2oneInt ();
    final One2OneChannelInt hold = Channel.one2oneInt ();
    final One2OneChannelInt fire = Channel.one2oneInt ();

    new Parallel (
      new CSProcess[] {
        new LaunchControl (start, abort.in (), hold.in (), countdown.out (), fire.out ()),
        new CSProcess () {
          public void run () {                 // public countdown announcements
            int count = start;
            while (count > 0) {
              count = countdown.in ().read ();
              System.out.println ("Countdown = " + count + " seconds and counting ...");
            }
          }
        },
        new CSProcess () {
          public void run () {                 // public status announcements
            boolean counting = true;
            while (counting) {
              switch (fire.in ().read ()) {
                case LaunchControl.HOLDING:
                  System.out.println ("HOLDING ... Enterprise is on hold ...");
                  fire.in ().read ();
                  System.out.println ("ON-LINE ... Enterprise is back on-line ...");
                break;
                case LaunchControl.ABORTED:
                  System.out.println ("ABORT-ABORT-ABORT ... Enterprise is standing down ...");
                  counting = false;
                break;
                case LaunchControl.FIRED:
                  System.out.println ("WE HAVE LIFT-OFF ... Enterprise is go ...");
                  counting = false;
                break;
              }
            }
	    System.exit (0);
          }
        },
        new CSProcess () {
          public void run () {                 // abort safety officer
            CSTimer tim = new CSTimer ();
            long t = tim.read ();
            tim.after (t + abortTime);
            System.out.println ("Who pressed that big red button ... ?!! ... !!! ...");
            abort.out ().write (0);
          }
        },
        new CSProcess () {
          public void run () {                 // hold safety officer
            CSTimer tim = new CSTimer ();
            long t = tim.read () + holdEvery;
            while (true) {
              tim.after (t);
              System.out.println ("HOLD - HOLD - HOLD ...");
              hold.out ().write (0);
              t += holdTime;
              tim.after (t);
              System.out.println ("RELEASE - RELEASE - RELEASE ...");
              hold.out ().write (0);
              t += holdEvery;
            }
          }
        }
      }
    ).run ();

  }

}
