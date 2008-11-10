
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

import org.jcsp.lang.*;
import org.jcsp.awt.*;

//{{{  javadoc
/**
 * A free-standing button process in its own frame, with <i>configure</i> and
 * <i>event</i> channels.
 * <H2>Process Diagram</H2>
 * <PRE>
 *                            __________________
 *                           |                  |
 *                           |                  |
 *                           |                  |
 *                           |                  |
 *                 {@link #FramedButton configure} |                  |  {@link #FramedButton event}
 *                ----->-----|   FramedButton   |----->-----
 *        <I>(java.lang.String)</I> |                  | <I>(java.lang.String)</I>
 *       <I>(java.lang.Boolean)</I> |                  |
 *  <I>(</I>{@link org.jcsp.awt.ActiveButton.Configure <I>ActiveButton.Configure</I>}<I>)</I> |                  |
 *                           |                  |
 *                           |__________________|  
 * </PRE>
 * <H2>Description</H2>
 * This process provides a free-standing button in its own frame.  It is just
 * an {@link org.jcsp.awt.ActiveButton} wrapped in an {@link org.jcsp.awt.ActiveClosingFrame},
 * but saves us the trouble of constructing it.
 * <p>
 * Wire it to application processes with a <i>configure</i>
 * channel (for setting its label, enabling/disabling and all other
 * configuration options) and an <i>event</i> channel (on which the
 * current label is sent when the button is clicked).
 * </p>
 * <p>
 * Initially, the button label is an <i>empty</i> <tt>java.lang.String</tt>.
 * To set the button label, send a <tt>java.lang.String</tt> down
 * the <i>configure</i> channel.
 * </p>
 * <p>
 * Initially, the button is <i>enabled</i>.
 * To <i>disable</i> the button, send <tt>java.lang.Boolean.FALSE</tt>
 * down the <i>configure</i> channel.
 * To <i>enable</i>, send <tt>java.lang.Boolean.TRUE</tt>.
 * </p>
 * <p>
 * For other configuration options, send objects implementing
 * the {@link org.jcsp.awt.ActiveButton.Configure} interface.
 * </p>
 * <p>
 * <I>IMPORTANT: it is essential that event channels from this process are
 * always serviced -- otherwise the Java Event Thread will be blocked and the GUI
 * will stop responding.  A simple way to guarantee this is to use channels
 * configured with overwriting buffers.
 * For example:</I>
 * <PRE>
 *   final One2OneChannel myButtonEvent =
 *     Channel.one2one (new OverWriteOldestBuffer (n));
 * </PRE>
 * <I>This will ensure that the Java Event Thread will never be blocked.
 * Slow or inattentive readers may miss rapidly generated events, but 
 * the </I><TT>n</TT><I> most recent events will always be available.</I>
 * </p>
 * <H2>Example</H2>
 * This runs a framed button in parallel with a simple application process
 * (<i>in-lined</i> in the {@link org.jcsp.lang.Parallel <tt>Parallel</tt>} below).
 * The application process configures the button with the first of an array
 * of <tt>String</tt> labels, reporting and changing it each time the button
 * is pressed.
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.util.*;
 * import org.jcsp.plugNplay.*;
 * 
 * public class FramedButtonExample {
 * 
 *   public static void main (String argv[]) {
 *   
 *     // initial pixel sizes for the button frame
 *     
 *     final int pixDown = 100;
 *     final int pixAcross = 250;
 *   
 *     // labels for the button
 * 
 *     final String[] label = {"JCSP", "Rocket Science", "occam-pi", "Goodbye World"};
 * 
 *     // the event channel is wired up to the button & reports all button presses ...
 * 
 *     final One2OneChannel event = Channel.one2one (new OverWriteOldestBuffer (10));
 * 
 *     // the configure channel is wired up to the button  ...
 * 
 *     final One2OneChannel configure = Channel.one2one ();
 * 
 *     // make the framed button (connecting up its wires) ...
 * 
 *     final FramedButton button =
 *       new FramedButton (
 *         "FramedButton Demo", pixDown, pixAcross, configure.in (), event.out ()
 *       );
 * 
 *     // testrig ...
 * 
 *     new Parallel (
 *     
 *       new CSProcess[] {
 *       
 *         button,
 *         
 *         new CSProcess () {
 *         
 *           public void run () {
 *     
 *             int i = 0;
 *             
 *             while (true) {
 *               configure.out ().write (label[i]);
 *               i = (i + 1) % label.length;
 *               final String s = (String) event.in ().read ();
 *               System.out.println ("Button `" + s + "' pressed ...");
 *             }
 *             
 *           }
 *           
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
 * @see org.jcsp.awt.ActiveButton
 * @see org.jcsp.plugNplay.FramedButtonArray
 * @see org.jcsp.plugNplay.FramedButtonGrid
 * @see org.jcsp.plugNplay.FramedScrollbar
 * 
 * @author P.H. Welch
 *
 */
//}}}
 
public final class FramedButton implements CSProcess {

  /** The frame for the button */
  private final ActiveClosingFrame activeClosingFrame;

  /** The button */
  private final ActiveButton button;

  /**
   * Construct a framed button process.
   * <p>
   *
   * @param title the title for the frame (must not be null)
   * @param pixDown the pixel hieght of the frame (must be at least 100)
   * @param pixAcross the pixel width of the frame (must be at least 100)
   * @param configure the configure channel for the button (must not be null)
   * @param event the event channel from the button (must not be null)
   * 
   */
  public FramedButton (String title, int pixDown, int pixAcross,
                       ChannelInput configure, ChannelOutput event) {

    // check everything ...

    if (title == null) {
      throw new IllegalArgumentException (
        "From FramedButton (title == null)"
      );
    }

    if ((pixDown < 100) || (pixAcross < 100)) {
      throw new IllegalArgumentException (
        "From FramedButton (pixDown < 100) || (pixAcross < 100)"
      );
    }

    if ((configure == null) || (event == null)) {
      throw new IllegalArgumentException (
        "From FramedButtonGrid (configure == null)  || (event == null)"
      );
    }

    // OK - now build ...

    activeClosingFrame = new ActiveClosingFrame (title);
    final ActiveFrame activeFrame = activeClosingFrame.getActiveFrame ();

    button = new ActiveButton (configure, event);

    activeFrame.setSize (pixAcross, pixDown);
    activeFrame.add (button);
    activeFrame.setVisible (true);

  }

  /**
   * The main body of this process.
   */
  public void run () {
    new Parallel (
      new CSProcess[] {
        activeClosingFrame,
        button
      }
    ).run ();
  }

}
