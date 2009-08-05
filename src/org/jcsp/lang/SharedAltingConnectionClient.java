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
 * Implements a client end of a Connection which can have multiple
 * client processes.
 * </p>
 * <p>
 * This object cannot itself be shared between concurrent processes
 * but duplicate objects can be generated that can be used by
 * multiple concurrent processes. This can be achieved using
 * the <code>{@link #duplicate()}</code> method.
 * </p>
 * <p>
 * The reply from the server can be ALTed over.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public class SharedAltingConnectionClient<T>
        extends AltingConnectionClientImpl<T>
        implements SharedConnectionClient<T>
{
    private ChannelInput synchIn;
    private ChannelOutput synchOut;
    private ConnectionWithSharedAltingClient parent;

    protected SharedAltingConnectionClient(AltingChannelInput fromServer,
                                           ChannelInput synchIn,
                                           ChannelOutput openToServer,
                                           ChannelOutput reqToServer,
                                           SharedChannelOutput synchOut,
                                           ChannelOutput backToClient,
                                           ConnectionWithSharedAltingClient
                                           parent)
    {
        super(fromServer, openToServer, reqToServer, backToClient);
        this.synchIn = synchIn;
        this.synchOut = synchOut;
        this.parent = parent;
    }

    protected final void claim()
    {
        synchOut.write(null);
    }

    protected final void release()
    {
        synchIn.read();
    }

    /**
     * <p>
     * Returns a <code>SharedConnectionClient</code> object that is
     * a duplicate of the object on which this method is called.
     * </p>
     * <p>
     * This allows a process using a <code>SharedAltingConnectionClient</code>
     * object to pass references to the connection client to multiple
     * processes.
     * </p>
     * <p>
     * The object returned can be cast into a
     * <code>SharedConnectionClient</code>  object.
     * </p>
     *
     * @return  a duplicate <code>SharedAltingConnectionClient</code> object.
     */
    public SharedConnectionClient duplicate()
    {
        return parent.client();
    }
}
