package org.jcsp.net2.cns;

import org.jcsp.net2.JCSPNetworkException;
import org.jcsp.net2.NetChannel;
import org.jcsp.net2.NetChannelInput;
import org.jcsp.net2.NetChannelLocation;
import org.jcsp.net2.NetChannelOutput;
import org.jcsp.net2.Node;
import org.jcsp.net2.NodeID;

/**
 * This is the service object used to register and resolve channel names with a Channel Name Server. This provides a
 * client front end.
 * 
 * @author Kevin Chalmers (updated from Quickstone Technologies)
 */
public final class CNSService
{
    /**
     * The channel to send messages to the CNS upon
     */
    private final NetChannelOutput toCNS;

    /**
     * The incoming channel to receive messages from the CNS from
     */
    private final NetChannelInput fromCNS;

    /**
     * Creates a new CNSService
     * 
     * @param cnsNode
     *            The NodeID of the Node with the CNS on it
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public CNSService(NodeID cnsNode)
        throws JCSPNetworkException
    {
        // Create the input and output channel
        this.toCNS = NetChannel.one2net(new NetChannelLocation(cnsNode, 1), new CNSNetworkMessageFilter.FilterTX());
        this.fromCNS = NetChannel.net2one(new CNSNetworkMessageFilter.FilterRX());

        // We now need to logon to the CNS
        CNSMessage message = new CNSMessage();
        message.type = CNSMessageProtocol.LOGON_MESSAGE;
        message.location1 = (NetChannelLocation)this.fromCNS.getLocation();
        this.toCNS.write(message);

        // Wait for logon reply message
        CNSMessage logonReply = (CNSMessage)this.fromCNS.read();

        // Check if we logged on OK
        if (logonReply.success == false)
        {
            Node.err.log(this.getClass(), "Failed to logon to CNS");
            throw new JCSPNetworkException("Failed to Logon to CNS");
        }
        Node.log.log(this.getClass(), "Logged into CNS");
    }

    /**
     * Registers an input end with the CNS
     * 
     * @param name
     *            The name to register the channel with
     * @param in
     *            The NetChannelInput to register with the CNS
     * @return True if the channel was successfully registered, false otherwise
     */
    public boolean register(String name, NetChannelInput in)
    {
        // Ensure that only one registration can happen at a time
        synchronized (this)
        {
            // Create a new registration message
            CNSMessage message = new CNSMessage();
            message.type = CNSMessageProtocol.REGISTER_REQUEST;
            message.name = name;
            message.location1 = (NetChannelLocation)this.fromCNS.getLocation();
            message.location2 = (NetChannelLocation)in.getLocation();
            // Write registration message to the CNS
            this.toCNS.write(message);
            // Read in reply
            CNSMessage reply = (CNSMessage)this.fromCNS.read();
            return reply.success;
        }
    }

    /**
     * Resolves a name on the CNS, retrieving the NetChannelLocation for the channel
     * 
     * @param name
     *            The name to resolve
     * @return The NetChannelLocation of the channel declared name
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public NetChannelLocation resolve(String name)
        throws JCSPNetworkException
    {
        // Create a temporary channel to receive the incoming NetChannelLocation
        NetChannelInput in = NetChannel.net2one(new CNSNetworkMessageFilter.FilterRX());
        // Create a resolution message
        CNSMessage message = new CNSMessage();
        message.type = CNSMessageProtocol.RESOLVE_REQUEST;
        message.location1 = (NetChannelLocation)in.getLocation();
        message.name = name;
        // Write the resolution message to the CNS
        this.toCNS.write(message);
        // Read in reply
        CNSMessage reply = (CNSMessage)in.read();
        // Destroy the temporary channel
        in.destroy();
        // Now return the resolved location, or throw an exception
        if (reply.success == true)
            return reply.location1;
        throw new JCSPNetworkException("Failed to resolve channel named: " + name);
    }
}
