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
 * This defines the interface for accepting CALL channels.
 * <H2>Description</H2>
 * <TT>ChannelAccept</TT> defines the interface for accepting CALL channels.
 * The interface contains only one method - {@link #accept <TT>accept</TT>}.
 *
 * <H2>Example</H2>
 * See the explanations and examples documented in the CALL channel super-classes
 * (listed below).
 *
 * @see org.jcsp.lang.One2OneCallChannel
 * @see org.jcsp.lang.Any2OneCallChannel
 * @see org.jcsp.lang.One2AnyCallChannel
 * @see org.jcsp.lang.Any2AnyCallChannel
 *
 * @author P.H. Welch
 */

public interface ChannelAccept
{
    /**
     * This is invoked by a <I>server</I> when it commits to accepting a CALL
     * from a <I>client</I>.  The parameter supplied must be a reference to this <I>server</I>
     * - see the <A HREF="One2OneCallChannel.html#Accept">example</A> from {@link One2OneCallChannel}.
     * It will not complete until a CALL has been made.  If the derived CALL channel has set
     * the <TT>selected</TT> field in the way defined by the standard
     * <A HREF="One2OneCallChannel.html#One2OneFooChannel">calling sequence</A>,
     * the value returned by this method will indicate which method was called.
     *
     * @param server the <I>server</I> process receiving the CALL.
     */
    public int accept(CSProcess server);
}
