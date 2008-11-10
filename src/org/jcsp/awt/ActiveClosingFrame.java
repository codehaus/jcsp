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
import java.awt.event.*;
import org.jcsp.lang.*;

/**
 * A specialisation of {@link ActiveFrame} that forces a <TT>System.exit</TT>
 * upon a <I>Window Closing</I> event.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/ActiveClosingFrame1.gif"></p>
 * <H2>Description</H2>
 * <TT>ActiveClosingFrame</TT> is a process containing an {@link ActiveFrame <TT>ActiveFrame</TT>},
 * configured with channels for event notification and configuration.  The event channels
 * should be connected to one or more application-specific server processes (instead
 * of registering a passive object as a <I>Listener</I> to this component).
 * <P>
 * All channels are optional.  The <TT>configure</TT> and <TT>event</TT> channels are
 * settable from a constructor.
 * <P>
 * The difference between this class and <TT>ActiveFrame</TT> is that a <I>Window Closing</I> event,
 * generated on the (internal) <TT>ActiveFrame</TT>, is intercepted by the <I>anonymous</I> process
 * and results in a <TT>System.exit</TT>.  This happens regardless as to whether
 * the (external) <TT>event</TT> channel is null.  Otherwise, <TT>WindowEvent</TT>s
 * are forwarded to the (external) <TT>event</TT> channel, so long as it's not null).
 * <P>
 * The internal <TT>ActiveFrame</TT> can be extracted with the <TT>getActiveFrame</TT> method.
 * Channels can then be added to notify the occurrence of any other type of <TT>Event</TT>
 * the <TT>ActiveFrame</TT> generates.  This is done by calling its appropriate
 * <TT>add</TT><I>XXX</I><TT>EventChannel</TT> method
 * (<I>before</I> the process is run).  As many channels can be added as are needed.
 * <P>
 * Messages can be sent down the <TT>configure</TT> channel at any time to configure
 * the component.  See the <A HREF="#Protocols">table below</A> for details.
 * <P>
 * All channels are managed by independent internal handler processes.  It is, therefore,
 * safe for a serial application process both to service an event channel and configure
 * the component -- no deadlock can occur.
 * <P>
 * <I>IMPORTANT: it is essential that the output channels from this process are
 * always serviced -- otherwise the Java Event Thread will be blocked and the GUI
 * will stop responding.  The simplest way to guarantee this is to use channels
 * configured with overwriting buffers.
 * For example:</I>
 * <PRE>
 *   final One2OneChannel myWindowEvent = Channel.one2one (new OverWriteOldestBuffer (n));
 *   final One2OneChannel myMouseEvent = Channel.one2one (new OverWriteOldestBuffer (n));
 * 
 *   final ActiveClosingFrame myFrame = new ActiveClosingFrame (myWindowEvent.out ());
 *   final ActiveFrame myActiveFrame = myFrame.getActiveFrame ();
 *   myActiveFrame.addMouseEventChannel (myMouseEvent.out ());
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
 *     <TH ROWSPAN="2">configure</TH>
 *     <TD>Boolean</TD>
 *     <TD>
 *       <OL>
 *         <LI>If this is the <TT>Boolean.TRUE</TT> object,
 *           the frame is made visible</LI>
 *         <LI>If this is the <TT>Boolean.FALSE</TT> object,
 *            the frame is made invisible</LI>
 *         <LI>Other <TT>Boolean</TT> objects are ignored</LI>
 *       </OL>
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TD>ActiveFrame.Configure</TD>
 *     <TD>Invoke the user-defined <TT>Configure.configure</TT> method on the frame.</TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>event</TH>
 *     <TD>WindowEvent</TD>
 *     <TD>The <TT>WindowEvent</TT> generated by the component (note that
 *         <I>Window Closing</I> is handled locally to cause
 *         a <TT>System.exit</TT>).</TD>
 *   </TR>
 *   <TR>
 *     <TH>containerEvent</TH>
 *     <TD>ContainerEvent</TD>
 *     <TD>See the
 *         {@link #getActiveFrame <TT>getActiveFrame</TT>}.{@link ActiveFrame#addContainerEventChannel
 *         <TT>addContainerEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>componentEvent</TH>
 *     <TD>ComponentEvent</TD>
 *     <TD>See the
 *         {@link #getActiveFrame <TT>getActiveFrame</TT>}.{@link ActiveFrame#addComponentEventChannel
 *         <TT>addComponentEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>focusEvent</TH>
 *     <TD>FocusEvent</TD>
 *     <TD>See the
 *         {@link #getActiveFrame <TT>getActiveFrame</TT>}.{@link ActiveFrame#addFocusEventChannel
 *         <TT>addFocusEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>keyEvent</TH>
 *     <TD>KeyEvent</TD>
 *     <TD>See the
 *         {@link #getActiveFrame <TT>getActiveFrame</TT>}.{@link ActiveFrame#addKeyEventChannel
 *         <TT>addKeyEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>mouseEvent</TH>
 *     <TD>MouseEvent</TD>
 *     <TD>See the
 *         {@link #getActiveFrame <TT>getActiveFrame</TT>}.{@link ActiveFrame#addMouseEventChannel
 *         <TT>addMouseEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>mouseMotionEvent</TH>
 *     <TD>MouseEvent</TD>
 *     <TD>See the
 *         {@link #getActiveFrame <TT>getActiveFrame</TT>}.{@link ActiveFrame#addMouseMotionEventChannel
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
 * public class ActiveClosingFrameButtonExample {
 * 
 *   public static void main (String argv[]) {
 * 
 *     final ActiveClosingFrame frame =
 *       new ActiveClosingFrame ("ActiveClosingFrameButton Example");
 * 
 *     final String[] label = {"Hello World", "Rocket Science", "CSP",
 *                             "Monitors", "Ignore Me", "Goodbye World"};
 * 
 *     final Any2OneChannel buttonEvent = Channel.any2one (new OverWriteOldestBuffer (10));
 * 
 *     final ActiveButton[] button = new ActiveButton[label.length];
 *     for (int i = 0; i < label.length; i++) {
 *       button[i] = new ActiveButton (null, buttonEvent.out (), label[i]);
 *     }
 * 
 *     final Frame realFrame = frame.getActiveFrame ();
 *     realFrame.setSize (300, 200);
 *     realFrame.setLayout (new GridLayout (label.length/2, 2));
 *     for (int i = 0; i < label.length; i++) {
 *       realFrame.add (button[i]);
 *     }
 *     realFrame.setVisible (true);
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         frame,
 *         new Parallel (button),
 *         new CSProcess () {
 *           public void run () {
 *             boolean running = true;
 *             while (running) {
 *               final String s = (String) buttonEvent.in ().read ();
 *               System.out.println ("Button `" + s + "' pressed ...");
 *               running = (s != label[label.length - 1]);
 *             }
 *             realFrame.setVisible (false);
 *             System.exit (0);
 *           }
 *         }
 *       }
 *     ).run ();
 * 
 *   }
 * 
 * }
 * </PRE>
 *
 * @see org.jcsp.awt.ActiveFrame
 * @see java.awt.Frame
 * @see java.awt.event.ContainerEvent
 * @see java.awt.event.ComponentEvent
 * @see java.awt.event.FocusEvent
 * @see java.awt.event.KeyEvent
 * @see java.awt.event.MouseEvent
 * @see org.jcsp.util.OverWriteOldestBuffer
 *
 * @author P.D. Austin and P.H. Welch
 */

