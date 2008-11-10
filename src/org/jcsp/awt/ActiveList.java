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
 * {@link java.awt.List <TT>java.awt.List</TT>}
 * with a channel interface.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/ActiveList1.gif"></p>
 * <H2>Description</H2>
 * <TT>ActiveList</TT> is a process extension of <TT>java.awt.List</TT>
 * with channels for run-time configuration and event notification.  The event channels
 * should be connected to one or more application-specific server processes (instead
 * of registering a passive object as a <I>Listener</I> to this component).
 * <P>
 * All channels are optional.  The <TT>configure</TT> and <TT>event</TT> channels are
 * settable from a constructor.
 * When an item in the <TT>ActiveList</TT> is
 * double-clicked, its label is written down the <TT>event</TT> channel.
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
 *   final One2OneChannel myListEvent = Channel.one2one (new OverWriteOldestBuffer (n));
 *   final One2OneChannel myListItemEvent = Channel.one2one (new OverWriteOldestBuffer (n));
 * 
 *   final ActiveList myList = new ActiveList (null, myListEvent.out ());
 *   myList.addItemEventChannel (myListItemEvent.out ());
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
 *     <TD>
 * Empty strings are ignored.  If the string is <TT>"-*"</TT>, all items in
 * the list are removed.  Otherwise, if the first character is <TT>'-'</TT>,
 * the item corresponding to the tail of the string is removed from the list.
 * Otherwise, the string is added to the end of the list.
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TD>Integer</TD>
 *     <TD>
 * Sets the selected item in this <TT>ActiveList</TT> list to be the item
 * at the specified position.
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TD>Boolean</TD>
 *     <TD>
 *       <OL>
 *         <LI>If this is the <TT>Boolean.TRUE</TT> object,
 *           the list is made active</LI>
 *         <LI>If this is the <TT>Boolean.FALSE</TT> object,
 *            the list is made inactive</LI>
 *         <LI>Other <TT>Boolean</TT> objects are ignored</LI>
 *       </OL>
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TD>ActiveList.Configure</TD>
 *     <TD>Invoke the user-defined <TT>Configure.configure</TT> method on the list.</TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>event</TH>
 *     <TD>String</TD>
 *     <TD>
 * When an item in the <TT>ActiveList</TT> is double-clicked,
 * its label is written down this channel.
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TH>itemEvent</TH>
 *     <TD>ItemEvent</TD>
 *     <TD>See the {@link #addItemEventChannel
 *         <TT>addItemEventChannel</TT>} method.</TD>
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
 * public class ActiveListExample {
 * 
 *   public static void main (String argv[]) {
 * 
 *     final Frame root = new Frame ("ActiveList Example");
 * 
 *     final One2OneChannel configure = Channel.one2one ();
 * 
 *     final One2OneChannel event = Channel.one2one (new OverWriteOldestBuffer (10));
 *     final One2OneChannel itemEvent = Channel.one2one (new OverWriteOldestBuffer (10));
 * 
 *     final ActiveList list = new ActiveList (configure.in (), event.out (), 0, true);
 *     list.addItemEventChannel (itemEvent.out ());
 * 
 *     final String[] menu = {"Hello World", "Rocket Science", "CSP",
 *                           "Monitors", "Ignore Me", "Goodbye World"};
 * 
 *     for (int i = 0; i < menu.length; i++) {
 *       list.add (menu[i]);
 *     }
 * 
 *     root.setSize (300, 105);
 *     root.add (list);
 *     root.setVisible (true);
 * 
 *     new Parallel (
 *       new CSProcess[] {           // respond to the event channel
 *         list,
 *         new CSProcess () {
 *           public void run () {
 *             boolean running = true;
 *             while (running) {
 *               final String s = (String) event.in ().read ();
 *               System.out.println ("                         Action ==> `" + s + "'");
 *               running = (s != menu[menu.length - 1]);
 *             }
 *             root.setVisible (false);
 *             System.exit (0);
 *           }
 *         },
 *         new CSProcess () {        // respond to the itemEvent channel
 *           public void run () {
 *             while (true) {
 *               final ItemEvent e = (ItemEvent) itemEvent.in ().read ();
 *               final Integer item = (Integer) e.getItem ();
 *               if (e.getStateChange () == ItemEvent.SELECTED) {
 *                 System.out.println ("Selected item " + item);
 *               } else {
 *                 System.out.println ("Unselected item " + item);
 *               }
 *             }
 *           }
 *         },
 *         new CSProcess () {        // dynamically reconfigure the list
 *           public void run () {
 *             CSTimer tim = new CSTimer ();
 *             long timeout = tim.read ();
 *             while (true) {
 *               timeout += 10000;
 *               tim.after (timeout);
 *               System.out.println ("*** Removing last three items ...");
 *               for (int i = 3; i < menu.length; i++) {
 *                 configure.out ().write ("-" + menu[i]);
 *               }
 *               timeout += 10000;
 *               tim.after (timeout);
 *               System.out.println ("*** Restoring last three items ...");
 *               for (int i = 3; i < menu.length; i++) {
 *                 configure.out ().write (menu[i]);
 *               }
 *             }
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
 * @see java.awt.List
 * @see java.awt.event.ItemEvent
 * @see java.awt.event.ComponentEvent
 * @see java.awt.event.FocusEvent
 * @see java.awt.event.KeyEvent
 * @see java.awt.event.MouseEvent
 * @see org.jcsp.util.OverWriteOldestBuffer
 *
 * @author P.D. Austin and P.H. Welch
 */

public class ActiveList extends List implements CSProcess
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
    * Constructs an <TT>ActiveList</TT> with no visible lines, no configuration
    * or event channels and only one item selectable.
    */
   public ActiveList()
   {
      this(null, null, 0, false);
   }
   
   /**
    * Constructs an <TT>ActiveList</TT> with the specified number of visible
    * lines, but with no configuration or event channels
    * and only one item selectable.
    *
    * @param rows the number of visible lines.
    */
   public ActiveList(int rows)
   {
      this(null, null, rows, false);
   }
   
   /**
    * Constructs an <TT>ActiveList</TT> with the specified number of visible
    * lines and a multiple selection option, but with no configuration
    * or event channels.
    *
    * @param rows the number of visible lines.
    * @param multipleMode the multiple selection option.
    */
   public ActiveList(int rows, boolean multipleMode)
   {
      this(null, null, rows, multipleMode);
   }
   
   /**
    * Constructs an <TT>ActiveList</TT> with configuration and event channels,
    * but no visible items and only one item selectable.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the String label from a list item will be output when
    * it is double-clicked -- can be null if no notification is required.
    */
   public ActiveList(ChannelInput configure, ChannelOutput event)
   {
      this(configure, event, 0, false);
   }
   
   /**
    * Constructs an <TT>ActiveList</TT> with configuration and event channels,
    * the specified number of visible lines and only one item selectable.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the String label from a list item will be output when
    * it is double-clicked -- can be null if no notification is required.
    * @param rows the number of visible lines.
    */
   public ActiveList(ChannelInput configure, ChannelOutput event, int rows)
   {
      this(configure, event, rows, false);
   }
   
   /**
    * Constructs an <TT>ActiveList</TT> with configuration and event channels,
    * the specified number of visible lines and a multiple selection option.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the String label from a list item will be output when
    * it is double-clicked -- can be null if no notification is required.
    * @param rows the number of visible lines.
    * @param multipleMode the multiple selection option.
    */
   public ActiveList(ChannelInput configure, ChannelOutput event, int rows, boolean multipleMode)
   {
      super(rows, multipleMode);
      if (event != null)
      {
         ActionEventHandler handler = new ActionEventHandler(event);
         addActionListener(handler);
         vec.addElement(handler);
      }
      this.configure = configure;
   }
   
   /**
    * Sets the configuration channel for this <TT>ActiveList</TT>.
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
    * an <TT>ItemEvent</TT> has occurred. <I>This should be used
    * instead of registering a ItemListener with the component.</I>  It is
    * possible to add more than one channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
    *
    * @param itemEvent the channel down which to send ItemEvents.
    */
   public void addItemEventChannel(ChannelOutput itemEvent)
   {
      if (itemEvent != null)
      {
         ItemEventHandler handler = new ItemEventHandler(itemEvent);
         addItemListener(handler);
         vec.addElement(handler);
      }
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
       * @param list the List being configured.
       */
      public void configure(final List list);
      
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
            {
               String s = (String) message;
               if (s.length() > 0)
               {
                  if (s.equals("-*"))
                     removeAll();
                  else if (s.charAt(0) == '-')
                     remove(s.substring(1));
                  else
                     add(s);
               }
            }
            else if (message instanceof Integer)
               select(((Integer) message).intValue());
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
