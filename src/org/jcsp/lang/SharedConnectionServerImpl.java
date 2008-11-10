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
 * This class does not need to be used by standard JCSP users. It is exposed so that the connection
 * mechanism can be extended for custom connections.
 *
 * @author Quickstone Technologies Limited
 */
public class SharedConnectionServerImpl implements SharedConnectionServer
{
    private AltingConnectionServerImpl connectionServerToUse;

    private ChannelInput synchIn;
    private ChannelOutput synchOut;
    private ConnectionWithSharedAltingServer parent;

    protected SharedConnectionServerImpl(AltingChannelInput openIn,
                                         AltingChannelInput requestIn,
                                         ChannelInput synchIn,
                                         SharedChannelOutput synchOut,
                                         ConnectionWithSharedAltingServer parent)
    {
        connectionServerToUse = new AltingConnectionServerImpl(openIn, requestIn);
        this.synchOut = synchOut;
        this.synchIn = synchIn;
        this.parent = parent;
    }

    public Object request()
    {
        if (connectionServerToUse.getServerState() == AltingConnectionServerImpl.SERVER_STATE_CLOSED)
            synchOut.write(null);
        return connectionServerToUse.request();
    }

    public void reply(Object data)
    {
        reply(data, false);
    }

    public void reply(Object data, boolean close)
    {
        connectionServerToUse.reply(data, close);
        if (connectionServerToUse.getServerState() == AltingConnectionServerImpl.SERVER_STATE_CLOSED)
            synchIn.read();
    }

    public void replyAndClose(Object data)
    {
        reply(data, true);
    }

    public SharedConnectionServer duplicate()
    {
        return parent.server();
    }
}
