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
 * This class provides static factory methods for constructing
 * different types of connection. The methods are equivalent to
 * the non-static methods of the <code>StandardConnectionFactory</code>
 * class.
 *
 * @author Quickstone Technologies Limited
 */
public class Connection
{
    private static StandardConnectionFactory factory = new StandardConnectionFactory();

    /**
     * Constructor for Connection.
     */
    private Connection()
    {
        super();
    }


    /**
     * @see org.jcsp.lang.ConnectionFactory#createOne2One()
     */
    public static One2OneConnection createOne2One()
    {
        return factory.createOne2One();
    }

    /**
     * @see org.jcsp.lang.ConnectionFactory#createAny2One()
     */
    public static Any2OneConnection createAny2One()
    {
        return factory.createAny2One();
    }

    /**
     * @see org.jcsp.lang.ConnectionFactory#createOne2Any()
     */
    public static One2AnyConnection createOne2Any()
    {
        return factory.createOne2Any();
    }

    /**
     * @see org.jcsp.lang.ConnectionFactory#createAny2Any()
     */
    public static Any2AnyConnection createAny2Any()
    {
        return factory.createAny2Any();
    }

    /**
     * @see org.jcsp.lang.ConnectionArrayFactory#createOne2One(int)
     */
    public static One2OneConnection[] createOne2One(int n)
    {
        return factory.createOne2One(n);
    }

    /**
     * @see org.jcsp.lang.ConnectionArrayFactory#createAny2One(int)
     */
    public static Any2OneConnection[] any2oneArray(int n)
    {
        return factory.createAny2One(n);
    }

    /**
     * @see org.jcsp.lang.ConnectionArrayFactory#createOne2Any(int)
     */
    public static One2AnyConnection[] createOne2Any(int n)
    {
        return factory.createOne2Any(n);
    }

    /**
     * @see org.jcsp.lang.ConnectionArrayFactory#createAny2Any(int)
     */
    public static Any2AnyConnection[] createAny2Any(int n)
    {
        return factory.createAny2Any(n);
    }

    /**
     * Returns an array of client connection ends suitable for use as guards in an <code>Alternative</code>
     * construct.
     *
     * @param c the connection array to get the client ends from.
     * @return the array of client ends.
     */
    public static AltingConnectionClient[] getClientArray(One2AnyConnection[] c)
    {
        AltingConnectionClient r[] = new AltingConnectionClient[c.length];
        for (int i = 0; i < c.length; i++)
            r[i] = c[i].client();
        return r;
    }

    /**
     * Returns an array of client connection ends suitable for use as guards in an <code>Alternative</code>
     * construct.
     *
     * @param c the connection array to get the client ends from.
     * @return the array of client ends.
     */
    public static AltingConnectionClient[] getClientArray(One2OneConnection[] c)
    {
        AltingConnectionClient r[] = new AltingConnectionClient[c.length];
        for (int i = 0; i < c.length; i++)
            r[i] = c[i].client();
        return r;
    }

    /**
     * Returns an array of client connection ends suitable for use by multiple concurrent
     * processes.
     *
     * @param c the connection array to get the client ends from.
     * @return the array of client ends.
     */
    public static SharedConnectionClient[] getClientArray(Any2AnyConnection[] c)
    {
        SharedConnectionClient r[] = new SharedConnectionClient[c.length];
        for (int i = 0; i < c.length; i++)
            r[i] = c[i].client();
        return r;
    }

    /**
     * Returns an array of client connection ends suitable for use by multiple concurrent
     * processes.
     *
     * @param c the connection array to get the client ends from.
     * @return the array of client ends.
     */
    public static SharedConnectionClient[] getClientArray(Any2OneConnection[] c)
    {
        SharedConnectionClient r[] = new SharedConnectionClient[c.length];
        for (int i = 0; i < c.length; i++)
            r[i] = c[i].client();
        return r;
    }

    /**
     * Returns an array of server connection ends suitable for use as guards in an <code>Alternative</code>
     * construct.
     *
     * @param c the connection array to get the server ends from.
     * @return the array of server ends.
     */
    public static AltingConnectionServer[] getServerArray(Any2OneConnection[] c)
    {
        AltingConnectionServer r[] = new AltingConnectionServer[c.length];
        for (int i = 0; i < c.length; i++)
            r[i] = c[i].server();
        return r;
    }

    /**
     * Returns an array of server connection ends suitable for use as guards in an <code>Alternative</code>
     * construct.
     *
     * @param c the connection array to get the server ends from.
     * @return the array of server ends.
     */
    public static AltingConnectionServer[] getServerArray(One2OneConnection[] c)
    {
        AltingConnectionServer r[] = new AltingConnectionServer[c.length];
        for (int i = 0; i < c.length; i++)
            r[i] = c[i].server();
        return r;
    }

    /**
     * Returns an array of server connection ends suitable for use by multiple concurrent
     * processes.
     *
     * @param c the connection array to get the server ends from.
     * @return the array of server ends.
     */
    public static SharedConnectionServer[] getServerArray(Any2AnyConnection[] c)
    {
        SharedConnectionServer r[] = new SharedConnectionServer[c.length];
        for (int i = 0; i < c.length; i++)
            r[i] = c[i].server();
        return r;
    }

    /**
     * Returns an array of server connection ends suitable for use by multiple concurrent
     * processes.
     *
     * @param c the connection array to get the server ends from.
     * @return the array of server ends.
     */
    public static SharedConnectionServer[] getServerArray(One2AnyConnection[] c)
    {
        SharedConnectionServer r[] = new SharedConnectionServer[c.length];
        for (int i = 0; i < c.length; i++)
            r[i] = c[i].server();
        return r;
    }
}
