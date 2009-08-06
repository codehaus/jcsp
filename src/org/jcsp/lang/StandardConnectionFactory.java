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
 * <p>
 * Implements a factory for creating connections.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public class StandardConnectionFactory
        implements ConnectionFactory, ConnectionArrayFactory
{
    /**
     * @see ConnectionFactory#createOne2One
     */
    public <T> One2OneConnection<T> createOne2One()
    {
        return new One2OneConnectionImpl<T>();
    }

    /**
     * @see ConnectionFactory#createAny2One
     */
    public <T> Any2OneConnection<T> createAny2One()
    {
        return new Any2OneConnectionImpl<T>();
    }

    /**
     * @see ConnectionFactory#createOne2Any
     */
    public <T> One2AnyConnection<T> createOne2Any()
    {
        return new One2AnyConnectionImpl<T>();
    }

    /**
     * @see ConnectionFactory#createAny2Any
     */
    public <T> Any2AnyConnection<T> createAny2Any()
    {
        return new Any2AnyConnectionImpl<T>();
    }

    /**
     * @see ConnectionArrayFactory#createOne2One
     */
    public <T> One2OneConnection<T>[] createOne2One(int n)
    {
        One2OneConnection<T>[] toReturn = (One2OneConnection<T>[]) new One2OneConnection[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createOne2One();
        return toReturn;
    }

    /**
     * @see ConnectionArrayFactory#createAny2One
     */
    public <T> Any2OneConnection<T>[] createAny2One(int n)
    {
        Any2OneConnection<T>[] toReturn = (Any2OneConnection<T>[]) new Any2OneConnection[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createAny2One();
        return toReturn;
    }

    /**
     * @see ConnectionArrayFactory#createOne2Any
     */
    public <T> One2AnyConnection<T>[] createOne2Any(int n)
    {
        One2AnyConnection<T>[] toReturn = (One2AnyConnection<T>[]) new One2AnyConnection[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createOne2Any();
        return toReturn;
    }

    /**
     * @see ConnectionArrayFactory#createAny2Any
     */
    public <T> Any2AnyConnection<T>[] createAny2Any(int n)
    {
        Any2AnyConnection<T>[] toReturn = (Any2AnyConnection<T>[]) new Any2AnyConnection[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createAny2Any();
        return toReturn;
    }
}
