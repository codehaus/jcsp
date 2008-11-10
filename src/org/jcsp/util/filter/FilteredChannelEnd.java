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
 * Static factory for creating channel end wrappers that support filtering.
 *
 * @author Quickstone Technologies Limited
 */
public class FilteredChannelEnd
{
    /**
     * The default factory for creating the channel ends.
     */
    private static final FilteredChannelEndFactory factory = new FilteredChannelEndFactory();

    /**
     * Private constructor to prevent any instances of this static factory from being created.
     */
    private FilteredChannelEnd()
    {
        // Noone's creating one of these
    }

    /**
     * Creates a new filtered input channel end around an existing input channel end. The channel end
     * can be used as a guard in an <code>Alternative</code>.
     *
     * @param in the existing channel end to create a filtered form of.
     * @return the new channel end with filtering ability.
     */
    public static FilteredAltingChannelInput createFiltered(AltingChannelInput in)
    {
        return factory.createFiltered(in);
    }

    /**
     * Creates a new filtered input channel end around an existing input channel end.
     *
     * @param in the existing channel end to create a filtered form of.
     * @return the new channel end with filtering ability.
     */
    public static FilteredChannelInput createFiltered(ChannelInput in)
    {
        return factory.createFiltered(in);
    }

    /**
     * Creates a new filtered input channel end around an existing input channel end that can be
     * shared by multiple processes.
     *
     * @param in the existing channel end to create a filtered form of,
     * @return the new channel end with filtering ability.
     */
    public static FilteredSharedChannelInput createFiltered(SharedChannelInput in)
    {
        return factory.createFiltered(in);
    }

    /**
     * Creates a new filtered output channel end around an existing output channel end.
     *
     * @param out the existing channel end to create a filtered form of.
     */
    public static FilteredChannelOutput createFiltered(ChannelOutput out)
    {
        return factory.createFiltered(out);
    }

    /**
     * Creates a new filtered output channel end around an existing output channel end that can be
     * shared by multiple processes.
     *
     * @param out the existing channel end to create a filtered form of.
     * @return the new channel end with filtering ability.
     */
    public static FilteredSharedChannelOutput createFiltered(SharedChannelOutput out)
    {
        return factory.createFiltered(out);
    }
}
