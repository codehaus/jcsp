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

import java.applet.*;
import java.util.Vector;
import org.jcsp.lang.*;

/**
 * {@link java.applet.Applet <TT>java.applet.Applet</TT>}
 * with a channel interface, specialising in the operation
 * of {@link org.jcsp.lang.CSProcess} networks as <I>applets</I>.
 * <P>
 * <A HREF="#constructor_summary">Shortcut to the Constructor and Method Summaries.</A>
 * <H2>Description</H2>
 * <TT>ActiveApplet</TT> is an extension of {@link java.applet.Applet} to simplify
 * the management of an associated {@link org.jcsp.lang.CSProcess}.  There are three
 * areas of provision:
 * <UL>
 *   <LI>
 *      <A HREF="#Processes">controlling the execution</A> of the associated process;
 *   <LI>
 *      <A HREF="#Channels">a channel interface</A> to and from the underlying {@link java.awt.Panel};
 *   <LI>
 *      some methods to help <A HREF="#Utilities">accessing <I>applet</I> parameters</A>.
 * </UL>
 * These mechanisms are described in the next three sections.
 *
 * <H2><A NAME="Processes">Applet Processes</H2>
 * In an application, a network of processes can be executed simply by invoking <TT>run</TT>
 * on the defining ({@link org.jcsp.lang.Parallel}) process from the <TT>main</TT> thread of control.
 * We cannot do this from the <I>applet</I> <TT>init</TT> or <TT>start</TT> methods since
 * the browser would freeze until the process network terminated.  Instead, the process
 * network must be started asynchronously with the <I>applet</I> thread.  This can be achieved
 * by using a {@link org.jcsp.lang.ProcessManager}.
 * <P>
 * We could do this by directly extending the {@link java.applet.Applet} class and
 * overriding its <TT>start</TT>, <TT>stop</TT> and <TT>destroy</TT> methods suitably -
 * however, the necessary logic has already been built into this class.  All the user
 * needs to do is override the <TT>init</TT> method to contruct the desired
 * {@link org.jcsp.lang.CSProcess} and register it with this <TT>ActiveApplet</TT>
 * (by means of the {@link #setProcess <TT>setProcess</TT>} method).
 * <P>
 * For example:
 * <PRE>
 * import org.jcsp.awt.*;
 * 
 * public class Flasher extends ActiveApplet {
 * 
 *   public void init () {
 *     setProcess (new FlasherNetwork (500, this));
 *   }
 * 
 * }
 * </PRE>
 * In the above, <TT>FlasherNetwork</TT> is the <TT>CSProcess</TT> that will be run
 * by the <TT>ActiveApplet</TT>.  The <TT>500</TT> parameter defines the period
 * (in milliseconds) of its <I>flashing</I> and <A HREF="#Utilities">should really
 * be a parameter of the <I>applet</I> itself</A>.
 * The reason for passing the reference to <TT>this</TT> into the <TT>FlasherNetwork</TT>
 * process is explained in <A HREF="#FlasherNetwork">the description of its implementation
 * in the next section</A>.
 *
 * <H3><A NAME="Default"><I>Default ActiveApplet stop/start/destroy Behaviour</I></H3>
 * The default behaviours of the <TT>start</TT>, <TT>stop</TT> and <TT>destroy</TT> methods
 * implemented by this <TT>ActiveApplet</TT> are as follows.
 * <P>
 * When the <I>HTML</I> browser invokes <TT>start</TT> for the first
 * time, the <TT>CSProcess</TT> (registered by the {@link #setProcess <TT>setProcess</TT>}
 * method) will be set running in its own thread of control.  As the browser subsequently
 * invokes <TT>stop</TT> and <TT>start</TT> methods, events will be posted to channels
 * which may be optionally registered with the Applet.
 * <P>
 * When the browser invokes <TT>destroy</TT>, an event will be posted to the optionally
 * registered channel. If this is not present or does not cause network termination the
 * process network will be forcefully destroyed.
 * <P>
 * <H3><A NAME="Plugin"><I>Sun's Java Plug-in</I></H3>
 * Most of the problems arising from <A HREF="#Default">the default <I>ActiveApplet</I>
 * controls</A> are avoided by taking note of the following:
 * <P>
 * <CENTER>
 * <TABLE BORDER="2">
 * <TR>
 * <TD>
 * Use of this <TT>ActiveApplet</TT> class is recommended only for browsers on systems
 * that have installed Sun's
 * <A HREF="http://java.sun.com/products/plugin/"><I>Java Plug-in</I></A>.
 * </TD>
 * </TR>
 * </TABLE>
 * </CENTER>
 * <P>
 * This has the robust behaviour of invoking a <TT>destroy</TT> immediately following
 * a <TT>stop</TT> whenever we leave a web page containing a <I>Plug-in</I> controlled
 * applet.  It re-launches the applet from scratch whenever we return.  Whilst this
 * removes the option of allowing applets continued execution during visits to other
 * pages (but see the <A HREF="#Cheat">cheat</A>), it does relieve various deadlock problems
 * that may otherwise strike our browser.
 * <P>
 * <I>Note:</I> to use the <I>Java Plug-in</I>, HTML pages containing applets must be
 * transformed using the
 * <A HREF="http://java.sun.com/products/plugin/"><I>Java Plug-in HTML Converter</I></A>.
 * This renders the HTML somewhat more complex than it was before conversion,
 * but the results are worthwhile.
 * A browser visiting such a page without the <I>Plug-in</I> installed is invited
 * to install it - a link is offered to make this easy.
 *
 * <H3><A NAME="Override"><I>User-Defined Applet stop/start/destroy Behaviours</I></H3>
 * The <I>applet</I> <TT>stop</TT>, <TT>start</TT> and <TT>destroy</TT> methods can,
 * of course, be overridden by the user to interact in some user-chosen way with
 * the process associated with the <TT>ActveApplet</TT>.
 * However, the proper way to interact with a process is to communicate with it over
 * a channel and support for this is provided without the user having to override anything.
 * <P>
 * The methods {@link #setStopStartChannel <TT>setStopStartChannel</TT>} and
 * {@link #setDestroyChannels <TT>setDestroyChannels</TT>} let us connect special channels
 * to the <TT>ActveApplet</TT> process:
 * <p><img src="doc-files/ActiveApplet1.gif"></p>
 * If a <TT>stopStart</TT> channel has been set,
 * an {@link #STOP <TT>ActiveApplet.STOP</TT>}
 * (respectively {@link #START <TT>ActiveApplet.START</TT>})
 * will be written to this channel whenever the browser invokes a <TT>stop</TT>
 * (respectively <TT>start</TT>) method.  As before, the <I>first</I> invocation
 * of <TT>start</TT> just starts the running of the associated process
 * (i.e. <TT>ActiveApplet.START</TT> messages are only sent on <I>second</I> and
 * <I>subsequent</I> <TT>start</TT>s).
 * This replaces the default <TT>start</TT> and <TT>stop</TT> mechanisms
 * described earlier.
 * <P>
 * If a pair of <TT>destroy</TT>/<TT>destroyAck</TT> channels have been set,
 * an {@link #DESTROY <TT>ActiveApplet.DESTROY</TT>} will be written to the <TT>destroy</TT>
 * <I>channel</I> when the browser invokes its <TT>destroy</TT> <I>method</I>.
 * The browser will wait for up to {@link #DEFAULT_TIMEOUT_ACK 10 seconds} (by default)
 * for the applet process to respond with a message on <TT>destroyAck</TT>, before
 * proceeding to execute <A HREF="#Default">the default <I>destroy</I> mechanisms</A>.
 * The timeout period can be specified (or disabled)
 * {@link ActiveApplet#setDestroyChannels(org.jcsp.lang.ChannelOutputInt,org.jcsp.lang.AltingChannelInputInt,int) when the channels are set}.
 * <P>
 * These channels should be set up <I>either</I> as an explicit part of the <TT>init</TT>
 * method (and passed to the user process as a constructor or mutator parameter) <I>or</I>
 * by the user process constructor itself (to which <I>this</I> <TT>ActveApplet</TT>
 * would have had to have been passed).  It is important that these channels are in place
 * before the user process starts running (otherwise the default mechanism may be triggered
 * if the user quits the <TT>ActveApplet</TT> web page or the browser too quickly).
 * <P>
 * It is crucial, of course, that these communications are accepted by the user process
 * associated with the <TT>ActveApplet</TT>.  Using a channel configured with
 * an overwriting buffer (of capacity one) will ensure this.  Note that this overwriting
 * cannot cause the loss of any significant information to the user process.
 * <P>
 * The <TT>stop</TT>/<TT>start</TT> and <TT>destroy</TT> operations are implemented
 * via separately set channels so that the designer may make separate decisions on whether
 * to use each of them.
 * <P>
 * <I>Freezing</I> a process network (e.g. in response to
 * a {@link #STOP} is usually easy to arrange - just pinch the data-flow at a crucial
 * point to achieve a temporary deadlock.  When the {@link #START} message arrives,
 * just let go.
 * <P>
 * <I>Note:</I> if the <A HREF="#Plugin"><I>Java Plug-in</I></A> is not
 * being used, it is strongly recommended to set and operate this <TT>stopStart</TT> channel
 * (rather than rely on the default <I>suspend</I> operations).
 * <P>
 * <I>Note:</I> if the <A HREF="#Plugin"><I>Java Plug-in</I></A> is being used, user-defined
 * behaviour for the <TT>stop</TT>/<TT>start</TT> operations are not needed.
 * Any <TT>stop</TT> is quickly followed by a <TT>destroy</TT> (for which the default
 * <TT>ActveApplet</TT> response will release any locked resource).
 * Second invocations of <TT>start</TT> never take place.
 *
 * <P>
 * The <TT>destroy</TT>/<TT>destroyAck</TT> channels enable the process network to
 * tidy up any created resources (e.g. to make invisible and dispose of any <I>widgets</I>
 * spawned off the web page) before termination.
 * Correctly terminating a process network (e.g. in response to a <TT>destroy</TT>)
 * is usually harder to arrange safely - so it may be better to leave this to the
 * default mechanism (which will follow the <TT>destroyAck</TT> signal).  However,
 * secure CSP algorithms, at least for 1-1 channels, are known
 * (e.g. the <I>graceful termination</I> protocol)
 * and support for this may be provided in later versions of this library.
 * <P>
 * <I>Note:</I> if the <TT>destroy</TT>/<TT>destroyAck</TT> channels have been set,
 * the default <I>suspend</I> mechanism (if not already overridden by setting
 * a <TT>stopStart</TT> channel) will be suppressed.  This is because the browser invokes
 * the <TT>stop</TT> method immediately before invoking the <TT>destroy</TT>.
 * If we did not do this, the suspended network would not be able to respond
 * to the <TT>destroy</TT> channel.
 * <P>
 *
 * <H2><A NAME="Channels">The ActveApplet as an ActivePanel</H2>
 * An <TT>ActveApplet</TT> extends a {@link java.applet.Applet <TT>java.applet.Applet</TT>}
 * which extends a {@link java.awt.Panel <TT>java.awt.Panel</TT>}.  Most <I>activeApplets</I>
 * will just use this panel as a container for other <I>active</I> GUI processes that
 * form part of their <TT>ActiveApplet</TT> network.  However, if there is a need for run-time
 * configuration of the underlying panel and/or reaction to GUI events occurring on the
 * panel, the <TT>ActveApplet</TT> can itself be used as a process in that network.
 * Fot this case, an <TT>ActveApplet</TT> has a channel interface (which, together with
 * <A HREF="#Override">the browser control channels</A>, extends that offered by
 * an {@link org.jcsp.awt.ActivePanel}):
 * <p><img src="doc-files/ActiveApplet2.gif"></p>
 * The event channels
 * should be connected to one or more application-specific server processes (instead
 * of registering a passive object as a <I>Listener</I> to this component).
 * <P>
 * All channels are optional.  The <TT>configure</TT> channel is connected by invoking
 * the {@link #setConfigureChannel <TT>setConfigureChannel</TT>} method.
 * Event channels can be added to notify the occurrence of any type of <TT>Event</TT>
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
 *   final One2OneChannel myMouseEvent =
 *     Channel.one2one (new OverWriteOldestBuffer (n));
 * 
 *   activeApplet.addMouseEventChannel (myMouseEvent);
 * </PRE>
 * <I>This will ensure that the Java Event Thread will never be blocked.
 * Slow or inattentive readers may miss rapidly generated events, but
 * the </I><TT>n</TT><I> most recent events will always be available.</I>
 * </P>
 * <H3><A NAME="Protocols">Channel Protocols</A></H3>
 * <CENTER>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH ROWSPAN="1">destroyAck</TH>
 *     <TD>int</TD>
 *     <TD>See the {@link #setDestroyChannels
 *         <TT>setDestroyChannels</TT>} methods.</TD>
 *   </TR>
 *   <TR>
 *     <TH>configure</TH>
 *     <TD>{@link ActiveApplet.Configure ActiveApplet.Configure}</TD>
 *     <TD>Invoke the user-defined <TT>Configure.configure</TT> method on the applet.</TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>stopStart</TH>
 *     <TD>int</TD>
 *     <TD>See the {@link #setStopStartChannel
 *         <TT>setStopStartChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>destroy</TH>
 *     <TD>int</TD>
 *     <TD>See the {@link #setDestroyChannels
 *         <TT>setDestroyChannels</TT>} methods.</TD>
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
 * <H3><A NAME="FlasherNetwork">Example</H3>
 * This example continues the <TT>Flasher</TT> example
 * <A HREF="#Processes">introduced earlier</A>.
 * <TT>FlasherNetwork</TT> consists of two processes: one is the <TT>ActiveApplet</TT>
 * (supplied as a parameter to its constructor) and the other is a user-written
 * <TT>FlasherControl</TT>.  Here is the network diagram:
 * <p><img src="doc-files/ActiveApplet3.gif"></p>
 * <TT>FlasherControl</TT> responds to mouse <I>entry</I> and <I>exit</I>
 * events on the applet area (delivered as messages down the <TT>mouseEvent</TT>
 * channel).  When the mouse is outside the applet, the applet is painted black.
 * When the mouse is inside the applet, <TT>FlasherControl</TT> paints the applet
 * a random colour every <TT>period</TT> milliseconds (where <TT>period</TT> is
 * the other parameter to its constructor).  The applet colour is controlled by
 * sending it a message down the <TT>appletConfigure</TT> channel.
 * <P>
 * Here is the code to set up this process:
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.awt.*;
 * 
 * public class FlasherNetwork implements CSProcess {
 * 
 *   final private long period;
 *   final private ActiveApplet activeApplet;
 * 
 *   public FlasherNetwork (final long period,
 *                          final ActiveApplet activeApplet) {
 *     this.period = period;
 *     this.activeApplet = activeApplet;
 *   }
 * 
 *   public void run () {
 * 
 *     final One2OneChannel mouseEvent = Channel.one2one ();
 *     final One2OneChannel appletConfigure = Channel.one2one ();
 * 
 *     activeApplet.addMouseEventChannel (mouseEvent.out ());
 *     activeApplet.setConfigureChannel (appletConfigure.in ());
 * 
 *     new Parallel (
 *       new CSProcess[] {
 *         activeApplet,
 *         new FlasherControl (period, mouseEvent.in (), appletConfigure.out ())
 *       }
 *     ).run ();
 * 
 *   }
 * 
 * }
 * </PRE>
 * <TT>FlasherControl</TT> demonstrates three basic JCSP techniques:
 * <UL>
 *   <LI>
 *      {@link org.jcsp.lang.Alternative <I>ALTing</I>} between guards (a channel
 *      communication and a timeout);
 *   <LI>
 *      {@link org.jcsp.lang.Alternative <I>ALTing</I>} with <I>pre-conditions</I>
 *      (the mouse must be present in the applet for the flashing - controlled
 *      by the timeout - to take place);
 *   <LI>
 *      construction and use of general configuration objects for an <I>Active</I> GUI
 *      process (including the double-buffering needed for security when reusing these
 *      objects - see the note in {@link ActiveApplet.Configure}).
 * </UL>
 * For completeness, here is its definition:
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.awt.*;
 * import java.awt.*;
 * import java.awt.event.*;
 * import java.util.*;
 * 
 * public class FlasherControl implements CSProcess {
 * 
 *   final private long period;
 *   final private AltingChannelInput mouseEvent;
 *   final private ChannelOutput appletConfigure;
 * 
 *   public FlasherControl (final long period,
 *                          final AltingChannelInput mouseEvent,
 *                          final ChannelOutput appletConfigure) {
 *     this.period = period;
 *     this.mouseEvent = mouseEvent;
 *     this.appletConfigure = appletConfigure;
 *   }
 * 
 *   private class AppletColour implements ActiveApplet.Configure {
 *     private Color colour = Color.lightGray;
 *     public void setColour (Color colour) {
 *       this.colour = colour;
 *     }
 *     public void configure (java.applet.Applet applet) {
 *       applet.setBackground (colour);
 *     }
 *   }
 * 
 *   public void run () {
 * 
 *     final Random random = new Random ();
 *     final CSTimer tim = new CSTimer ();
 * 
 *     final Alternative alt = new Alternative (new Guard[] {mouseEvent, tim});
 *     final boolean[] preCondition = {true, false};
 *     final int MOUSE = 0;
 *     final int TIMER = 1;
 * 
 *     final AppletColour[] appletColour = {new AppletColour (), new AppletColour ()};
 *     final AppletColour panelBlack = new AppletColour ();
 *     panelBlack.setColour (Color.black);
 * 
 *     appletConfigure.write (panelBlack);
 * 
 *     int index = 0;
 *     AppletColour appletCol = appletColour[index];
 *     appletCol.setColour (new Color (random.nextInt ()));
 * 
 *     long timeout = tim.read ();
 *     boolean mousePresent = false;
 *     boolean running = true;
 * 
 *     while (running) {
 * 
 *       switch (alt.priSelect (preCondition)) {
 * 
 *         case MOUSE:
 *           switch (((MouseEvent) mouseEvent.read ()).getID ()) {
 *             case MouseEvent.MOUSE_ENTERED:
 *               if (! mousePresent) {
 *                 mousePresent = true;
 *                 timeout = tim.read () + period;
 *                 tim.setAlarm (timeout);
 *                 appletConfigure.write (appletCol);
 *                 preCondition[TIMER] = true;
 *               }
 *             break;
 *             case MouseEvent.MOUSE_EXITED:
 *               if (mousePresent) {
 *                 mousePresent = false;
 *                 appletConfigure.write (panelBlack);
 *                 preCondition[TIMER] = false;
 *               }
 *             break;
 *           }
 *         break;
 * 
 *         case TIMER:
 *           timeout += period;
 *           tim.setAlarm (timeout);
 *           index = 1 - index;
 *           appletCol = appletColour[index];
 *           appletCol.setColour (new Color (random.nextInt ()));
 *           appletConfigure.write (appletCol);
 *         break;
 * 
 *       }
 * 
 *     }
 * 
 *   }
 * 
 * }
 * </PRE>
 *
 * <H2><A NAME="Utilities">Accessing Applet Parameters</H2>
 * <TT>ActveApplet</TT> provides a set of methods for simplifying
 * the acquisition of parameters from the HTML source code.  These methods are not
 * specific for JCSP - they are useful for <I>any</I> applets.
 * <P>
 * All methods <TT>getApplet</TT><I>XXX</I> have the same form, where <I>XXX</I>
 * is one of Java's primitive types (<TT>int</TT>, <TT>boolean</TT>, <TT>byte</TT>,
 * <TT>short</TT>, <TT>long</TT>, <TT>float</TT> and <TT>double</TT>).  Each returns
 * an <I>XXX</I> value, parsed from the applet parameter named in the method's
 * first parameter.  A <TT>standby</TT> value must be supplied in case the named
 * applet parameter does not exist or parse correctly.  The number returning methods
 * must supply <TT>min</TT> and <TT>max</TT> values against which the applet supplied
 * value will be checked - values below <TT>min</TT> being truncated to <TT>min</TT>
 * and values above <TT>max</TT> to <TT>max</TT>.
 * <P>
 * For example, <A HREF="#Processes"><TT>Flasher</TT></A> should really
 * acquire its <TT>period</TT> value as an applet parameter:
 * <PRE>
 * import org.jcsp.lang.*;
 * import org.jcsp.awt.*;
 * import java.awt.*;
 * 
 * public class Flasher extends ActiveApplet {
 * 
 *   public static final int minPeriod = 300;       // milliseconds
 *   public static final int maxPeriod = 1000;      // milliseconds
 *   public static final int defaultPeriod = 500;   // milliseconds
 * 
 *   public void init () {
 * 
 *     final int period =
 *       getAppletInt ("period", minPeriod, maxPeriod, defaultPeriod);
 * 
 *     setProcess (new FlasherNetwork (period, this));
 * 
 *   }
 * 
 * }
 * </PRE>
 * Now, if no applet parameter <TT>"period"</TT> is present, the <TT>defaultPeriod</TT>
 * is chosen.  Same if the parameter was present but doesn't parse.  Otherwise,
 * values below <TT>minPeriod</TT> are rounded up to <TT>minPeriod</TT>,
 * values above <TT>maxPeriod</TT> are rounded down to <TT>maxPeriod</TT>
 * and <I>good</I> values are just accepted.
 *
 * @see java.applet.Applet
 * @see java.awt.Panel
 * @see java.awt.event.ContainerEvent
 * @see java.awt.event.ComponentEvent
 * @see java.awt.event.FocusEvent
 * @see java.awt.event.KeyEvent
 * @see java.awt.event.MouseEvent
 * @see org.jcsp.util.OverWriteOldestBuffer
 *
 * @author P.D. Austin and P.H. Welch
 */

