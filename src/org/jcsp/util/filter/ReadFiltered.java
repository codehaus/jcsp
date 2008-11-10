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

/**
 * <p>Interface for a channel end supporting read filtering operations. A channel end that implements this
 * interface can have instances of the <code>Filter</code> interface installed to apply transformations
 * on data as it is read from the channel.</p>
 *
 * <p>Multiple filters can be installed and referenced by a zero-based index to specify a specific
 * ordering.</p>
 *
 * <p>If multiple filters are installed, they are applied in order of increasing index. For example:</p>
 *
 * <pre>
 *   FilteredChannelInput in = ...;
 *
 *   Filter f1 = ...;
 *   Filter f2 = ...;
 *
 *   in.addReadFilter (f1, 0);
 *   in.addReadFilter (f2, 1);
 * </pre>
 *
 * <p>The <code>in.read()</code> method will return <code>f2.filter (f1.filter (obj))</code> where
 * <code>obj</code> is the data value that would have been delivered in the absence of filters.</p>
 *
 * @see org.jcsp.util.filter.Filter
 * @see org.jcsp.util.filter.FilteredChannelInput
 *
 * @author Quickstone Technologies Limited
 */
public interface ReadFiltered
{
    /**
     * Installs a read filter defining a transformation to be applied by the <code>read</code> method of the
     * channel end. The filter will be appended to the end of the current list, making it the last to
     * be applied.
     *
     * @param filter the filter to be installed; may not be null.
     */
    public void addReadFilter(Filter filter);

    /**
     * Installs a read filter defining a transformation to be applied by the <code>read</code> method of the
     * channel end at a specific index. If there is already a filter at that index position the existing
     * filters are shifted to make room. If the index is greater than the number of filters already
     * installed the filter is placed at the end.
     *
     * @param filter the filter to be installed; may not be null.
     * @param index the zero based index; may not be negative.
     */
    public void addReadFilter(Filter filter, int index);

    /**
     * Removes the first read filter (lowest index) matching the filter given as a parameter. The filter
     * removed, <code>r</code>, will satisfy the condition <code>r.equals (filter)</code>. The remaining
     * filters are shifted to close the gap in the index allocation.
     *
     * @param filter the filter to be removed; may not be null.
     */
    public void removeReadFilter(Filter filter);

    /**
     * Removes the read filter installed at the given index. The remaining filters are shifted to close the
     * gap in the index allocation.
     *
     * @param index zero-based index of the filter to be removed.
     */
    public void removeReadFilter(int index);

    /**
     * Returns the read filter installed at the given index.
     *
     * @param index zero-based index of the filter to return.
     * @return the filter at that position.
     */
    public Filter getReadFilter(int index);

    /**
     * Returns the number of read filters currently installed.
     */
    public int getReadFilterCount();
}
