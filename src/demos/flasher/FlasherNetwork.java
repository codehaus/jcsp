import org.jcsp.lang.*;
import org.jcsp.awt.*;

public class FlasherNetwork implements CSProcess {

  final private long period;
  final private ActiveApplet activeApplet;

  public FlasherNetwork (final long period,
                         final ActiveApplet activeApplet) {
    this.period = period;
    this.activeApplet = activeApplet;
  }
    
  public void run () {

    final One2OneChannel mouseEvent = Channel.one2one ();
    final One2OneChannel appletConfigure = Channel.one2one ();

    activeApplet.addMouseEventChannel (mouseEvent.out());
    activeApplet.setConfigureChannel (appletConfigure.in());

    new Parallel (
      new CSProcess[] {
        activeApplet,
        new FlasherControl (period, mouseEvent.in(), appletConfigure.out())
      }
    ).run ();

  }

}