public class ActiveApplet extends Applet implements CSProcess 
{
   
   /**
    * The <TT>process</TT> defining the behaviour of this Applet.
    */
   private CSProcess process = null;
   
   /**
    * This must be called during the <TT>init()</TT> method for this <TT>ActiveApplet</TT>.
    * It defines the process that is to be managed.
    *
    * @param process the process defining the applet behaviour.
    */
   public void setProcess(final CSProcess process)
   {
      this.process = process;
   }
   
   /**
    * The ProcessManager used to control the execution of this Applet.
    */
   private ProcessManager manager = null;
   
   /**
    * TRUE iff the applet is currently active.
    */
   private boolean started = false;
   
   /**
    * This value is sent down a {@link #setStopStartChannel <TT>stopStart</TT>} channel
    * when the browser invokes a <TT>stop</TT> -
    * see the <A HREF="#Override">user-defined stop/start response</A>.
    */
   public static final int STOP = 0;
   
   /**
    * This value is sent down a {@link #setStopStartChannel <TT>stopStart</TT>} channel
    * on second and subsequent browser invocations of <TT>start</TT> -
    * see the <A HREF="#Override">user-defined stop/start response</A>.
    */
   public static final int START = 1;
   
   /**
    * This value is sent down a {@link #setDestroyChannels <TT>destroy</TT>} channel
    * when the browser invokes a <TT>destroy</TT> -
    * see the <A HREF="#Override">user-defined destroy response</A>.
    */
   public static final int DESTROY = 2;
   
