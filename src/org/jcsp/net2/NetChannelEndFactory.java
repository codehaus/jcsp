package org.jcsp.net2;

/**
 * This interface defines the methods that any networked channel factory must implement. For a concrete implementation,
 * see StandardNetChannelEndFactory
 * 
 * @see StandardNetChannelEndFactory
 * @author Kevin Chalmers (updated from Quickstone Technologies)
 */
public interface NetChannelEndFactory
{
    /**
     * Creates a new NetAltingChannelInput
     * 
     * @return A new NetAltingChannelInput
     */
    public <T> NetAltingChannelInput<T> net2one();

    /**
     * Creates a new NetAltingChannelInput with the given immunity level for poison
     * 
     * @param immunityLevel
     *            The immunity level for poison
     * @return A new NetAltingChannelInput
     */
    public <T> NetAltingChannelInput<T> net2one(int immunityLevel);

    /**
     * Creates a new NetAltingChannelInput which uses the given filter to decode incoming messages
     * 
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetAltingChannelInput
     */
    public <T> NetAltingChannelInput<T> net2one(NetworkMessageFilter.FilterRx filter);

    /**
     * Creates a new NetAltingChannelInput with the given poison immunity level which uses the given filter to decode
     * incoming messages
     * 
     * @param immunityLevel
     *            The immunity level to poison for the created channel
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetAltingChannelInput
     */
    public <T> NetAltingChannelInput<T> net2one(int immunityLevel, NetworkMessageFilter.FilterRx filter);

    /**
     * Creates a new NetSharedChannelInput
     * 
     * @return A new NetSharedChannelInput
     */
    public <T> NetSharedChannelInput<T> net2any();

    /**
     * Creates a new NetSharedChannelInput with the given poison immunity level
     * 
     * @param immunityLevel
     *            The immunity level to poison for this channel
     * @return A new NetSharedChannelInput
     */
    public <T> NetSharedChannelInput<T> net2any(int immunityLevel);

    /**
     * Creates a new NetSharedChannelInput which uses the given filter to decode incoming messages
     * 
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetSharedChannelInput
     */
    public <T> NetSharedChannelInput<T> net2any(NetworkMessageFilter.FilterRx filter);

    /**
     * Creates a new NetSharedChannelInput with the given poison immunity level, which uses the given filter to decode
     * messages
     * 
     * @param immunityLevel
     *            The immunity level to poison for this channel
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetSharedChannelInput
     */
    public <T> NetSharedChannelInput<T> net2any(int immunityLevel, NetworkMessageFilter.FilterRx filter);

    /**
     * Creates a new NetAltingChannelInput with the given index
     * 
     * @param index
     *            The index to create the channel with
     * @return A new NetAltingChannelInput
     * @throws IllegalArgumentException
     *             Thrown if a channel with the given index already exists
     */
    public <T> NetAltingChannelInput<T> numberedNet2One(int index)
        throws IllegalArgumentException;

    /**
     * Creates a new NetAltingChannelInput with the given index and the given poison immunity level
     * 
     * @param index
     *            The index to create the channel with
     * @param immunityLevel
     *            The immunity to poison that the channel has
     * @return A new NetAltingChannelInput
     * @throws IllegalArgumentException
     *             Thrown if a channel with the given index already exists
     */
    public <T> NetAltingChannelInput<T> numberedNet2One(int index, int immunityLevel)
        throws IllegalArgumentException;

    /**
     * Creates a new NetAltingChannelInput with the given index that uses the given filter to decode incoming messages
     * 
     * @param index
     *            The index to create the channel with
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetAltingChannelInput
     * @throws IllegalArgumentException
     *             Thrown if a channel with the given index already exists
     */
    public <T> NetAltingChannelInput<T> numberedNet2One(int index, NetworkMessageFilter.FilterRx filter)
        throws IllegalArgumentException;

