package org.jcsp.net2.bns;

import org.jcsp.net2.JCSPNetworkException;
import org.jcsp.net2.NetBarrier;
import org.jcsp.net2.NetBarrierLocation;
import org.jcsp.net2.NetChannel;
import org.jcsp.net2.NetChannelInput;
import org.jcsp.net2.NetChannelLocation;
import org.jcsp.net2.NetChannelOutput;
import org.jcsp.net2.Node;
import org.jcsp.net2.NodeID;

/**
 * This is the service object used to register and resolve barrier names with a Barrier Name Server. This provides a
 * client front end.
 * 
 * @author Kevin Chalmers (updated from Quickstone Technologies)
 */
public final class BNSService
{
    /**
     * The channel to send messages to the BNS upon
     */
    private final NetChannelOutput toBNS;

    /**
     * The incoming channel to receive messages from the BNS
     */
    private final NetChannelInput fromBNS;

    /**
     * Creates a new BNSService
     * 
     * @param bnsNode
     *            The Node that the BNS is on
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public BNSService(NodeID bnsNode)
        throws JCSPNetworkException
    {
        // Create input and output end
        this.toBNS = NetChannel.one2net(bnsNode, 2, new BNSNetworkMessageFilter.FilterTX());
        this.fromBNS = NetChannel.net2one(new BNSNetworkMessageFilter.FilterRX());

        // Logon to the BNS
        BNSMessage message = new BNSMessage();
        message.type = BNSMessageProtocol.LOGON_MESSAGE;
        message.serviceLocation = (NetChannelLocation)this.fromBNS.getLocation();
        this.toBNS.write(message);

        // Wait for logon reply message
        BNSMessage logonReply = (BNSMessage)this.fromBNS.read();

        // Check if we logged on OK
        if (logonReply.success == false)
        {
            Node.err.log(this.getClass(), "Failed to logon to BNS");
            throw new JCSPNetworkException("Failed to logon to BNS");
        }
        Node.log.log(this.getClass(), "Logged into BNS");
    }

    /**
     * Registers a Server end of a NetBarrier with the BNS
     * 
     * @param name
     *            Name to register with BNS
     * @param bar
     *            Barrier to register
     * @return True if the name was registered successfully, false otherwise
     */
    public boolean register(String name, NetBarrier bar)
    {
        // Ensure only one registration can happen at a time
        synchronized (this)
        {
            // Create a new registration message
            BNSMessage message = new BNSMessage();
            message.type = BNSMessageProtocol.REGISTER_REQUEST;
            message.name = name;
            message.serviceLocation = (NetChannelLocation)this.fromBNS.getLocation();
            message.location = (NetBarrierLocation)bar.getLocation();
            // Write registration message to the BNS
            this.toBNS.write(message);
            // Read in reply
            BNSMessage reply = (BNSMessage)this.fromBNS.read();
            return reply.success;
        }
    }

    /**
     * Resolves a name on the BNS, retrieving the NetBarrierLocation for the NetBarrier
     * 
     * @param name
     *            The name to resolve from the BNS
     * @return The NetBarrierLocation of the NetBarrier declared with name
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public NetBarrierLocation resolve(String name)
        throws JCSPNetworkException
    {
        // Create a temporary channel to receive the incoming NetBarrierLocation
        NetChannelInput responseChan = NetChannel.net2one(new BNSNetworkMessageFilter.FilterRX());

        // Create the resolution message
        BNSMessage message = new BNSMessage();
        message.type = BNSMessageProtocol.RESOLVE_REQUEST;
        message.serviceLocation = (NetChannelLocation)responseChan.getLocation();
        message.name = name;

        // Write resolution message to the BNS
        this.toBNS.write(message);

        // Read in reply
        BNSMessage reply = (BNSMessage)responseChan.read();

        // Destroy temporary channel
        responseChan.destroy();

        // Return result
        if (reply.success)
            return reply.location;
        throw new JCSPNetworkException("Failed to resolve barrier named: " + name);
    }
}