   /**
    * This is the default time (in milliseconds) that the browser will wait for the applet
    * process to acknowledge (on <TT>destroyAck</TT>) a <TT>DESTROY</TT> message
    * (sent down <TT>destroy</TT>).  The default value is 10000 (i.e. 10 seconds).
    * See the <A HREF="#Override">user-defined destroy response</A>.
    */
   public static final int DEFAULT_TIMEOUT_ACK = 10000;
   
   /**
    * This is the time (in milliseconds) that the browser will wait for the applet
    * process to acknowledge (on <TT>destroyAck</TT>) a <TT>DESTROY</TT> message
    * (sent down <TT>destroy</TT>).  If negative, no timeout will be set.
    * <P>
    * The default value is 10000 (i.e. 10 seconds).
    */
   private int timeoutAck = DEFAULT_TIMEOUT_ACK;
   
   /**
    * If this channel is set, the default stop/start behaviour changes -
    * see the <A HREF="#Override">user-defined stop/start response</A>.
    */
   private ChannelOutputInt stopStart = null;
   
   /**
    * If this channel is set, the default destroy behaviour changes -
    * see the <A HREF="#Override">user-defined destroy response</A>.
    */
   private ChannelOutputInt destroy = null;
   
   /**
    * If this channel is set, the default destroy behaviour changes -
    * see the <A HREF="#Override">user-defined destroy response</A>.
    */
   private AltingChannelInputInt destroyAck = null;
   
