    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2006 Peter Welch and Paul Austin.            //
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
    //  Author contact: P.H.Welch@ukc.ac.uk                             //
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
    public One2OneConnection createOne2One()
    {
        return new One2OneConnectionImpl();
    }

    /**
     * @see ConnectionFactory#createAny2One
     */
    public Any2OneConnection createAny2One()
    {
        return new Any2OneConnectionImpl();
    }

    /**
     * @see ConnectionFactory#createOne2Any
     */
    public One2AnyConnection createOne2Any()
    {
        return new One2AnyConnectionImpl();
    }

    /**
     * @see ConnectionFactory#createAny2Any
     */
    public Any2AnyConnection createAny2Any()
    {
        return new Any2AnyConnectionImpl();
    }

    /**
     * @see ConnectionArrayFactory#createOne2One
     */
    public One2OneConnection[] createOne2One(int n)
    {
        One2OneConnection[] toReturn = new One2OneConnection[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createOne2One();
        return toReturn;
    }

    /**
     * @see ConnectionArrayFactory#createAny2One
     */
    public Any2OneConnection[] createAny2One(int n)
    {
        Any2OneConnection[] toReturn = new Any2OneConnection[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createAny2One();
        return toReturn;
    }

    /**
     * @see ConnectionArrayFactory#createOne2Any
     */
    public One2AnyConnection[] createOne2Any(int n)
    {
        One2AnyConnection[] toReturn = new One2AnyConnection[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createOne2Any();
        return toReturn;
    }

    /**
     * @see ConnectionArrayFactory#createAny2Any
     */
    public Any2AnyConnection[] createAny2Any(int n)
    {
        Any2AnyConnection[] toReturn = new Any2AnyConnection[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = createAny2Any();
        return toReturn;
    }
}
