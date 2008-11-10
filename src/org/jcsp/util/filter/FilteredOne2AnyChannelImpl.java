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
 * Implements an <code>One2Any</code> channel that supports filtering at each end.
 *
 * @see org.jcsp.lang.One2AnyChannel
 * @see org.jcsp.util.filter.ReadFiltered
 * @see org.jcsp.util.filter.WriteFiltered
 *
 * @author Quickstone Technologies Limited
 */
class FilteredOne2AnyChannelImpl implements FilteredOne2AnyChannel
{
    /**
     * The filtered input end of the channel.
     */
    private FilteredSharedChannelInput in;

    /**
     * The filtered output end of the channel.
     */
    private FilteredChannelOutput out;

    /**
     * Constructs a new filtered channel from an existing channel.
     *
     * @param chan the existing channel.
     */
    public FilteredOne2AnyChannelImpl(One2AnyChannel chan)
    {
        in = new FilteredSharedChannelInputWrapper(chan.in());
        out = new FilteredChannelOutputWrapper(chan.out());
    }

    public SharedChannelInput in()
    {
        return in;
    }

    public ChannelOutput out()
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
