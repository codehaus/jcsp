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

import org.jcsp.util.*;

/**
 * This class is an implementation of <code>Any2OneConnection</code>.
 * Each end is safe to be used by one thread at a time.
 *
 * @author Quickstone Technologies Limited
 */
class Any2OneConnectionImpl<T> implements Any2OneConnection<T>
{
    private AltingConnectionServer server;
    private One2OneChannel chanToServer;
    private One2OneChannel chanFromServer;
    private Any2OneChannel chanSynch;

    /**
     * Initializes all the attributes to necessary values.
     * Channels are created using the static factory in the
     * <code>ChannelServer</code> inteface.
     *
     * Constructor for One2OneConnectionImpl.
     */
    public Any2OneConnectionImpl() {
        super();
        chanToServer = ConnectionServer.FACTORY.createOne2One(new Buffer(1));
        chanFromServer = ConnectionServer.FACTORY.createOne2One(new Buffer(1));
        chanSynch = ConnectionServer.FACTORY.createAny2One(new Buffer(1));
        //create the server object - client object created when accessed
        server = new AltingConnectionServerImpl(chanToServer.in(), chanToServer.in());
    }

    /**
     * Returns the <code>AltingConnectionClient</code> that can
     * be used by a single process at any instance.
     *
     * @return the <code>AltingConnectionClient</code> object.
     */
    public SharedAltingConnectionClient client()
    {
        return new SharedAltingConnectionClient(chanFromServer.in(),
                                                chanSynch.in(),
                                                chanToServer.out(),
                                                chanToServer.out(),
                                                chanSynch.out(),
                                                chanFromServer.out(),
                                                this);
    }

    /**
     * Returns the <code>AltingConnectionServer</code> that can
     * be used by a single process at any instance.
     *
     * @return the <code>AltingConnectionServer</code> object.
     */
    public AltingConnectionServer server()
    {
        return server;
    }
}
