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
    //                                                                  //
    //////////////////////////////////////////////////////////////////////


import org.jcsp.lang.*;
import java.awt.event.*;

/**
 * @author P.H. Welch
 */
public class BounceController implements CSProcess {

  private final AltingChannelInput direction;
  private final AltingChannelInputInt speed;
  private final ChannelOutputInt control;
  private final int MAX_SCALE;

  public BounceController (final AltingChannelInput direction,
                           final AltingChannelInputInt speed,
                           final ChannelOutputInt control,
                           final int MAX_SCALE) {
    this.direction = direction;
    this.speed = speed;
    this.control = control;
    this.MAX_SCALE = MAX_SCALE;
  }

  public void run() {

    final Thread me = Thread.currentThread ();
    System.out.println ("BounceController " + " priority = " + me.getPriority ());
    me.setPriority (Thread.MIN_PRIORITY);
    System.out.println ("BounceController " + " priority = " + me.getPriority ());

    final int TICKS_PER_SECOND = 1000;
    final int MAX_FPS = 100;
    final int MIN_FPS = 1;
    final int SPAN_FPS = (MAX_FPS - MIN_FPS) + 1;
    final int MINFPS_MAXSCALE = MIN_FPS * MAX_SCALE;

    boolean forwards = true;
    int speedValue = 0;
    int interval = 0;
    long timeout = 0;

    final CSTimer tim = new CSTimer ();
    final Guard[] guard = {tim, speed, direction};
    final boolean[] preCondition = {false, true, true};
    final Alternative alt = new Alternative (guard);
    
    while (true) {
      switch (alt.fairSelect (preCondition)) {
        case 0:  // time-out
          // timeout += interval;
          timeout = tim.read () + interval;
          tim.setAlarm (timeout);
          if (forwards) {
            control.write (+1);
          } else {
            control.write (-1);
          }
        break;
        case 1:
          int value = MAX_SCALE - speed.read ();
          if (value > 0) {
            int fps = ((value - 1 ) * SPAN_FPS + MINFPS_MAXSCALE) / MAX_SCALE;
            interval = TICKS_PER_SECOND / fps;
            if (speedValue <= 0) {                 //  "<=" is work-around for slider bug in IE JVM
              timeout = tim.read () + interval;
              tim.setAlarm (timeout);
              preCondition[0] = true;
            }
          } else {
            preCondition[0] = false;
          }
          speedValue = value;
        break;  
        case 2:
          MouseEvent event = (MouseEvent) direction.read();
          if (event.getID () == MouseEvent.MOUSE_PRESSED) { 
            forwards = ! forwards;
          }
        break;
      }
    }

  }

}
