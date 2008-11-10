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
 * {@link java.awt.CheckboxMenuItem <TT>java.awt.CheckboxMenuItem</TT>}
 * with a channel interface.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/ActiveCheckboxMenuItem1.gif"></p>
 * <H2>Description</H2>
 * <TT>ActiveCheckboxMenuItem</TT> is a process extension of <TT>java.awt.CheckboxMenuItem</TT>
 * with channels for run-time configuration and event notification.  The event channels
 * should be connected to one or more application-specific server processes (instead
 * of registering a passive object as a <I>Listener</I> to this component).
 * <P>
 * All channels are optional.  The <TT>configure</TT> and <TT>event</TT> channels are
 * settable from a constructor.
 * The <TT>event</TT> channel delivers the generated <TT>java.awt.ItemEvent</TT> whenever
 * the <TT>ActiveCheckboxMenuItem</TT> is selected.
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
 *   final One2OneChannel myCheckboxMenuItemEvent = Channel.one2one (new OverWriteOldestBuffer (n));
 * 
 *   final ActiveCheckboxMenuItem myCheckboxMenuItem =
 *     new ActiveCheckboxMenuItem (null, myCheckboxMenuItemEvent.out (), "Choose Me");
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
 *     <TD>Change the label on the <TT>ActiveCheckboxMenuItem</TT> to the value of the <TT>String</TT></TD>
 *   </TR>
 *   <TR>
 *     <TD>java.awt.MenuShortcut</TD>
 *     <TD>Sets the <TT>MenuShortcut</TT> for the <TT>ActiveCheckboxMenuItem</TT>
 *   </TR>
 *   <TR>
 *     <TD>Boolean</TD>
 *     <TD>
 *       <OL>
 *         <LI>If this is the <TT>Boolean.TRUE</TT> object,
 *           the checkboxMenuItem is made active</LI>
 *         <LI>If this is the <TT>Boolean.FALSE</TT> object,
 *            the checkboxMenuItem is made inactive</LI>
 *         <LI>Other <TT>Boolean</TT> objects are ignored</LI>
 *       </OL>
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TD>ActiveCheckboxMenuItem.Configure</TD>
 *     <TD>Invoke the user-defined <TT>Configure.configure</TT> method on the checkboxMenuItem.</TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>event</TH>
 *     <TD>java.awt.ItemEvent</TD>
 *     <TD>The generated <TT>java.awt.ItemEvent</TT> is written down this channel
 *         (when the checkboxMenuItem is selected or deslected)
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TH>actionEvent</TH>
 *     <TD>ActionEvent</TD>
 *     <TD>See the {@link #addActionEventChannel
 *         <TT>addActionEventChannel</TT>} method.</TD>
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
 * public class ActiveCheckboxMenuItemExample {
 * 
 *   public static void main (String argv[]) {
 * 
 *     final ActiveClosingFrame activeClosingFrame =
 *       new ActiveClosingFrame ("ActiveCheckboxMenuItem Example");
 * 
 *     final ActiveFrame frame = activeClosingFrame.getActiveFrame ();
 * 
 *     final MenuBar menuBar = new MenuBar ();
 *     frame.setMenuBar (menuBar);
 * 
 *     final Menu fileMenu = new Menu ("File");
 *     final Menu langMenu = new Menu ("Language");
 *     menuBar.add (fileMenu);
 *     menuBar.add (langMenu);
 * 
 *     final String[] fileOptions = {"Hello World", "Rocket Science", "CSP",
 *                                   "Monitors", "Ignore Me", "Goodbye World"};
 *     final String[] langOptions = {"occam-pi", "Java", "Smalltalk", "Algol-60",
 *                                   "Pascal", "Haskell", "SML", "Lisp"};
 * 
 *     final Any2OneChannel event[] = Channel.any2oneArray (2, new OverWriteOldestBuffer (10));
 * 
 *     final ActiveMenuItem[] fileMenuItem =
 *       new ActiveMenuItem[fileOptions.length];
 *     for (int i = 0; i < fileOptions.length; i++) {
 *       fileMenuItem[i] = new ActiveMenuItem (null, event[0].out (), fileOptions[i]);
 *       fileMenu.add (fileMenuItem[i]);
 *     }
 * 
 *     final ActiveCheckboxMenuItem[] langCheckboxMenuItem =
 *       new ActiveCheckboxMenuItem[langOptions.length];
 *     for (int i = 0; i < langOptions.length; i++) {
 *       langCheckboxMenuItem[i] =
 *         new ActiveCheckboxMenuItem (null, event[1].out (), langOptions[i]);
 *       langMenu.add (langCheckboxMenuItem[i]);
 *     }
 * 
 *     frame.setSize (300, 200);
 *     frame.setBackground (Color.green);
 *     frame.setVisible (true);
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         activeClosingFrame,
 *         new Parallel (fileMenuItem),
 *         new Parallel (langCheckboxMenuItem),
 *         new CSProcess () {
 *           public void run () {
 *             boolean running = true;
 *             while (running) {
 *               final String s = (String) event[0].in ().read ();
 *               System.out.println ("File ==> `" + s + "' selected ...");
 *               running = (s != fileOptions[fileOptions.length - 1]);
 *             }
 *             frame.setVisible (false);
 *             System.exit (0);
 *           }
 *         },
 *         new CSProcess () {
 *           public void run () {
 *             while (true) {
 *               final ItemEvent e = (ItemEvent) event[1].in ().read ();
 *               final String item = (String) e.getItem ();
 *               System.out.print ("Language ==> `" + item);
 *               if (e.getStateChange () == ItemEvent.SELECTED) {
 *                 System.out.println ("' selected ...");
 *               } else {
 *                 System.out.println ("' deselected ...");
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
 * @see java.awt.CheckboxMenuItem
 * @see java.awt.event.ComponentEvent
 * @see java.awt.event.FocusEvent
 * @see java.awt.event.KeyEvent
 * @see java.awt.event.MouseEvent
 * @see org.jcsp.util.OverWriteOldestBuffer
 *
 * @author P.D. Austin and P.H. Welch
 */

public class ActiveCheckboxMenuItem extends CheckboxMenuItem implements CSProcess
{
   /**
    * The Vector construct containing the handlers.
    */
   private Vector vec = new Vector();
   
   /**
    * The Configurer.
    */
   // private Configurer configurer = null;
   
   /**
    * The channel from which configuration messages arrive.
    */
   private ChannelInput configure;
   
   /**
    * Constructs a new unchecked <TT>ActiveCheckboxMenuItem</TT> with no initial label
    * and no configuration or event channels.
    *
    */
   public ActiveCheckboxMenuItem()
   {
      this(null, null, "", false);
   }
   
   /**
    * Constructs a new unchecked <TT>ActiveCheckboxMenuItem</TT>
    * with no configuration or event channels.
    *
    * @param s the initial label displayed on the checkboxMenuItem.
    */
   public ActiveCheckboxMenuItem(String s)
   {
      this(null, null, s, false);
   }
   
   /**
    * Constructs a new <TT>ActiveCheckboxMenuItem</TT>
    * with no configuration or event channels.
    *
    * @param s the initial label displayed on the checkboxMenuItem.
    * @param state the initial state of the checkboxMenuItem.
    */
   public ActiveCheckboxMenuItem(String s, boolean state)
   {
      this(null, null, s, state);
   }
   /**
    * Constructs a new unchecked <TT>ActiveCheckboxMenuItem</TT> with no initial label.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the current label will be output when the checkboxMenuItem is selected
    * -- can be null if no notification is required.
    */
   public ActiveCheckboxMenuItem(ChannelInput configure, ChannelOutput event)
   {
      this(configure, event, "", false);
   }
   
   /**
    * Constructs a new unchecked <TT>ActiveCheckboxMenuItem</TT>.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the current label will be output when the checkboxMenuItem is selected
    * -- can be null if no notification is required.
    * @param s the initial label displayed on the checkboxMenuItem.
    */
   public ActiveCheckboxMenuItem(ChannelInput configure, ChannelOutput event, String s)
   {
      this(configure, event, s, false);
   }
   
   /**
    * Constructs a new <TT>ActiveCheckboxMenuItem</TT>.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the current label will be output when the checkboxMenuItem is selected
    * -- can be null if no notification is required.
    * @param s the initial label displayed on the checkboxMenuItem.
    * @param state the initial state of the checkboxMenuItem.
    */
   public ActiveCheckboxMenuItem(ChannelInput configure, ChannelOutput event, String s, boolean state)
   {
      super(s, state);
      if (event != null)
      {
         ItemEventHandler handler = new ItemEventHandler(event);
         addItemListener(handler);
         vec.addElement(handler);
      }
      this.configure = configure;
      
   }
   
   /**
    * Sets the configuration channel for this <TT>ActiveCheckboxMenuItem</TT>.
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
    * an <TT>ActionEvent</TT> has occurred. <I>This should be used
    * instead of registering a ActionListener with the component.</I>  It is
    * possible to add more than one channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
    *
    * @param actionEvent the channel down which to send ActionEvents.
    */
   public void addActionEventChannel(ChannelOutput actionEvent)
   {
      if (actionEvent != null)
      {
         ActionEventHandler handler = new ActionEventHandler(actionEvent);
         addActionListener(handler);
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
       * @param checkboxMenuItem the CheckboxMenuItem being configured.
       */
      public void configure(final CheckboxMenuItem checkboxMenuItem);
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
               setLabel((String) message);
            else if (message instanceof MenuShortcut)
               setShortcut((MenuShortcut) message);
            else if (message instanceof Character)
            {
               switch (((Character) message).charValue())
               {
                  case 'S':
                     setState(true);
                     break;
                  case 's':
                     setState(false);
                     break;
               }
            }
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
