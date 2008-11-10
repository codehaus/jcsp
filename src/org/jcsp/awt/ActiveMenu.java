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
 * {@link java.awt.Menu <TT>java.awt.Menu</TT>}
 * with a channel interface.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/ActiveMenu1.gif"></p>
 * <P>
 * <H2>Description</H2>
 * <TT>ActiveMenu</TT> is a process extension of <TT>java.awt.Menu</TT>
 * with channels for run-time configuration and event notification.  The event channel
 * should be connected to an application-specific server process (instead
 * of registering a passive object as a <I>Listener</I> to this component).
 * <P>
 * The <TT>configure</TT> and <TT>event</TT> channels are
 * settable from a constructor.
 * The <TT>event</TT> channel delivers the command string associated with this
 * <TT>ActiveMenu</TT> whenever it is selected.
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
 *   final One2OneChannel myMenuEvent = Channel.one2one (new OverWriteOldestBuffer (n));
 * 
 *   final ActiveMenu myMenu =
 *     new ActiveMenu (null, myMenuEvent.out (), "Look at this");
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
 *     <TH ROWSPAN="5">configure</TH>
 *     <TD>String</TD>
 *     <TD>Change the label on the <TT>ActiveMenu</TT> to the value of the <TT>String</TT></TD>
 *   </TR>
 *   <TR>
 *     <TD>java.awt.MenuShortcut</TD>
 *     <TD>Sets the <TT>MenuShortcut</TT> for the <TT>ActiveMenu</TT></TD>
 *   </TR>
 *   <TR>
 *     <TD>Integer</TD>
 *     <TD>
 * Inserts a separator at the specified position.
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TD>Boolean</TD>
 *     <TD>
 *       <OL>
 *         <LI>If this is the <TT>Boolean.TRUE</TT> object,
 *           the menu is made active</LI>
 *         <LI>If this is the <TT>Boolean.FALSE</TT> object,
 *            the menu is made inactive</LI>
 *         <LI>Other <TT>Boolean</TT> objects are ignored</LI>
 *       </OL>
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TD>ActiveMenu.Configure</TD>
 *     <TD>Invoke the user-defined <TT>Configure.configure</TT> method on the activeMenu.</TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>event</TH>
 *     <TD>String</TD>
 *     <TD>The command for the <TT>ActiveMenu</TT> (when the menu is selected)</TD>
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
 * public class ActiveMenuExample {
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
 *     menuBar.add (fileMenu);
 * 
 *     final String[] fileOptions = {"Hello World", "Rocket Science", "CSP",
 *                                   "Monitors", "Ignore Me", "Goodbye World"};
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
 *     fileMenu.addSeparator ();
 * 
 *     final Any2OneChannel langConfigure = Channel.any2one ();
 *     final ActiveMenu langMenu = new ActiveMenu (langConfigure.in (), null, "Language");
 *     fileMenu.add (langMenu);  // set up the active langMenu as a sub-menu
 * 
 *     final String[] langOptions = {"occam-pi", "Java", "Smalltalk", "Algol-60",
 *                                   "Pascal", "Haskell", "SML", "Lisp"};
 * 
 *     final ActiveCheckboxMenuItem[] langCheckboxMenuItem =
 *       new ActiveCheckboxMenuItem[langOptions.length];
 *     for (int i = 0; i < langOptions.length; i++) {
 *       langCheckboxMenuItem[i] =
 *         new ActiveCheckboxMenuItem (null, event[1].out (), langOptions[i]);
 *       langMenu.add (langCheckboxMenuItem[i]);
 *     }
 * 
 *     frame.setSize (700, 350);
 *     frame.setBackground (Color.green);
 *     frame.setVisible (true);
 * 
 *     new Parallel (
 *       new CSProcess[] {     // don't forget to include all active processes
 *         langMenu,
 *         activeClosingFrame,
 *         new Parallel (fileMenuItem),
 *         new Parallel (langCheckboxMenuItem),
 *         new CSProcess () {
 *           public void run () {
 *             boolean running = true;
 *             while (running) {
 *               final String s = (String) event[0].in ().read ();
 *               System.out.println ("File ==> `" + s + "' selected ...");
 *               if (s == fileOptions[0]) {
 *                 langConfigure.out ().write (Boolean.TRUE);
 *                 System.out.println ("`Language' enabled ...");
 *               }
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
 *                 if (item == langOptions[0]) {
 *                   langConfigure.out ().write (Boolean.FALSE);
 *                   System.out.println ("`Language' disabled ...");
 *                 }
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
 * @see java.awt.Menu
 * @see java.awt.event.ItemEvent
 * @see java.awt.event.ComponentEvent
 * @see java.awt.event.FocusEvent
 * @see java.awt.event.KeyEvent
 * @see java.awt.event.MouseEvent
 * @see org.jcsp.util.OverWriteOldestBuffer
 *
 * @author P.D. Austin and P.H. Welch
 */

public class ActiveMenu extends Menu implements CSProcess
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
    * Constructs an <TT>ActiveMenu</TT> with no initial label and no configuration
    * or event channels.
    */
   public ActiveMenu()
   {
      this(null, null, "", false);
   }
   
   /**
    * Constructs an <TT>ActiveMenu</TT> with an initial label,
    * but with no configuration or event channels.
    *
    * @param label the label on the menu.
    */
   public ActiveMenu(String label)
   {
      this(null, null, label, false);
   }
   
   /**
    * Constructs an <TT>ActiveMenu</TT> with an initial label
    * and a tear-off option, but with no configuration
    * or event channels.
    *
    * @param label the label on the menu.
    * @param tearOff if true, this is a <I>tear-off</I> menu.
    */
   public ActiveMenu(String label, boolean tearOff)
   {
      this(null, null, label, tearOff);
   }
   
   /**
    * Constructs an <TT>ActiveMenu</TT> with configuration and event channels,
    * but with no initial label.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    */
   public ActiveMenu(ChannelInput configure, ChannelOutput event)
   {
      this(configure, event, "", false);
   }
   
   /**
    * Constructs an <TT>ActiveMenu</TT> with configuration and event channels
    * and an initial label.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param label the label on the menu.
    */
   public ActiveMenu(ChannelInput configure, ChannelOutput event, String label)
   {
      this(configure, event, label, false);
   }
   
   /**
    * Constructs an <TT>ActiveMenu</TT> with configuration and event channels,
    * an initial label and a tear-off option.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param label the label on the menu.
    * @param tearOff if true, this is a <I>tear-off</I> menu.
    */
   public ActiveMenu(ChannelInput configure, ChannelOutput event, String label, boolean tearOff)
   {
      super(label, tearOff);
      if (event != null)
      {
         ActionEventHandler handler = new ActionEventHandler(event);
         addActionListener(handler);
         vec.addElement(handler);
      }
      this.configure = configure;
   }
   
   /**
    * Sets the configuration channel for this <TT>ActiveMenu</TT>.
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
       * @param menu the Menu being configured.
       */
      public void configure(final Menu menu);
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
            if (message instanceof Integer)
               insertSeparator(((Integer) message).intValue());
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
