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
 * <p>Factory for creating filtered channel ends around existing channel ends.</p>
 *
 * <p>An instance of this class can be created and used, or alternatively the static factory
 * <code>FilteredChannelEnd</code> may be more convenient.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class FilteredChannelEndFactory
{
    /**
     * Constructs a new <code>FilteredChannelEndFactory</code>.
     */
    public FilteredChannelEndFactory()
    {
        super();
    }

    /**
     * Creates a new filtered channel input end around an existing channel end. The created channel end
     * can be used as a guard in an <code>Alternative</code>.
     *
     * @param in the existing channel end.
     * @return the created channel end.
     */
    public FilteredAltingChannelInput createFiltered(AltingChannelInput in)
    {
        return new FilteredAltingChannelInput(in);
    }

    /**
     * Creates a new filtered channel input end around an existing channel end.
     *
     * @param in the existing channel end.
     * @return the created channel end.
     */
    public FilteredChannelInput createFiltered(ChannelInput in)
    {
        return new FilteredChannelInputWrapper(in);
    }

    /**
     * Creates a new filtered channel input end around an existing channel end. The created channel end
     * can be shared by multiple processes.
     *
     * @param in the existing channel end.
     * @return the created channel end.
     */
    public FilteredSharedChannelInput createFiltered(SharedChannelInput in)
    {
        return new FilteredSharedChannelInputWrapper(in);
    }

    /**
     * Creates a new filtered channel output end around an existing channel end.
     *
     * @param out the existing channel end.
     * @return the created channel end.
     */
    public FilteredChannelOutput createFiltered(ChannelOutput out)
    {
        return new FilteredChannelOutputWrapper(out);
    }

    /**
     * Creates a new filtered channel output end around an existing channel end. The created channel end
     * can be shared by multiple processes.
     *
     * @param out the existing channel end.
     * @return the created channel end.
     */
    public FilteredSharedChannelOutput createFiltered(SharedChannelOutput out)
    {
        return new FilteredSharedChannelOutputWrapper(out);
    }
}
