import org.jcsp.lang.*;
import org.jcsp.awt.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class FlasherControl implements CSProcess {

  final private long period;
  final private AltingChannelInput mouseEvent;
  final private ChannelOutput appletConfigure;

  public FlasherControl (final long period,
                         final AltingChannelInput mouseEvent,
                         final ChannelOutput appletConfigure) {
    this.period = period;
    this.mouseEvent = mouseEvent;
    this.appletConfigure = appletConfigure;
  }

  private class AppletColour implements ActiveApplet.Configure {
    private Color colour = Color.lightGray;
    public void setColour (Color colour) {
      this.colour = colour;
    }
    public void configure (java.applet.Applet applet) {
      applet.setBackground (colour);
      applet.repaint ();
    }
  }
    
  public void run () {

    final Random random = new Random ();
    final CSTimer tim = new CSTimer ();

    final Alternative alt = new Alternative (new Guard[] {mouseEvent, tim});
    final boolean[] preCondition = {true, false};
    final int MOUSE = 0;
    final int TIMER = 1;

    final AppletColour[] appletColour = {new AppletColour (), new AppletColour ()};
    final AppletColour panelBlack = new AppletColour ();
    panelBlack.setColour (Color.black);

    appletConfigure.write (panelBlack);

    int index = 0;
    AppletColour appletCol = appletColour[index];
    appletCol.setColour (new Color (random.nextInt ()));

    long timeout = tim.read ();
    boolean mousePresent = false;
    boolean running = true;

    while (running) {

      switch (alt.priSelect (preCondition)) {

        case MOUSE:
          switch (((MouseEvent) mouseEvent.read ()).getID ()) {
            case MouseEvent.MOUSE_ENTERED:
              if (! mousePresent) {
                mousePresent = true;
                timeout = tim.read () + period;
                tim.setAlarm (timeout);
                appletConfigure.write (appletCol);
                preCondition[TIMER] = true;
              }
            break;
            case MouseEvent.MOUSE_EXITED:
              if (mousePresent) {
                mousePresent = false;
                appletConfigure.write (panelBlack);
                preCondition[TIMER] = false;
              }
            break;
          }
        break;

        case TIMER:
                  System.out.println ("FlasherControl: tick");
          timeout += period;
          tim.setAlarm (timeout);
          index = 1 - index;
          appletCol = appletColour[index];
          appletCol.setColour (new Color (random.nextInt ()));
          appletConfigure.write (appletCol);
        break;

      }

    }

  }

}
