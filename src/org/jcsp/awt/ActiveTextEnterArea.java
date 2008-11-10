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

import org.jcsp.lang.*;
import org.jcsp.util.*;
import org.jcsp.plugNplay.*;
import java.awt.event.*;

/**
 * A specialisation of {@link ActiveTextArea} that writes text to
 * the <TT>event</TT> channel only when <I>ESCAPE</I> is pressed.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/ActiveTextEnterArea1.gif"></p>
 * <P>
 * <H2>Description</H2>
 * <TT>ActiveTextEnterArea</TT> is a process containing an {@link ActiveTextArea <TT>ActiveTextArea</TT>},
 * configured with channels for event notification and configuration.  The event channels
 * should be connected to one or more application-specific server processes (instead
 * of registering a passive object as a <I>Listener</I> to this component).
 * <P>
 * All channels are optional.  The <TT>configure</TT> and <TT>event</TT> channels are
 * settable from a constructor.
 * <P>
 * The difference between this class and <TT>ActiveTextArea</TT> is that text
 * is only written to the <TT>event</TT> channel when <I>ESCAPE</I> is pressed on
 * the text area (not on every keystroke).  When this happens, the text area
 * is temporarilly disabled for (by default) half a second; this is to give
 * feedback to the user.  The disable period can be changed by calling the
 * <TT>setDisableTime</TT> method <I>before</I> the process is run.
 * <P>
 * The internal <TT>ActiveTextArea</TT> can be extracted with the <TT>getActiveTextArea</TT> method.
 * Channels can then be added to notify the occurrence of any other type of <TT>Event</TT>
 * the <TT>ActiveFrame</TT> generates.  This is done by calling its appropriate
 * <TT>add</TT><I>XXX</I><TT>EventChannel</TT> method (<I>before</I> the process
 * is run).  As many channels can be added as are needed.
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
 *   final One2OneChannel myTextAreaEvent = Channel.one2one (new OverWriteOldestBuffer (n));
 * 
 *   final ActiveTextEnterArea myTextEnterArea =
 *     new ActiveTextEnterArea (null, myTextAreaEvent.out (), "Edit Me", 5, 20);
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
 *     <TD>Append the string to the text in this <TT>ActiveTextEnterArea</TT></TD>
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
 *     <TD><I>otherwise</I></TD>
 *     <TD>Append the <TT>toString</TT> form of the object to the text in this <TT>ActiveTextArea</TT>.</TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>event</TH>
 *     <TD>String</TD>
 *     <TD>The text in the <TT>ActiveTextEnterArea</TT> (whenever <I>ESCAPE</I> is pressed)</TD>
 *   </TR>
 *   <TR>
 *     <TH>componentEvent</TH>
 *     <TD>ComponentEvent</TD>
 *     <TD>See the
 *         {@link #getActiveTextArea <TT>getActiveTextArea</TT>}.{@link ActiveTextArea#addComponentEventChannel
 *         <TT>addComponentEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>focusEvent</TH>
 *     <TD>FocusEvent</TD>
 *     <TD>See the
 *         {@link #getActiveTextArea <TT>getActiveTextArea</TT>}.{@link ActiveTextArea#addFocusEventChannel
 *         <TT>addFocusEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>keyEvent</TH>
 *     <TD>KeyEvent</TD>
 *     <TD>See the
 *         {@link #getActiveTextArea <TT>getActiveTextArea</TT>}.{@link ActiveTextArea#addKeyEventChannel
 *         <TT>addKeyEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>mouseEvent</TH>
 *     <TD>MouseEvent</TD>
 *     <TD>See the
 *         {@link #getActiveTextArea <TT>getActiveTextArea</TT>}.{@link ActiveTextArea#addMouseEventChannel
 *         <TT>addMouseEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>mouseMotionEvent</TH>
 *     <TD>MouseEvent</TD>
 *     <TD>See the
 *         {@link #getActiveTextArea <TT>getActiveTextArea</TT>}.{@link ActiveTextArea#addMouseMotionEventChannel
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
 * public class ActiveTextEnterAreaExample {
 * 
 *   public static void main (String argv[]) {
 * 
 *     final ActiveClosingFrame frame =
 *       new ActiveClosingFrame ("ActiveTextEnterArea Example");
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
 *     final ActiveTextEnterArea[] activeText =
 *       new ActiveTextEnterArea[string.length];
 * 
 *     for (int i = 0; i < string.length; i++) {
 *       activeText[i] = new ActiveTextEnterArea (null, event.out (), string[i], 5, 40);
 *     }
 * 
 *     Panel panel = new Panel (new GridLayout (string.length/2, 2));
 *     for (int i = 0; i < string.length; i++) {
 *       panel.add (activeText[i].getActiveTextArea ());
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
 * @see org.jcsp.awt.ActiveTextArea
 * @see java.awt.TextArea
 * @see java.awt.event.ComponentEvent
 * @see java.awt.event.FocusEvent
 * @see java.awt.event.KeyEvent
 * @see java.awt.event.MouseEvent
 * @see org.jcsp.util.OverWriteOldestBuffer
 *
 * @author P.D. Austin and P.H. Welch
 */