   private final CSTimer tim = new CSTimer();
   private Alternative destroyAlt;
   private final int ACKNOWLEDGE = 0;
   private final int TIMEOUT = 1;
   
   /**
    * This sets a <TT>stopStart</TT> channel to allow
    * a <A HREF="#Override">user-defined stop/start response</A>.
    * If the channel passed is <TT>null</TT>, no action will be taken.
    *
    * @param stopStart the channel controlling the stop/start behaviour of the applet.
    */
   public void setStopStartChannel(final ChannelOutputInt stopStart)
   {
      this.stopStart = stopStart;
   }
   
   /**
    * This sets <TT>destroy</TT>/<TT>destroyAck</TT> channels to allow
    * a <A HREF="#Override">user-defined destroy response</A>.
    * If <I>either</I> channel passed is <TT>null</TT>, no action will be taken.
    * A {@link #DEFAULT_TIMEOUT_ACK default timeout delay} (10000 milliseconds)
    * for the applet process to acknowledge the <TT>destroy</TT> signal is set.
    *
    * @param destroy the channel to which a {@link #DESTROY} will be written.
    * @param destroyAck the acknowledgement channel associated with destroy.
    */
   public void setDestroyChannels(final ChannelOutputInt destroy,
           final AltingChannelInputInt destroyAck)
   {
      setDestroyChannels(destroy, destroyAck, DEFAULT_TIMEOUT_ACK);
   }
   