    /**
     * Creates a new NetAltingChannelInput with the given index and given poison immunity, which uses the given filter
     * to decode incoming messages
     * 
     * @param index
     *            The index to create the channel with
     * @param immunityLevel
     *            The immunity to poison that the channel has
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetAltingChannelInput
     * @throws IllegalArgumentException
     *             Thrown if a channel with the given index already exists
     */
    public <T> NetAltingChannelInput<T> numberedNet2One(int index, int immunityLevel, NetworkMessageFilter.FilterRx filter)
        throws IllegalArgumentException;

    /**
     * Creates a new NetSharedChannelInput with the given index
     * 
     * @param index
     *            The index to create the channel with
     * @return A new NetSharedChannelInput
     * @throws IllegalArgumentException
     *             Thrown if a channel with the given index already exists
     */
    public <T> NetSharedChannelInput<T> numberedNet2Any(int index)
        throws IllegalArgumentException;

    /**
     * Creates a new NetSharedChannelInput with the given index and poison immunity level
     * 
     * @param index
     *            The index to create the channel with
     * @param immunityLevel
     *            The immunity to poison the channel has
     * @return A new NetSharedChannelInput
     * @throws IllegalArgumentException
     *             Thrown if a channel with the given index already exists
     */
    public <T> NetSharedChannelInput<T> numberedNet2Any(int index, int immunityLevel)
        throws IllegalArgumentException;

    /**
     * Creates a new NetSharedChannelInput with the given index that uses the given filter to decode incoming messages
     * 
     * @param index
     *            The index to create the channel with
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetSharedChannelInput
     * @throws IllegalArgumentException
     *             Thrown if a channel with the given index already exists
     */
    public <T> NetSharedChannelInput<T> numberedNet2Any(int index, NetworkMessageFilter.FilterRx filter)
        throws IllegalArgumentException;

    /**
     * Creates a new NetSharedChannelInput with the given index and poison immunity level, which uses the given filter
     * to decode incoming messages.
     * 
     * @param index
     *            The index to create the channel with
     * @param immunityLevel
     *            The immunity level to poison that the channel has
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetSharedChannelInput
     * @throws IllegalArgumentException
     *             Thrown if a channel with the given index already exists.
     */
    public <T> NetSharedChannelInput<T> numberedNet2Any(int index, int immunityLevel, NetworkMessageFilter.FilterRx filter)
        throws IllegalArgumentException;

