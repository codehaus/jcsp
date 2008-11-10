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

package org.jcsp.awt;

import java.awt.*;
import java.util.Vector;
import org.jcsp.lang.*;

/**
 * {@link java.awt.Canvas <TT>java.awt.Canvas</TT>}
 * with a channel interface.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/ActiveCanvas1.gif"></p>
 * <H2>Description</H2>
 * <TT>ActiveCanvas</TT> is a process extension of <TT>java.awt.Canvas</TT>
 * with channels for run-time configuration/interrogation and event notification.
 * The event channels should be connected to one or more application-specific server.
 * processes (instead of registering a passive object as a <I>Listener</I>
 * to this component).
 * <P>
 * All channels are optional.  The <TT>toGraphics</TT>/<TT>fromGraphics</TT> channels are
 * set by calling the {@link ActiveCanvas#setGraphicsChannels <TT>setGraphicsChannels</TT>} method.
 * Event channels can be added to notify the occurrence of any type of <TT>Event</TT>
 * the component generates by calling the appropriate
 * <TT>add</TT><I>XXX</I><TT>EventChannel</TT> method.
 * All channel connections must be made <I>before</I> the process is run.
 * <P>
 * Messages can be sent down the <TT>toGraphics</TT> channel at any time to configure
 * or interrogate the component.  A reply or acknowledgment is returned down
 * the <TT>fromGraphics</TT> channel <I>and must be read</I>.
 * See the <A HREF="#Protocols">table below</A> and {@link GraphicsProtocol} for details.
 * <P>
 * Graphics operations on an <TT>ActiveCanvas</TT> are most conveniently managed
 * by attaching, and then setting {@link GraphicsCommand}s on, a {@link DisplayList}
 * (or any user-defined object implementing the {@link Paintable} interface).
 * This can be done either <I>statically</I> via the {@link #setPaintable setPaintable}
 * method, or <I>dynamically</I>
 * via the {@link #setGraphicsChannels <TT>toGraphics</TT>/<TT>fromGraphics</TT>} channels
 * (see {@link GraphicsProtocol.SetPaintable}).
 * <P>
 * All channels are managed by independent internal handler processes.  It is, therefore,
 * safe for a serial application process both to service an event channel and configure
 * the component -- no deadlock can occur.
 * <P>
 * <I>IMPORTANT: it is essential that event channels from this process are
 * always serviced -- otherwise the Java Event Thread will be blocked and the GUI
 * will stop responding.  A simple way to guarantee this is to use channels
 * configured with overwriting buffers.
 * For example:</I>
 * <PRE>
 *   final One2OneChannel myMouseEvent = Channel.one2one (new OverWriteOldestBuffer (n));
 * 
 *   final ActiveCanvas myCanvas = new ActiveCanvas ();
 *   myCanvas.addMouseEventChannel (myMouseEvent.out ());
 * </PRE>
 * <I>This will ensure that the Java Event Thread will never be blocked.
 * Slow or inattentive readers may miss rapidly generated events, but
 * the </I><TT>n</TT><I> most recent events will always be available.</I>
 * </P>
 * <H2><A NAME="Protocols">Channel Protocols</A></H2>
 * <CENTER>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH ROWSPAN="1">displayList</TH>
 *     <TD>GraphicsCommand[]</TD>
 *     <TD>See {@link org.jcsp.awt.DisplayList} and {@link org.jcsp.awt.GraphicsCommand}.</TD>
 *   </TR>
 *   <TR>
 *     <TH>toGraphics</TH>
 *     <TD>GraphicsProtocol</TD>
 *     <TD>See {@link org.jcsp.awt.GraphicsProtocol}.</TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>fromGraphics</TH>
 *     <TD>Object</TD>
 *     <TD>Response to the <TT>fromGraphics</TT> message.  See the documentation on
 *         {@link org.jcsp.awt.GraphicsProtocol}.
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TH>componentEvent</TH>
 *     <TD>ComponentEvent</TD>
 *     <TD>See the {@link #addComponentEventChannel
 *         <TT>addComponentEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>focusEvent</TH>
 *     <TD>FocusEvent</TD>
 *     <TD>See the {@link #addFocusEventChannel
 *         <TT>addFocusEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>keyEvent</TH>
 *     <TD>KeyEvent</TD>
 *     <TD>See the {@link #addKeyEventChannel
 *         <TT>addKeyEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>mouseEvent</TH>
 *     <TD>MouseEvent</TD>
 *     <TD>See the {@link #addMouseEventChannel
 *         <TT>addMouseEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>mouseMotionEvent</TH>
 *     <TD>MouseEvent</TD>
 *     <TD>See the {@link #addMouseMotionEventChannel
 *         <TT>addMouseMotionEventChannel</TT>} method.</TD>
 *   </TR>
 * </TABLE>
 * </CENTER>
 * <H2>Example</H2>
 * <PRE>
 * import java.awt.*;
 * import java.awt.event.*;
 * import org.jcsp.lang.*;
 * import org.jcsp.util.*;
 * import org.jcsp.awt.*;
 * 
 * public class ActiveCanvasExample {
 * 
 *   public static void main (String argv[]) {
 * 
 *     final ActiveClosingFrame activeClosingFrame =
 *       new ActiveClosingFrame ("ActiveCanvas Example");
 *     final Frame frame = activeClosingFrame.getActiveFrame ();
 * 
 *     final One2OneChannel mouseEvent = Channel.one2one (new OverWriteOldestBuffer (10));
 * 
 *     final DisplayList displayList = new DisplayList ();
 * 
 *     final ActiveCanvas canvas = new ActiveCanvas ();
 *     canvas.addMouseEventChannel (mouseEvent.out ());
 *     canvas.setPaintable (displayList);
 *     canvas.setSize (600, 400);
 * 
 *     frame.add (canvas);
 *     frame.pack ();
 *     frame.setVisible (true);
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         activeClosingFrame,
 *         canvas,
 *         new CSProcess () {
 *           public void run () {
 *             final String clickMessage = "D O U B L E - C L I C K   T H E   M O U S E   T O   E X I T";
 *             final String clickPlea = "       P L E A S E   M O V E   T H E   M O U S E   B A C K    ";
 *             final GraphicsCommand[] mouseEntered =
 *               {new GraphicsCommand.SetColor (Color.cyan),
 *                new GraphicsCommand.FillRect (0, 0, 600, 400),
 *                new GraphicsCommand.SetColor (Color.black),
 *                new GraphicsCommand.DrawString (clickMessage, 140, 200)};
 *             final GraphicsCommand[] mouseExited =
 *               {new GraphicsCommand.SetColor (Color.pink),
 *                new GraphicsCommand.FillRect (0, 0, 600, 400),
 *                new GraphicsCommand.SetColor (Color.black),
 *                new GraphicsCommand.DrawString (clickPlea, 140, 200)};
 *             final GraphicsCommand mousePressed =
 *               new GraphicsCommand.DrawString (clickMessage, 160, 220);
 *             final GraphicsCommand mouseReleased =
 *               new GraphicsCommand.DrawString (clickMessage, 140, 200);
 *             displayList.set (mouseExited);
 *             boolean running = true;
 *             while (running) {
 *               final MouseEvent event = (MouseEvent) mouseEvent.in ().read ();
 *               switch (event.getID ()) {
 *                 case MouseEvent.MOUSE_ENTERED:
 *                   System.out.println ("MOUSE_ENTERED");
 *                   displayList.set (mouseEntered);
 *                 break;
 *                 case MouseEvent.MOUSE_EXITED:
 *                   System.out.println ("MOUSE_EXITED");
 *                   displayList.set (mouseExited);
 *                 break;
 *                 case MouseEvent.MOUSE_PRESSED:
 *                   System.out.println ("MOUSE_PRESSED");
 *                   displayList.change (mousePressed, 3);
 *                 break;
 *                 case MouseEvent.MOUSE_RELEASED:
 *                   System.out.println ("MOUSE_RELEASED");
 *                   displayList.change (mouseReleased, 3);
 *                 break;
 *                 case MouseEvent.MOUSE_CLICKED:
 *                   if (event.getClickCount() > 1) {
 *                     System.out.println ("MOUSE_DOUBLE_CLICKED ... goodbye!");
 *                     running = false;
 *                   }
 *                 break;
 *               }
 *             }
 *             frame.setVisible (false);
 *             System.exit (0);
 *           }
 *         }
 *       }
 *     ).run ();
 *   }
 * 
 * }
 * </PRE>
 *
 * @see org.jcsp.awt.DisplayList
 * @see org.jcsp.awt.Display
 * @see org.jcsp.awt.GraphicsCommand
 * @see org.jcsp.awt.Paintable
 * @see org.jcsp.awt.GraphicsProtocol
 * @see org.jcsp.util.OverWriteOldestBuffer
 * @see java.awt.Canvas
 * @see java.awt.event.ComponentEvent
 * @see java.awt.event.FocusEvent
 * @see java.awt.event.KeyEvent
 * @see java.awt.event.MouseEvent
 *
 * @author P.D. Austin and P.H. Welch
 */

