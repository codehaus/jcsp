package org.jcsp.net2.mobile;

import org.jcsp.lang.Alternative;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Guard;
import org.jcsp.net2.JCSPNetworkException;
import org.jcsp.net2.NetAltingChannelInput;
import org.jcsp.net2.NetChannel;
import org.jcsp.net2.NetChannelLocation;
import org.jcsp.net2.NetChannelOutput;
import org.jcsp.net2.NetworkMessageFilter;
import org.jcsp.net2.Node;

final class MessageBox
    implements CSProcess
{
    private final NetAltingChannelInput in;

    private final NetAltingChannelInput fromInputEnd;

    private NetChannelOutput toInputEnd = null;

    private final NetworkMessageFilter.FilterTx encoder;

    private final NetChannelLocation inputEndLoc = null;

    MessageBox(NetAltingChannelInput intoBox, NetAltingChannelInput requestChannel,
            NetworkMessageFilter.FilterTx encodingFilter)
    {
        this.in = intoBox;
        this.fromInputEnd = requestChannel;
        this.encoder = encodingFilter;
    }

    public void run()
    {
        try
        {
            while (true)
            {
                MobileChannelMessage msg = (MobileChannelMessage)fromInputEnd.read();
                if (msg.type == MobileChannelMessage.REQUEST)
                {
                    if (!msg.inputLocation.equals(this.inputEndLoc))
                    {
                        if (this.toInputEnd != null)
                        {
                            this.toInputEnd.destroy();
                        }
                        this.toInputEnd = NetChannel.one2net(msg.inputLocation, this.encoder);
                    }
                    Object obj = this.in.read();
                    this.toInputEnd.write(obj);
                }
                else if (msg.type == MobileChannelMessage.CHECK)
                {
                    if (!msg.inputLocation.equals(this.inputEndLoc))
                    {
                        if (this.toInputEnd != null)
                        {
                            this.toInputEnd.destroy();
                        }
                        this.toInputEnd = NetChannel.one2net(msg.inputLocation, this.encoder);
                    }

                    MobileChannelMessage response = new MobileChannelMessage();
                    response.type = MobileChannelMessage.CHECK_RESPONSE;
                    if (this.in.pending())
                    {
                        response.ready = true;
                        this.toInputEnd.write(response);
                    }
                    else
                    {
                        this.toInputEnd.write(response);
                        Guard[] guards = { this.fromInputEnd, this.in };
                        Alternative alt = new Alternative(guards);
                        int selected = alt.priSelect();
                        if (selected == 1)
                        {
                            MobileChannelMessage resp = new MobileChannelMessage();
                            resp.type = MobileChannelMessage.CHECK_RESPONSE;
                            resp.ready = true;
                            try
                            {
                                // Try and write to the input end
                                this.toInputEnd.write(response);
                            }
                            catch (JCSPNetworkException ex)
                            {
                                // The channel input end is no longer there.
                                // Quietly ignore and wait for request.
                            }
                        }
                        // If a new message from the input end has been received, then deal with
                        // that message separately. Go into the main loop again.
                    }
                }
            }
        }
        catch (JCSPNetworkException jne)
        {
            // Something went wrong during comms. Kill the message box and all channels.
            this.in.destroy();
            this.fromInputEnd.destroy();
            if (this.toInputEnd != null)
            {
                this.toInputEnd.destroy();
            }
            Node.err.log(this.getClass(), "Message box threw exception during comms.  Destroying");
        }
    }
}