   /**
    * This sets <TT>destroy</TT>/<TT>destroyAck</TT> channels to allow
    * a <A HREF="#Override">user-defined destroy response</A>.
    * If <I>either</I> channel passed is <TT>null</TT>, no action will be taken.
    * The timeout delay for the applet process to acknowledge the <TT>destroy</TT>
    * signal is set by <TT>timeoutAck</TT>.  If this is set negative,
    * no timeout will be set - i.e. the browser will wait indefinitely (which may
    * cause a problem if the applet process is mis-programmed).
    *
    * @param destroy the channel to which a {@link #DESTROY} will be written.
    * @param destroyAck the acknowledgement channel associated with destroy.
    * @param timeoutAck the timeout (in milliseconds) allowed for the acknowledgement
    *   - if negative, no timeout will be set.
    */
   public void setDestroyChannels(final ChannelOutputInt destroy,
           final AltingChannelInputInt destroyAck,
           final int timeoutAck)
   {
      if ((destroy != null) &&  (destroyAck != null))
      {
         this.destroy = destroy;
         this.destroyAck = destroyAck;
         this.timeoutAck = timeoutAck;
         this.destroyAlt = new Alternative(new Guard[] {destroyAck, tim});
      }
   }
   
   /**
    * Called by the browser when the <TT>ActiveApplet</TT> is first started and each time
    * its web page is revisited.  See above for the <A HREF="#Default">default</A> and
    * <A HREF="#Override">user-definable</A> behaviours obtainable from this method.
    */
   public void start()
   {
      if (manager != null)
      {
         if (!started)
         {
            manager.start();
            started = true;
         }
         if (stopStart != null)
            stopStart.write(START);
      }
      else
      {
         if (process == null)
            System.err.println("*** org.jcsp.awt.ActiveApplet: no process defined");
         else
         {
            Parallel.resetDestroy();
            manager = new ProcessManager(process);
            manager.start();
            started = true;
         }
      }
   }
   
