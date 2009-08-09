package org.jcsp.net2;

import java.io.IOException;

import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.Any2OneChannel;
import org.jcsp.lang.Channel;
import org.jcsp.net2.NetworkMessageFilter.FilterRx;
import org.jcsp.util.InfiniteBuffer;

/**
 * A concrete implementation of a NetAltingChannelInput. This is a hidden class created by the architecture. To create
 * an instance of this object, use the NetChannel factory, or the CNS.
 * 
 * @see NetChannelInput
 * @see NetAltingChannelInput
 * @see NetChannel
 * @author Kevin Chalmers (updated from Quickstone Technologies)
 */
final class Net2OneChannel<T>
    extends NetAltingChannelInput<T>
{
    /**
     * The input channel coming into the networked channel input object from Links or locally connected net channel
     * outputs.
     */
    private final AltingChannelInput in;

    /**
     * The ChannelData structure representing this channel.
     */
    private final ChannelData data;

    /**
     * The lastRead ChannelMessage. Used during an extended read operation to allow the ACK message to be sent to the
     * correct Node.
     */
    private NetworkMessage lastRead = null;

    /**
     * The filter used to convert the incoming byte array into an object
     */
    private NetworkMessageFilter.FilterRx messageFilter;

    /**
     * The location of this channel
     */
    private final NetChannelLocation location;

    /**
     * Static factory method used to create a new Net2OneChannel. Used internally within the architecture.
     * 
     * @param poisonImmunity
     *            The immunity level of the channel
     * @param filter
     *            The filter on the channel used to convert read bytes into an object
     * @return A new Net2OneChannel
     */
    static <T2> Net2OneChannel<T2> create(int poisonImmunity, NetworkMessageFilter.FilterRx filter)
    {
        // Create a new ChannelData object
        ChannelData data = new ChannelData();

        // Create a new infinitely buffered any2one channel connecting the Links to the channel object
        Any2OneChannel chan = Channel.any2one(new InfiniteBuffer());

        // Add the output end to the ChannelData object
        data.toChannel = chan.out();

        // Set the immunity level
        data.immunityLevel = poisonImmunity;

        // Initialise the ChannelData object with the ChannelManager
        ChannelManager.getInstance().create(data);

        // Return a new Net2OneChannel
        return new Net2OneChannel<T2>(chan.in(), data, filter);
    }

    /**
     * Static factory method used to create a new Net2OneChannel with a given index. Used internally within the
     * architecture
     * 
     * @param index
     *            The index to create the channel with
     * @param poisonImmunity
     *            The immunity level of the channel
     * @param filter
     *            The filter used to take the incoming byte array and convert it into an object
     * @return A new Net2OneChannel
     * @throws IllegalArgumentException
     *             Thrown if the index given is already allocated within the ChannelManager
     */
    static <T2> Net2OneChannel<T2> create(int index, int poisonImmunity, NetworkMessageFilter.FilterRx filter)
        throws IllegalArgumentException
    {
        // Create a new ChannelData object
        ChannelData data = new ChannelData();

        // Create a new infinitely buffered any2one channel connecting the Links to the channel object
        Any2OneChannel chan = Channel.any2one(new InfiniteBuffer());

        // Add the output end to the ChannelData object
        data.toChannel = chan.out();

        // Set the immunity level
        data.immunityLevel = poisonImmunity;

        // Initialise the ChannelData object with the ChannelManager. Use the index given
        ChannelManager.getInstance().create(index, data);

        // Return a new Net2OneChannel
        return new Net2OneChannel<T2>(chan.in(), data, filter);
    }

    /**
     * Private constructor for creating a new instance of a Net2OneChannel. This is called by the create method to
     * create the channel.
     * 
     * @param input
     *            The input channel connecting to the networked channel.
     * @param chanData
     *            The ChannelData object representing the networked channel.
     * @param filter
     *            The filter used to convert the incoming byte array to an object
     * @throws JCSPNetworkException
     */
    private Net2OneChannel(AltingChannelInput input, ChannelData chanData, NetworkMessageFilter.FilterRx filter)
        throws JCSPNetworkException
    {
        // Set the wrapper's alting channel input so the channel can be used as a guard
        super(input);

        // Set the various properties
        this.in = input;
        this.data = chanData;
        this.data.state = ChannelDataState.OK_INPUT;
        this.location = new NetChannelLocation(Node.getInstance().getNodeID(), this.data.vcn);
        this.messageFilter = filter;
    }

    /**
     * Ends an extended read operation.
     * 
     * @throws IllegalStateException
     *             Thrown if the method is called when the channel is not in an extended read state
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     * @throws NetworkPoisonException
     *             Thrown if the channel has been poisoned
     */
    public void endRead()
        throws IllegalStateException, JCSPNetworkException, NetworkPoisonException
    {
        // First check the state of the channel. These are sanity checks. Really, if the channel is in an extended
        // read state then it should not be destroyed or poisoned
        if (this.data.state == ChannelDataState.DESTROYED)
            throw new JCSPNetworkException("The channel has been destroyed");
        if (this.data.state == ChannelDataState.POISONED)
            throw new NetworkPoisonException(this.data.poisonLevel);

        // Now check if a extended read is in progress
        if (this.lastRead != null)
        {
            // We are performing an extended read. Now check if we should be actually sending a reply
            if (this.lastRead.type != NetworkProtocol.ASYNC_SEND)
            {
                // The last message wasn't an asynchronous send, therefore we need to send the ACK
                // Create ACK message
                NetworkMessage ack = new NetworkMessage();
                ack.type = NetworkProtocol.ACK;
                // Destination is source of lastRead
                ack.attr1 = this.lastRead.attr2;
                // Attribute 2 is not used
                ack.attr2 = -1;
                // Write the acknowledgement back to the sender
                this.lastRead.toLink.write(ack);
            }
            // Set the lastRead to null. End of extended read operation
            this.lastRead = null;
        }
        else
        {
            // We are not performing an extended read. This is a problem. Thrown an exception
            throw new IllegalStateException("End read was called on a channel not in an extended read state");
        }
    }

    /**
     * Checks if any data is waiting on the channel.
     * 
     * @return True if data is ready, false otherwise.
     * @throws JCSPNetworkException
     *             Thrown if the channel has been destroyed
     * @throws NetworkPoisonException
     *             Thrown if the channel has poisoned
     */
    public boolean pending()
        throws JCSPNetworkException, NetworkPoisonException
    {
        // First check the state of the channel.
        if (this.data.state == ChannelDataState.DESTROYED)
            throw new JCSPNetworkException("The channel has been destroyed");
        if (this.data.state == ChannelDataState.POISONED)
            throw new NetworkPoisonException(this.data.poisonLevel);

        // Return if the channel has any pending messages
        return this.in.pending();
    }

    /**
     * Poisons the underlying channel
     * 
     * @param strength
     *            The strength of the poison
     */
    public void poison(int strength)
    {
        // First check the state of the channel
        if (this.data.state == ChannelDataState.DESTROYED)
            // In this case we do nothing. Destroyed is considered stronger than poisoned
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

                // Are we extended? If so send poison back there first
                if (this.lastRead != null)
                {
                    // We are extended. Send poison as an acknowledgement
                    NetworkMessage poison = new NetworkMessage();
                    poison.type = NetworkProtocol.POISON;
                    poison.attr1 = this.lastRead.attr2;
                    poison.attr2 = this.data.poisonLevel;
                    this.lastRead.toLink.write(poison);
                    this.lastRead = null;
                }

                // Now we must send POISON to any waiting SEND messages
                while (this.in.pending())
                {
                    // We have an incoming message. Read the message.
                    NetworkMessage pending = (NetworkMessage)this.in.read();

                    switch (pending.type)
                    {
                        // We have three possible outcomes. Either the message is a send of some sort, another poison,
                        // or neither of these (which should not be really possible)
                        // We must reply to sends with a POISON
                        case NetworkProtocol.SEND:
                        case NetworkProtocol.ASYNC_SEND:
                            // Create a new POISON message
                            NetworkMessage poison = new NetworkMessage();
                            poison.type = NetworkProtocol.POISON;
                            // Destination is the source of the previous message
                            poison.attr1 = pending.attr2;
                            // Attribute 2 is the poison level
                            poison.attr2 = this.data.poisonLevel;
                            // Write poison message to the channel attached to pending message
                            pending.toLink.write(poison);
                            break;

                        case NetworkProtocol.POISON:
                            // We have received another poison message. We now check if it is stronger than the previous
                            // poison.
                            if (pending.attr2 > this.data.poisonLevel)
                            {
                                // We have stronger poison. Increase the poison level
                                this.data.poisonLevel = pending.attr2;
                            }
                            // Poison is not greater than the previous poison. Do nothing.
                            break;

                        default:
                            // In any other case, we do nothing.
                            break;
                    }
                }
            }
            // Poison is not above our immunity level. Do nothing.
        }
        // Strength of the poison is not above our previous poison level. Do nothing.
    }

    /**
     * Reads the next message from the channel
     * 
     * @return The message read from the channel
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     * @throws NetworkPoisonException
     *             Thrown if the channel is poisoned
     * @throws IllegalStateException
     *             Thrown if the channel is in an extended read state
     */
    public T read()
        throws JCSPNetworkException, NetworkPoisonException, IllegalStateException
    {
        // First check our state
        if (this.data.state == ChannelDataState.DESTROYED)
            throw new JCSPNetworkException("Channel has been destroyed");
        if (this.data.state == ChannelDataState.POISONED)
            throw new NetworkPoisonException(this.data.poisonLevel);

        // Now check if we are in an extended read state
        if (this.lastRead != null)
        {
            // We are in an extended read. Throw exception
            throw new IllegalStateException("The channel is in an extended read state");
        }

        // We are in an OK state to perform a read.

        // We need to loop until we return a message, or throw an exception
        while (true)
        {
            // Read in the next message
            NetworkMessage msg = (NetworkMessage)this.in.read();

            // Now we need to decode the message and act accordingly
            try
            {
                switch (msg.type)
                {
                    // We can either receive a SEND, ASYNC_SEND, or POISON message
                    case NetworkProtocol.SEND:
                    {
                        // We have received a SEND
                        // Convert the message into the object again. This may throw an IOException
                        Object toReturn = this.messageFilter.filterRX(msg.data);

                        // We have a SEND, we need to acknowledge.
                        // Create an ACK message
                        NetworkMessage ack = new NetworkMessage();
                        ack.type = NetworkProtocol.ACK;
                        // Destination is source of the previous message
                        ack.attr1 = msg.attr2;
                        // Attribute 2 is unused
                        ack.attr2 = -1;
                        // Write ACK to the channel attached to the message
                        msg.toLink.write(ack);
                        // Return read object
                        return (T) toReturn; // Messy cast. We'll trust the sender.
                    }
                    case NetworkProtocol.ASYNC_SEND:
                    {
                        // We have received an ASYNC_SEND
                        // Convert the message into the object again. This may throw an IOException
                        Object toReturn = this.messageFilter.filterRX(msg.data);
                        // Return read object
                        return (T) toReturn; // Messy cast. We'll trust the sender.
                    }
                    case NetworkProtocol.POISON:
                        // First we change our poison level. Poison level is Attribute 2 of the message
                        this.data.poisonLevel = msg.attr2;

                        // The Link checked our immunity level, so this poison message must be greater than our immunity

                        // We need to lock on the state to ensure no link is using it as we change it
                        synchronized (this.data)
                        {
                            // Now change our state
                            this.data.state = ChannelDataState.POISONED;
                        }

                        // Now we must send POISON to any waiting SEND messages
                        while (this.in.pending())
                        {
                            // We have an incoming message. Read the message.
                            NetworkMessage pending = (NetworkMessage)this.in.read();

                            switch (pending.type)
                            {
                                // We have three possible outcomes. Either the message is a send of some sort, another
                                // poison, or neither of these (which should not be really possible)
                                // We must reply to sends with a POISON
                                case NetworkProtocol.SEND:
                                case NetworkProtocol.ASYNC_SEND:
                                    // Create a new POISON message
                                    NetworkMessage poison = new NetworkMessage();
                                    poison.type = NetworkProtocol.POISON;
                                    // Destination is the source of the previous message
                                    poison.attr1 = pending.attr2;
                                    // Attribute 2 is the poison level
                                    poison.attr2 = this.data.poisonLevel;
                                    // Write poison message to the channel attached to pending message
                                    pending.toLink.write(poison);
                                    break;

                                case NetworkProtocol.POISON:
                                    // We have received another poison message. We now check if it is stronger than the
                                    // previous poison.
                                    if (pending.attr2 > this.data.poisonLevel)
                                    {
                                        // We have stronger poison. Increase the poison level
                                        this.data.poisonLevel = pending.attr2;
                                    }
                                    // Poison is not greater than the previous poison. Do nothing.
                                    break;

                                default:
                                    // In any other case, we do nothing.
                                    break;
                            }
                        }

                        // We now throw the poison exception
                        throw new NetworkPoisonException(this.data.poisonLevel);
                }
            }
            // The filter may have thrown an IOException. Deal with it here and throw a JCSPNetworkException instead
            catch (IOException ioe)
            {
                throw new JCSPNetworkException("Incoming message was corrupted");
            }
        }
    }

    /**
     * Performs an extended read operation on the channel
     * 
     * @return The message read from the channel
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     * @throws IllegalStateException
     *             Thrown if the channel is already in an extended read state
     * @throws NetworkPoisonException
     *             Thrown if the channel is poisoned.
     */
    public T startRead()
        throws JCSPNetworkException, IllegalStateException, NetworkPoisonException
    {
        // First check our state
        if (this.data.state == ChannelDataState.DESTROYED)
            throw new JCSPNetworkException("Channel has been destroyed");
        if (this.data.state == ChannelDataState.POISONED)
            throw new NetworkPoisonException(this.data.poisonLevel);

        // Now check if we are in an extended read state
        if (this.lastRead != null)
        {
            // We are in an extended read. Throw exception
            throw new IllegalStateException("The channel is in an extended read state");
        }

        // We are in an OK state to perform a read.

        // We need to loop until we return a message, or throw an exception
        while (true)
        {
            // Read in the next message
            NetworkMessage msg = (NetworkMessage)this.in.read();

            // Now we need to decode the message and act accordingly
            try
            {
                switch (msg.type)
                {
                    // We can either receive a SEND, ASYNC_SEND, or POISON message
                    case NetworkProtocol.SEND:
                    case NetworkProtocol.ASYNC_SEND:
                    {
                        // We have received a SEND or ASYNC_SEND
                        // Convert the message into the object again. This may throw an IOException
                        Object toReturn = this.messageFilter.filterRX(msg.data);

                        // Now set the lastRead to the incoming message so we can acknowledge during the endRead
                        // operation
                        this.lastRead = msg;

                        return (T) toReturn; // Messy cast, but as we can't
											 // check types at both ends, this
											 // is sufficient (We'll get a run
											 // time ClassCastException if the
											 // sender sends us the 'wrong' (ie.
											 // not T) type
                    }
                    case NetworkProtocol.POISON:
                        // First we change our poison level. Poison level is Attribute 2 of the message
                        this.data.poisonLevel = msg.attr2;

                        // The Link checked our immunity level, so this poison message must be greater than our immunity

                        // We need to lock on the state to ensure no link is using it as we change it
                        synchronized (this.data)
                        {
                            // Now change our state
                            this.data.state = ChannelDataState.POISONED;
                        }

                        // Now we must send POISON to any waiting SEND messages
                        while (this.in.pending())
                        {
                            // We have an incoming message. Read the message.
                            NetworkMessage pending = (NetworkMessage)this.in.read();

                            switch (pending.type)
                            {
                                // We have three possible outcomes. Either the message is a send of some sort, another
                                // poison, or neither of these (which should not be really possible)
                                // We must reply to sends with a POISON
                                case NetworkProtocol.SEND:
                                case NetworkProtocol.ASYNC_SEND:
                                    // Create a new POISON message
                                    NetworkMessage poison = new NetworkMessage();
                                    poison.type = NetworkProtocol.POISON;
                                    // Destination is the source of the previous message
                                    poison.attr1 = pending.attr2;
                                    // Attribute 2 is the poison level
                                    poison.attr2 = this.data.poisonLevel;
                                    // Write poison message to the channel attached to pending message
                                    pending.toLink.write(poison);
                                    break;

                                case NetworkProtocol.POISON:
                                    // We have received another poison message. We now check if it is stronger than the
                                    // previous poison.
                                    if (pending.attr2 > this.data.poisonLevel)
                                    {
                                        // We have stronger poison. Increase the poison level
                                        this.data.poisonLevel = pending.attr2;
                                    }
                                    // Poison is not greater than the previous poison. Do nothing.
                                    break;

                                default:
                                    // In any other case, we do nothing.
                                    break;
                            }
                        }

                        // We now throw the poison exception
                        throw new NetworkPoisonException(this.data.poisonLevel);
                }
            }
            // The filter may have thrown an IOException. Deal with it here and throw a JCSPNetworkException instead
            catch (IOException ioe)
            {
                throw new JCSPNetworkException("Incoming message was corrupted");
            }
        }
    }

    /**
     * Returns the NetChannelLocation of the channel
     * 
     * @return Location of this channel
     */
    public NetLocation getLocation()
    {
        return this.location;
    }

    /**
     * Destroys the underlying channel
     */
    public void destroy()
    {
        // First acquire a lock on the channel
        synchronized (this.data)
        {
            // Now we can change our state
            this.data.state = ChannelDataState.DESTROYED;
        }

        // Remove the channel from the ChannelManager
        ChannelManager.getInstance().removeChannel(this.data);

        // Check if we are in an extended read. If so we need to reject the last message
        if (this.lastRead != null)
        {
            // We are extended. Send a reject to the pending message
            NetworkMessage reject = new NetworkMessage();
            reject.type = NetworkProtocol.REJECT_CHANNEL;
            reject.attr1 = this.lastRead.attr2;
            reject.attr2 = -1;
            this.lastRead.toLink.write(reject);
            this.lastRead = null;
        }

        // Now we must reject any incoming messages, except poison
        while (this.in.pending())
        {
            // We have a pending message. Deal with it.
            NetworkMessage msg = (NetworkMessage)this.in.read();

            // Check that it is not a poison
            if (msg.type != NetworkProtocol.POISON)
            {
                // The message is not a poison message, and therefore must be a send or some type. Reject the message
                // Create REJECT_CHANNEL message
                NetworkMessage reject = new NetworkMessage();
                reject.type = NetworkProtocol.REJECT_CHANNEL;
                // Destination is the source of the incoming message
                reject.attr1 = msg.attr2;
                // Attribute 2 is not used
                reject.attr2 = -1;
                // Write reject to the channel attached to the incoming message
                msg.toLink.write(reject);
            }
        }
    }

    /**
     * Gets the channel data object for this channel.
     * 
     * @return The ChannelData for this Channel
     */
    final ChannelData getChannelData()
    {
        return this.data;
    }

    /**
     * Sets the underlying message filter
     * 
     * @param decoder
     *            The message filter to use
     */
    public void setDecoder(FilterRx decoder)
    {
        this.messageFilter = decoder;
    }

}
