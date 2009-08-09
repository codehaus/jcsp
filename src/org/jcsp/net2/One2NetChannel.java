package org.jcsp.net2;

import java.io.IOException;

import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.Channel;
import org.jcsp.lang.ChannelOutput;
import org.jcsp.lang.One2OneChannel;
import org.jcsp.lang.PoisonException;
import org.jcsp.net2.NetworkMessageFilter.FilterTx;
import org.jcsp.util.InfiniteBuffer;

/**
 * An outputting end of a networked channel (TX). This is a concrete implementation created internally by JCSP. For
 * information on how to use networked channels, and how to create them, see the relevant documentation.
 * 
 * @see NetChannelOutput
 * @see NetChannel
 * @author Kevin Chalmers (Updated from Quickstone Technologies)
 */
final class One2NetChannel<T>
    implements NetChannelOutput<T>
{

    /**
     * The channel connecting to the Link that connects to the networked input end of this channel.
     */
    private final ChannelOutput toLinkTx;

    /**
     * The actual Link this output channel sends on. We keep this as it allows us to register and unregister with the
     * Link as we are created and destroyed, allowing the Link to inform the channel when a Link goes down.
     */
    private final Link linkConnectedTo;

    /**
     * This is used if we are ever connected locally. We use this to check the state of a locally connected channel
     * prior to sending a message.
     */
    private final ChannelData localChannel;

    /**
     * The channel used to receive acknowledgements from the input end via the Link.
     */
    private final AltingChannelInput theAckChannel;

    /**
     * A structure containing the information on the state of the channel.
     */
    private final ChannelData data;

    /**
     * The location that this channel is connected to (the input channel ends location)
     */
    private final NetChannelLocation remoteLocation;

    /**
     * The local channel end location
     */
    private final NetChannelLocation localLocation;

    /**
     * Flag to determine if this is a locally connected channel or not
     */
    private final boolean isLocal;

    /**
     * The filter used to encode outgoing messages
     */
    private NetworkMessageFilter.FilterTx messageFilter;

    /**
     * Creates a new One2NetChannel by connecting to an already created NetChannelInput
     * 
     * @param loc
     *            The location of the NetChannelInput
     * @param immunity
     *            The immunity level of the channel
     * @param filter
     *            The filter used to encode outgoing messages
     * @return A new One2NetChannel
     * @throws JCSPNetworkException
     *             Thrown if the connection to the remote Node fails
     */
    static <T2> One2NetChannel<T2> create(NetChannelLocation loc, int immunity, NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException
    {
        // Create the channel data structure
        ChannelData data = new ChannelData();

        // Create the channel linking this to the Link level. This channel is the one used to receive acknowledgement
        // messages
        One2OneChannel chan = Channel.one2one(new InfiniteBuffer());
        data.toChannel = chan.out();

        // Set state of channel
        data.state = ChannelDataState.OK_OUTPUT;

        // Register channel
        ChannelManager.getInstance().create(data);

        // We now need to create the connection to the input end of the channel
        ChannelOutput toLink;

        // Check if this is a local link, and if so connect directly to the input channel
        if (loc.getNodeID().equals(Node.getInstance().getNodeID()))
        {
            toLink = ChannelManager.getInstance().getChannel(loc.getVCN()).toChannel;
            return new One2NetChannel<T2>(chan.in(), toLink, null, data, loc, immunity, filter);
        }

        // Connect to remote node if necessary
        Link link = LinkManager.getInstance().requestLink(loc.getNodeID());

        // Check if an existing connection exists
        if (link == null)
        {
            // We are not connected. Connect to remote Node.
            link = LinkFactory.getLink(loc.getNodeID());
        }

        // Get the connection to the Link.
        toLink = link.getTxChannel();

        // Return new channel
        return new One2NetChannel<T2>(chan.in(), toLink, link, data, loc, immunity, filter);
    }

    /**
     * Private constructor for creating a One2NetChannel. This is called by the create method.
     * 
     * @param ackChannel
     *            The channel used to receive acknowledgements from Links
     * @param toLink
     *            The channel used to send messages to the input end
     * @param link
     *            The Link that this channel is connected to
     * @param chanData
     *            The structure used to store the state of the channel
     * @param loc
     *            The location of the input end that this channel is connected to
     * @param immunity
     *            The poison immunity level of the channel
     * @param filter
     *            Filter used to encode outgoing messages
     */
    private One2NetChannel(AltingChannelInput ackChannel, ChannelOutput toLink, Link link, ChannelData chanData,
            NetChannelLocation loc, int immunity, NetworkMessageFilter.FilterTx filter)
    {
        // Set all the object properties for the channel
        this.toLinkTx = toLink;
        this.theAckChannel = ackChannel;
        this.data = chanData;
        this.remoteLocation = loc;
        this.localLocation = new NetChannelLocation(Node.getInstance().getNodeID(), chanData.vcn);
        this.data.immunityLevel = immunity;
        this.messageFilter = filter;

        // We now must either register with the Link connecting us to the input end, or we connect directly to the
        // channel if it is local
        if (link != null)
        {
            // We are connected to a remote Node. Register with Link
            this.linkConnectedTo = link;
            this.linkConnectedTo.registerChannel(this.data);

            // Set the localised parameters accordingly
            this.isLocal = false;
            this.localChannel = null;
        }
        else
        {
            // We are connected to an input end on this Node. Set the localised parameters
            this.isLocal = true;
            // Get hold of the local channel data structure
            this.localChannel = ChannelManager.getInstance().getChannel(this.remoteLocation.getVCN());

            // Set the Link connected to to null
            this.linkConnectedTo = null;
        }
    }

    /**
     * Poisons the underlying channel.
     * 
     * @param strength
     *            The strength of the poison being placed on the channel
     */
    public void poison(int strength)
    {
        // First check the state of the channel
        if (this.data.state == ChannelDataState.DESTROYED || this.data.state == ChannelDataState.BROKEN)
            // In this case we do nothing. Destroyed and broken are considered stronger than poisoned
            return;

        // Now check if the strength of the poison is greater than a previous poison
        if (strength > this.data.poisonLevel)
        {
            // Change the current poison level
            this.data.poisonLevel = strength;

            // Now check if the poison level is strong enough to poison us.
            if (strength > this.data.immunityLevel)
            {
                // The poison is strong enough. We need to change our state to poisoned.

                // We need to lock on the state to ensure no link is using it as we change it
                synchronized (this.data)
                {
                    // Now change our state
                    this.data.state = ChannelDataState.POISONED;
                }

                // Now we need to send the poison. Create the poison message
                NetworkMessage msg = new NetworkMessage();
                msg.type = NetworkProtocol.POISON;
                msg.attr1 = this.remoteLocation.getVCN();
                msg.attr2 = strength;

                // Now we must send the poison. Check if we are locally connected
                if (this.isLocal)
                {
                    // Now check the state of the local input end. Acquire lock
                    synchronized (this.localChannel)
                    {
                        // We only need to forward the message if the opposite channel is in OK_INPUT
                        if (this.localChannel.state == ChannelDataState.OK_INPUT)
                            // Write the message
                            this.toLinkTx.write(msg);
                    }
                }

                // We are not locally connected. Just forward the poison
                this.toLinkTx.write(msg);
            }
            // Poison is not above our immunity level. Do nothing.
        }
    }

    /**
     * Gets the NetChannelLocation that this channel is connected to (i.e. the input end location)
     * 
     * @return The NetChannelLocation that this channel is connected to
     */
    public NetLocation getLocation()
    {
        return this.remoteLocation;
    }

    /**
     * Gets the local NetChannelLocation that represents this channel.
     * 
     * @return The local location of the output end
     */
    NetChannelLocation localLocation()
    {
        return this.localLocation;
    }

    /**
     * Writes an object to the input end
     * 
     * @param object
     *            The object to send to the input end.
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the network architecture
     * @throws PoisonException
     *             Thrown if the channel has been poisoned
     */
    public void write(T object)
        throws JCSPNetworkException, PoisonException
    {
        // First we do a state check, and throw an exception if necessary
        if (this.data.state == ChannelDataState.DESTROYED)
            throw new JCSPNetworkException("Channel has been destroyed");
        if (this.data.state == ChannelDataState.BROKEN)
            throw new JCSPNetworkException("Channel has broken");
        if (this.data.state == ChannelDataState.POISONED)
            throw new NetworkPoisonException(this.data.poisonLevel);

        // The channel is in a suitable state to do a write. Continue write operation

        // First check that no pending messages have been left for us. This can happen if we did an async write and
        // then were rejected, poisoned, or if the link went down.
        if (this.theAckChannel.pending())
        {
            NetworkMessage msg = (NetworkMessage)this.theAckChannel.read();

            // Lock onto our state object as we may be changing our state
            synchronized (this.data)
            {
                // A previous ASYNC_SEND was rejected. Break channel.
                if (msg.type == NetworkProtocol.REJECT_CHANNEL)
                {
                    this.data.state = ChannelDataState.BROKEN;

                    // Remove ourselves from the ChannelManager
                    ChannelManager.getInstance().removeChannel(this.data);

                    throw new JCSPNetworkException("Channel rejected during previous send");
                }

                // The link to the input end has gone down. Break the channel
                else if (msg.type == NetworkProtocol.LINK_LOST)
                {
                    this.data.state = ChannelDataState.BROKEN;

                    // Remove ourselves from the ChannelManager
                    ChannelManager.getInstance().removeChannel(this.data);

                    throw new JCSPNetworkException("Link to Node lost.  Send cannot complete");
                }

                // A previous ASYNC_SEND resulted in us being poisoned. Poison the channel. There is nothing else that
                // can be done, as the input end has effectively gone.
                else if (msg.type == NetworkProtocol.POISON)
                {
                    this.data.state = ChannelDataState.POISONED;
                    this.data.poisonLevel = msg.attr2;
                    throw new NetworkPoisonException(msg.attr2);
                }

                // This shouldn't really happen, but possibly someone ACK'd us when they were not meant to. Throw
                // exception.
                else
                {
                    Node.err.log(this.getClass(), "Channel " + this.data.vcn + " reports unexpected message.");
                    throw new JCSPNetworkException("NetChannelOutput received an unexpected exception");
                }
            }
        }

        // Create a new SEND message.
        NetworkMessage msg = new NetworkMessage();
        msg.type = NetworkProtocol.SEND;
        msg.attr1 = this.remoteLocation.getVCN();
        msg.attr2 = this.data.vcn;

        try
        {
            // Pass the message through the filter to convert it into bytes for transfer. Standard method is to convert
            // an object into a byte array via object serialization, but implementation specific methods can be
            // developed.
            // See NetworkMessageFilter and ObjectNetworkMessageFilter.
            msg.data = this.messageFilter.filterTX(object);

            // Now we must determine how to send the message. If it is to a remote Node, simply write to the Link.
            if (!this.isLocal)
            {
                this.toLinkTx.write(msg);
            }

            // If the input end is actually on this Node, then we attached our ackChannel to the message so the input
            // channel can send the ACK directly to us
            else
            {
                // Acquire lock on the input ends data structure
                synchronized (this.localChannel)
                {
                    // Now check the local channels state and behave accordingly
                    switch (this.localChannel.state)
                    {
                        case ChannelDataState.OK_INPUT:
                            // We have an input end. Send message
                            msg.toLink = this.data.toChannel;
                            this.toLinkTx.write(msg);
                            break;

                        case ChannelDataState.POISONED:
                            // The input end has been poisoned. Set our state and throw exception.

                            // We don't need to acquire a lock. Only we can be operating on the channel
                            this.data.state = ChannelDataState.POISONED;
                            this.data.poisonLevel = this.localChannel.poisonLevel;

                            throw new NetworkPoisonException(this.localChannel.poisonLevel);

                        default:
                            // In all other circumstances, we cause a network exception. Set state to broken

                            // We do not need to acquire a lock. Only we can be operating on the channel
                            this.data.state = ChannelDataState.BROKEN;

                            // Remove ourselves from the ChannelManager
                            ChannelManager.getInstance().removeChannel(this.data);

                            throw new JCSPNetworkException("Channel rejected during send");
                    }
                }
            }
        }
        catch (IOException ioe)
        {
            throw new JCSPNetworkException("Error when trying to convert the message for sending");
        }

        // Now we wait for a reply on our ackChannel
        NetworkMessage reply = (NetworkMessage)this.theAckChannel.read();

        // The SEND was rejected. Break channel.
        if (reply.type == NetworkProtocol.REJECT_CHANNEL)
        {
            this.data.state = ChannelDataState.BROKEN;

            // Remove ourselves from the ChannelManager
            ChannelManager.getInstance().removeChannel(this.data);

            throw new JCSPNetworkException("Channel rejected during send");
        }

        // The link to the input end has gone down. Break the channel
        else if (reply.type == NetworkProtocol.LINK_LOST)
        {
            this.data.state = ChannelDataState.BROKEN;

            // Remove ourselves from the ChannelManager
            ChannelManager.getInstance().removeChannel(this.data);

            throw new JCSPNetworkException("Link to Node lost.  Send cannot complete");
        }

        // The SEND resulted in us being poisoned. Poison the channel. There is nothing else that
        // can be done, as the input end has effectively gone.
        else if (reply.type == NetworkProtocol.POISON)
        {
            this.data.state = ChannelDataState.POISONED;
            this.data.poisonLevel = msg.attr2;
            throw new NetworkPoisonException(msg.attr2);
        }

        // We received an ACK. Return.
        else if (reply.type == NetworkProtocol.ACK)
        {
            return;
        }

        // This shouldn't happen. Throw exception.
        else
        {
            Node.err.log(this.getClass(), "Channel " + this.data.vcn + " reports unexpected message.");
            throw new JCSPNetworkException("NetChannelOutput received an unexpected exception");
        }
    }

    /**
     * Asynchronously writes an object to the channel
     * 
     * @param object
     *            The object being written to the channel
     * @throws JCSPNetworkException
     *             Thrown when something goes wrong in the network architecture
     * @throws PoisonException
     *             Thrown if the channel is poisoned
     */
    public void asyncWrite(T object)
        throws JCSPNetworkException, PoisonException
    {
        // First we do a state check, and throw an exception if necessary
        if (this.data.state == ChannelDataState.DESTROYED)
            throw new JCSPNetworkException("Channel has been destroyed");
        if (this.data.state == ChannelDataState.BROKEN)
            throw new JCSPNetworkException("Channel has broken");
        if (this.data.state == ChannelDataState.POISONED)
            throw new NetworkPoisonException(this.data.poisonLevel);

        // The channel is in a suitable state to do a write. Continue write operation

        // First check that no pending messages have been left for us. This can happen if we did an async write and
        // then were rejected, poisoned, or if the link went down.
        if (this.theAckChannel.pending())
        {
            NetworkMessage msg = (NetworkMessage)this.theAckChannel.read();

            // Lock onto our state object as we may be changing our state
            synchronized (this.data)
            {
                // A previous ASYNC_SEND was rejected. Break channel.
                if (msg.type == NetworkProtocol.REJECT_CHANNEL)
                {
                    this.data.state = ChannelDataState.BROKEN;

                    // Remove ourselves from the ChannelManager
                    ChannelManager.getInstance().removeChannel(this.data);

                    // Now we may need to unregister from the Link
                    if (!this.isLocal)
                    {
                        this.linkConnectedTo.deRegisterChannel(this.data);
                    }

                    throw new JCSPNetworkException("Channel rejected during previous send");
                }

                // The link to the input end has gone down. Break the channel
                else if (msg.type == NetworkProtocol.LINK_LOST)
                {
                    this.data.state = ChannelDataState.BROKEN;

                    // Remove ourselves from the ChannelManager
                    ChannelManager.getInstance().removeChannel(this.data);

                    // Now we may need to unregister from the Link
                    if (!this.isLocal)
                    {
                        this.linkConnectedTo.deRegisterChannel(this.data);
                    }

                    throw new JCSPNetworkException("Link to Node lost.  Send cannot complete");
                }

                // A previous ASYNC_SEND resulted in us being poisoned. Poison the channel. There is nothing else that
                // can be done, as the input end has effectively gone.
                else if (msg.type == NetworkProtocol.POISON)
                {
                    this.data.state = ChannelDataState.POISONED;
                    this.data.poisonLevel = msg.attr2;
                    throw new NetworkPoisonException(msg.attr2);
                }

                // This shouldn't really happen, but possibly someone ACK'd us when they were not meant to. Throw
                // exception.
                else
                {
                    Node.err.log(this.getClass(), "Channel " + this.data.vcn + " reports unexpected message.");
                    throw new JCSPNetworkException("NetChannelOutput received an unexpected exception");
                }
            }
        }

        // Create a new SEND message.
        NetworkMessage msg = new NetworkMessage();
        msg.type = NetworkProtocol.ASYNC_SEND;
        msg.attr1 = this.remoteLocation.getVCN();
        msg.attr2 = this.data.vcn;

        try
        {
            // Pass the message through the filter to convert it into bytes for transfer. Standard method is to convert
            // an object into a byte array via object serialization, but implementation specific methods can be
            // developed.
            // See NetworkMessageFilter and ObjectNetworkMessageFilter.
            msg.data = this.messageFilter.filterTX(object);

            // Now we must determine how to send the message. If it is to a remote Node, simply write to the Link.
            if (!this.isLocal)
            {
                this.toLinkTx.write(msg);
            }

            // If the input end is actually on this Node, then we attached our ackChannel to the message so the input
            // channel can send the ACK directly to us
            else
            {
                // Acquire lock on the input ends data structure
                synchronized (this.localChannel)
                {
                    // Now check the local channels state and behave accordingly
                    switch (this.localChannel.state)
                    {
                        case ChannelDataState.OK_INPUT:
                            // We have an input end. Send message
                            msg.toLink = this.data.toChannel;
                            this.toLinkTx.write(msg);
                            break;

                        case ChannelDataState.POISONED:
                            // The input end has been poisoned. Set our state and throw exception.

                            // We don't need to acquire a lock. Only we can be operating on the channel
                            this.data.state = ChannelDataState.POISONED;
                            this.data.poisonLevel = this.localChannel.poisonLevel;

                            throw new NetworkPoisonException(this.localChannel.poisonLevel);

                        default:
                            // In all other circumstances, we cause a network exception. Set state to broken

                            // We do not need to acquire a lock. Only we can be operating on the channel
                            this.data.state = ChannelDataState.BROKEN;

                            // Remove ourselves from the ChannelManager
                            ChannelManager.getInstance().removeChannel(this.data);

                            throw new JCSPNetworkException("Channel rejected during send");
                    }
                }
            }
        }
        catch (IOException ioe)
        {
            throw new JCSPNetworkException("Error when trying to convert the message for sending");
        }
        // We are asynchronous, so we simply return.
    }

    /**
     * Gets the channel data state for this channel.
     * 
     * @return ChannelData for this channel
     */
    final ChannelData getChannelData()
    {
        return this.data;
    }

    /**
     * Destroys the channel and removes it from the ChannelManager.
     */
    public void destroy()
    {
        // Lock the channel data state
        synchronized (this.data)
        {
            // Set state to DESTROYED
            this.data.state = ChannelDataState.DESTROYED;

            // Remove channel from ChannelManager
            ChannelManager.getInstance().removeChannel(this.data);

            // Deregister from the Link if required. May be local
            if (this.linkConnectedTo != null)
                this.linkConnectedTo.deRegisterChannel(this.data);
        }
    }

    /**
     * Sets the underlying message filter
     * 
     * @param encoder
     *            The new message filter to use
     */
    public void setEncoder(FilterTx encoder)
    {
        this.messageFilter = encoder;
    }

}
