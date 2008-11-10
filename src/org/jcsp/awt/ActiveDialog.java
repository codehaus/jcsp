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
 * {@link java.awt.Dialog <TT>java.awt.Dialog</TT>}
 * with a channel interface.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/ActiveDialog1.gif"></p>
 * <H2>Description</H2>
 * <TT>ActiveDialog</TT> is a process extension of <TT>java.awt.Dialog</TT>
 * with channels for run-time configuration and event notification.  The event channels
 * should be connected to one or more application-specific server processes (instead
 * of registering a passive object as a <I>Listener</I> to this component).
 * <P>
 * All channels are optional.  The <TT>configure</TT> and <TT>event</TT> channels are
 * settable from a constructor.
 * The <TT>event</TT> channel delivers a <TT>WindowEvent</TT> whenever one is generated
 * on the <TT>ActiveDialog</TT>.
 * Other event channels can be added to notify the occurrence of any other events
 * the component generates (by calling the appropriate
 * <TT>add</TT><I>XXX</I><TT>EventChannel</TT> method <I>before</I> the process is run).
 * Messages can be sent down the <TT>configure</TT> channel at any time to configure
 * the component.  See the <A HREF="#Protocols">table below</A> for details.
 * <P>
 * All channels are managed by independent internal handler processes.  It is, therefore,
 * safe for a serial application process both to service an event channel and configure
 * the component &ndash; no deadlock can occur.
 * <P>
 * <I>IMPORTANT: it is essential that event channels from this process are
 * always serviced &ndash; otherwise the Java Event Thread will be blocked and the GUI
 * will stop responding.  A simple way to guarantee this is to use channels
 * configured with overwriting buffers.
 * For example:</I>
 * <PRE>
 *   final One2OneChannel myMouseEvent = Channel.one2one (new OverWriteOldestBuffer (n));
 * 
 *   final ActiveDialog myDialog = new ActiveDialog ();
 *   myDialog.addMouseEventChannel (myMouseEvent.out ());
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
 *           the dialog is made visible</LI>
 *         <LI>If this is the <TT>Boolean.FALSE</TT> object,
 *            the dialog is made invisible</LI>
 *         <LI>Other <TT>Boolean</TT> objects are ignored</LI>
 *       </OL>
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TD>ActiveDialog.Configure</TD>
 *     <TD>Invoke the user-defined <TT>Configure.configure</TT> method on the dialog.</TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>event</TH>
 *     <TD>WindowEvent</TD>
 *     <TD>The <TT>WindowEvent</TT> generated by the component</TD>
 *   </TR>
 *   <TR>
 *     <TH>containerEvent</TH>
 *     <TD>ContainerEvent</TD>
 *     <TD>See the {@link #addContainerEventChannel
 *         <TT>addContainerEventChannel</TT>} method.</TD>
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
 * public class ActiveDialogExample {
 * 
 *   public static void main (String argv[]) {
 * 
 *     final Frame root = new Frame ();
 * 
 *     final One2OneChannel event = Channel.one2one (new OverWriteOldestBuffer (10));
 * 
 *     final ActiveDialog dialog = new ActiveDialog (null, event.out (), root, "ActiveDialog Example");
 * 
 *     dialog.setSize (300, 200);
 *     dialog.setVisible (true);
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         dialog,
 *         new CSProcess () {
 *           public void run () {
 *             while (true) {
 *               WindowEvent w = (WindowEvent) event.in ().read ();
 *               System.out.println (w);
 *             }
 *           }
 *         }
 *       }
 *     ).run ();
 *   }
 * 
 * }
 * </PRE>
 *
 * @see java.awt.Dialog
 * @see java.awt.event.ContainerEvent
 * @see java.awt.event.ComponentEvent
 * @see java.awt.event.FocusEvent
 * @see java.awt.event.KeyEvent
 * @see java.awt.event.MouseEvent
 * @see org.jcsp.util.OverWriteOldestBuffer
 *
 * @author P.D. Austin and P.H. Welch
 */

