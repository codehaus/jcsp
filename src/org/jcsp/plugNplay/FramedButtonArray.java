
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

import java.awt.GridLayout;

import org.jcsp.lang.*;
import org.jcsp.awt.*;

//{{{  javadoc
/**
 * A free-standing array of button processes in their own frame,
 * with <i>configure</i> and <i>event</i> channels.
 * <H2>Process Diagram</H2>
 * Please check out the process diagram for a framed <i>single</i> button
 * in {@link FramedButton}.
 * Imagine here an array of these, each with individual <i>configure</i> and
 * <i>event</i> channels.
 * <H2>Description</H2>
 * This process provides a free-standing array of button processes in their
 * own frame.
 * They are just {@link org.jcsp.awt.ActiveButton}s wrapped in
 * an {@link org.jcsp.awt.ActiveClosingFrame},
 * but save us the trouble of constructing them.
 * They may be displayed in a row or column.
 * <p>
 * Wire them to application processes with <i>configure</i>
 * channels (for setting labels, enabling/disabling and all other
 * configuration options) and <i>event</i> channels (on which the
 * current label on any button is sent when that button is clicked).
 * Note that all the <i>events</i> may be streamed to the <i>same</i>
 * channel, provided an <tt>Any2*Channel</tt> is used (as in the example
 * below).
 * </p>
 * <p>
 * Initially, all button labels are <i>empty</i> <tt>java.lang.String</tt>s.
 * To set a button label, send a <tt>java.lang.String</tt> down the appropriate
 * <i>configure</i> channel.
 * </p>
 * <p>
 * Initially, all buttons are <i>enabled</i>.
 * To <i>disable</i> a button, send <tt>java.lang.Boolean.FALSE</tt>
 * down the appropriate <i>configure</i> channel.
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
 * This runs a framed button array in parallel with a simple application process
 * (<i>in-lined</i> in the {@link org.jcsp.lang.Parallel <tt>Parallel</tt>} below).
 * All <i>event</i> channels from the buttons are mulitplexed through
 * an {@link Any2OneChannel} to the application process.
 * The application configures the buttons with their labels, then reports
 * each time any of them is pressed.
 * The application ends when the button labelled <i>`Goodbye World'</i> is pressed.
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.util.*;
 * import org.jcsp.plugNplay.*;
 * 
 * public class FramedButtonArrayExample {
 * 
 *   public static void main (String argv[]) {
 * 
 *     // labels for the array of buttons
 * 
 *     final String[] label = {"JCSP", "Rocket Science", "occam-pi", "Goodbye World"};
 * 
 *     final int nButtons = label.length;
 * 
 *     // row or column?
 * 
 *     final boolean horizontal = true;
 *   
 *     // initial pixel sizes for the frame for the button array
 *     
 *     final int pixDown = 20 + (horizontal ? 120 : nButtons*120);
 *     final int pixAcross = horizontal ? nButtons*120 : 120;
 *   
 *     // all button events are wired (for this example) to the same channel ...
 * 
 *     final Any2OneChannel allEvents =
 *       Channel.any2one (new OverWriteOldestBuffer (10));
 * 
 *     final Any2OneChannel[] event = new Any2OneChannel[nButtons];
 *     
 *     for (int i = 0; i < nButtons; i++) {
 *       event[i] = allEvents;
 *     }
 * 
 *     // each button is given its own configuration channel ...
 * 
 *     final One2OneChannel[] configure = Channel.one2oneArray (nButtons);
 * 
 *     // make the array of buttons ...
 * 
 *     final FramedButtonArray buttons =
 *       new FramedButtonArray (
 *         "FramedButtonArray Demo", nButtons,
 *         pixDown, pixAcross, horizontal,
 *         Channel.getInputArray (configure), Channel.getOutputArray (event)
 *       );
 * 
 *     // testrig ...
 * 
 *     new Parallel (
 *     
 *       new CSProcess[] {
 *       
 *         buttons,
 *         
 *         new CSProcess () {
 *         
 *           public void run () {
 *     
 *             for (int i = 0; i < nButtons; i++) {
 *               configure[i].out ().write (label[i]);
 *             }
 *             
 *             boolean running = true;
 *             while (running) {
 *               final String s = (String) allEvents.in ().read ();
 *               System.out.println ("Button `" + s + "' pressed ...");
 *               running = (s != label[nButtons - 1]);
 *             }
 *             
 *             System.exit (0);
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
 * @see org.jcsp.plugNplay.FramedButton
 * @see org.jcsp.plugNplay.FramedButtonGrid
 * @see org.jcsp.plugNplay.FramedScrollbar
 *
 * @author P.H. Welch
 *
 */
//}}}
 
public final class FramedButtonArray implements CSProcess {

  /** The frame for the buttons */
  private final ActiveClosingFrame activeClosingFrame;
  
  /** The buttons */
  private final ActiveButton[] button;

  /**
   * Construct a framed button array process.
   * <p>
   *
   * @param title the title for the frame (must not be null)
   * @param nButtons the number of buttons (must be at least 1)
   * @param pixDown the pixel hieght of the frame (must be at least 100)
   * @param pixAcross the pixel width of the frame (must be at least 100)
   * @param horizontal <tt>true</tt> for a horizontal array of buttons, <tt>false</tt> for a vertical one
   * @param configure the configure channels for the buttons (must not be null)
   * @param event the event channels from the buttons (must not be null)
   * 
   */
  public FramedButtonArray (String title, int nButtons,
                            int pixDown, int pixAcross, boolean horizontal,
                            ChannelInput[] configure, ChannelOutput[] event) {

    // check everything ...

    if (title == null) {
      throw new IllegalArgumentException (
        "From FramedButtonArray (title == null)"
      );
    }

    if ((nButtons < 1) || (pixDown < 100) || (pixAcross < 100)) {
      throw new IllegalArgumentException (
        "From FramedButtonArray (nButtons < 1) || (pixDown < 100) || (pixAcross < 100)"
      );
    }

    if ((configure == null) || (event == null)) {
      throw new IllegalArgumentException (
        "From FramedButtonArray (configure == null)"
      );
    }

    if ((nButtons != configure.length) || (configure.length != event.length)) {
      throw new IllegalArgumentException (
        "From FramedButtonArray (nButtons != configure.length) || (configure.length != event.length)"
      );
    }

    for (int i = 0; i < configure.length; i++) {
      if ((configure[i] == null) || (event[i] == null)) {
        throw new IllegalArgumentException (
          "From FramedButtonArray (configure[i] == null) || (event[i] == null)"
        );
      }
    }

    // OK - now build ...

    activeClosingFrame = new ActiveClosingFrame (title);
    final ActiveFrame activeFrame = activeClosingFrame.getActiveFrame ();

    button = new ActiveButton[nButtons];
    for (int i = 0; i < nButtons; i++) {
        button[i] = new ActiveButton (configure[i], event[i]);
    }

    activeFrame.setSize (pixAcross, pixDown);
    if (horizontal) {
      activeFrame.setLayout (new GridLayout (1, nButtons));
    } else {
      activeFrame.setLayout (new GridLayout (nButtons, 1));
    }
    for (int i = 0; i < button.length; i++) {
      activeFrame.add (button[i]);
    }
    activeFrame.setVisible (true);

  }

  public void run () {
    new Parallel (
      new CSProcess[] {
        activeClosingFrame,
        new Parallel (button)
      }
    ).run ();
  }

}