   /**
    * Called by the browser when the web page containing this <TT>ActiveApplet</TT> is
    * replaced by another page or just before this <TT>ActiveApplet</TT> is to be destroyed.
    * See above for the <A HREF="#Default">default</A> and
    * <A HREF="#Override">user-definable</A> behaviours obtainable from this method.
    */
   public void stop()
   {
      if (manager != null)
      {
         if (stopStart != null)
            stopStart.write(STOP);
         else
         {
            manager.interrupt();    // used to call stop() ...
            started = false;
         }
      }
   }
   
   /**
    * Called by the browser when the <TT>ActiveApplet</TT> needs to be destroyed.
    * See above for the <A HREF="#Default">default</A> and
    * <A HREF="#Override">user-definable</A> behaviours obtainable from this method.
    */
   public void destroy()
   {
      if (manager != null)
      {
         if (destroy != null)
         {
            destroy.write(DESTROY);
            if (timeoutAck < 0)
               destroyAck.read();
            else
            {
               tim.setAlarm(tim.read() + timeoutAck);
               switch (destroyAlt.select())
               {
                  case ACKNOWLEDGE:
                     destroyAck.read();
                     break;
                  case TIMEOUT:
                     break;
               }
            }
         }
         manager = null;
         process = null;
      }
      Parallel.destroy();
   }
   
   ////////////////////////////////////////////////////
   // Special methods for applet parameter gathering //
   ////////////////////////////////////////////////////
   
   /**
    * This looks for the named parameter in the HTML <I>applet</I> source code
    * and attempts to parse it into an <TT>int</TT>.  If all is well, the value is
    * checked that it lies between <TT>min</TT> and <TT>max</TT> inclusive and
    * then returned.  If the parameter is not present or doesn't parse,
    * the <TT>standby</TT> value is returned.  If the checks fail, the value
    * is truncated to either <TT>min</TT> or <TT>max</TT> (depending on the direction
    * of the error) and returned.
    *
    * @param parameter the applet parameter name.
    * @param min the minimum acceptable parameter value.
    * @param max the maximum acceptable parameter value.
    * @param standby the value to retun in case the applet parameter is bad.
    *
    * @return either the value of the named parameter or <TT>standby</TT>.
    */
   public int getAppletInt(String parameter, int min, int max, int standby)
   {
      int i;
      final String s = getParameter(parameter);
      if (s == null)
      {
         System.out.println("*** Applet parameter " + parameter +
                 " is missing - defaulting to " + standby);
         i = standby;
      }
      else
         try
         {
            i = Integer.parseInt(s);
            if (i < min)
            {
               System.out.println("*** Applet parameter " + parameter +
                       " too small (" + i + ") - defaulting to " + min);
               i = min;
            }
            else if (i > max)
            {
               System.out.println("*** Applet parameter " + parameter + " too large (" + i +
                       ") - defaulting to " + max);
               i = max;
            }
         }
         catch (NumberFormatException e)
         {
            System.out.println("*** Applet parameter " + parameter + " is not an integer (" + s +
                    ") - defaulting to " + standby);
            i =  standby;
         }
      return i;
   }
   
   /**
    * This looks for the named parameter in the HTML <I>applet</I> source code
    * and attempts to parse it into a <TT>boolean</TT>.  If the parameter is not present
    * or doesn't parse, the <TT>standby</TT> value is returned.
    *
    * @param parameter the applet parameter name.
    * @param standby the value to retun in case the applet parameter is bad.
    *
    * @return either the value of the named parameter or <TT>standby</TT>.
    */
   public boolean getAppletBoolean(String parameter, boolean standby)
   {
      final String s = getParameter(parameter);
      if (s == null)
      {
         System.out.println("*** Applet parameter " + parameter + " is missing - defaulting to "
                 + standby);
         return standby;
      }
      final String sl = s.toLowerCase();
      if (sl.equals("true")) return true;
      if (sl.equals("false")) return false;
      System.out.println("*** Applet parameter " + parameter +
              " is not a boolean - defaulting to " + standby);
      return standby;
   }
   