public class ActiveClosingFrame implements CSProcess
{
   private ChannelInput configure;
   private ChannelOutput event;
   private String title;
   
   private ActiveFrame frame;
   
   private One2OneChannel windowEvent = Channel.one2one();
   
   /**
    * Constructs a new <TT>ActiveClosingFrame</TT> with no title and no configuration
    * or event channels.
    *
    */
   public ActiveClosingFrame()
   {
      this(null, null, "");
   }
   
   /**
    * Constructs a new <TT>ActiveClosingFrame</TT> with a title but no configuration
    * or event channels.
    *
    * @param title the title for the frame.
    */
   public ActiveClosingFrame(String title)
   {
      this(null, null, title);
   }
   
   /**
    * Constructs a new <TT>ActiveClosingFrame</TT> with configuration
    * and event channels, but no title.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event a WindowEvent will be output whenever it occurs
    * -- can be null if no notification is required.
    */
   public ActiveClosingFrame(ChannelInput configure, ChannelOutput event)
   {
      this(configure, event, "");
   }
   
   /**
    * Constructs a new <TT>ActiveClosingFrame</TT> with configuration
    * and event channels and a title.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event a WindowEvent will be output whenever it occurs
    * -- can be null if no notification is required.
    * @param title the title for the frame.
    */
   public ActiveClosingFrame(ChannelInput configure, ChannelOutput event, String title)
   {     
      this.configure = configure;
      this.event = event;
      this.title = title;  
      this.frame = new ActiveFrame(this.configure, windowEvent.out(), title);
   }
   
   /**
    * Sets the configuration channel for this <TT>ActiveButton</TT>.
    * This method overwrites any configuration channel set in the constructor.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    */
   public void setConfigureChannel(ChannelInput configure)
   {
      this.frame.setConfigureChannel(configure);
   }
   
   /**
    * This is used to get the <TT>ActiveFrame</TT> within this component
    * so that it can be configured or have components added
    * (using {@link java.awt.Frame} or {@link org.jcsp.awt.ActiveFrame} methods).
    * For example, event channels can be added by invoking
    * <TT>getActiveFrame().add</TT><I>XXX</I><TT>EventChannel(...)</TT>.
    * <P>
    * <I>NOTE: This must be finished before this process is run.</I>
    *
    * @return the <TT>Frame</TT> within this component.
    */
   public ActiveFrame getActiveFrame()
   {
      return frame;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      new Parallel(new CSProcess[] 
                  {frame,
                   new CSProcess()
                   {
                     public void run()
                     {
                        boolean running = true;
                        ChannelInput windowEventIn = windowEvent.in();
                        while (running)
                        {
                           final WindowEvent window = (WindowEvent) windowEventIn.read();
                           if (event != null) 
                              event.write(window);
                           switch (window.getID())
                           {
                              case WindowEvent.WINDOW_CLOSING:
                                 running = false;
                                 break;
                           }
                        }
                        frame.setVisible(false);
                        System.exit(0);
                     }
                  }
               }).run();
   }
}