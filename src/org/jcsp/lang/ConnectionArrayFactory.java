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
 * Defines an interface for a factory that can create arrays of connections.
 *
 * @author Quickstone Technologies Limited
 */
public interface ConnectionArrayFactory
{
    /**
     * Constructs and returns an array of instances of an implementation of
     * <code>One2OneConnection</code>.
     *
     * @param n	the number of <code>One2OneConnection</code> objects
     * 			    to construct.
     *
     * @return	the constructed array of <code>One2OneConnection</code>
     *          objects.
     */
    public <T> One2OneConnection<T>[] createOne2One(int n);

    /**
     * Constructs and returns an array of instances of an implementation of
     * <code>Any2OneConnection</code>.
     *
     * @param n	the number of <code>Any2OneConnection</code> objects
     * 			    to construct.
     *
     * @return	the constructed array of <code>Any2OneConnection</code>
     *          objects.
     */
    public <T> Any2OneConnection<T>[] createAny2One(int n);

    /**
     * Constructs and returns an array of instances of an implementation of
     * <code>One2AnyConnection</code>.
     *
     * @param n	the number of <code>One2AnyConnection</code> objects
     * 			    to construct.
     *
     * @return	the constructed array of <code>One2AnyConnection</code>
     * 			objects.
     */
    public <T> One2AnyConnection<T>[] createOne2Any(int n);

    /**
     * Constructs and returns an array of instances of an implementation of
     * <code>Any2AnyConnection</code>.
     *
     * @param n	the number of <code>Any2AnyConnection</code> objects
     * 			    to construct.
     *
     * @return	the constructed array of <code>Any2AnyConnection</code>
     * 			objects.
     */
    public <T> Any2AnyConnection<T>[] createAny2Any(int n);
}