public class ActiveCanvas extends Canvas implements CSProcess
{
   private ChannelInput toGraphics;
   private ChannelOutput fromGraphics;
   
   /**
    * Set the <TT>toGraphics</TT>/<TT>fromGraphics</TT> channels for configuring
    * and/or examining this component.
    * A {@link GraphicsProtocol <TT>org.jcsp.awt.GraphicsProtocol</TT>} object sent down
    * the <TT>toGraphics</TT> channel generates the appropriate configuration or enquiry
    * action.  A reply object is always returned down the <TT>fromGraphics</TT> channel.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
    * <P>
    *
    * @param toGraphics the channel down which GraphicsProtocol objects are sent.
    * @param fromGraphics the reply/acknowledgement channel responding to the GraphicsProtocol object.
    */
   public void setGraphicsChannels(ChannelInput toGraphics, ChannelOutput fromGraphics)
   {
      this.toGraphics = toGraphics;
      this.fromGraphics = fromGraphics;
   }
   
   private Paintable paintable;
   
   /**
    * Set the {@link Paintable <TT>Paintable</TT>} object that will be used by the {@link ActiveCanvas#paint paint}
    * and {@link ActiveCanvas#update update} methods of this canvas.  If <TT>paintable</TT> is
    * null, the <TT>paint</TT>/<TT>update</TT> methods will not be overriden.
    * <P>
    * JCSP provides a {@link DisplayList} as an example <TT>Paintable</TT> class.
    * This can be thought of as a special form of channel.  A user process at the other
    * end <I>writes</I> to it by setting up and/or editing an ordered list of
    * {@link GraphicsCommand}s.  This method may be used to connect the <TT>DisplayList</TT>
    * to this <TT>ActiveCanvas</TT>.  For example:
    * <PRE>
    *   final ActiveCanvas myActiveCanvas = new ActiveCanvas ();
    *   
    *   final DisplayList draw = new DisplayList ();
    *   myActiveCanvas.setPaintable (draw);
    *   
    *   ...  connect other channels (toGraphics, fromGraphics, mouseEvent, etc.)
    *   
    *   // myActiveCanvas is now ready to run
    * </PRE>
    * <P>
    * <I>NOTE: If <TT>setPaintable</TT> is going to be invoked, this must happen
    * <I>before</I> this process is run.</I>
    * <P>
    * Alternatively, the {@link Paintable <TT>Paintable</TT>} object may be set dynamically
    * by sending an appropriate {@link GraphicsProtocol.SetPaintable} object down the
    * {@link ActiveCanvas#setGraphicsChannels(org.jcsp.lang.ChannelInput,org.jcsp.lang.ChannelOutput) toGraphics}
    * channel.
    * <P>
    *
    * @param paintable the object to be used for painting/updating the canvas.
    */
   public void setPaintable(Paintable paintable)
   {
      this.paintable = paintable;
      paintable.register(this);
   }
   