    /**
     * Creates a new NetChannelOutput connected to the input end with the given NetChannelLocation
     * 
     * @param loc
     *            The location of the input end of the channel
     * @return A new NetChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetChannelOutput<T> one2net(NetChannelLocation loc)
        throws JCSPNetworkException;

    /**
     * Creates a new NetChannelOutput connected to the input end with the given NetChannelLocation, and having the given
     * poison immunity level
     * 
     * @param loc
     *            The location of the input end of the channel
     * @param immunityLevel
     *            The immunity to poison that this channel has
     * @return A new NetChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetChannelOutput<T> one2net(NetChannelLocation loc, int immunityLevel)
        throws JCSPNetworkException;

    /**
     * Creates a new NetChannelOutput connected to the input end with the given NetChannelLocation, and uses the given
     * filter to encode outgoing messages
     * 
     * @param loc
     *            The location of the input end of the channel
     * @param filter
     *            The filter used to encode outgoing messages
     * @return A new NetChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetChannelOutput<T> one2net(NetChannelLocation loc, NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException;

    /**
     * Creates a new NetChannelOutput connected to the given location with the given poison immunity level, and uses the
     * given filter to encode outgoing messages.
     * 
     * @param loc
     *            The location of the input end of the channel
     * @param immunityLevel
     *            The immunity to poison that this channel has
     * @param filter
     *            The filter used to encode outgoing messages
     * @return A new NetChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetChannelOutput<T> one2net(NetChannelLocation loc, int immunityLevel, NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException;

    /**
     * Creates a new NetSharedChannelOutput connected to the input end at the given location
     * 
     * @param loc
     *            The location of the input end of the channel
     * @return A new NetSharedChannelInput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetSharedChannelOutput<T> any2net(NetChannelLocation loc)
        throws JCSPNetworkException;

    /**
     * Creates a new NetSharedChannelOutput connected to the input end at the given location and with the given poison
     * immunity level
     * 
     * @param loc
     *            The location of the input end of the channel
     * @param immunityLevel
     *            The immunity to poison that the channel has
     * @return A new NetSharedChannelInput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetSharedChannelOutput<T> any2net(NetChannelLocation loc, int immunityLevel)
        throws JCSPNetworkException;

    /**
     * Creates a new NetSharedChannelOutput connected to the input end with the given location, and uses the given
     * filter to encode outgoing messages
     * 
     * @param loc
     *            The location of the input end of the channel
     * @param filter
     *            The filter used to encode outgoing messages
     * @return A new NetSharedChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetSharedChannelOutput<T> any2net(NetChannelLocation loc, NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException;

    /**
     * Creates a new NetSharedChannelOutput connected to the input end with the given location and with the given
     * immunity level, which uses the given filter to encode outgoing messages.
     * 
     * @param loc
     *            The location of the input end of the channel
     * @param immunityLevel
     *            The immunity to poison that this channel has
     * @param filter
     *            The filter used to encode outgoing messages
     * @return A new NetSharedChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetSharedChannelOutput<T> any2net(NetChannelLocation loc, int immunityLevel,
            NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException;

    /**
     * Creates a new NetChannelOutput connected to the channel with the given vcn on the given Node
     * 
     * @param nodeID
     *            The NodeID of the node that the input channel resides on
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @return A new NetChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetChannelOutput<T> one2net(NodeID nodeID, int vcn)
        throws JCSPNetworkException;

    /**
     * Creates a new NetChannelOutput connected to the channel with the given vcn on the given Node, and with the given
     * poison immunity level
     * 
     * @param nodeID
     *            The NodeID of the Node that the input channel resides on
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @param immunityLevel
     *            The immunity to poison of the channel
     * @return A new NetChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetChannelOutput<T> one2net(NodeID nodeID, int vcn, int immunityLevel)
        throws JCSPNetworkException;

    /**
     * Creates a new NetChannelOutput connected to the channel with the given vcn on the given Node, which uses the
     * given filter to encode outgoing messages
     * 
     * @param nodeID
     *            The NodeID of the Node that the input channel resides on
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @param filter
     *            The filter to encode outgoing messages
     * @return A new NetChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong with the underlying architecture
     */
    public <T> NetChannelOutput<T> one2net(NodeID nodeID, int vcn, NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException;

    /**
     * Creates a new NetChannelOutput connected to the channel with the given vcn on the given Node, with the given
     * poison immunity level and uses the given filter to encode outgoing messages
     * 
     * @param nodeID
     *            The NodeID of the Node that the input channel resides on
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @param immunityLevel
     *            The immunity to poison that the channel has
     * @param filter
     *            The filter that encodes the outgoing messages
     * @return A new NetChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong with the underlying architecture
     */
    public <T> NetChannelOutput<T> one2net(NodeID nodeID, int vcn, int immunityLevel, NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException;

    /**
     * Creates a new NetSharedChannelOutput connected to the channel with the given vcn on the given Node
     * 
     * @param nodeID
     *            The NodeID of the Node to connect to
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @return A new NetSharedChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetSharedChannelOutput<T> any2net(NodeID nodeID, int vcn)
        throws JCSPNetworkException;

    /**
     * Creates a new NetSharedChannelOutput connected to the channel with the given vcn on the given Node and the given
     * poison immunity
     * 
     * @param nodeID
     *            The NodeID of the Node to connect to
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @param immunityLevel
     *            The immunity to poison that the channel has
     * @return A new NetSharedChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetSharedChannelOutput<T> any2net(NodeID nodeID, int vcn, int immunityLevel)
        throws JCSPNetworkException;

    /**
     * Creates a new NetSharedChannelOutput connected to the channel with the given vcn on the given Node, which uses
     * the given filter to encode outgoing messages
     * 
     * @param nodeID
     *            The NodeID of the Node to connect to
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @param filter
     *            The filter used to encode the outgoing messages
     * @return A new NetSharedChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetSharedChannelOutput<T> any2net(NodeID nodeID, int vcn, NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException;

    /**
     * Creates a new NetSharedChannelOutput connected to the channel with the given vcn on the given Node, with the
     * given poison immunity level, which uses the given filter to encode outgoing messages
     * 
     * @param nodeID
     *            The NodeID of the Node to connect to
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @param immunityLevel
     *            The immunity to poison that the channel has
     * @param filter
     *            The filter used to encode outgoing messages
     * @return A new NetSharedChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetSharedChannelOutput<T> any2net(NodeID nodeID, int vcn, int immunityLevel,
            NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException;

    /**
     * Creates a new NetChannelOutput connected to the channel with the given vcn on the given Node
     * 
     * @param nodeAddr
     *            The NodeAddress of the Node to connect to
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @return A new NetChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetChannelOutput<T> one2net(NodeAddress nodeAddr, int vcn)
        throws JCSPNetworkException;

    /**
     * Creates a new NetChannelOutput connected to the channel with the given vcn on the given Node with the given
     * poison immunity
     * 
     * @param nodeAddr
     *            The NodeAddress of the Node to connect to
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @param immunityLevel
     *            The immunity to poison the channel has
     * @return A new NetChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetChannelOutput<T> one2net(NodeAddress nodeAddr, int vcn, int immunityLevel)
        throws JCSPNetworkException;

    /**
     * Creates a new NetChannelOutput connected to the channel with the given vcn on the given Node which uses the given
     * filter to encode outgoing messages
     * 
     * @param nodeAddr
     *            The NodeAddress of the Node to connect to
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @param filter
     *            The filter used to encode outgoing messages
     * @return A new NetChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetChannelOutput<T> one2net(NodeAddress nodeAddr, int vcn, NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException;

    /**
     * Creates a new NetChannelOutput connected to the channel with the given vcn on the given Node which has the given
     * poison immunity and uses the given filter to encode outgoing messages
     * 
     * @param nodeAddr
     *            The NodeAddress of the Node to connect to
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @param immunityLevel
     *            The immunity to poison that the channel has
     * @param filter
     *            The filter used to encode outgoing messages
     * @return A new NetChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetChannelOutput<T> one2net(NodeAddress nodeAddr, int vcn, int immunityLevel,
            NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException;

    /**
     * Creates a new NetSharedChannelOutput connected to the channel with the given vcn on the given Node
     * 
     * @param nodeAddr
     *            The NodeAddress of the Node to connect to
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @return A new NetSharedChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetSharedChannelOutput<T> any2net(NodeAddress nodeAddr, int vcn)
        throws JCSPNetworkException;

    /**
     * Creates a new NetSharedChannelOutput connected to the channel with the given vcn on the given Node which has the
     * given poison immunity
     * 
     * @param nodeAddr
     *            The NodeAddress of the Node to connect to
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @param immunityLevel
     *            The immunity to poison that the channel has
     * @return A new NetSharedChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetSharedChannelOutput<T> any2net(NodeAddress nodeAddr, int vcn, int immunityLevel)
        throws JCSPNetworkException;

    /**
     * Creates a new NetSharedChannelOutput connected to the channel with the given vcn on the given Node which uses the
     * given filter to encode outgoing messages
     * 
     * @param nodeAddr
     *            The NodeAddress of the Node to connect to
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @param filter
     *            The immunity to poison that the channel has
     * @return A new NetSharedChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetSharedChannelOutput<T> any2net(NodeAddress nodeAddr, int vcn, NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException;

    /**
     * Creates a new NetSharedChannelOutput connected to the channel with the given vcn on the given Node that has the
     * given poison immunity level and uses the given filter to encode outgoing messages
     * 
     * @param nodeAddr
     *            The NodeAddress of the Node to connect to
     * @param vcn
     *            The Virtual Channel Number of the input channel
     * @param immunityLevel
     *            The immunity to poison that the channel has
     * @param filter
     *            The filter used to encode outgoing messages
     * @return A new NetSharedChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public <T> NetSharedChannelOutput<T> any2net(NodeAddress nodeAddr, int vcn, int immunityLevel,
            NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException;

}
