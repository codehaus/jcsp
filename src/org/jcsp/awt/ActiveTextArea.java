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
 * {@link java.awt.TextArea <TT>java.awt.TextArea</TT>}
 * with a channel interface.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/ActiveTextArea1.gif"></p>
 * <P>
 * <H2>Description</H2>
 * <TT>ActiveTextArea</TT> is a process extension of <TT>java.awt.TextArea</TT>
 * with channels for run-time configuration and event notification.  The event channels
 * should be connected to one or more application-specific server processes (instead
 * of registering a passive object as a <I>Listener</I> to this component).
 * <P>
 * All channels are optional.  The <TT>configure</TT> and <TT>event</TT> channels are
 * settable from a constructor.
 * The <TT>event</TT> channel delivers updated text whenever
 * the <TT>ActiveTextArea</TT> is changed.
 * Other event channels can be added to notify the occurrence of any other events
 * the component generates (by calling the appropriate
 * <TT>add</TT><I>xxx</I><TT>EventChannel</TT> method <I>before</I> the process is run).
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
 *   final One2OneChannel myTextAreaEvent = Channel.one2one (new OverWriteOldestBuffer (n));
 * 
 *   final ActiveTextArea myTextArea =
 *     new ActiveTextArea (null, myTextAreaEvent.out (), "Edit Me", 5, 20);
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
 *     <TH ROWSPAN="4">configure</TH>
 *     <TD>String</TD>
 *     <TD>Append the string to the text in this <TT>ActiveTextArea</TT></TD>
 *   </TR>
 *   <TR>
 *     <TD>Boolean</TD>
 *     <TD>
 *       <OL>
 *         <LI>If this is the <TT>Boolean.TRUE</TT> object,
 *           the text area is made active</LI>
 *         <LI>If this is the <TT>Boolean.FALSE</TT> object,
 *            the text area is made inactive</LI>
 *         <LI>Other <TT>Boolean</TT> objects are ignored</LI>
 *       </OL>
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TD>ActiveTextArea.Configure</TD>
 *     <TD>Invoke the user-defined <TT>Configure.configure</TT> method on the textArea.</TD>
 *   </TR>
 *   <TR>
 *     <TD><I>otherwise ...</I></TD>
 *     <TD>Append the <TT>toString</TT> form of the object to the text in this <TT>ActiveTextArea</TT>.</TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>event</TH>
 *     <TD>String</TD>
 *     <TD>The text in the <TT>ActiveTextArea</TT> (whenever the text area is altered)</TD>
 *   </TR>
 *   <TR>
 *     <TH>componentEvent</TH>
 *     <TD>ComponentEvent</TD>
 *     <TD>See the {@link ActiveTextArea#addComponentEventChannel
 *         <TT>addComponentEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>focusEvent</TH>
 *     <TD>FocusEvent</TD>
 *     <TD>See the {@link ActiveTextArea#addFocusEventChannel
 *         <TT>addFocusEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>keyEvent</TH>
 *     <TD>KeyEvent</TD>
 *     <TD>See the {@link ActiveTextArea#addKeyEventChannel
 *         <TT>addKeyEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>mouseEvent</TH>
 *     <TD>MouseEvent</TD>
 *     <TD>See the {@link ActiveTextArea#addMouseEventChannel
 *         <TT>addMouseEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>mouseMotionEvent</TH>
 *     <TD>MouseEvent</TD>
 *     <TD>See the {@link ActiveTextArea#addMouseMotionEventChannel
 *         <TT>addMouseMotionEventChannel</TT>} method.</TD>
 *   </TR>
 * </TABLE>
 * </CENTER>
 * <H2>Example</H2>
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.util.*;
 * import org.jcsp.awt.*;
 * import java.awt.*;
 * 
 * public class ActiveTextAreaExample {
 * 
 *   public static void main (String argv[]) {
 * 
 *     final ActiveClosingFrame frame =
 *       new ActiveClosingFrame ("ActiveTextAreaExample Example");
 * 
 *     final Any2OneChannel event = Channel.any2one (new OverWriteOldestBuffer (10));
 * 
 *     final String[] string =
 *       {"Entia Non Sunt Multiplicanda Praeter Necessitatem",
 *        "Everything we do, we do it to you",
 *        "Race Hazards - What Rice Hozzers?",
 *        "Cogito Ergo Occam"};
 * 
 *     final String goodbye = "Goodbye World";
 * 
 *     final ActiveTextArea[] activeText =
 *       new ActiveTextArea[string.length];
 * 
 *     for (int i = 0; i < string.length; i++) {
 *       activeText[i] = new ActiveTextArea (null, event.out (), string[i], 5, 40);
 *     }
 * 
 *     Panel panel = new Panel (new GridLayout (string.length/2, 2));
 *     for (int i = 0; i < string.length; i++) {
 *       panel.add (activeText[i]);
 *     }
 * 
 *     final Frame realFrame = frame.getActiveFrame ();
 *     realFrame.setBackground (Color.green);
 *     realFrame.add (panel);
 *     realFrame.pack ();
 *     realFrame.setVisible (true);
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         frame,
 *         new Parallel (activeText),
 *         new CSProcess () {
 *           public void run () {
 *             boolean running = true;
 *             while (running) {
 *               String s = (String) event.in ().read ();
 *               System.out.println (s);
 *               running = (! s.equals (goodbye));
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
 * @see org.jcsp.awt.ActiveTextEnterArea
 * @see java.awt.TextArea
 * @see java.awt.event.ComponentEvent
 * @see java.awt.event.FocusEvent
 * @see java.awt.event.KeyEvent
 * @see java.awt.event.MouseEvent
 * @see org.jcsp.util.OverWriteOldestBuffer
 *
 * @author P.D. Austin and P.H. Welch
 */

public class ActiveTextArea extends TextArea implements CSProcess
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
    * Constructs a new <TT>ActiveTextArea</TT> with scrollbars, but with no configuration
    * or event channels or initial text or size.
    */
   public ActiveTextArea()
   {
      this(null, null, "", 0, 0, SCROLLBARS_BOTH);
   }
   
   /**
    * Constructs a new <TT>ActiveTextArea</TT> with scrollbars and initial text,
    * but with no configuration or event channels or initial size.
    *
    * @param s the initial text displayed in the area.
    */
   public ActiveTextArea(String s)
   {
      this(null, null, s, 0, 0, SCROLLBARS_BOTH);
   }
   
   /**
    * Constructs a new <TT>ActiveTextArea</TT> with scrollbars and initial text and size,
    * but with no configuration or event channels.
    *
    * @param s the initial text displayed in the area.
    * @param rows the rows of the area.
    * @param columns the columns of the area.
    */
   public ActiveTextArea(String s, int rows, int columns)
   {
      this(null, null, s, rows, columns, SCROLLBARS_BOTH);
   }
   
   /**
    * Constructs a new <TT>ActiveTextArea</TT> with user-defined scrollbars and initial text and size,
    * but with no configuration or event channels.
    *
    * @param s the initial text displayed in the area.
    * @param rows the rows of the area.
    * @param columns the columns of the area.
    * @param scrollbars the columns of the area (java.awt.TextArea.SCROLLBARS_BOTH,
    * <TT>java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY</TT>,
    * <TT>java.awt.TextArea.SCROLLBARS_HORIZONTAL_ONLY</TT>
    * or <TT>java.awt.TextArea.SCROLLBARS_NONE</TT>).
    */
   public ActiveTextArea(String s, int rows, int columns, int scrollbars)
   {
      this(null, null, s, rows, columns, scrollbars);
   }
   
   /**
    * Constructs a new <TT>ActiveTextArea</TT> with configuration and event channels and scrollbars,
    * but with no initial text or size.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the current text will be output when the text area is changed
    * -- can be null if no notification is required.
    */
   public ActiveTextArea(ChannelInput configure, ChannelOutput event)
   {
      this(configure, event, "", 0, 0, SCROLLBARS_BOTH);
   }
   
   /**
    * Constructs a new <TT>ActiveTextArea</TT> with configuration and event channels
    * and scrollbars and initial text, but with no initial size.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the current text will be output when the text area is changed
    * -- can be null if no notification is required.
    * @param s the initial text displayed in the area.
    */
   public ActiveTextArea(ChannelInput configure, ChannelOutput event, String s)
   {
      this(configure, event, s, 0, 0, SCROLLBARS_BOTH);
   }
   
   /**
    * Constructs a new <TT>ActiveTextArea</TT> with configuration and event channels
    * and scrollbars and initial text and size.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the current text will be output when the text area is changed
    * -- can be null if no notification is required.
    * @param s the initial text displayed in the area.
    * @param rows the rows of the area.
    * @param columns the columns of the area.
    */
   public ActiveTextArea(ChannelInput configure, ChannelOutput event,
           String s, int rows, int columns)
   {
      this(configure, event, s, rows, columns, SCROLLBARS_BOTH);
   }
   
   /**
    * Constructs a new <TT>ActiveTextArea</TT> with configuration and event channels
    * and user-defined scrollbars and initial text and size.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the current text will be output when the text area is changed
    * -- can be null if no notification is required.
    * @param s the initial text displayed in the area.
    * @param rows the rows of the area.
    * @param columns the columns of the area.
    * @param scrollbars the columns of the area (java.awt.TextArea.SCROLLBARS_BOTH,
    * <TT>java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY</TT>,
    * <TT>java.awt.TextArea.SCROLLBARS_HORIZONTAL_ONLY</TT>
    * or <TT>java.awt.TextArea.SCROLLBARS_NONE</TT>).
    */
   public ActiveTextArea(ChannelInput configure, ChannelOutput event,
           String s, int rows, int columns, int scrollbars)
   {
      super(s, rows, columns, scrollbars);
      if (event != null)
      {
         TextEventHandler handler = new TextEventHandler(event);
         addTextListener(handler);
         vec.addElement(handler);
      }
      this.configure = configure;
   }
   
   /**
    * Sets the configuration channel for this <TT>ActiveTextArea</TT>.
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
       * @param textArea the TextArea being configured.
       */
      public void configure(final TextArea textArea);
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
            if (message instanceof String)
               append((String) message);
            else if (message instanceof Boolean)
            {
               if (message == Boolean.TRUE)
                  setEnabled(true);
               else if (message == Boolean.FALSE)
                  setEnabled(false);
            }
            else if (message instanceof Configure)
               ((Configure) message).configure(this);
            else
               append(message.toString());
         }
      }
   }
}