   /**
    * This method is used by the JVM <I>event thread</I> -- it is not really for public
    * consumption.  If {@link ActiveCanvas#setPaintable setPaintable} has been invoked
    * on this canvas or a {@link GraphicsProtocol.SetPaintable} object has been sent down
    * the {@link ActiveCanvas#setGraphicsChannels(org.jcsp.lang.ChannelInput,org.jcsp.lang.ChannelOutput) toGraphics}
    * channel, this method uses the supplied {@link Paintable <TT>Paintable</TT>} object
    * to do the painting.
    * <P>
    */
   public void paint(Graphics g)
   {
      if (paintable == null)
         super.paint(g);
      else
         paintable.paint(g);
   }
   
   /**
    * This method is used by the JVM <I>event thread</I> -- it is not really for public
    * consumption.  If {@link ActiveCanvas#setPaintable setPaintable} has been invoked
    * on this canvas or a {@link GraphicsProtocol.SetPaintable} object has been sent down
    * the {@link ActiveCanvas#setGraphicsChannels(org.jcsp.lang.ChannelInput,org.jcsp.lang.ChannelOutput) toGraphics}
    * channel, this method uses the supplied {@link Paintable <TT>Paintable</TT>} object
    * to do the updating.
    * <P>
    */
   public void update(Graphics g)
   {
      if (paintable == null)
         super.update(g);
      else
         paintable.update(g);
   }
   