   /**
    * This looks for the named parameter in the HTML <I>applet</I> source code
    * and attempts to parse it into a <TT>byte</TT>.  If all is well, the value is
    * checked that it lies between <TT>min</TT> and <TT>max</TT> inclusive and
    * then returned.  If the parameter is not present or doesn't parse,
    * the <TT>standby</TT> value is returned.  If the checks fail, the value
    * is truncated to either <TT>min</TT> or <TT>max</TT> (depending on the direction
    * of the error) and returned.
    *
    * @param parameter the applet parameter name.
    * @param min the minimum acceptable parameter value.
    * @param max the maximum acceptable parameter value.
    * @param standby the value to retun in case the applet parameter is bad.
    *
    * @return either the value of the named parameter or <TT>standby</TT>.
    */
   public byte getAppletByte(String parameter, byte min, byte max, byte standby)
   {
      byte i;
      final String s = getParameter(parameter);
      if (s == null)
      {
         System.out.println("*** Applet parameter " + parameter + " is missing - defaulting to "
                 + standby);
         i = standby;
      }
      else try
      {
         i = Byte.parseByte(s);
         if (i < min)
         {
            System.out.println("*** Applet parameter " + parameter + " too small (" + i +
                    ") - defaulting to " + min);
            i = min;
         }
         else if (i > max)
         {
            System.out.println("*** Applet parameter " + parameter + " too large (" + i +
                    ") - defaulting to " + max);
            i = max;
         }
      }
      catch (NumberFormatException e)
      {
         System.out.println("*** Applet parameter " + parameter + " is not a byte (" + s +
                 ") - defaulting to " + standby);
         i =  standby;
      }
      return i;
   }
   
   /**
    * This looks for the named parameter in the HTML <I>applet</I> source code
    * and attempts to parse it into a <TT>short</TT>.  If all is well, the value is
    * checked that it lies between <TT>min</TT> and <TT>max</TT> inclusive and
    * then returned.  If the parameter is not present or doesn't parse,
    * the <TT>standby</TT> value is returned.  If the checks fail, the value
    * is truncated to either <TT>min</TT> or <TT>max</TT> (depending on the direction
    * of the error) and returned.
    *
    * @param parameter the applet parameter name.
    * @param min the minimum acceptable parameter value.
    * @param max the maximum acceptable parameter value.
    * @param standby the value to retun in case the applet parameter is bad.
    *
    * @return either the value of the named parameter or <TT>standby</TT>.
    */
   public short getAppletShort(String parameter, short min, short max, short standby)
   {
      short i;
      final String s = getParameter(parameter);
      if (s == null)
      {
         System.out.println("*** Applet parameter " + parameter + " is missing - defaulting to "
                 + standby);
         i = standby;
      }
      else try
      {
         i = Short.parseShort(s);
         if (i < min)
         {
            System.out.println("*** Applet parameter " + parameter + " too small (" + i +
                    ") - defaulting to " + min);
            i = min;
         }
         else if (i > max)
         {
            System.out.println("*** Applet parameter " + parameter + " too large (" + i +
                    ") - defaulting to " + max);
            i = max;
         }
      }
      catch (NumberFormatException e)
      {
         System.out.println("*** Applet parameter " + parameter + " is not a short (" + s +
                 ") - defaulting to " + standby);
         i =  standby;
      }
      return i;
   }
   
   /**
    * This looks for the named parameter in the HTML <I>applet</I> source code
    * and attempts to parse it into a <TT>long</TT>.  If all is well, the value is
    * checked that it lies between <TT>min</TT> and <TT>max</TT> inclusive and
    * then returned.  If the parameter is not present or doesn't parse,
    * the <TT>standby</TT> value is returned.  If the checks fail, the value
    * is truncated to either <TT>min</TT> or <TT>max</TT> (depending on the direction
    * of the error) and returned.
    *
    * @param parameter the applet parameter name.
    * @param min the minimum acceptable parameter value.
    * @param max the maximum acceptable parameter value.
    * @param standby the value to retun in case the applet parameter is bad.
    *
    * @return either the value of the named parameter or <TT>standby</TT>.
    */
   public long getAppletLong(String parameter, long min, long max, long standby)
   {
      long i;
      final String s = getParameter(parameter);
      if (s == null)
      {
         System.out.println("*** Applet parameter " + parameter + " is missing - defaulting to "
                 + standby);
         i = standby;
      }
      else try
      {
         i = Long.parseLong(s);
         if (i < min)
         {
            System.out.println("*** Applet parameter " + parameter + " too small (" + i +
                    ") - defaulting to " + min);
            i = min;
         }
         else if (i > max)
         {
            System.out.println("*** Applet parameter " + parameter + " too large (" + i +
                    ") - defaulting to " + max);
            i = max;
         }
      }
      catch (NumberFormatException e)
      {
         System.out.println("*** Applet parameter " + parameter + " is not a long (" + s +
                 ") - defaulting to " + standby);
         i =  standby;
      }
      return i;
   }
   
   /**
    * This looks for the named parameter in the HTML <I>applet</I> source code
    * and attempts to parse it into a <TT>float</TT>.  If all is well, the value is
    * checked that it lies between <TT>min</TT> and <TT>max</TT> inclusive and
    * then returned.  If the parameter is not present or doesn't parse,
    * the <TT>standby</TT> value is returned.  If the checks fail, the value
    * is truncated to either <TT>min</TT> or <TT>max</TT> (depending on the direction
    * of the error) and returned.
    *
    * @param parameter the applet parameter name.
    * @param min the minimum acceptable parameter value.
    * @param max the maximum acceptable parameter value.
    * @param standby the value to retun in case the applet parameter is bad.
    *
    * @return either the value of the named parameter or <TT>standby</TT>.
    */
   public float getAppletFloat(String parameter, float min, float max, float standby)
   {
      float i;
      final String s = getParameter(parameter);
      if (s == null)
      {
         System.out.println("*** Applet parameter " + parameter + " is missing - defaulting to "
                 + standby);
         i = standby;
      }
      else try
      {
         i = Float.valueOf(s).floatValue();
         if (i < min)
         {
            System.out.println("*** Applet parameter " + parameter + " too small (" + i +
                    ") - defaulting to " + min);
            i = min;
         }
         else if (i > max)
         {
            System.out.println("*** Applet parameter " + parameter + " too large (" + i +
                    ") - defaulting to " + max);
            i = max;
         }
      }
      catch (NumberFormatException e)
      {
         System.out.println("*** Applet parameter " + parameter + " is not a float (" + s +
                 ") - defaulting to " + standby);
         i =  standby;
      }
      return i;
   }
   
