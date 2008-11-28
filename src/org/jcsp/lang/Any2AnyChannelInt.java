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

/**
 * This defines an interface for an <i>any-to-any</i> integer channel,
 * safe for use by many writers and many readers.
 * <P>
 * The only methods provided are to obtain the <i>ends</i> of the channel,
 * through which all reading and writing operations are done.
 * Only an appropriate <i>channel-end</i> should be plugged into a process
 * &ndash; not the <i>whole</i> channel.
 * A process may use its external channels in one direction only
 * &ndash; either for <i>writing</i> or <i>reading</i>.
 * </P>
 * <P>Actual channels conforming to this interface are made using the relevant
 * <tt>static</tt> construction methods from {@link Channel}.
 * Channels may be {@link Channel#any2anyInt() <i>synchronising</i>},
 * {@link Channel#any2anyInt(org.jcsp.util.ints.ChannelDataStoreInt) <i>buffered</i>},
 * {@link Channel#any2anyInt(int) <i>poisonable</i>}
 * or {@link Channel#any2anyInt(org.jcsp.util.ints.ChannelDataStoreInt,int) <i>both</i>}
 * <i>(i.e. buffered and poisonable)</i>.
 * </P>
 * <H2>Description</H2>
 * <TT>Any2AnyChannelInt</TT> is an interface for a channel which
 * is safe for use by many reading and writing processes.  Reading processes
 * compete with each other to use the channel.  Writing processes compete
 * with each other to use the channel.  Only one reader and one writer will
 * actually be using the channel at any one time.  This is managed by the
 * channel &ndash; user processes just read from or write to it.
 * </P>
 * <P>
 * <I>Please note that this is a safely shared channel and not
 * a broadcaster or message gatherer.  Currently, broadcasting or gathering has to be managed by
 * writing active processes (see {@link org.jcsp.plugNplay.DynamicDelta}
 * for an example of broadcasting).</I>
 * </P>
 * <P>
 * All reading processes and writing processes commit to the channel
 * (i.e. may not back off).  This means that the reading processes
 * <I>may not</I> {@link Alternative <TT>ALT</TT>} on this channel.
 * </P>
 * <P>
 * The default semantics of the channel is that of CSP &ndash; i.e. it is
 * zero-buffered and fully synchronised.  A reading process must wait
 * for a matching writer and vice-versa.
 * </P>
 * <P>
 * The <tt>static</tt> methods of {@link Channel} construct channels with
 * either the default semantics or with buffering to user-specified capacity
 * and a range of blocking/overwriting policies.
 * Various buffering plugins are given in the <TT>org.jcsp.util</TT> package, but
 * <I>careful users</I> may write their own.
 * </P>
 * <P>
 * The {@link Channel} methods also provide for the construction of
 * {@link Poisonable} channels and for arrays of channels.
 *
 * <H3><A NAME="Caution">Implementation Note and Caution</H3>
 * <I>Fair</I> servicing of readers and writers to this channel depends on the <I>fair</I>
 * servicing of requests to enter a <TT>synchronized</TT> block (or method) by
 * the underlying Java Virtual Machine (JVM).  Java does not specify how threads
 * waiting to synchronize should be handled.  Currently, Sun's standard JDKs queue
 * these requests - which is <I>fair</I>.  However, there is at least one JVM
 * that puts such competing requests on a stack - which is legal but <I>unfair</I>
 * and can lead to infinite starvation.  This is a problem for <I>any</I> Java system
 * relying on good behaviour from <TT>synchronized</TT>, not just for these
 * <I>any-any</I> channels.
 *
 * @see org.jcsp.lang.Channel
 * @see org.jcsp.lang.One2OneChannelInt
 * @see org.jcsp.lang.Any2OneChannelInt
 * @see org.jcsp.lang.One2AnyChannelInt
 * @see org.jcsp.util.ints.ChannelDataStoreInt
 *
 * @author P.D. Austin and P.H. Welch
 */
public interface Any2AnyChannelInt
{
    /**
     * Returns the input channel end.
     */
    public SharedChannelInputInt in();

    /**
     * Returns the output channel end.
     */
    public SharedChannelOutputInt out();
}
