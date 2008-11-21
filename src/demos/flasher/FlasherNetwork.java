import jcsp.lang.*;
import jcsp.awt.*;

public class FlasherNetwork implements CSProcess {

  final private long period;
  final private ActiveApplet activeApplet;

  public FlasherNetwork (final long period,
                         final ActiveApplet activeApplet) {
    this.period = period;
    this.activeApplet = activeApplet;
  }
    
  public void run () {

    final One2OneChannel mouseEvent = new One2OneChannel ();
    final One2OneChannel appletConfigure = new One2OneChannel ();

    activeApplet.addMouseEventChannel (mouseEvent);
    activeApplet.setConfigureChannel (appletConfigure);

    new Parallel (
      new CSProcess[] {
        activeApplet,
        new FlasherControl (period, mouseEvent, appletConfigure)
      }
    ).run ();

  }

}
