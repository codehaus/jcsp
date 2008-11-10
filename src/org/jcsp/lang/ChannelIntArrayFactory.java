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

/**
 * Defines an interface for a factory that can create arrays of channels carrying integers.
 *
 * @author Quickstone Technologies Limited
 * 
 * @deprecated These channel factories are deprecated in favour of the new one2one() methods in the Channel class.
 */
public interface ChannelIntArrayFactory
{
    /**
     * Creates a populated array of <code>n</code> <code>One2One</code> channels.
     *
     * @param n the size of the array.
     * @return the created array of channels.
     */
    public One2OneChannelInt[] createOne2One(int n);

    /**
     * Creates a populated array of <code>n</code> <code>Any2One</code> channels.
     *
     * @param n the size of the array.
     * @return the created array of channels.
     */
    public Any2OneChannelInt[] createAny2One(int n);

    /**
     * Creates a populated array of <code>n</code> <code>One2Any</code> channels.
     *
     * @param n the size of the array.
     * @return the created array of channels.
     */
    public One2AnyChannelInt[] createOne2Any(int n);

    /**
     * Creates a populated array of <code>n</code> <code>Any2Any</code> channels.
     *
     * @param n the size of the array.
     * @return the created array of channels.
     */
    public Any2AnyChannelInt[] createAny2Any(int n);
}