public class ActiveDialog extends Dialog implements CSProcess
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
    * Constructs a new <I>non-modal</I> <TT>ActiveDialog</TT> with a blank title
    * and no configuration or event channels.
    *
    * @param parent the parent frame for the dialog.
    */
   public ActiveDialog(Frame parent)
   {
      this(null, null, parent, "", false);
   }
   
   /**
    * Constructs a new <TT>ActiveDialog</TT> with a blank title
    * and no configuration or event channels.
    *
    * @param parent the parent frame for the dialog.
    * @param modal if true, dialog blocks input to the parent window when shown.
    */
   public ActiveDialog(Frame parent, boolean modal)
   {
      this(null, null, parent, "", modal);
   }
   
   /**
    * Constructs a new <I>non-modal</I> <TT>ActiveDialog</TT>
    * with no configuration or event channels.
    *
    * @param parent the parent frame for the dialog.
    * @param title the title of the dialog.
    */
   public ActiveDialog(Frame parent, String title)
   {
      this(null, null, parent, title, false);
   }
   
   /**
    * Constructs a new <TT>ActiveDialog</TT>
    * with no configuration or event channels.
    *
    * @param parent the parent frame for the dialog.
    * @param title the title of the dialog.
    * @param modal if true, dialog blocks input to the parent window when shown.
    */
   public ActiveDialog(Frame parent, String title, boolean modal)
   {
      this(null, null, parent, title, false);
   }
   
   /**
    * Constructs a new <I>non-modal</I> <TT>ActiveDialog</TT> with a blank title.
    *
    * @param configure the channel for configuration events
    * (can be null if no configuration is required).
    * @param event the WindowEvent will be output whenever it occurs
    * (can be null if no notification is required).
    * @param parent the parent frame for the dialog.
    */
   public ActiveDialog(ChannelInput configure, ChannelOutput event, Frame parent)
   {
      this(configure, event, parent, "", false);
   }
   
   /**
    * Constructs a new <TT>ActiveDialog</TT> with a blank title.
    *
    * @param configure the channel for configuration events
    * (can be null if no configuration is required).
    * @param event the WindowEvent will be output whenever it occurs
    * (can be null if no notification is required).
    * @param parent the parent frame for the dialog.
    * @param modal if true, dialog blocks input to the parent window when shown.
    */
   public ActiveDialog(ChannelInput configure, ChannelOutput event, Frame parent, boolean modal)
   {
      this(configure, event, parent, "", modal);
   }
   
   /**
    * Constructs a new <I>non-modal</I> <TT>ActiveDialog</TT>.
    *
    * @param configure the channel for configuration events
    * (can be null if no configuration is required).
    * @param event the WindowEvent will be output whenever it occurs
    * (can be null if no notification is required).
    * @param parent the parent frame for the dialog.
    * @param title the title of the dialog.
    */
   public ActiveDialog(ChannelInput configure, ChannelOutput event, Frame parent, String title)
   {
      this(configure, event, parent, title, false);
   }
   
   /**
    * Constructs a new <TT>ActiveDialog</TT>.
    *
    * @param configure the channel for configuration events
    * (can be null if no configuration is required).
    * @param event the WindowEvent will be output whenever it occurs
    * (can be null if no notification is required).
    * @param parent the parent frame for the dialog.
    * @param title the title of the dialog.
    * @param modal if true, dialog blocks input to the parent window when shown.
    */
   public ActiveDialog(ChannelInput configure, ChannelOutput event, Frame parent, String title, boolean modal)
   {
      super(parent, title, modal);
      if (event != null)
      {
         WindowEventHandler handler = new WindowEventHandler(event);
         addWindowListener(handler);
         vec.addElement(handler);
      }
      this.configure = configure;
   }
   
   /**
    * Sets the configuration channel for this <TT>ActiveDialog</TT>.
    * This method overwrites any configuration channel set in the constructor.
    *
    * @param configure the channel for configuration events
    * (can be null if no configuration is required).
    */
   public void setConfigureChannel(ChannelInput configure)
   {
      this.configure = configure;
   }
   
   /**
    * Add a new channel to this component that will be used to notify that
    * a <TT>ContainerEvent</TT> has occurred. <I>This should be used
    * instead of registering a ContainerListener with the component.</I> It is
    * possible to add more than one Channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
    *
    * @param containerEvent the channel down which to send ContainerEvents.
    */
   public void addContainerEventChannel(ChannelOutput containerEvent)
   {
      if (containerEvent != null)
      {
         ContainerEventHandler handler = new ContainerEventHandler(containerEvent);
         addContainerListener(handler);
         vec.addElement(handler);
      }
   }
   
   /**
    * Add a new channel to this component that will be used to notify that
    * a <TT>ComponentEvent</TT> has occurred. <I>This should be used
    * instead of registering a ComponentListener with the component.</I> It is
    * possible to add more than one Channel by calling this method multiple times
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
    * possible to add more than one Channel by calling this method multiple times
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
    * possible to add more than one Channel by calling this method multiple times
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
    * possible to add more than one Channel by calling this method multiple times
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
    * possible to add more than one Channel by calling this method multiple times
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
       * @param dialog the Dialog being configured.
       */
      public void configure(final Dialog dialog);
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
            if (message instanceof Boolean)
            {
               if (message == Boolean.TRUE)
                  setVisible(true);
               else if (message == Boolean.FALSE)
                  setVisible(false);
            }
            else if (message instanceof Configure)
               ((Configure) message).configure(this);
         }
      }
   }
}
