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
 * This class is an implementation of <code>One2OneConnection</code>.
 * Each end is safe to be used by one thread at a time.
 *
 * @author Quickstone Technologies Limited
 */
class One2OneConnectionImpl<T> extends AbstractConnectionImpl implements One2OneConnection<T>
{
    private AltingConnectionClient<T> client;
    private AltingConnectionServer<T> server;

    /**
     * Initializes all the attributes to necessary values.
     * Channels are created using the static factory in the
     * <code>ChannelServer</code> inteface.
     *
     * Constructor for One2OneConnectionImpl.
     */
    public One2OneConnectionImpl()
    {
        super();

// Okay, we can't use the static ConnectionServer.FACTORY any more if we want
// total type-safety...unless we want warnings. Going to leave as-is for now.
        One2OneChannel<ConnectionMessage<T>> chanToServer = (One2OneChannel<ConnectionMessage<T>>) ConnectionServer.FACTORY.createOne2One(new Buffer(1));
        One2OneChannel<ConnectionMessage<T>> chanFromServer = (One2OneChannel<ConnectionMessage<T>>) ConnectionServer.FACTORY.createOne2One(new Buffer(1));

        //create the client and server objects
        client = new AltingConnectionClientImpl<T>(chanFromServer.in(),
                                                chanToServer.out(),
                                                chanToServer.out(),
                                                chanFromServer.out());
        server = new AltingConnectionServerImpl<T>(chanToServer.in(),
                                                chanToServer.in());
    }

    /**
     * Returns the <code>AltingConnectionClient</code> that can
     * be used by a single process at any instance.
     *
     * This method will always return the same
     * <code>AltingConnectionClient</code> object.
     * <code>One2OneConnection</code> is only intendended to have two ends.
     *
     * @return the <code>AltingConnectionClient</code> object.
     */
    public AltingConnectionClient<T> client()
    {
        return client;
    }

    /**
     * Returns the <code>AltingConnectionServer</code> that can
     * be used by a single process at any instance.
     *
     * This method will always return the same
     * <code>AltingConnectionServer</code> object.
     * <code>One2OneConnection</code> is only intendended to have two ends.
     *
     * @return the <code>AltingConnectionServer</code> object.
     */
    public AltingConnectionServer<T> server()
    {
        return server;
    }
}