   /**
    * The Vector construct containing the handlers.
    */
   private Vector vec = new Vector();
   
   /**
    * Add a new channel to this component that will be used to notify that
    * a <TT>ComponentEvent</TT> has occurred. <I>This should be used
    * instead of registering a ComponentListener with the component.</I> It is
    * possible to add more than one Channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
    * <P>
    *
    * @param componentEvent the channel down which to send ComponentEvents.
    */
   public void addComponentEventChannel(ChannelOutput componentEvent)
   {
      if (componentEvent != null)
      {
         ComponentEventHandler handler = new ComponentEventHandler(componentEvent);
         addComponentListener(handler);
         vec.addElement(handler);
      }
   }
   
   /**
    * Add a new channel to this component that will be used to notify that
    * a <TT>FocusEvent</TT> has occurred. <I>This should be used
    * instead of registering a FocusListener with the component.</I> It is
    * possible to add more than one Channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
    * <P>
    *
    * @param focusEvent the channel down which to send FocusEvents.
    */
   public void addFocusEventChannel(ChannelOutput focusEvent)
   {
      if (focusEvent != null)
      {
         FocusEventHandler handler = new FocusEventHandler(focusEvent);
         addFocusListener(handler);
         vec.addElement(handler);
      }
   }
   
   /**
    * Add a new channel to this component that will be used to notify that
    * a <TT>KeyEvent</TT> has occurred. <I>This should be used
    * instead of registering a KeyListener with the component.</I> It is
    * possible to add more than one Channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
    * <P>
    *
    * @param keyEvent the channel down which to send KeyEvents.
    */
   public void addKeyEventChannel(ChannelOutput keyEvent)
   {
      if (keyEvent != null)
      {
         KeyEventHandler handler = new KeyEventHandler(keyEvent);
         addKeyListener(handler);
         vec.addElement(handler);
      }
   }
   
