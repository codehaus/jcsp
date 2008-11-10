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
 * This is wrapper for a <code>SharedChannelInput</code> that adds
 * read filtering. Instances of this class can be safely used by
 * multiple concurrent processes.
 *
 * @author Quickstone Technologies Limited
 */
public class FilteredSharedChannelInputWrapper
        extends FilteredChannelInputWrapper
        implements FilteredSharedChannelInput
{    
    /**
     * The object used for synchronization by the methods here to protect the readers from each other
     * when manipulating the filters and reading data.
     */
    private Object synchObject;

    /**
     * Constructs a new wrapper for the given channel input end.
     *
     * @param in the existing channel end.
     */
    public FilteredSharedChannelInputWrapper(SharedChannelInput in)
    {
        super(in);        
        synchObject = new Object();
    }

    public Object read()
    {
        synchronized (synchObject)
        {
            return super.read();
        }
    }

    public void addReadFilter(Filter filter)
    {
        synchronized (synchObject)
        {
            super.addReadFilter(filter);
        }
    }

    public void addReadFilter(Filter filter, int index)
    {
        synchronized (synchObject)
        {
            super.addReadFilter(filter, index);
        }
    }

    public void removeReadFilter(Filter filter)
    {
        synchronized (synchObject)
        {
            super.removeReadFilter(filter);
        }
    }

    public void removeReadFilter(int index)
    {
        synchronized (synchObject)
        {
            super.removeReadFilter(index);
        }
    }

    public Filter getReadFilter(int index)
    {
        synchronized (synchObject)
        {
            return super.getReadFilter(index);
        }
    }

    public int getReadFilterCount()
    {
        synchronized (synchObject)
        {
            return super.getReadFilterCount();
        }
    }
}
