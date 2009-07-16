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
 * This defines the interface for a <i>one-to-one</i> Object channel.
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
 * Channels may be {@link Channel#one2one() <i>synchronising</i>},
 * {@link Channel#one2one(org.jcsp.util.ChannelDataStore) <i>buffered</i>},
 * {@link Channel#one2one(int) <i>poisonable</i>}
 * or {@link Channel#one2one(org.jcsp.util.ChannelDataStore,int) <i>both</i>}
 * <i>(i.e. buffered and poisonable)</i>.
 * </P>
 * </P>
 * <H2>Description</H2>
 * <TT>One2OneChannelImpl</TT> is an interface for a one-to-one object channel.  Multiple
 * readers or multiple writers are not allowed &ndash; these are catered for
 * by {@link Any2OneChannel},
 * {@link One2AnyChannel} or
 * {@link Any2AnyChannel}.
 * <P>
 * The reading process may {@link Alternative <TT>ALT</TT>} on this channel.
 * The writing process is committed (i.e. it may not back off).
 * </P>
 * <P>
 * The default semantics of the channel is that of CSP &ndash; i.e. it is
 * zero-buffered and fully synchronised.  The reading process must wait
 * for a matching writer and vice-versa.
 * <P>
 * </P>
 * The <tt>static</tt> methods of {@link Channel} construct channels with
 * either the default semantics or with buffering to user-specified capacity
 * and a range of blocking/overwriting policies.
 * Various buffering plugins are given in the <TT>org.jcsp.util</TT> package, but
 * <I>careful users</I> may write their own.
 * <P>
 * The {@link Channel} methods also provide for the construction of
 * {@link Poisonable} channels and for arrays of channels.
 * </P>
 *
 * @see org.jcsp.lang.Channel
 * @see org.jcsp.lang.Alternative
 * @see org.jcsp.lang.Any2OneChannel
 * @see org.jcsp.lang.One2AnyChannel
 * @see org.jcsp.lang.Any2AnyChannel
 * @see org.jcsp.util.ChannelDataStore
 *
 * @author P.D. Austin
 * @author P.H. Welch
 */

public interface One2OneChannel<T>
{
    /**
     * Returns the input channel end.
     */
    public AltingChannelInput<T> in();

    /**
     * Returns the output channel end.
     */
    public ChannelOutput<T> out();
}
