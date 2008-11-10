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
 * {@link java.awt.MenuItem <TT>java.awt.MenuItem</TT>}
 * with a channel interface.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/ActiveMenuItem1.gif"></p>
 * <P>
 * <H2>Description</H2>
 * <TT>ActiveMenuItem</TT> is a process extension of <TT>java.awt.MenuItem</TT>
 * with channels for run-time configuration and event notification.  The event channel
 * should be connected to an application-specific server process (instead
 * of registering a passive object as a <I>Listener</I> to this component).
 * <P>
 * The <TT>configure</TT> and <TT>event</TT> channels are
 * settable from a constructor.
 * The <TT>event</TT> channel delivers the current label on the
 * <TT>ActiveMenuItem</TT> whenever it is selected.
 * Messages can be sent down the <TT>configure</TT> channel at any time to configure
 * the component.  See the <A HREF="#Protocols">table below</A> for details.
 * <P>
 * All channels are managed by independent internal handler processes.  It is, therefore,
 * safe for a serial application process both to service the event channel and configure
 * the component -- no deadlock can occur.
 * <P>
 * <I>IMPORTANT: it is essential that a (non-null) event channel from this process is
 * always serviced -- otherwise the Java Event Thread will be blocked and the GUI
 * will stop responding.  A simple way to guarantee this is to use channels
 * configured with overwriting buffers.
 * For example:</I>
 * <PRE>
 *   final One2OneChannel myMenuItemEvent = Channel.one2one (new OverWriteOldestBuffer (n));
 * 
 *   final ActiveMenuItem myMenuItem =
 *     new ActiveMenuItem (null, myMenuItemEvent.out (), "Choose Me");
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
 *     <TD>Change the label on the <TT>ActiveMenuItem</TT> to the value of the <TT>String</TT></TD>
 *   </TR>
 *   <TR>
 *     <TD>java.awt.MenuShortcut</TD>
 *     <TD>Sets the <TT>MenuShortcut</TT> for the <TT>ActiveMenuItem</TT></TD>
 *   </TR>
 *   <TR>
 *     <TD>Boolean</TD>
 *     <TD>
 *       <OL>
 *         <LI>If this is the <TT>Boolean.TRUE</TT> object,
 *           the menuItem is made active</LI>
 *         <LI>If this is the <TT>Boolean.FALSE</TT> object,
 *            the menuItem is made inactive</LI>
 *         <LI>Other <TT>Boolean</TT> objects are ignored</LI>
 *       </OL>
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TD>ActiveMenuItem.Configure</TD>
 *     <TD>Invoke the user-defined <TT>Configure.configure</TT> method on the menuItem.</TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>event</TH>
 *     <TD>String</TD>
 *     <TD>The label on the <TT>ActiveMenuItem</TT> (when the item is selected)</TD>
 *   </TR>
 * </TABLE>
 * </CENTER>
 * <H2>Example</H2>
 * <PRE>
 * import java.awt.*;
 * import org.jcsp.lang.*;
 * import org.jcsp.util.*;
 * import org.jcsp.awt.*;
 * 
 * public class ActiveMenuItemExample {
 * 
 *   public static void main (String argv[]) {
 * 
 *     final ActiveClosingFrame activeClosingFrame =
 *       new ActiveClosingFrame ("ActiveMenuItem Example");
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
 *     final ActiveMenuItem[] fileMenuItem = new ActiveMenuItem[fileOptions.length];
 *     for (int i = 0; i < fileOptions.length; i++) {
 *       fileMenuItem[i] = new ActiveMenuItem (null, event[0].out (), fileOptions[i]);
 *       fileMenu.add (fileMenuItem[i]);
 *     }
 * 
 *     final ActiveMenuItem[] langMenuItem = new ActiveMenuItem[langOptions.length];
 *     for (int i = 0; i < langOptions.length; i++) {
 *       langMenuItem[i] = new ActiveMenuItem (null, event[1].out (), langOptions[i]);
 *       langMenu.add (langMenuItem[i]);
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
 *         new Parallel (langMenuItem),
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
 *               final String s = (String) event[1].in ().read ();
 *               System.out.println ("Language ==> `" + s + "' selected ...");
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
 * @see java.awt.MenuItem
 * @see java.awt.event.ComponentEvent
 * @see java.awt.event.FocusEvent
 * @see java.awt.event.KeyEvent
 * @see java.awt.event.MouseEvent
 * @see org.jcsp.util.OverWriteOldestBuffer
 *
 * @author P.D. Austin and P.H. Welch
 */

public class ActiveMenuItem extends MenuItem implements CSProcess
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
    * Constructs a new <TT>ActiveMenuItem</TT> with no label and no shortcut
    * and no configuration or event channels.
    *
    */
   public ActiveMenuItem()
   {
      this(null, null, "", null);
   }
   
   /**
    * Constructs a new <TT>ActiveMenuItem</TT> with no shortcut
    * and no configuration or event channels.
    *
    * @param s the initial label displayed on the menuItem.
    */
   public ActiveMenuItem(String s)
   {
      this(null, null, s, null);
   }
   
   /**
    * Constructs a new <TT>ActiveMenuItem</TT>
    * with no configuration or event channels.
    *
    * @param s the initial label displayed on the menuItem.
    * @param ms the MenuShortcut for the menuItem.
    */
   public ActiveMenuItem(String s, MenuShortcut ms)
   {
      this(null, null, s, ms);
   }
   /**
    * Constructs a new <TT>ActiveMenuItem</TT> with no label and no shortcut.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the current label will be output when the menuItem is selected
    * -- can be null if no notification is required.
    */
   public ActiveMenuItem(ChannelInput configure, ChannelOutput event)
   {
      this(configure, event, "", null);
   }
   
   /**
    * Constructs a new <TT>ActiveMenuItem</TT> with no shortcut.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the current label will be output when the menuItem is selected
    * -- can be null if no notification is required.
    * @param s the initial label displayed on the menuItem.
    */
   public ActiveMenuItem(ChannelInput configure, ChannelOutput event, String s)
   {
      this(configure, event, s, null);
   }
   
   /**
    * Constructs a new <TT>ActiveMenuItem</TT>.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param event the current label will be output when the menuItem is selected
    * -- can be null if no notification is required.
    * @param s the initial label displayed on the menuItem.
    * @param ms the MenuShortcut for the menuItem.
    */
   public ActiveMenuItem(ChannelInput configure, ChannelOutput event, String s, MenuShortcut ms)
   {
      super(s, ms);
      if (event != null)
      {
         ActionEventHandler handler = new ActionEventHandler(event);
         addActionListener(handler);
         vec.addElement(handler);
      }
      this.configure = configure;
   }
   
   /**
    * Sets the configuration channel for this <TT>ActiveMenuItem</TT>.
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
    * This enables general configuration of this component.  Any object implementing
    * this interface and sent down the <TT>configure</TT> channel to this component will have its
    * <TT>configure</TT> method invoked on this component.
    * <P>
    * For an example, see {@link ActiveApplet.Configure}.
    */
   static public interface Configure
   {
      /**
       * @param menuItem the MenuItem being configured.
       */
      public void configure(final MenuItem menuItem);
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
