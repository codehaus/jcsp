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

import org.jcsp.util.Buffer;

/**
 * This class is an implementation of <code>One2AnyConnection</code>.
 * Each end is safe to be used by one thread at a time.
 *
 * @author Quickstone Technologies Limited
 */
class One2AnyConnectionImpl<T> implements One2AnyConnection<T>
{
    private AltingConnectionClient client;
    private One2OneChannel<T> chanToServer;
    private One2OneChannel<T> chanFromServer;
    private Any2OneChannel<T> chanSynch;

    /**
     * Initializes all the attributes to necessary values.
     * Channels are created using the static factory in the
     * <code>ChannelServer</code> interface.
     *
     * Constructor for One2OneConnectionImpl.
     */
    public One2AnyConnectionImpl()
    {
        super();
        chanToServer = (One2OneChannel<T>) ConnectionServer.FACTORY.createOne2One(new Buffer(1));
        chanFromServer = (One2OneChannel<T>) ConnectionServer.FACTORY.createOne2One(new Buffer(1));
        chanSynch = (Any2OneChannel<T>) ConnectionServer.FACTORY.createAny2One(new Buffer(1));

        //create the client and server objects
        client = new AltingConnectionClientImpl(chanFromServer.in(),
                                                chanToServer.out(),
                                                chanToServer.out(),
                                                chanFromServer.out());
    }

    /**
     * Returns the <code>AltingConnectionClient</code> that can
     * be used by a single process at any instance.
     *
     * Each call to this method will return the same object reference.
     *
     * @return the <code>AltingConnectionClient</code> object.
     */
    public AltingConnectionClient client()
    {
        return client;
    }

    /**
     * Returns a <code>SharedConnectionServer</code> object for this
     * connection. This method can be called multiple times to return a new
     * <code>SharedConnectionServer</code> object each time. Any object
     * created can only be used by one process at a time but the set of
     * objects constructed can be used concurrently.
     *
     * @return a new <code>SharedConnectionServer</code> object.
     */
    public SharedConnectionServer server()
    {
        return new SharedConnectionServerImpl(chanToServer.in(),
                                              chanToServer.in(), chanSynch.in(),
                                              chanSynch.out(), this);
    }
}
