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

import org.jcsp.util.ints.*;

/**
 * Defines an interface for a factory that can create integer carrying channels with
 * user-definable buffering semantics.
 *
 * @author Quickstone Technologies Limited
 * 
 * @deprecated These channel factories are deprecated in favour of the new one2one() methods in the Channel class.
 */
public interface BufferedChannelIntFactory
{
    /**
     * Creates a new <code>One2One</code> channel with the given buffering behaviour.
     *
     * @param buffer the buffer implementation to use.
     * @return the created channel.
     */
    public One2OneChannelInt createOne2One(ChannelDataStoreInt buffer);

    /**
     * Creates a new <code>Any2One</code> channel with the given buffering behaviour.
     *
     * @param buffer the buffer implementation to use.
     * @return the created channel.
     */
    public Any2OneChannelInt createAny2One(ChannelDataStoreInt buffer);

    /**
     * Creates a new <code>One2Any</code> channel with the given buffering behaviour.
     *
     * @param buffer the buffer implementation to use.
     * @return the created channel.
     */
    public One2AnyChannelInt createOne2Any(ChannelDataStoreInt buffer);

    /**
     * Creates a new <code>Any2Any</code> channel with the given buffering behaviour.
     *
     * @param buffer the buffer implementation to use.
     * @return the created channel.
     */
    public Any2AnyChannelInt createAny2Any(ChannelDataStoreInt buffer);
}
