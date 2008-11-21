import jcsp.lang.*;
import jcsp.awt.*;

public class FlasherNetworkSSD implements CSProcess {

  final private long period;
  final private ActiveApplet activeApplet;

  public FlasherNetworkSSD (final long period,
                            final ActiveApplet activeApplet) {
    this.period = period;
    this.activeApplet = activeApplet;
  }
    
  public void run () {

    final One2OneChannel mouseEvent = new One2OneChannel ();
    final One2OneChannel appletConfigure = new One2OneChannel ();
    final One2OneChannelInt stopStart = new One2OneChannelInt ();
    final One2OneChannelInt destroy = new One2OneChannelInt ();
    final One2OneChannelInt destroyAck = new One2OneChannelInt ();

    activeApplet.addMouseEventChannel (mouseEvent);
    activeApplet.setConfigureChannel (appletConfigure);
    activeApplet.setStopStartChannel (stopStart);
    activeApplet.setDestroyChannels (destroy, destroyAck);
    // activeApplet.setDestroyChannels (destroy, destroyAck, -1);

    new Parallel (
      new CSProcess[] {
        activeApplet,
        new FlasherControl (period, mouseEvent, appletConfigure),
        new CSProcess () {
          public void run () {
            while (true) {
              switch (stopStart.read ()) {
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
              switch (destroy.read ()) {
                case ActiveApplet.DESTROY:
                  System.out.println ("FlasherNetworkSSD: ActiveApplet.DESTROY received");
                  destroyAck.write (0);
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
