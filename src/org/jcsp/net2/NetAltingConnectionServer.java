package org.jcsp.net2;

import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.AltingConnectionServer;
import org.jcsp.lang.Any2OneChannel;
import org.jcsp.lang.Channel;
import org.jcsp.util.InfiniteBuffer;

public final class NetAltingConnectionServer
    extends AltingConnectionServer
    implements NetConnectionServer
{
    private final AltingChannelInput requestIn;

    private final AltingChannelInput openIn;

    private final NetConnectionLocation location;

    private final NetworkMessageFilter.FilterRx inputFilter;

    private final NetworkMessageFilter.FilterTx outputFilter;

    private final ConnectionData data;

    private final NetworkMessage lastRead = null;

    private final Link linkConnectedTo = null;

    static NetAltingConnectionServer create(int index, NetworkMessageFilter.FilterRx filterRX,
            NetworkMessageFilter.FilterTx filterTX)
        throws IllegalArgumentException
    {
        ConnectionData data = new ConnectionData();
        Any2OneChannel requestChan = Channel.any2one(new InfiniteBuffer());
        Any2OneChannel openChan = Channel.any2one(new InfiniteBuffer());
        data.toConnection = requestChan.out();
        data.openServer = openChan.out();
        data.state = ConnectionDataState.SERVER_STATE_CLOSED;
        ConnectionManager.getInstance().create(index, data);
        return new NetAltingConnectionServer(openChan.in(), requestChan.in(), data, filterRX, filterTX);
    }

    static NetAltingConnectionServer create(NetworkMessageFilter.FilterRx filterRX,
            NetworkMessageFilter.FilterTx filterTX)
    {
        ConnectionData data = new ConnectionData();
        Any2OneChannel requestChan = Channel.any2one(new InfiniteBuffer());
        Any2OneChannel openChan = Channel.any2one(new InfiniteBuffer());
        data.toConnection = requestChan.out();
        data.openServer = openChan.out();
        data.state = ConnectionDataState.SERVER_STATE_CLOSED;
        ConnectionManager.getInstance().create(data);
        return new NetAltingConnectionServer(openChan.in(), requestChan.in(), data, filterRX, filterTX);
    }

    private NetAltingConnectionServer(AltingChannelInput openChan, AltingChannelInput requestChan,
            ConnectionData connData, NetworkMessageFilter.FilterRx filterRX, NetworkMessageFilter.FilterTx filterTX)
        throws JCSPNetworkException
    {
        super(openChan);
        this.openIn = openChan;
        this.requestIn = requestChan;
        this.data = connData;
        this.inputFilter = filterRX;
        this.outputFilter = filterTX;
        this.location = new NetConnectionLocation(Node.getInstance().getNodeID(), this.data.vconnn);
    }

    public void destroy()
    {
        // TODO Auto-generated method stub

    }

    public NetLocation getLocation()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void reply(Object data, boolean close)
        throws IllegalStateException
    {
        // TODO Auto-generated method stub

    }

    public void reply(Object data)
        throws IllegalStateException
    {
        // TODO Auto-generated method stub

    }

    public void replyAndClose(Object data)
        throws IllegalStateException
    {
        // TODO Auto-generated method stub

    }

    public Object request()
        throws IllegalStateException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
