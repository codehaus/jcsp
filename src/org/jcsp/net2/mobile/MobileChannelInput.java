package org.jcsp.net2.mobile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jcsp.lang.ProcessManager;
import org.jcsp.net2.NetAltingChannelInput;
import org.jcsp.net2.NetChannel;
import org.jcsp.net2.NetChannelInput;
import org.jcsp.net2.NetChannelLocation;
import org.jcsp.net2.NetChannelOutput;
import org.jcsp.net2.NetLocation;
import org.jcsp.net2.NetworkMessageFilter;
import org.jcsp.net2.ObjectNetworkMessageFilter;
import org.jcsp.net2.NetworkMessageFilter.FilterRx;

public final class MobileChannelInput
    implements NetChannelInput, Serializable
{

    private NetChannelLocation messageBoxLoc;

    private NetChannelLocation msgBoxReqLoc;

    private transient NetChannelInput actualIn;

    private transient NetChannelOutput toMessageBox;

    public MobileChannelInput()
    {
        NetAltingChannelInput toMsgBox = NetChannel.net2one();
        NetAltingChannelInput msgBoxReq = NetChannel.net2one();
        MessageBox msgBox = new MessageBox(toMsgBox, msgBoxReq, new ObjectNetworkMessageFilter.FilterTX());
        new ProcessManager(msgBox).start();
        this.messageBoxLoc = (NetChannelLocation)toMsgBox.getLocation();
        this.msgBoxReqLoc = (NetChannelLocation)msgBoxReq.getLocation();
        this.actualIn = NetChannel.net2one();
        this.toMessageBox = NetChannel.one2net(this.msgBoxReqLoc);
    }

    public MobileChannelInput(NetworkMessageFilter.FilterTx encoder, NetworkMessageFilter.FilterRx decoder)
    {
        NetAltingChannelInput toMsgBox = NetChannel.net2one(decoder);
        NetAltingChannelInput msgBoxReq = NetChannel.net2one();
        MessageBox msgBox = new MessageBox(toMsgBox, msgBoxReq, encoder);
        new ProcessManager(msgBox).start();
        this.messageBoxLoc = (NetChannelLocation)toMsgBox.getLocation();
        this.msgBoxReqLoc = (NetChannelLocation)msgBoxReq.getLocation();
        this.actualIn = NetChannel.net2one(decoder);
        this.toMessageBox = NetChannel.one2net(this.msgBoxReqLoc);
    }

    public void endRead()
    {
        this.actualIn.endRead();
    }

    public Object read()
    {
        MobileChannelMessage msg = new MobileChannelMessage();
        msg.type = MobileChannelMessage.REQUEST;
        msg.inputLocation = (NetChannelLocation)this.actualIn.getLocation();
        this.toMessageBox.write(msg);
        Object toReturn = this.actualIn.read();
        return toReturn;
    }

    public Object startRead()
    {
        MobileChannelMessage msg = new MobileChannelMessage();
        msg.type = MobileChannelMessage.REQUEST;
        msg.inputLocation = (NetChannelLocation)this.actualIn.getLocation();
        this.toMessageBox.write(msg);
        Object toReturn = this.actualIn.startRead();
        return toReturn;
    }

    public void poison(int strength)
    {
        this.actualIn.poison(strength);
    }

    public void destroy()
    {
        this.actualIn.destroy();
        this.toMessageBox.destroy();
    }

    public NetLocation getLocation()
    {
        return this.messageBoxLoc;
    }

    public void setDecoder(FilterRx decoder)
    {
        this.actualIn.setDecoder(decoder);
    }

    private void writeObject(ObjectOutputStream output)
        throws IOException
    {
        output.writeObject(this.messageBoxLoc);
        output.writeObject(this.msgBoxReqLoc);
        this.actualIn.destroy();
        this.toMessageBox.destroy();
    }

    private void readObject(ObjectInputStream input)
        throws IOException, ClassNotFoundException
    {
        this.messageBoxLoc = (NetChannelLocation)input.readObject();
        this.msgBoxReqLoc = (NetChannelLocation)input.readObject();
        this.actualIn = NetChannel.net2one();
        this.toMessageBox = NetChannel.one2net(this.messageBoxLoc);
    }
}
