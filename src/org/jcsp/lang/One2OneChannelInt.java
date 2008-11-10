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
 * This implements a one-to-one integer channel.
 * <H2>Description</H2>
 * <TT>One2OneChannelIntImpl</TT> implements a one-to-one integer channel.  Multiple
 * readers or multiple writers are not allowed -- these are catered for
 * by {@link Any2OneChannelInt},
 * {@link One2AnyChannelInt} or
 * {@link Any2AnyChannelInt}.
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
 * Standard examples are given in the <TT>org.jcsp.util</TT> package, but
 * <I>careful users</I> may write their own.
 * <P>
 * Other static <TT>create</TT> methods allows the user to create fully
 * initialised arrays of channels, including plug-ins if required.
 *
 * @see org.jcsp.lang.Alternative
 * @see org.jcsp.lang.Any2OneChannelIntImpl
 * @see org.jcsp.lang.One2AnyChannelIntImpl
 * @see org.jcsp.lang.Any2AnyChannelIntImpl
 * @see org.jcsp.util.ints.ChannelDataStoreInt
 *
 * @author P.D. Austin
 * @author P.H. Welch
 */
public interface One2OneChannelInt
{
    /**
     * Returns the input end of the channel.
     */
    public AltingChannelInputInt in();

    /**
     * Returns the output end of the channel.
     */
    public ChannelOutputInt out();
}