public class ActiveTextEnterArea implements CSProcess
{
   private AltingChannelInput configure;
   private ChannelOutput event;
   private String s;
   private int rows;
   private int columns;
   private int scrollbars;
   
   private long disableTime = 500;
   
   private ActiveTextArea area;
   
   private One2OneChannel keyEvent = Channel.one2one(new OverWriteOldestBuffer(10));
   private One2OneChannel textEvent = Channel.one2one(new OverWriteOldestBuffer(10));
   
   private One2OneChannel configureA = Channel.one2one();
   private One2OneChannel configureB = Channel.one2one();
   
   
   /**
    * Constructs a new <TT>ActiveTextEnterArea</TT> with scrollbars, but with no configuration
    * or event channels or initial text or size.
    */
   public ActiveTextEnterArea()
   {
      this(null, null, "", 0, 0, java.awt.TextArea.SCROLLBARS_BOTH);
   }
   
   /**
    * Constructs a new <TT>ActiveTextEnterArea</TT> with scrollbars and initial text,
    * but with no configuration or event channels or initial size.
    *
    * @param s the initial text displayed in the area.
    */
   public ActiveTextEnterArea(String s)
   {
      this(null, null, s, 0, 0, java.awt.TextArea.SCROLLBARS_BOTH);
   }
   
   /**
    * Constructs a new <TT>ActiveTextEnterArea</TT> with scrollbars and initial text and size,
    * but with no configuration or event channels.
    *
    * @param s the initial text displayed in the area.
    * @param rows the rows of the area.
    * @param columns the columns of the area.
    */
   public ActiveTextEnterArea(String s, int rows, int columns)
   {
      this(null, null, s, rows, columns, java.awt.TextArea.SCROLLBARS_BOTH);
   }
   
   /**
    * Constructs a new <TT>ActiveTextEnterArea</TT> with user-defined scrollbars and initial text and size,
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
   public ActiveTextEnterArea(String s, int rows, int columns, int scrollbars)
   {
      this(null, null, s, rows, columns, scrollbars);
   }
   
   /**
    * Constructs a new <TT>ActiveTextEnterArea</TT> with configuration and event channels and scrollbars,
    * but with no initial text or size.
    *
    * @param configure the AltingChannelInput for configuration events
    * -- can be null if no configuration is required.
    * @param event the current text will be output when the text area is changed
    * -- can be null if no notification is required.
    */
   public ActiveTextEnterArea(AltingChannelInput configure, ChannelOutput event)
   {
      this(configure, event, "", 0, 0, java.awt.TextArea.SCROLLBARS_BOTH);
   }
   
   /**
    * Constructs a new <TT>ActiveTextEnterArea</TT> with configuration and event channels
    * and scrollbars and initial text, but with no initial size.
    *
    * @param configure the AltingChannelInput for configuration events
    * -- can be null if no configuration is required.
    * @param event the current text will be output when the text area is changed
    * -- can be null if no notification is required.
    * @param s the initial text displayed in the area.
    */
   public ActiveTextEnterArea(AltingChannelInput configure, ChannelOutput event, String s)
   {
      this(configure, event, s, 0, 0, java.awt.TextArea.SCROLLBARS_BOTH);
   }
   
