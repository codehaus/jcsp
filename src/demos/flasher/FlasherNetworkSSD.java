import org.jcsp.lang.*;
import org.jcsp.awt.*;

public class FlasherNetworkSSD implements CSProcess {

  final private long period;
  final private ActiveApplet activeApplet;

  public FlasherNetworkSSD (final long period,
                            final ActiveApplet activeApplet) {
    this.period = period;
    this.activeApplet = activeApplet;
  }
    
  public void run () {

    final One2OneChannel mouseEvent = Channel.one2one ();
    final One2OneChannel appletConfigure = Channel.one2one ();
    final One2OneChannelInt stopStart = Channel.one2oneInt ();
    final One2OneChannelInt destroy = Channel.one2oneInt ();
    final One2OneChannelInt destroyAck = Channel.one2oneInt ();

    activeApplet.addMouseEventChannel (mouseEvent.out());
    activeApplet.setConfigureChannel (appletConfigure.in());
    activeApplet.setStopStartChannel (stopStart.out());
    activeApplet.setDestroyChannels (destroy.out(), destroyAck.in());
    // activeApplet.setDestroyChannels (destroy, destroyAck, -1);

    new Parallel (
      new CSProcess[] {
        activeApplet,
        new FlasherControl (period, mouseEvent.in(), appletConfigure.out()),
        new CSProcess () {
          public void run () {
            while (true) {
		switch (stopStart.in().read ()) {
                case ActiveApplet.STOP:
                  System.out.println ("FlasherNetworkSSD: ActiveApplet.STOP received");
                break;
                case ActiveApplet.START:
                  System.out.println ("FlasherNetworkSSD: ActiveApplet.START received");
                break;
                default:
                  System.out.println ("FlasherNetworkSSD: ActiveApplet.<not STOP/START> received");
                break;
              }
            }
          }
        },
        new CSProcess () {
          public void run () {
            while (true) {
		switch (destroy.in().read ()) {
                case ActiveApplet.DESTROY:
                  System.out.println ("FlasherNetworkSSD: ActiveApplet.DESTROY received");
                  destroyAck.out().write (0);
                break;
                default:
                  System.out.println ("FlasherNetworkSSD: ActiveApplet.<not DESTROY> received");
                break;
              }
            }
          }
        }
      }
    ).run ();

  }

}
