
  /*************************************************************************
  *                                                                        *
  *  JCSP ("CSP for Java") libraries                                       *
  *  Copyright (C) 1996-2001 Peter Welch and Paul Austin.                  *
  *                                                                        *
  *  This library is free software; you can redistribute it and/or         *
  *  modify it under the terms of the GNU Lesser General Public            *
  *  License as published by the Free Software Foundation; either          *
  *  version 2.1 of the License, or (at your option) any later version.    *
  *                                                                        *
  *  This library is distributed in the hope that it will be useful,       *
  *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU     *
  *  Lesser General Public License for more details.                       *
  *                                                                        *
  *  You should have received a copy of the GNU Lesser General Public      *
  *  License along with this library; if not, write to the Free Software   *
  *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307,  *
  *  USA.                                                                  *
  *                                                                        *
  *  Author contact: P.H.Welch@kent.ac.uk                                   *
  *                                                                        *
  *************************************************************************/

package org.jcsp.plugNplay;

import java.awt.Scrollbar;

import org.jcsp.lang.*;
import org.jcsp.awt.*;

//{{{  javadoc
/**
 * A free-standing scrollbar process in its own frame, with <i>configure</i> and
 * <i>event</i> channels.
 * <H2>Process Diagram</H2>
 * <PRE>
 *                               ___________________
 *                              |                   |
 *                              |                   |
 *                              |                   |
 *                              |                   |
 *                    {@link #FramedScrollbar configure} |                   |  {@link #FramedScrollbar event}
 *                   ----->-----|  FramedScrollbar  |----->-----
 *          <I>(java.lang.Integer)</I> |                   | <I>(int)</I>
 *          <I>(java.lang.Boolean)</I> |                   |
 *  <I>(</I>{@link org.jcsp.awt.ActiveScrollbar.Configure <I>ActiveScrollbar.Configure</I>}<I>)</I> |                   |
 *                              |                   |
 *                              |___________________|  
 * </PRE>
 * <H2>Description</H2>
 * This process provides a free-standing scrollbar in its own frame.  It is just
 * an {@link org.jcsp.awt.ActiveScrollbar} wrapped in an {@link org.jcsp.awt.ActiveClosingFrame},
 * but saves us the trouble of constructing it.
 * <p>
 * Wire it to application processes with a <i>configure</i> channel (for setting
 * its position, enabling/disabling and all other configuration options)
 * and an <i>event</i> channel (on which the current position is sent whenever
 * the button in the scrollbar is moved).
 * </p>
 * <p>
 * Initially, the button in the scrollbar is at its minimum position.
 * An application process may change this by sending a <tt>java.lang.Integer</tt>
 * (with value between the minimum and maximum defined for the scrollbar)
 * down the <i>configure</i> channel.
 * </p>
 * <p>
 * Initially, the button is <i>enabled</i>.
 * To <i>disable</i> the button, send <tt>java.lang.Boolean.FALSE</tt>
 * down the <i>configure</i> channel.
 * To <i>enable</i>, send <tt>java.lang.Boolean.TRUE</tt>.
 * </p>
 * <p>
 * For other configuration options, send objects implementing
 * the {@link org.jcsp.awt.ActiveScrollbar.Configure} interface.
 * </p>
 * <p>
 * <I>IMPORTANT: it is essential that event channels from this process are
 * always serviced -- otherwise the Java Event Thread will be blocked and the GUI
 * will stop responding.  A simple way to guarantee this is to use channels
 * configured with overwriting buffers.
 * For example:</I>
 * <PRE>
 *   final One2OneChannel myScrollbarEvent =
 *     Channel.one2one (new OverWriteOldestBuffer (n));
 * </PRE>
 * <I>This will ensure that the Java Event Thread will never be blocked.
 * Slow or inattentive readers may miss rapidly generated events, but 
 * the </I><TT>n</TT><I> most recent events will always be available.</I>
 * </p>
 * <H2>Example</H2>
 * This runs a framed scrollbar in parallel with two simple application processes
 * (<i>in-lined</i> in the {@link org.jcsp.lang.Parallel <tt>Parallel</tt>} below).
 * One application process reports all movements of the scrollbar.
 * The other disables the scrollbar for 5 seconds at 15 second intervals.
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.util.ints.*;
 * import org.jcsp.plugNplay.*;
 * 
 * public class FramedScrollbarExample {
 * 
 *   public static void main (String argv[]) {
 *   
 *     // initial pixel sizes for the scrollbar frame
 *     
 *     final boolean horizontal = true;
 *   
 *     final int pixDown = horizontal ? 300 : 400;
 *     final int pixAcross = horizontal ? 400 : 300;
 *   
 *     // the event channel is wired up to the scrollbar & reports all slider movements ...
 * 
 *     final One2OneChannelInt event =
 *       Channel.one2oneInt (new OverWriteOldestBufferInt (10));
 * 
 *     // the configure channel is wired up to the scrollbar  ...
 * 
 *     final One2OneChannel configure = Channel.one2one ();
 * 
 *     // make the framed scrollbar (connecting up its wires) ...
 * 
 *     final FramedScrollbar scrollbar =
 *       new FramedScrollbar (
 *         "FramedScrollbar Demo", pixDown, pixAcross,
 *         configure.in (), event.out (),
 *         horizontal, 0, 10, 0, 100
 *       );
 * 
 *     // testrig ...
 * 
 *     new Parallel (
 *     
 *       new CSProcess[] {
 *       
 *         scrollbar,
 *         
 *         new CSProcess () {        
 *           public void run () {            
 *             while (true) {
 *               final int n = event.in ().read ();
 *               System.out.println ("FramedScrollbar ==> " + n);
 *             }            
 *           }          
 *         },
 *         
 *         new CSProcess () {        
 *           public void run () {
 *             final int second = 1000;                // time is in millisecs
 *             final int enabledTime = 10*second;
 *             final int disabledCountdown = 5;
 *             final CSTimer tim = new CSTimer ();
 *             while (true) {
 *               tim.sleep (enabledTime);
 *               configure.out ().write (Boolean.FALSE);
 *               for (int i = disabledCountdown; i > 0; i--) {
 *                 System.out.println ("\t\t\t\tScrollbar disabled ... " + i);
 *                 tim.sleep (second);
 *               }
 *               configure.out ().write (Boolean.TRUE);
 *               System.out.println ("\t\t\t\tScrollbar enabled ...");
 *             }            
 *           }          
 *         }
 *         
 *       }
 *     ).run ();
 * 
 *   }
 * 
 * }
 * </PRE>
 *
 * @see org.jcsp.awt.ActiveScrollbar
 * @see org.jcsp.plugNplay.FramedButton
 * @see org.jcsp.plugNplay.FramedButtonArray
 * @see org.jcsp.plugNplay.FramedButtonGrid
 *
 * @author P.H. Welch
 *
 */
