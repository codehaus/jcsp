
    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2008 Peter Welch and Paul Austin.            //
    //                2001-2004 Quickstone Technologies Limited.        //
    //                                                                  //
    //  This library is free software; you can redistribute it and/or   //
    //  modify it under the terms of the GNU Lesser General Public      //
    //  License as published by the Free Software Foundation; either    //
    //  version 2.1 of the License, or (at your option) any later       //
    //  version.                                                        //
    //                                                                  //
    //  This library is distributed in the hope that it will be         //
    //  useful, but WITHOUT ANY WARRANTY; without even the implied      //
    //  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR         //
    //  PURPOSE. See the GNU Lesser General Public License for more     //
    //  details.                                                        //
    //                                                                  //
    //  You should have received a copy of the GNU Lesser General       //
    //  Public License along with this library; if not, write to the    //
    //  Free Software Foundation, Inc., 59 Temple Place, Suite 330,     //
    //  Boston, MA 02111-1307, USA.                                     //
    //                                                                  //
    //  Author contact: P.H.Welch@kent.ac.uk                             //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////


import org.jcsp.lang.*;
import org.jcsp.awt.*;

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
  
    // System.out.println ("PongMouseControl running ...");

    PriParallel.setPriority (Thread.MAX_PRIORITY);
    
    boolean mousePresent = false;

    while (true) {
    
      switch (((MouseEvent) mouseEvent.read ()).getID ()) {
      
        case MouseEvent.MOUSE_ENTERED:
          // System.out.println ("PongMouseControl: MouseEvent.MOUSE_ENTERED");
          toFlasher.write (FLASH_INTERVAL);
        break;
        
        case MouseEvent.MOUSE_EXITED:
          // System.out.println ("PongMouseControl: MouseEvent.MOUSE_EXITED");
          toFlasher.write (FLASH_OFF);
        break;
        
      }

      toGraphics.write (GraphicsProtocol.REQUEST_FOCUS);
      fromGraphics.read ();
    
    }

  }

}
