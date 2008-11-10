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
 * Interface for channel plug-ins that define filtering operations -
 * transformations on the data as it is read or written. A channel (or channel end) that supports
 * filtering will implement the <code>ReadFiltered</code> or <code>WriteFiltered</code> interface which
 * allows instances of <code>Filter</code> to be installed or removed from the channel.
 *
 * @see org.jcsp.util.filter.FilteredChannel
 * @see org.jcsp.util.filter.FilteredChannelEnd
 * @see org.jcsp.util.filter.ReadFiltered
 * @see org.jcsp.util.filter.WriteFiltered
 *
 * @author Quickstone Technologies Limited
 */
public interface Filter
{
    /**
     * Applies the filter operation. The object given can be modified and returned or another object
     * substituted in its place.
     *
     * @param obj the original object in the channel communication.
     * @return the modified/substituted object after filtration.
     */
    public Object filter(Object obj);
}
