
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
 * A free-standing grid of button processes in their own frame,
 * with <i>configure</i> and <i>event</i> channels.
 * <H2>Process Diagram</H2>
 * Please check out the process diagram for a framed <i>single</i> button
 * in {@link FramedButton}.
 * Imagine here a 2D grid of these, each with individual <i>configure</i> and
 * <i>event</i> channels.
 * <H2>Description</H2>
 * This process provides a free-standing grid of button processes in their
 * own frame.
 * They are just {@link org.jcsp.awt.ActiveButton}s wrapped in
 * an {@link org.jcsp.awt.ActiveClosingFrame},
 * but save us the trouble of constructing them.
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
 * This runs a framed button grid in parallel with a simple application process
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
 * public class FramedButtonGridExample {
 * 
 *   public static void main (String argv[]) {
 *   
 *     // labels for the grid of buttons
 * 
 *     final String[][] label = {
 *       new String[] {"Java", "occam-pi", "Handel-C"},
 *       new String[] {"C", "C++", "C#"},
 *       new String[] {"Haskell", "Modula", "Goodbye World"}
 *     };
 * 
 *     final int nDown = label.length;
 *     final int nAcross = label[0].length;
 * 
 *     // initial pixel sizes for the frame for the grid of buttons
 * 
 *     final int pixDown = 20 + (nDown*100);
 *     final int pixAcross = nAcross*120;
 *   
 *     // all button events are wired (for this example) to the same channel ...
 * 
 *     final Any2OneChannel allEvents =
 *       Channel.any2one (new OverWriteOldestBuffer (10));
 * 
 *     final Any2OneChannel[][] event = new Any2OneChannel[nDown][nAcross];
 *     
 *     for (int i = 0; i < nDown; i++) {
 *       for (int j = 0; j < nAcross; j++) {
 *         event[i][j] = allEvents;
 *       }
 *     }
 * 
 *     // make the grid of buttons (each one separately configured) ...
 * 
 *     final One2OneChannel[][] configure = new One2OneChannel[nDown][nAcross];
 *     
 *     for (int i = 0; i < nDown; i++) {
 *       configure[i] = Channel.one2oneArray (nAcross);
 *     }
 * 
 *     final ChannelInput[][] configureIn = new ChannelInput[nDown][nAcross];
 *     final ChannelOutput[][] eventOut = new ChannelOutput[nDown][nAcross];
 *     
 *     for (int i = 0; i < nDown; i++) {
 *       configureIn[i] = Channel.getInputArray (configure[i]);
 *       eventOut[i] = Channel.getOutputArray (event[i]);
 *     }
 * 
 *     final FramedButtonGrid grid =
 *       new FramedButtonGrid (
 *         "FramedButtonGrid Demo", nDown, nAcross,
 *         pixDown, pixAcross, configureIn, eventOut
 *       );
 * 
 *     // testrig ...
 * 
 *     new Parallel (
 *     
 *       new CSProcess[] {
 *       
 *         grid,
 *         
 *         new CSProcess () {
 *         
 *           public void run () {
 *     
 *             for (int i = 0; i < nDown; i++) {
 *               for (int j = 0; j < nAcross; j++) {
 *                 configure[i][j].out ().write (label[i][j]);
 *               }
 *             }
 *             
 *             boolean running = true;
 *             while (running) {
 *               final String s = (String) allEvents.in ().read ();
 *               System.out.println ("Button `" + s + "' pressed ...");
 *               running = (s != label[nDown - 1][nAcross - 1]);
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
 * @see org.jcsp.plugNplay.FramedButtonArray
 * @see org.jcsp.plugNplay.FramedScrollbar
 *
 * @author P.H. Welch
 *
 */
//}}}

public final class FramedButtonGrid implements CSProcess {

  /** The frame for the buttons */
  private final ActiveClosingFrame activeClosingFrame;

  /** The buttons */
  private final ActiveButton[] button;;

  /**
   * Construct a framed button grid process.
   * <p>
   *
   * @param title the title for the frame (must not be null)
   * @param nDown the number of buttons down in the grid (must be at least 1)
   * @param nAcross the number of buttons across in the grid (must be at least 1)
   * @param pixDown the pixel hieght of the frame (must be at least 100)
   * @param pixAcross the pixel width of the frame (must be at least 100)
   * @param configure the matrix of configure channels for the buttons (must not be null)
   * @param event the matrix of event channels from the buttons (must not be null)
   * 
   */
  public FramedButtonGrid (String title, int nDown, int nAcross,
                           int pixDown, int pixAcross,
                           ChannelInput[][] configure, ChannelOutput[][] event) {

    // check everything ...

    if (title == null) {
      throw new IllegalArgumentException (
        "From FramedButtonGrid (title == null)"
      );
    }

    if ((nDown < 1) || (nAcross < 1) || (pixDown < 100) || (pixAcross < 100)) {
      throw new IllegalArgumentException (
        "From FramedButtonGrid (nDown < 1) || (nAcross < 1) || (pixDown < 100) || (pixAcross < 100)"
      );
    }

    if ((configure == null) || (event == null)) {
      throw new IllegalArgumentException (
        "From FramedButtonGrid (configure == null) || (event == null)"
      );
    }

    if ((nDown != configure.length) || (configure.length != event.length)) {
      throw new IllegalArgumentException (
        "From FramedButtonGrid (nDown != configure.length) || (configure.length != event.length)"
      );
    }

    for (int i = 0; i < configure.length; i++) {
      if ((configure[i] == null) || (event[i] == null)) {
        throw new IllegalArgumentException (
	  "From FramedButtonGrid (configure[i] == null) || (event[i] == null)"
	);
      }
      if ((nAcross != configure[i].length) || (configure[i].length != event[i].length)) {
        throw new IllegalArgumentException (
	  "From FramedButtonGrid (nAcross != configure[i].length) || (configure[i].length != event[i].length)"
        );
      }
      for (int j = 0; j < nAcross; j++) {
        if ((configure[i][j] == null) || (event[i][j] == null)) {
          throw new IllegalArgumentException (
	    "From FramedButtonGrid (configure[i][j] == null) || (event[i][j] == null)"
	  );
        }
      }
    }

    // OK - now build ...

    activeClosingFrame = new ActiveClosingFrame (title);
    final ActiveFrame activeFrame = activeClosingFrame.getActiveFrame ();

    button = new ActiveButton[nDown*nAcross];
    for (int i = 0; i < nDown; i++) {
      for (int j = 0; j < nAcross; j++) {
        button[(i*nAcross) + j] = new ActiveButton (configure[i][j], event[i][j]);
      }
    }

    activeFrame.setSize (pixAcross, pixDown);
    activeFrame.setLayout (new GridLayout (nDown, nAcross));
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
