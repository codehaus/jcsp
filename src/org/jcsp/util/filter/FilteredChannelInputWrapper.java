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
 * Wrapper for an input channel end to include read filtering functionality.
 *
 * @author Quickstone Technologies Limited
 */
class FilteredChannelInputWrapper
        extends ChannelInputWrapper
        implements FilteredChannelInput
{
    /**
     * Set of read filters installed.
     */
    private FilterHolder filters = null;

    /**
     * Constructs a new <code>FilteredChannelInputWrapper</code> around the existing channel end.
     *
     * @param in channel end to create the wrapper around.
     */
    FilteredChannelInputWrapper(ChannelInput in)
    {
        super(in);
    }

    public Object read()
    {
        Object toFilter = super.read();
        for (int i = 0; filters != null && i < filters.getFilterCount(); i++)
            toFilter = filters.getFilter(i).filter(toFilter);
        return toFilter;
    }

    public void addReadFilter(Filter filter)
    {
        if (filters == null)
            filters = new FilterHolder();
        filters.addFilter(filter);
    }

    public void addReadFilter(Filter filter, int index)
    {
        if (filters == null)
            filters = new FilterHolder();
        filters.addFilter(filter, index);
    }

    public void removeReadFilter(Filter filter)
    {
        if (filters == null)
            filters = new FilterHolder();
        filters.removeFilter(filter);
    }

    public void removeReadFilter(int index)
    {
        if (filters == null)
            filters = new FilterHolder();
        filters.removeFilter(index);
    }

    public Filter getReadFilter(int index)
    {
        if (filters == null)
            filters = new FilterHolder();
        return filters.getFilter(index);
    }

    public int getReadFilterCount()
    {
        if (filters == null)
            return 0;
        return filters.getFilterCount();
    }
}
