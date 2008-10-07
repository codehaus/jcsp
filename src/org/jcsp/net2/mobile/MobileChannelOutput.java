package org.jcsp.net2.mobile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jcsp.net2.JCSPNetworkException;
import org.jcsp.net2.NetChannel;
import org.jcsp.net2.NetChannelLocation;
import org.jcsp.net2.NetChannelOutput;
import org.jcsp.net2.NetLocation;
import org.jcsp.net2.NetworkPoisonException;
import org.jcsp.net2.NetworkMessageFilter.FilterTx;

/**
 * @author Kevin
 */
public final class MobileChannelOutput
    implements NetChannelOutput, Serializable
{
    private NetChannelLocation msgBoxLocation;

    private transient NetChannelOutput actualOut;

    public MobileChannelOutput(NetChannelLocation loc)
    {
        this.msgBoxLocation = loc;
        this.actualOut = NetChannel.one2net(loc);
    }

    public MobileChannelOutput(NetChannelLocation loc, FilterTx encoder)
    {
        this.msgBoxLocation = loc;
        this.actualOut = NetChannel.one2net(loc, encoder);
    }

    public void write(Object object)
    {
        this.actualOut.write(object);
    }

    public void destroy()
    {
        this.actualOut.destroy();
    }

    public NetLocation getLocation()
    {
        return this.actualOut.getLocation();
    }

    public void poison(int strength)
    {
        this.actualOut.poison(strength);
    }

    public void asyncWrite(Object obj)
        throws JCSPNetworkException, NetworkPoisonException
    {
        this.actualOut.asyncWrite(obj);
    }

    public void setEncoder(FilterTx encoder)
    {
        this.actualOut.setEncoder(encoder);
    }

    private void writeObject(ObjectOutputStream output)
        throws IOException
    {
        output.writeObject(this.msgBoxLocation);
        this.actualOut.destroy();
    }

    private void readObject(ObjectInputStream input)
        throws IOException, ClassNotFoundException
    {
        this.msgBoxLocation = (NetChannelLocation)input.readObject();
        this.actualOut = NetChannel.one2net(this.msgBoxLocation);
    }

}