   /**
    * This looks for the named parameter in the HTML <I>applet</I> source code
    * and attempts to parse it into a <TT>double</TT>.  If all is well, the value is
    * checked that it lies between <TT>min</TT> and <TT>max</TT> inclusive and
    * then returned.  If the parameter is not present or doesn't parse,
    * the <TT>standby</TT> value is returned.  If the checks fail, the value
    * is truncated to either <TT>min</TT> or <TT>max</TT> (depending on the direction
    * of the error) and returned.
    *
    * @param parameter the applet parameter name.
    * @param min the minimum acceptable parameter value.
    * @param max the maximum acceptable parameter value.
    * @param standby the value to retun in case the applet parameter is bad.
    *
    * @return either the value of the named parameter or <TT>standby</TT>.
    */
   public double getAppletDouble(String parameter, double min, double max, double standby)
   {
      double i;
      final String s = getParameter(parameter);
      if (s == null)
      {
         System.out.println("*** Applet parameter " + parameter + " is missing - defaulting to "
                 + standby);
         i = standby;
      }
      else try
      {
         i = Double.valueOf(s).doubleValue();
         if (i < min)
         {
            System.out.println("*** Applet parameter " + parameter + " too small (" + i +
                    ") - defaulting to " + min);
            i = min;
         }
         else if (i > max)
         {
            System.out.println("*** Applet parameter " + parameter + " too large (" + i +
                    ") - defaulting to " + max);
            i = max;
         }
      }
      catch (NumberFormatException e)
      {
         System.out.println("*** Applet parameter " + parameter + " is not a double (" + s +
                            ") - defaulting to " + standby);
         i =  standby;
      }
      return i;
   }
   
   ////////////////////////////////////
   // ActiveApplet as an ActivePanel //
   ////////////////////////////////////
   
   /**
    * The Vector construct containing the handlers.
    */
   private Vector vec = new Vector();
   
   /**
    * The channel from which configuration messages arrive.
    */
   private ChannelInput configure;
   
   // /**
   //  * Constructs a new <TT>ActiveApplet</TT> with no configuration channel.
   //  */
   // public ActiveApplet () {
   //   this (null);
   // }
   
   // /**
   //  * Constructs a new <TT>ActiveApplet</TT> with a configuration channel.
   //  *
   //  * @param configure the channel for configuration events
   //  * -- can be null if no configuration is required.
   //  */
   // public ActiveApplet (ChannelInput configure) {
   //   this.configure = configure;
   // }
   
   /**
    * Sets the configuration channel for this <TT>ActiveApplet</TT>.
    *
    * @param configure the channel for configuration events.
    * If the channel passed is <TT>null</TT>, no action will be taken.
    */
   public void setConfigureChannel(ChannelInput configure)
   {
      this.configure = configure;
   }
   
   /**
    * Add a new channel to this component that will be used to notify that
    * a <TT>ContainerEvent</TT> has occurred. <I>This should be used
    * instead of registering a ContainerListener with the component.</I> It is
    * possible to add more than one Channel by calling this method multiple times.
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
    * possible to add more than one Channel by calling this method multiple times.
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
    * possible to add more than one Channel by calling this method multiple times.
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
    * possible to add more than one Channel by calling this method multiple times.
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
    * possible to add more than one Channel by calling this method multiple times.
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
    * possible to add more than one Channel by calling this method multiple times.
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
    * this interface and sent down the <TT>configure</TT> channel to this component will
    * have its <TT>configure</TT> method invoked on this component.
    * <P>
    * For example, to set the foreground/background colours, first define:
    * <PRE>
    *   private class AppletColours implements ActiveApplet.Configure {
    * 
    *     private Color foreground = Color.white;
    *     private Color background = Color.black;
    * 
    *     public void setColour (Color foreground, Color background) {
    *       this.foreground = foreground;
    *       this.background = background;
    *     }
    * 
    *     public void configure (java.applet.Applet applet) {
    *       applet.setForeground (foreground);
    *       applet.setBackground (background);
    *     }
    * 
    *   }
    * </PRE>
    * Then, construct an instance of <TT>AppletColours</TT>, set its foreground/background
    * colours as required and send it down the <TT>configure</TT> channel when appropriate.
    * <P>
    * Note that an instance of the above <TT>AppletColours</TT> may have its colours reset
    * and may be resent down the channel.  To ensure against <I>race-hazards</I>, construct
    * at least two instances and use them alternately.  Acceptance of one of them by the
    * <TT>ActiveApplet</TT> means that it has finished using the other and, therefore,
    * the other may be safely reset.  So long as the <TT>configure</TT> channel is
    * unbuffered, completion of the <TT>write</TT> method means that the message has been
    * <TT>read</TT> (i.e. accepted) by the receiving process.
    */
   static public interface Configure
   {
      /**
       * @param applet the Applet being configured.
       */
      public void configure(final Applet applet);
      
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
            if (message instanceof Configure)
            {
               ((Configure) message).configure(this);
            }
         }
      }
   }
}
