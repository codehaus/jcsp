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
 * This class is an implementation of <code>Any2AnyConnection</code>.
 * Each end is safe to be used by one thread at a time.
 *
 * @author Quickstone Technologies Limited
 */
class Any2AnyConnectionImpl<T> extends AbstractConnectionImpl implements Any2AnyConnection<T>
{
    private One2OneChannel<T> chanToServer;
    private One2OneChannel<T> chanFromServer;
    private Any2OneChannel<T> chanClientSynch;
    private Any2OneChannel<T> chanServerSynch;

    /**
     * Initializes all the attributes to necessary values.
     * Channels are created using the static factory in the
     * <code>ChannelServer</code> inteface.
     *
     * Constructor for One2OneConnectionImpl.
     */
    public Any2AnyConnectionImpl()
    {
        super();
        chanToServer = (One2OneChannel<T>) ConnectionServer.FACTORY.createOne2One(new Buffer(1));
        chanFromServer = (One2OneChannel<T>) ConnectionServer.FACTORY.createOne2One(new Buffer(1));
        chanClientSynch = (Any2OneChannel<T>) ConnectionServer.FACTORY.createAny2One(new Buffer(1));
        chanServerSynch = (Any2OneChannel<T>) ConnectionServer.FACTORY.createAny2One(new Buffer(1));
    }

    /**
     * Returns a <code>SharedAltingConnectionClient</code> object for this
     * connection. This method can be called multiple times to return a new
     * <code>SharedAltingConnectionClient</code> object each time. Any object
     * created can only be used by one process at a time but the set of
     * objects constructed can be used concurrently.
     *
     * @return a new <code>SharedAltingConnectionClient</code> object.
     */
    public SharedAltingConnectionClient client()
    {
        return new SharedAltingConnectionClient(
                chanFromServer.in(),
                chanClientSynch.in(),
                chanToServer.out(),
                chanToServer.out(),
                chanClientSynch.out(),
                chanFromServer.out(),
                this);
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
        return new SharedConnectionServerImpl(
                chanToServer.in(),
                chanToServer.in(),
                chanServerSynch.in(),
                chanServerSynch.out(),
                this);
    }
}
