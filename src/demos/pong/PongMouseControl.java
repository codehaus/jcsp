import jcsp.lang.*;
import jcsp.awt.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class PongMouseControl implements CSProcess {

  public static final int FLASH_INTERVAL = 500;  // milli-seconds
  public static final int FLASH_OFF = -1;

  private final ChannelOutputInt toFlasher;
  private final ChannelOutput toGraphics;
  private final ChannelInput fromGraphics;
  final private ChannelInput mouseEvent;

  public PongMouseControl (final ChannelOutputInt toFlasher,
                           final ChannelOutput toGraphics,
                           final ChannelInput fromGraphics,
                           final ChannelInput mouseEvent) {
    this.toFlasher = toFlasher;
    this.toGraphics = toGraphics;
    this.fromGraphics = fromGraphics;
    this.mouseEvent = mouseEvent;
  }

  public void run() {
  
System.out.println ("PongMouseControl running ...");

    PriParallel.setPriority (Thread.MAX_PRIORITY);
    
    boolean mousePresent = false;

    while (true) {
    
      toGraphics.write (GraphicsProtocol.REQUEST_FOCUS);
      fromGraphics.read ();
    
      switch (((MouseEvent) mouseEvent.read ()).getID ()) {
      
        case MouseEvent.MOUSE_ENTERED:
System.out.println ("PongMouseControl: MouseEvent.MOUSE_ENTERED");
          toFlasher.write (FLASH_INTERVAL);
        break;
        
        case MouseEvent.MOUSE_EXITED:
System.out.println ("PongMouseControl: MouseEvent.MOUSE_EXITED");
          toFlasher.write (FLASH_OFF);
        break;
        
      }

    }

  }

}