//}}}

public final class FramedScrollbar implements CSProcess {

  /** The frame for the scrollbar */
  private final ActiveClosingFrame activeClosingFrame;
  
/** The scrollbar */
  private final ActiveScrollbar scrollbar;

  /**
   * Construct a framed scrollbar process.
   * <p>
   *
   * @param title the title for the frame (must not be null)
   * @param pixDown the pixel hieght of the frame (must be at least 10 if horizontal, otherwise at least 100)
   * @param pixAcross the pixel width of the frame ((must be at least 10 if vertical, otherwise at least 100)
   * @param configure the configure channel for the scrollbar (may be null)
   * @param event the event channel from the scrollbar (must not be null)
   * @param horizontal <tt>true</tt> for a horizontal scrollbar, <tt>false</tt> for a vertical one
   * @param value the initial position of the button in the scrollbar (must be between <tt>minimum</tt> and <tt>maximum</tt> inclusive)
   * @param visible the size of the button in the scrollbar (must be at least 10)
   * @param minimum the minimum position of the button in the scrollbar
   * @param maximum the maximum position of the button in the scrollbar
   * 
   */
  public FramedScrollbar (String title, int pixDown, int pixAcross,
                          ChannelInput configure, ChannelOutputInt event,
                          boolean horizontal, int value, int visible,
                          int minimum, int maximum) {

    // check everything ...

    if (title == null) {
      throw new IllegalArgumentException (
        "From FramedScrollbar (title == null)"
      );
    }

/*
    if ((orientation != Scrollbar.HORIZONTAL) && (orientation != Scrollbar.VERTICAL)) {
      throw new IllegalArgumentException (
        "From FramedScrollbar (illegal orientation)"
      );
    }
*/

    if (horizontal) {
      if ((pixDown < 60) || (pixAcross < 100)) {
        throw new IllegalArgumentException (
          "From FramedScrollbar (pixDown < 10) || (pixAcross < 100)"
        );
      }
    } else {
      if ((pixDown < 100) || (pixAcross < 10)) {
        throw new IllegalArgumentException (
          "From FramedScrollbar (pixDown < 100) || (pixAcross < 10)"
        );
      }
    }

    int orientation = horizontal ? Scrollbar.HORIZONTAL : Scrollbar.VERTICAL;

    if (visible < 10) {
      throw new IllegalArgumentException (
        "From FramedScrollbar (visible < 10)"
      );
    }

    if (minimum >= maximum) {
      throw new IllegalArgumentException (
        "From FramedScrollbar (minimum >= maximum)"
      );
    }

    if ((value < minimum) || (maximum < value)) {
      throw new IllegalArgumentException (
        "From FramedScrollbar (value < minimum) || (maximum < value)"
      );
    }

    if (event == null) {
      throw new IllegalArgumentException (
        "From FramedScrollbar (event == null)"
      );
    }

    // OK - now build ...

    activeClosingFrame = new ActiveClosingFrame (title);
    final ActiveFrame activeFrame = activeClosingFrame.getActiveFrame ();

    scrollbar =
       new ActiveScrollbar (
         configure, event, orientation, value, visible, minimum, visible + maximum
       );

    activeFrame.setSize (pixAcross, pixDown);
    activeFrame.add (scrollbar);
    activeFrame.setVisible (true);

  }

  /**
   * The main body of this process.
   */
  public void run () {
    new Parallel (
      new CSProcess[] {
        activeClosingFrame,
        scrollbar
      }
    ).run ();
  }

}
