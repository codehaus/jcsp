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

package org.jcsp.lang;

import java.io.Serializable;

/**
 * This is the super-class for one-to-any <TT>interface</TT>-specific CALL channels,
 * safe for use by one client and many servers.
 * <P>
 * <A HREF="#constructor_summary">Shortcut to the Constructor and Method Summaries.</A>
 *
 * <H2>Description</H2>
 * Please see {@link One2OneCallChannel} for general information about CALL channels.
 * Documented here is information specific to this <I>1-any</I> version.
 *
 * <H3><A NAME="Convert">Converting a Method Interface into a Variant CALL Channel</H3>
 * Constructing a <I>1-any</I> CALL channel for a specific <TT>interface</TT>
 * follows exactly the same pattern as in the <I>1-1</I> case.  Of course, it must
 * extend <TT>One2AnyCallChannel</TT> rather than <TT>One2OneCallChannel</TT>.
 * <P>
 * For example, using the same <A HREF="One2OneCallChannel.html#Foo"><TT>Foo</TT></A>
 * interface as before, we derive:
 * <PRE>
 * import org.jcsp.lang.*;
 * <I></I>
 * public class One2AnyFooChannel extends One2AnyCallChannel implements Foo {
 * <I></I>
 *   ...  same body as <A HREF="One2OneCallChannel.html#One2OneFooChannel"><TT>One2OneFooChannel</TT></A>
 * <I></I>
 * }
 * </PRE>
 *
 * <H3><A NAME="Call">Calling a CALL Channel</H3>
 * All the <I>client</I> needs to see is the method <TT>interface</TT>
 * implemented by the CALL channel.  So far as the <I>client</I> is concerned, therefore,
 * there is <I>no</I> difference between any of the varieties of CALL channel
 * - it just <A HREF="One2OneCallChannel.html#Call">makes the call</A>.
 *
 * <H3><A NAME="Accept">Accepting a CALL Channel</H3>
 * The mechanics of accepting a CALL channel are the same for all varieties.
 * However, the <I>server</I> should declare which kind (or kinds) it allows
 * to be attached:
 * <PRE>
 * import org.jcsp.lang.*;
 * <I></I>
 * class B implements CSProcess, Foo {
 * <I></I>
 *   private final ChannelAccept in;
 * <I></I>
 *   public B (final One2OneFooChannel in) {         // original constructor
 *     this.in = in;
 *   }
 * <I></I>
 *   public B (final One2AnyFooChannel in) {        // additional constructor
 *     this.in = in;
 *   }
 * <I></I>
 *   ...  rest <A HREF="One2OneCallChannel.html#Accept">as before</A>
 * <I></I>
 * }
 * </PRE>
 * When wrapping the above to hide its raw method interface, don't forget to include
 * the extra constructor(s):
 * <PRE>
 * import org.jcsp.lang.*;
 * <I></I>
 * public class B2 implements CSProcess {            // no Foo interface
 * <I></I>
 *   private final B b;
 * <I></I>
 *   public B2 (final One2OneFooChannel in) {        // original constructor
 *     b = new B (in);
 *   }
 * <I></I>
 *   public B2 (final One2AnyFooChannel in) {       // additional constructor
 *     b = new B (in);
 *   }
 * <I></I>
 *   public void run () {
 *     b.run ();
 *   }
 * <I></I>
 * }
 * </PRE>
 *
 * <H3><A NAME="ALTing">ALTing on a CALL Channel</H3>
 * As for <I>ordinary</I> channels, ALTing over <I>1-Any</I> or <I>Any-Any</I> versions
 * is not supported.  Hence, a server can only choose to {@link #accept <TT>accept</TT>}
 * or not to <TT>accept</TT> a <TT>One2AnyFooChannel</TT> - it cannot back off because
 * of some other event.
 *
 * <H3><A NAME="Network">Building a CALL Channel Network</H3>
 * Network building with CALL channels is the same as building with <I>ordinary</I>
 * channels.  First construct the channels and, then, construct the processes
 * - plugging in the channels as required and running them in {@link Parallel}.
 * <P>
 * For example, the network consisting of one <I>client</I> and several <I>servers</I>:
 * <p><IMG SRC="doc-files/One2AnyCallChannel1.gif"></p>
 * where <TT>A</TT> is unchanged from its definition
 * in <A HREF="One2OneCallChannel.html#Call"><TT>One2OneCallChannel</TT></A>,
 * is implemented by:
 * <PRE>
 *     One2AnyFooChannel c = new One2AnyFooChannel ();
 * <I></I>
 *     final B2[] bServers = new B2[n_bClients];
 *     for (int i = 0; i < bServers.length; i++) {
 *       bServers[i] = new B2 (c);
 *     }
 * <I></I>
 *     new Parallel (
 *       new CSProcess[] {
 *         new A (c),
 *         new Parallel (bServers)
 *       }
 *     ).run ();
 * </PRE>
 * [Reminder: <I>XXX-any</I> channels are not broadcasters of information.
 * In the above, when <TT>A</TT> makes a CALL on <TT>c</TT>, it must not care
 * <I>which</I> of the <TT>B2</TT> servers picks it up.  The servers compete
 * with each other to service the client.]
 *
 * <H2><A NAME="Example">Example</H2>
 * Please see <A HREF="Any2AnyCallChannel.html#Example"><TT>Any2AnyCallChannel</TT></A>
 * for an example that includes many <I>clients</I> and many <I>servers</I> competing for
 * each other's attention.
 *
 * @see org.jcsp.lang.One2OneCallChannel
 * @see org.jcsp.lang.Any2OneCallChannel
 * @see org.jcsp.lang.Any2AnyCallChannel
 * @see org.jcsp.lang.Alternative
 *
 * @author P.H. Welch
 */

