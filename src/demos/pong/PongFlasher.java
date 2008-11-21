import org.jcsp.lang.*;
import org.jcsp.awt.*;

import java.util.*;
import java.awt.*;

public class PongFlasher implements CSProcess {

  private final ChannelInput fromControl;
  private final AltingChannelInputInt trigger;
  private final DisplayList displayList;

  public PongFlasher (final ChannelInput fromControl,
                      final AltingChannelInputInt trigger,
                      final DisplayList displayList) {
    this.fromControl = fromControl;
    this.trigger = trigger;
    this.displayList = displayList;
  }

  private final static class Graphic implements GraphicsCommand.Graphic {
    public Color colour;
    public int x, y, width, height;
    public void doGraphic (java.awt.Graphics g, java.awt.Component c) {
      c.setBackground (colour);
      g.clearRect (x, y, width, height);
    }
  }

  public void run() {

    Graphic oldGraphic = new Graphic ();
    Graphic newGraphic = new Graphic ();

    GraphicsCommand oldCommand = new GraphicsCommand.General (oldGraphic);
    GraphicsCommand newCommand = new GraphicsCommand.General (newGraphic);
    
    final Random random = new Random ();
  
    System.out.println ("Flasher running ...");

    final Dimension graphicsDim = (Dimension) fromControl.read ();
    System.out.println ("Flasher : " + graphicsDim);

    // initialise data for background colour ...

    newGraphic.colour = Color.black;
    newGraphic.width = graphicsDim.width;
    newGraphic.height = graphicsDim.height;
    newGraphic.x = 0;
    newGraphic.y = 0;

    oldGraphic.width = newGraphic.width;
    oldGraphic.height = newGraphic.height;
    oldGraphic.x = 0;
    oldGraphic.y = 0;

    displayList.set (newCommand);

    fromControl.read ();    // let control continue

    final CSTimer tim = new CSTimer ();

    final Thread me = Thread.currentThread ();
    me.setPriority (Thread.MAX_PRIORITY);

    final long second = 1000;  // JCSP Timer units are milliseconds
    long interval = -1;        // negative ==> not flashing

    final Alternative alt = new Alternative (new Guard[] {trigger, tim});
    final boolean[] preCondition = {true, interval >= 0};
    final int TRIGGER = 0;
    final int TIMER = 1;

    long timeout = 0;
    
    boolean mousePresent = false;
    boolean running = true;

    while (running) {
    
      final Graphic tmpGraphic = oldGraphic;
      oldGraphic = newGraphic;
      newGraphic = tmpGraphic;
      
      final GraphicsCommand tmpCommand = oldCommand;
      oldCommand = newCommand;
      newCommand = tmpCommand;

      switch (alt.priSelect (preCondition)) {

        case TRIGGER:
          interval = trigger.read ();
          if (interval >= 0) {
            timeout = tim.read () + interval;
            tim.setAlarm (timeout);
            newGraphic.colour = new Color (random.nextInt ());
            preCondition[TIMER] = true;
          } else {
            newGraphic.colour = Color.black;
            preCondition[TIMER] = false;
          }
        break;

        case TIMER:
          timeout += interval;
          tim.setAlarm (timeout);
          newGraphic.colour = new Color (random.nextInt ());
        break;

      }

      displayList.change (newCommand, 0);

    }

  }

}
