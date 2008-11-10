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
 * {@link java.awt.Scrollbar <TT>java.awt.Scrollbar</TT>}
 * with a channel interface.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/ActiveScrollbar1.gif"></p>
 * <P>
 * <H2>Description</H2>
 * <TT>ActiveScrollbar</TT> is a process extension of <TT>java.awt.Scrollbar</TT>
 * with channels for run-time configuration and event notification.  The event channels
 * should be connected to one or more application-specific server processes (instead
 * of registering a passive object as a <I>Listener</I> to this component).
 * <P>
 * All channels are optional.  The <TT>configure</TT> and <TT>event</TT> channels are
 * settable from a constructor.
 * The <TT>event</TT> channel delivers the adjusted value whenever
 * the <TT>ActiveScrollbar</TT> is moved.
 * Other event channels can be added to notify the occurrence of any other events
 * the component generates (by calling the appropriate
 * <TT>add</TT><I>XXX</I><TT>EventChannel</TT> method <I>before</I> the process is run).
 * Messages can be sent down the <TT>configure</TT> channel at any time to configure
 * the component.  See the <A HREF="#Protocols">table below</A> for details.
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
 *   final One2OneChannelInt myScrollbarEvent = Channel.one2oneInt (new OverWriteOldestBuffer (n));
 * 
 *   final ActiveScrollbar myScrollbar =
 *     new ActiveScrollbar (null, myScrollbarEvent.out (), Scrollbar.HORIZONTAL, 0, 10, 0, 110);
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
 *     <TH ROWSPAN="3">configure</TH>
 *     <TD>Integer</TD>
 *     <TD>Sets the value of the scrollbar to the value received</TD>
 *   </TR>
 *   <TR>
 *     <TD>Boolean</TD>
 *     <TD>
 *       <OL>
 *         <LI>If this is the <TT>Boolean.TRUE</TT> object,
 *           the scrollbar is made active</LI>
 *         <LI>If this is the <TT>Boolean.FALSE</TT> object,
 *            the scrollbar is made inactive</LI>
 *         <LI>Other <TT>Boolean</TT> objects are ignored</LI>
 *       </OL>
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TD>ActiveScrollbar.Configure</TD>
 *     <TD>Invoke the user-defined <TT>Configure.configure</TT> method on the scrollbar.</TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>event</TH>
 *     <TD>int</TD>
 *     <TD>
 *       The new value of the scrollbar
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
 * import org.jcsp.util.ints.*;
 * import org.jcsp.awt.*;
 * 
 * public class ActiveScrollbarExample {
 * 
 *   public static void main (String argv[]) {
 * 
 *     final ActiveClosingFrame activeFrame = new ActiveClosingFrame ("ActiveScrollbar Example");
 * 
 *     final One2OneChannel configure = Channel.one2one ();
 *     final One2OneChannelInt scrollEvent = Channel.one2oneInt (new OverWriteOldestBufferInt (10));
 * 
 *     final ActiveScrollbar scrollBar =
 *       new ActiveScrollbar (
 *         null, scrollEvent.out (), Scrollbar.HORIZONTAL, 0, 10, 0, 110
 *       );
 * 
 *     final Frame realFrame = activeFrame.getActiveFrame ();
 *     realFrame.setSize (400, 75);
 *     realFrame.add (scrollBar);
 *     realFrame.setVisible (true);
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         activeFrame,
 *         scrollBar,
 *         new CSProcess () {
 *           public void run () {
 *             final long FREE_TIME = 10000, BUSY_TIME = 250;
 *             CSTimer tim = new CSTimer ();
 *             Alternative alt = new Alternative (new Guard[] {tim, scrollEvent.in ()});
 *             final int TIM = 0, SCROLL = 1;
 *             long timeout = tim.read ();
 *             tim.setAlarm (timeout + FREE_TIME);
 *             boolean running = true;
 *             while (running) {
 *               switch (alt.priSelect ()) {
 *                 case TIM:                                     // timeout happened
 *                   configure.out ().write (Boolean.FALSE);     // disable scrollbar
 *                   for (int i = 40; i > 0; i--) {
 *                     System.out.println ("*** busy busy busy ... " + i);
 *                     timeout = tim.read ();
 *                     tim.after (timeout + BUSY_TIME);
 *                   }
 *                   System.out.println ("*** free free free ... 0");
 *                   configure.out ().write (Boolean.TRUE);      // enable scrollbar
 *                   timeout = tim.read ();
 *                   tim.setAlarm (timeout + FREE_TIME);
 *                 break;
 *                 case SCROLL:                                  // scrollEvent happened
 *                   int position = scrollEvent.in ().read ();
 *                   System.out.println ("ScrollBar ==> " + position);
 *                   running = (position != 100);
 *                 break;
 *               }
 *             }
 *             realFrame.setVisible (false);
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
 * @see java.awt.Scrollbar
 * @see java.awt.event.ComponentEvent
 * @see java.awt.event.FocusEvent
 * @see java.awt.event.KeyEvent
 * @see java.awt.event.MouseEvent
 * @see org.jcsp.util.OverWriteOldestBuffer
 *
 * @author P.D. Austin and P.H. Welch
 */

public class ActiveScrollbar extends Scrollbar implements CSProcess
{
   /**
    * The Vector construct containing the handlers.
    */
   private Vector vec = new Vector();
   
   /**
    * The channel from which configuration messages arrive.
    */
   private ChannelInput configure;
   
   /**
    * Constructs a vertical <TT>ActiveScrollbar</TT> with no configuration
    * or event channels.
    */
   public ActiveScrollbar()
   {
      this(Scrollbar.VERTICAL);
   }
   
   /**
    * Constructs a <TT>ActiveScrollbar</TT> with the specified orientation, but still with no
    * configuration or event channels.  The minimum and maximum values default to 0 and 100,
    * with the initial setting at 0.
    *
    * @param orientation indicates the orientation of the scroll bar
    * (Scrollbar.VERTICAL, Scrollbar.HORIZONTAL).
    */
   public ActiveScrollbar(int orientation)
   {
      this(orientation, 0, 10, 0, 110);
   }
   
   /**
    * Constructs a <TT>ActiveScrollbar</TT> with all its options, but still with no
    * configuration or event channels.
    *
    * @param orientation indicates the orientation of the scroll bar
    * (Scrollbar.VERTICAL, Scrollbar.HORIZONTAL).
    * @param value the initial value of the scroll bar.
    * @param visible the size of the scroll bar's bubble, representing the visible portion; the scroll bar uses this value when paging up or down by a page.
    * @param minimum the minimum value of the scroll bar.
    * @param maximum the maximum value of the scroll bar.
    */
   public ActiveScrollbar(int orientation, int value, int visible, int minimum, int maximum)
   {
      this(null, null, orientation, value, visible, minimum, maximum);
   }
   
   /**
    * Constructs a vertical <TT>ActiveScrollbar</TT> with configuration and event channels.
    * The minimum and maximum values default to 0 and 100, with the initial setting at 0.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the adjusted value will be output when the scrollbar is moved
    * -- can be null if no notification is required.
    */
   public ActiveScrollbar(ChannelInput configure, ChannelOutputInt event)
   {
      this(configure, event, Scrollbar.VERTICAL);
   }
   
   /**
    * Constructs a <TT>ActiveScrollbar</TT> with configuration and event channels and
    * the specified orientation.
    * The minimum and maximum values default to 0 and 100, with the initial setting at 0.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the adjusted value will be output when the scrollbar is moved
    * -- can be null if no notification is required.
    * @param orientation indicates the orientation of the scroll bar
    * (Scrollbar.VERTICAL, Scrollbar.HORIZONTAL).
    */
   public ActiveScrollbar(ChannelInput configure, ChannelOutputInt event, int orientation)
   {
      this(configure, event, orientation, 0, 10, 0, 110);
   }
   
   /**
    * Constructs a <TT>ActiveScrollbar</TT> with configuration and event channels and
    * all its options.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the adjusted value will be output when the scrollbar is moved
    * -- can be null if no notification is required.
    * @param orientation indicates the orientation of the scroll bar
    * (Scrollbar.VERTICAL, Scrollbar.HORIZONTAL).
    * @param value the initial value of the scroll bar.
    * @param visible the size of the scroll bar's bubble, representing the visible portion; the scroll bar uses this value when paging up or down by a page.
    * @param minimum the minimum value of the scroll bar.
    * @param maximum the maximum value of the scroll bar.
    */
   public ActiveScrollbar(ChannelInput configure, ChannelOutputInt event,
           int orientation, int value, int visible, int minimum, int maximum)
   {
      super(orientation, value, visible, minimum, maximum);
      if (event != null)
      {
         AdjustmentEventHandler handler = new AdjustmentEventHandler(event);
         addAdjustmentListener(handler);
         vec.addElement(handler);
      }
      this.configure = configure;
   }
   
   /**
    * Sets the configuration channel for this <TT>ActiveScrollbar</TT>.
    * This method overwrites any configuration channel set in the constructor.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    */
   public void setConfigureChannel(ChannelInput configure)
   {
      this.configure = configure;
   }
   
   /**
    * Add a new channel to this component that will be used to notify that
    * a <TT>ComponentEvent</TT> has occurred. <I>This should be used
    * instead of registering a ComponentListener with the component.</I>  It is
    * possible to add more than one channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
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
    * possible to add more than one channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
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
    * possible to add more than one channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
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
    * possible to add more than one channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
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
    * possible to add more than one channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
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
   
   /**
    * This enables general configuration of this component.  Any object implementing
    * this interface and sent down the <TT>configure</TT> channel to this component will have its
    * <TT>configure</TT> method invoked on this component.
    * <P>
    * For an example, see {@link ActiveApplet.Configure}.
    */
   static public interface Configure
   {
      /**
       * @param scrollbar the Scrollbar being configured.
       */
      public void configure(final Scrollbar scrollbar);
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      if (configure != null)
      {
         while (true)
         {
            Object message = configure.read();
            if (message instanceof Integer)
               setValue(((Integer) message).intValue());
            else if (message instanceof Boolean)
            {
               if (message == Boolean.TRUE)
                  setEnabled(true);
               else if (message == Boolean.FALSE)
                  setEnabled(false);
            }
            else if (message instanceof Configure)
               ((Configure) message).configure(this);
         }
      }
   }
}
