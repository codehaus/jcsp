import org.jcsp.lang.*;
 
public class Regular implements CSProcess {
 
  final private ChannelOutput out;
  final private Integer N;
  final private long interval;
 
  public Regular (final ChannelOutput out, final int n, final long interval) {
    this.out = out;
    this.N = new Integer (n);
    this.interval = interval;
  }
 
  public void run () {
 
    final CSTimer tim = new CSTimer ();
    long timeout = tim.read ();       // read the (absolute) time once only
 
    while (true) {
      out.write (N);
      timeout += interval;            // set the next (absolute) timeout
      tim.after (timeout);            // wait until that (absolute) timeout
    }
  }
 
}
