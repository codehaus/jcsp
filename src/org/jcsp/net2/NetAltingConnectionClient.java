package org.jcsp.net2;

import java.io.IOException;

import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.AltingConnectionClient;
import org.jcsp.lang.Any2OneChannel;
import org.jcsp.lang.Channel;
import org.jcsp.lang.ChannelOutput;
import org.jcsp.util.InfiniteBuffer;

public final class NetAltingConnectionClient
    extends AltingConnectionClient
    implements NetConnectionClient
{
    private final AltingChannelInput in;

    private final ChannelOutput toLinkTX;

    private final Link linkConnectedTo;

    private final NetConnectionLocation serverLocation;

    private final NetConnectionLocation localLocation;

    private final ConnectionData localConnection;

    private final boolean isLocal;

    private final NetworkMessageFilter.FilterTx outputFilter;

    private final NetworkMessageFilter.FilterRx inputFilter;

    private final ConnectionData data;

    static NetAltingConnectionClient create(NetConnectionLocation loc, NetworkMessageFilter.FilterTx filterTX,
            NetworkMessageFilter.FilterRx filterRX)
        throws JCSPNetworkException
    {
        // Create the connection data structure
        ConnectionData data = new ConnectionData();

        // Create channel linking this to the Link level. This channel is used to receive response and
        // acknowledgement messages
        Any2OneChannel chan = Channel.any2one(new InfiniteBuffer());
        data.toConnection = chan.out();

        // Set state of connection
        data.state = ConnectionDataState.CLIENT_STATE_CLOSED;

        ConnectionManager.getInstance().create(data);

        ChannelOutput toLink;

        if (loc.getNodeID().equals(Node.getInstance().getNodeID()))
        {
            toLink = ConnectionManager.getInstance().getConnection(loc.getVConnN()).toConnection;
            return new NetAltingConnectionClient(chan.in(), toLink, null, data, loc, filterTX, filterRX);
        }

        Link link = LinkManager.getInstance().requestLink(loc.getNodeID());

        if (link == null)
        {
            link = LinkFactory.getLink(loc.getNodeID());
        }

        toLink = link.getTxChannel();

        return new NetAltingConnectionClient(chan.in(), toLink, link, data, loc, filterTX, filterRX);
    }

    private NetAltingConnectionClient(AltingChannelInput input, ChannelOutput toLink, Link link,
            ConnectionData connData, NetConnectionLocation loc, NetworkMessageFilter.FilterTx filterTX,
            NetworkMessageFilter.FilterRx filterRX)
    {
        super(input);
        this.toLinkTX = toLink;
        this.in = input;
        this.data = connData;
        this.serverLocation = loc;
        this.localLocation = new NetConnectionLocation(Node.getInstance().getNodeID(), connData.vconnn);
        this.outputFilter = filterTX;
        this.inputFilter = filterRX;

        if (link != null)
        {
            this.linkConnectedTo = link;
            // TODO: registration stuff

            this.isLocal = false;
            this.localConnection = null;
        }
        else
        {
            this.isLocal = true;
            this.localConnection = ConnectionManager.getInstance().getConnection(this.serverLocation.getVConnN());
            this.linkConnectedTo = null;
        }
    }

    public boolean isOpen()
        throws IllegalStateException, JCSPNetworkException
    {
        if (this.data.state == ConnectionDataState.CLIENT_STATE_MADE_REQ)
            throw new IllegalStateException("Can only call isOpen after a reply has been received from the server");
        if (this.data.state == ConnectionDataState.DESTROYED)
            throw new JCSPNetworkException("Client connection end has been destroyed");
        if (this.data.state == ConnectionDataState.BROKEN)
            throw new JCSPNetworkException("Client connection end has broken");
        return this.data.state == ConnectionDataState.CLIENT_STATE_OPEN;
    }

    public Object reply()
        throws IllegalStateException, JCSPNetworkException
    {
        if (this.data.state != ConnectionDataState.CLIENT_STATE_MADE_REQ)
            throw new IllegalStateException("Can only call reply() after a request()");
        if (this.data.state == ConnectionDataState.DESTROYED)
            throw new JCSPNetworkException("Client connection end has been destroyed");
        if (this.data.state == ConnectionDataState.BROKEN)
            throw new JCSPNetworkException("Client connection end has broken");

        while (true)
        {
            NetworkMessage msg = (NetworkMessage)this.in.read();

            try
            {
                synchronized (this.data)
                {
                    switch (msg.type)
                    {
                        case NetworkProtocol.REPLY:
                        {
                            Object toReturn = this.inputFilter.filterRX(msg.data);
                            NetworkMessage ack = new NetworkMessage();
                            ack.type = NetworkProtocol.REPLY_ACK;
                            ack.attr1 = msg.attr2;
                            ack.attr2 = -1;
                            this.toLinkTX.write(ack);
                            this.data.state = ConnectionDataState.CLIENT_STATE_OPEN;
                            return toReturn;
                        }
                        case NetworkProtocol.REPLY_AND_CLOSE:
                        {
                            Object toReturn = this.inputFilter.filterRX(msg.data);
                            NetworkMessage ack = new NetworkMessage();
                            ack.attr1 = msg.attr2;
                            ack.attr2 = -1;
                            this.toLinkTX.write(ack);
                            this.data.state = ConnectionDataState.CLIENT_STATE_CLOSED;
                            return toReturn;
                        }
                        case NetworkProtocol.LINK_LOST:
                        {
                            this.data.state = ConnectionDataState.BROKEN;
                            ConnectionManager.getInstance().removeConnection(this.data);
                            throw new JCSPNetworkException("Link to server Node was lost.  Reply cannot complete");
                        }
                        case NetworkProtocol.REJECT_CONNECTION:
                        {
                            this.data.state = ConnectionDataState.BROKEN;
                            ConnectionManager.getInstance().removeConnection(this.data);
                            throw new JCSPNetworkException("Server connection rejected previous request");
                        }
                        default:
                        {
                            this.data.state = ConnectionDataState.BROKEN;
                            ConnectionManager.getInstance().removeConnection(this.data);
                            Node.err.log(this.getClass(), "Connection " + this.data.vconnn
                                                          + " received unexpected message");
                            throw new JCSPNetworkException("NetAltingConnectionClient received unexpected message");
                        }
                    }
                }
            }
            catch (IOException ioe)
            {
                throw new JCSPNetworkException("Incoming message was corrupted");
            }
        }
    }

    public void request(Object obj)
        throws IllegalStateException, JCSPNetworkException
    {
        if (this.data.state == ConnectionDataState.CLIENT_STATE_MADE_REQ)
            throw new IllegalStateException("Cannot call request(Object) twice without calling reply()");
        if (this.data.state == ConnectionDataState.DESTROYED)
            throw new JCSPNetworkException("Client connection end has been destroyed");
        if (this.data.state == ConnectionDataState.BROKEN)
            throw new JCSPNetworkException("Client connection end has broken");

        if (this.in.pending())
        {
            NetworkMessage msg = (NetworkMessage)this.in.read();

            synchronized (this.data)
            {
                if (msg.type == NetworkProtocol.LINK_LOST)
                {
                    this.data.state = ConnectionDataState.BROKEN;
                    ConnectionManager.getInstance().removeConnection(this.data);
                    throw new JCSPNetworkException("Link to server Node lost.  Send cannot complete");
                }
                Node.err.log(this.getClass(), "Connection " + this.data.vconnn + " reports unexpected message");
                throw new JCSPNetworkException("NetAltingConnecionClient received an unexpected message");
            }
        }

        NetworkMessage msg = new NetworkMessage();

        if (this.data.state == ConnectionDataState.CLIENT_STATE_CLOSED)
        {
            msg.type = NetworkProtocol.OPEN;
        }
        else
        {
            msg.type = NetworkProtocol.REQUEST;
        }

        msg.attr1 = this.serverLocation.getVConnN();
        msg.attr2 = this.data.vconnn;

        try
        {
            msg.data = this.outputFilter.filterTX(obj);

            synchronized (this.data)
            {
                this.data.state = ConnectionDataState.CLIENT_STATE_MADE_REQ;
            }

            if (!this.isLocal)
            {
                this.toLinkTX.write(msg);
            }
            else
            {
                synchronized (this.localConnection)
                {
                    switch (this.localConnection.state)
                    {
                        case ConnectionDataState.SERVER_STATE_CLOSED:
                        case ConnectionDataState.SERVER_STATE_OPEN:
                        case ConnectionDataState.SERVER_STATE_RECEIVED:
                            msg.toLink = this.data.toConnection;
                            this.localConnection.openServer.write(msg);
                            break;

                        default:
                            this.data.state = ConnectionDataState.BROKEN;
                            ConnectionManager.getInstance().removeConnection(this.data);
                            throw new JCSPNetworkException("Connection rejected during request");
                    }
                }
            }
        }
        catch (IOException ioe)
        {
            throw new JCSPNetworkException("Error when trying to convert the message for sending");
        }

        NetworkMessage reply = (NetworkMessage)this.in.read();

        if (reply.type == NetworkProtocol.REJECT_CONNECTION)
        {
            this.data.state = ConnectionDataState.BROKEN;
            ConnectionManager.getInstance().removeConnection(this.data);
            throw new JCSPNetworkException("Connection rejected during request");
        }
        else if (reply.type == NetworkProtocol.LINK_LOST)
        {
            this.data.state = ConnectionDataState.BROKEN;
            ConnectionManager.getInstance().removeConnection(this.data);
            throw new JCSPNetworkException("Link to server Node was lost.  Request cannot complete");
        }
        else if (reply.type == NetworkProtocol.REQUEST_ACK)
        {
            return;
        }
        else
        {
            Node.err.log(this.getClass(), "Connection " + this.data.vconnn + " reports unexpected message.");
            throw new JCSPNetworkException("NetAltingConnectionClient received an unexpected message");
        }
    }

    public void destroy()
    {
        synchronized (this.data)
        {
            this.data.state = ConnectionDataState.DESTROYED;
            ConnectionManager.getInstance().removeConnection(this.data);
            // TODO: deregistration from link

            // Deal with left over messages
            while (this.in.pending())
            {
                NetworkMessage msg = (NetworkMessage)this.in.read();
                switch (msg.type)
                {
                    case NetworkProtocol.REPLY:
                    case NetworkProtocol.REPLY_AND_CLOSE:
                    {
                        NetworkMessage reply = new NetworkMessage();
                        reply.type = NetworkProtocol.REJECT_CONNECTION;
                        reply.attr1 = msg.attr2;
                        reply.attr2 = -1;
                        this.toLinkTX.write(reply);
                        break;
                    }

                    case NetworkProtocol.REQUEST_ACK:
                    {
                        NetworkMessage reply = new NetworkMessage();
                        reply.type = NetworkProtocol.REJECT_CONNECTION;
                        reply.attr1 = msg.attr1;
                        reply.attr2 = -1;
                        this.toLinkTX.write(reply);
                        break;
                    }

                    default:
                        break;
                }
            }
        }
    }

    public NetLocation getLocation()
    {
        return this.serverLocation;
    }

    NetConnectionLocation getLocalLocation()
    {
        return this.localLocation;
    }

    final ConnectionData getConnectionData()
    {
        return this.data;
    }
}
