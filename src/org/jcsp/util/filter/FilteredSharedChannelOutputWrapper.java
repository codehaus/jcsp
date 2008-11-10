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
 * This is wrapper for a <code>SharedChannelOutput</code> that adds
 * write filtering. Instances of this class can be safely used by
 * multiple concurrent processes.
 *
 * @author Quickstone Technologies Limited
 */
public class FilteredSharedChannelOutputWrapper
        extends FilteredChannelOutputWrapper
        implements FilteredSharedChannelOutput
{    

    /**
     * The synchronization object to protect the writers from each other when they read data or update
     * the write filters.
     */
    private Object synchObject;

    /**
     * Constructs a new wrapper for the given channel output end.
     *
     * @param out the existing channel end.
     */
    public FilteredSharedChannelOutputWrapper(SharedChannelOutput out)
    {
        super(out);
        synchObject = new Object();
    }

    public void write(Object data)
    {
        synchronized (synchObject)
        {
            super.write(data);
        }
    }

    public void addWriteFilter(Filter filter)
    {
        synchronized (synchObject)
        {
            super.addWriteFilter(filter);
        }
    }

    public void addWriteFilter(Filter filter, int index)
    {
        synchronized (synchObject)
        {
            super.addWriteFilter(filter, index);
        }
    }

    public void removeWriteFilter(Filter filter)
    {
        synchronized (synchObject)
        {
            super.removeWriteFilter(filter);
        }
    }

    public void removeWriteFilter(int index)
    {
        synchronized (synchObject)
        {
            super.removeWriteFilter(index);
        }
    }

    public Filter getWriteFilter(int index)
    {
        synchronized (synchObject)
        {
            return super.getWriteFilter(index);
        }
    }

    public int getWriteFilterCount()
    {
        synchronized (synchObject)
        {
            return super.getWriteFilterCount();
        }
    }
}
