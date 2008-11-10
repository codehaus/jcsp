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

package org.jcsp.util.filter;

import org.jcsp.lang.*;

/**
 * This wraps up an Any2AnyChannel object so that its
 * input and output ends are separate objects. Both ends of the channel
 * have filtering enabled.
 *
 * @author Quickstone Technologies Limited
 */
class FilteredAny2AnyChannelImpl implements FilteredAny2AnyChannel
{
    /**
     * The input end of the channel.
     */
    private FilteredSharedChannelInput in;

    /**
     * The output end of the channel.
     */
    private FilteredSharedChannelOutput out;

    /**
     * Constructs a new filtered channel object based on an existing channel.
     */
    FilteredAny2AnyChannelImpl(Any2AnyChannel chan)
    {
        in = new FilteredSharedChannelInputWrapper(chan.in());
        out = new FilteredSharedChannelOutputWrapper(chan.out());
    }

    public SharedChannelInput in()
    {
        return in;
    }

    public SharedChannelOutput out()
    {
        return out;
    }

    public ReadFiltered inFilter()
    {
        return in;
    }

    public WriteFiltered outFilter()
    {
        return out;
    }
}