public abstract class One2AnyCallChannel implements ChannelAccept, Serializable
{
    /**
     * This is used to synchronise the calling and accepting process.
     */
    final private One2OneChannelImpl c = new One2OneChannelImpl();

    /**
     * This holds a reference to a <I>server</I> process so that a <I>client</I> may
     * make the call.  The reference is only valid between the {@link #join <TT>join</TT>}
     * and {@link #fork <TT>fork</TT>} elements of the standard
     * <A HREF="One2OneCallChannel.html#One2OneFooChannel">calling sequence</A>.
     * As shown in that sequence, it will need casting up to the relevant interface
     * supported by the specific CALL channel derived from this class.
     */
    protected CSProcess server;

    /**
     * This may be set during the standard <A HREF="One2OneCallChannel.html#One2OneFooChannel">calling sequence</A> to record
     * which method was invoked by a <I>client</I>.  It is only safe to do this between
     * the {@link #join <TT>join</TT>} and {@link #fork <TT>fork</TT>} elements of
     * that sequence.  Either <I>all</I> the CALL
     * channel methods should do this or <I>none</I> - in the latter case, its default
     * value remains as zero.  Its value is returned to a <I>server</I> as the result
     * the <I>server</I>'s invocation of {@link #accept <TT>accept</TT>}.
     */
    protected int selected = 0;

    /**
     * This is invoked by a <I>server</I> when it commits to accepting a CALL
     * from a <I>client</I>.  The parameter supplied must be a reference to this <I>server</I>
     * - see the <A HREF="One2OneCallChannel.html#Accept">example</A> from {@link One2OneCallChannel}.
     * It will not complete until a CALL has been made.  If the derived CALL channel has set
     * the {@link #selected} field in the way defined by the standard
     * <A HREF="One2OneCallChannel.html#One2OneFooChannel">calling sequence</A>,
     * the value returned by this method will indicate which method was called.
     *
     * @param server the <I>server</I> process receiving the CALL.
     */
    public synchronized int accept(CSProcess server)
    {
        this.server = server;
        c.read(); // ready to ACCEPT the CALL
        c.read(); // wait until the CALL is complete
        return selected;
    }

    /**
     * This is invoked by a <I>client</I> during the standard <A HREF="One2OneCallChannel.html#One2OneFooChannel">calling
     * sequence</A>.  It will not complete until a <I>server</I> invokes
     * an {@link #accept <TT>accept</TT>} on this channel.  In turn, that <TT>accept</TT>
     * will not complete until the <I>client</I> invokes a {@link #fork <TT>fork</TT>},
     * after having made its CALL on the <I>server</I>.
     */
    protected void join()
    {
        c.write(null);
    }

    /**
     * This is invoked by a <I>client</I> during the standard <A HREF="One2OneCallChannel.html#One2OneFooChannel">calling
     * sequence</A>.  A <I>server</I> must have invoked an {@link #accept <TT>accept</TT>}
     * for the <I>client</I> to have got this far in the sequence - see
     * the {@link #join <TT>join</TT>}.  This call unblocks that <TT>accept</TT>,
     * releasing the <I>server</I> and <I>client</I> to resume separate lives.
     */
    protected void fork()
    {
        c.write(null);
    }
}
