import org.jcsp.lang.*;
 
public class FairPlex implements CSProcess {

  private final AltingChannelInput[] in;
  private final ChannelOutput out;
 
  public FairPlex (final AltingChannelInput[] in, final ChannelOutput out) {
    this.in = in;
    this.out = out;
  }
 
  public void run () {
 
    final Alternative alt = new Alternative (in);
 
    while (true) {
      final int index = alt.fairSelect ();
      out.write (in[index].read ());
    }
 
  }
 
}