   /**
    * Constructs a new <TT>ActiveTextEnterArea</TT> with configuration and event channels
    * and scrollbars and initial text and size.
    *
    * @param configure the AltingChannelInput for configuration events
    * -- can be null if no configuration is required.
    * @param event the current text will be output when the text area is changed
    * -- can be null if no notification is required.
    * @param s the initial text displayed in the area.
    * @param rows the rows of the area.
    * @param columns the columns of the area.
    */
   public ActiveTextEnterArea(AltingChannelInput configure, ChannelOutput event,
           String s, int rows, int columns)
   {
      this(configure, event, s, rows, columns, java.awt.TextArea.SCROLLBARS_BOTH);
   }
   
   /**
    * Constructs a new <TT>ActiveTextEnterArea</TT> with configuration and event channels
    * and user-defined scrollbars and initial text and size.
    *
    * @param configure the AltingChannelInput for configuration events
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
   public ActiveTextEnterArea(AltingChannelInput configure, ChannelOutput event,
           String s, int rows, int columns, int scrollbars)
   {
      if (configure == null)
         this.configure = Channel.one2one().in();  // the Plex2 process must have non-null channels
      else
         this.configure = configure;
      this.event = event;
      this.s = s;
      this.rows = rows;
      this.columns = columns;
      this.scrollbars = scrollbars;
      this.area = new ActiveTextArea(configureB.in(), textEvent.out(), s, rows, columns, scrollbars);
   }
   
   /**
    * Sets the configuration channel for this <TT>ActiveTextEnterArea</TT>.
    * This method overwrites any configuration channel set in the constructor.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    */
   public void setConfigureChannel(AltingChannelInput configure)
   {
      if (configure != null)
         this.configure = configure; // the Plex2 process must have non-null channels
   }
   
   /**
    * This is used to set the time during which the text area is disabled
    * after an <I>ESCAPE</I> has been entered.
    * <P>
    * <I>NOTE: This may only be called before this process is run.</I>
    *
    * @param disableTime the disable time after an <I>ESCAPE</I>.
    */
   public void setDisableTime(final long disableTime)
   {
      this.disableTime = disableTime;
   }
   
   /**
    * This is used to get the <TT>ActiveTextArea</TT> within this component
    * so that it can be configured (using {@link java.awt.TextArea} or
    * {@link org.jcsp.awt.ActiveTextArea} methods) or added to some container.
    * For example, event channels can be added by invoking
    * <TT>getActiveTextArea().add</TT><I>XXX</I><TT>EventChannel(...)</TT>.
    * <P>
    * <I>NOTE: such configuration must be finished before this process is run.</I>
    * <P>
    * <I>NOTE: do not use this method to set a configure channel - use an appropriate
    * constructor or {@link #setConfigureChannel setConfigureChannel}.</I>
    *
    * @return the <TT>ActiveTextArea</TT> within this component.
    */
   public ActiveTextArea getActiveTextArea()
   {
      return area;
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      area.addKeyEventChannel(keyEvent.out());
      new Parallel(new CSProcess[] 
      {
         new Plex2(configure, configureA.in(), configureB.out()),
         area,
         new CSProcess()
         {
            public void run()
            {
               CSTimer tim = new CSTimer();
               Guard[] guard = {keyEvent.in(), textEvent.in()};
               Alternative alt = new Alternative(guard);
               String text = s;
               ChannelInput keyEventIn = keyEvent.in();
               ChannelInput textEventIn = textEvent.in();
               ChannelOutput configureAOut = configureA.out();
               while (true)
               {
                  switch (alt.priSelect())
                  {
                     case 0:
                        final KeyEvent key = (KeyEvent) keyEventIn.read();
                        switch (key.getKeyCode())
                        {
                           case KeyEvent.VK_ESCAPE:
                              switch (key.getID())
                              {
                                 case KeyEvent.KEY_PRESSED:
                                    if (event != null) 
                                       event.write(text);
                                    configureAOut.write(Boolean.FALSE);
                                    tim.after(tim.read() + disableTime);
                                    configureAOut.write(Boolean.TRUE);
                                 break;
                              }
                           break;
                        }
                     break;
                     case 1:
                        text = (String) textEventIn.read();
                     break;
                  }
               }
            }
         }
      }).run();
   }
}