   /**
    * Add a new channel to this component that will be used to notify that
    * a <TT>MouseEvent</TT> has occurred. <I>This should be used
    * instead of registering a MouseListener with the component.</I> It is
    * possible to add more than one Channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
    * <P>
    *
    * @param mouseEvent the channel down which to send MouseEvents.
    */
   public void addMouseEventChannel(ChannelOutput mouseEvent)
   {
      if (mouseEvent != null)
      {
         MouseEventHandler handler = new MouseEventHandler(mouseEvent);
         addMouseListener(handler);
         vec.addElement(handler);
      }
   }
   
   /**
    * Add a new channel to this component that will be used to notify that
    * a <TT>MouseMotionEvent</TT> has occurred. <I>This should be used
    * instead of registering a MouseMotionListener with the component.</I> It is
    * possible to add more than one Channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
    * <P>
    *
    * @param mouseMotionEvent the channel down which to send MouseMotionEvents.
    */
   public void addMouseMotionEventChannel(ChannelOutput mouseMotionEvent)
   {
      if (mouseMotionEvent != null)
      {
         MouseMotionEventHandler handler = new MouseMotionEventHandler(mouseMotionEvent);
         addMouseMotionListener(handler);
         vec.addElement(handler);
      }
   }
   
   private int requestedWidth, requestedHeight;
   
   /**
    * Request that the canvas takes the size given by the parameters.
    * The methods {@link #getPreferredSize <TT>getPreferredSize</TT>}
    * and {@link #getMinimumSize <TT>getMinimumSize</TT>} are
    * overridden to return these dimensions.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
    * <P>
    *
    * @param requestedWidth the requested width for the canvas.
    * @param requestedHeight the requested height for the canvas.
    */
   public void setSize(int requestedWidth, int requestedHeight)
   {
      this.requestedWidth = requestedWidth;
      this.requestedHeight = requestedHeight;
   }
   
   /**
    * This method is used by system classes -- it is not really for public consumption!
    * <P>
    */
   public Dimension getPreferredSize()
   {
      return new Dimension(requestedWidth, requestedHeight);
   }
   
   /**
    * This method is used by system classes -- it is not really for public consumption!
    * <P>
    */
   public Dimension getMinimumSize()
   {
      return new Dimension(requestedWidth, requestedHeight);
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      if (toGraphics != null)
      {
         while (true)
         {
            final GraphicsProtocol gp = (GraphicsProtocol) toGraphics.read();
            if (gp == null)
               break;
            else
            {
               switch (gp.tag)
               {
                  case GraphicsProtocol.GET_DIMENSION_TAG:
                     fromGraphics.write(getSize());
                     break;
                  case GraphicsProtocol.GET_COMPONENT_TAG:
                     fromGraphics.write(this);
                     break;
                  case GraphicsProtocol.GET_BACKGROUND_TAG:
                     fromGraphics.write(getBackground());
                     break;
                  case GraphicsProtocol.SET_BACKGROUND_TAG:
                     final GraphicsProtocol.SetBackground sbc = (GraphicsProtocol.SetBackground) gp;
                     setBackground(sbc.color);
                     fromGraphics.write(Boolean.TRUE);
                     break;
                  case GraphicsProtocol.REQUEST_FOCUS_TAG:
                     requestFocus();
                     fromGraphics.write(Boolean.TRUE);
                     break;
                  case GraphicsProtocol.MAKE_MIS_IMAGE_TAG:
                     final GraphicsProtocol.MakeMISImage mmi = (GraphicsProtocol.MakeMISImage) gp;
                     fromGraphics.write(createImage(mmi.mis));
                     break;
                  case GraphicsProtocol.SET_PAINTABLE_TAG:
                     final GraphicsProtocol.SetPaintable sp = (GraphicsProtocol.SetPaintable) gp;
                     paintable = sp.paintable;
                     paintable.register(this);
                     fromGraphics.write(Boolean.TRUE);
                     break;
                  case GraphicsProtocol.GENERAL_TAG:
                     final GraphicsProtocol.General general = (GraphicsProtocol.General) gp;
                     fromGraphics.write(general.c.configure(this));
                     break;
               }
            }
         }
      }
   }
}
