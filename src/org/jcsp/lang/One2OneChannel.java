    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2001 Peter Welch and Paul Austin.            //
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
    //  Author contact: P.H.Welch@ukc.ac.uk                             //
    //                  mailbox@quickstone.com                          //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

package org.jcsp.lang;

/**
 * This implements a one-to-one object channel.
 * <H2>Description</H2>
 * <TT>One2OneChannelImpl</TT> implements a one-to-one object channel.  Multiple
 * readers or multiple writers are not allowed -- these are catered for
 * by {@link Any2OneChannel},
 * {@link One2AnyChannel} or
 * {@link Any2AnyChannel}.
 * <P>
 * The reading process may {@link Alternative <TT>ALT</TT>} on this channel.
 * The writing process is committed (i.e. it may not back off).
 * <P>
 * The default semantics of the channel is that of CSP -- i.e. it is
 * zero-buffered and fully synchronised.  The reading process must wait
 * for a matching writer and vice-versa.
 * <P>
 * A factory pattern is used to create channel instances. The <tt>create</tt> methods of {@link Channel}
 * allow creation of channels, arrays of channels and channels with varying semantics such as
 * buffering with a user-defined capacity or overwriting with various policies.
 * Standard examples are given in the <TT>com.quickstone.jcsp.util</TT> package, but
 * <I>careful users</I> may write their own.
 * <P>
 * Other static <TT>create</TT> methods allows the user to create fully
 * initialised arrays of channels, including plug-ins if required.
 *
 * @see org.jcsp.lang.Alternative
 * @see org.jcsp.lang.Any2OneChannelImpl
 * @see org.jcsp.lang.One2AnyChannelImpl
 * @see org.jcsp.lang.Any2AnyChannelImpl
 * @see org.jcsp.util.ChannelDataStore
 *
 * @author P.D.Austin
 * @author P.H.Welch
 */

public interface One2OneChannel
{
    /**
     * Returns the input channel end.
     */
    public AltingChannelInput in();

    /**
     * Returns the output channel end.
     */
    public ChannelOutput out();
}
