/**
 * 
 */
package org.jcsp.net2;

/**
 * A static factory object used to create networked channels. This class basically wraps around a
 * StandardNetChannelEndFactory and allows static access to its channel creation methods.
 * 
 * @see NetChannelEndFactory
 * @see StandardNetChannelEndFactory
 * @author Kevin Chalmers
 */
public final class NetChannel
{
    /**
     * The factory used to create the networked channels
     */
    private static StandardNetChannelEndFactory factory = new StandardNetChannelEndFactory();

    /**
     * Empty, private default constructor. This is a static access class.
     */
    private NetChannel()
    {
        // Empty constructor
    }

    /**
     * Creates a new NetAltingChannelInput
     * 
     * @deprecated Use net2one instead
     * @return A new NetAltingChannelInput
     */
    public static NetAltingChannelInput createNet2One()
    {
        return factory.net2one();
    }

    /**
     * Creates a new NetSharedChannelInput
     * 
     * @deprecated Use net2any instead
     * @return A new NetSharedChannelInput
     */
    public static NetSharedChannelInput createNet2Any()
    {
        return factory.net2any();
    }

    /**
     * Creates a new NetChannelOutput connected to the input channel end with the given location
     * 
     * @deprecated Use one2net instead
     * @param loc
     *            The location to connect the output end to
     * @return A new NetChannelOutput connected to the input end at the given location
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static NetChannelOutput createOne2Net(NetChannelLocation loc)
        throws JCSPNetworkException
    {
        return factory.one2net(loc);
    }

    /**
     * Creates a new NetSharedChannelOutput connected to the input end at the given location
     * 
     * @deprecated Use any2net instead
     * @param loc
     *            The location to connect the output end to
     * @return A new NetSharedChannelOutput connected to the input end at the given location
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static NetSharedChannelOutput createAny2Net(NetChannelLocation loc)
        throws JCSPNetworkException
    {
        return factory.any2net(loc);
    }

    /**
     * Creates a new NetAltingChannelInput
     * 
     * @return A new NetAltingChannelInput
     */
    public static <T> NetAltingChannelInput<T> net2one()
    {
        return factory.net2one();
    }

    /**
     * Creates a new NetAltingChannelInput with the given immunity level for poison
     * 
     * @param immunityLevel
     *            The immunity level for poison
     * @return A new NetAltingChannelInput
     */
    public static <T> NetAltingChannelInput<T> net2one(int immunityLevel)
    {
        return factory.net2one(immunityLevel);
    }

    /**
     * Creates a new NetAltingChannelInput which uses the given filter to decode incoming messages
     * 
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetAltingChannelInput
     */
    public static <T> NetAltingChannelInput<T> net2one(NetworkMessageFilter.FilterRx filter)
    {
        return factory.net2one(filter);
    }

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
    public static <T> NetAltingChannelInput<T> net2one(int immunityLevel, NetworkMessageFilter.FilterRx filter)
    {
        return factory.net2one(immunityLevel, filter);
    }

    /**
     * Creates a new NetSharedChannelInput
     * 
     * @return A new NetSharedChannelInput
     */
    public static <T> NetSharedChannelInput<T> net2any()
    {
        return factory.net2any();
    }

    /**
     * Creates a new NetSharedChannelInput with the given poison immunity level
     * 
     * @param immunityLevel
     *            The immunity level to poison for this channel
     * @return A new NetSharedChannelInput
     */
    public static <T> NetSharedChannelInput<T> net2any(int immunityLevel)
    {
        return factory.net2any(immunityLevel);
    }

    /**
     * Creates a new NetSharedChannelInput which uses the given filter to decode incoming messages
     * 
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetSharedChannelInput
     */
    public static <T> NetSharedChannelInput<T> net2any(NetworkMessageFilter.FilterRx filter)
    {
        return factory.net2any(filter);
    }

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
    public static <T> NetSharedChannelInput<T> net2any(int immunityLevel, NetworkMessageFilter.FilterRx filter)
    {
        return factory.net2any(immunityLevel, filter);
    }

    /**
     * Creates a new NetAltingChannelInput with the given index
     * 
     * @param index
     *            The index to create the channel with
     * @return A new NetAltingChannelInput
     * @throws IllegalArgumentException
     *             Thrown if a channel with the given index already exists
     */
    public static <T> NetAltingChannelInput<T> numberedNet2One(int index)
        throws IllegalArgumentException
    {
        return factory.numberedNet2One(index);
    }

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
    public static <T> NetAltingChannelInput<T> numberedNet2One(int index, int immunityLevel)
        throws IllegalArgumentException
    {
        return factory.numberedNet2One(index, immunityLevel);
    }

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
    public static <T> NetAltingChannelInput<T> numberedNet2One(int index, NetworkMessageFilter.FilterRx filter)
        throws IllegalArgumentException
    {
        return factory.numberedNet2One(index, filter);
    }

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
    public static <T> NetAltingChannelInput<T> numberedNet2One(int index, int immunityLevel,
            NetworkMessageFilter.FilterRx filter)
        throws IllegalArgumentException
    {
        return factory.numberedNet2One(index, immunityLevel, filter);
    }

    /**
     * Creates a new NetSharedChannelInput with the given index
     * 
     * @param index
     *            The index to create the channel with
     * @return A new NetSharedChannelInput
     * @throws IllegalArgumentException
     *             Thrown if a channel with the given index already exists
     */
    public static <T> NetSharedChannelInput<T> numberedNet2Any(int index)
        throws IllegalArgumentException
    {
        return factory.numberedNet2Any(index);
    }

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
    public static <T> NetSharedChannelInput<T> numberedNet2Any(int index, int immunityLevel)
        throws IllegalArgumentException
    {
        return factory.numberedNet2Any(index, immunityLevel);
    }

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
    public static <T> NetSharedChannelInput<T> numberedNet2Any(int index, NetworkMessageFilter.FilterRx filter)
        throws IllegalArgumentException
    {
        return factory.numberedNet2Any(index, filter);
    }

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
    public static <T> NetSharedChannelInput<T> numberedNet2Any(int index, int immunityLevel,
            NetworkMessageFilter.FilterRx filter)
        throws IllegalArgumentException
    {
        return factory.numberedNet2Any(index, immunityLevel, filter);
    }

    /**
     * Creates a new NetChannelOutput connected to the input end with the given NetChannelLocation
     * 
     * @param loc
     *            The location of the input end of the channel
     * @return A new NetChannelOutput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static <T> NetChannelOutput<T> one2net(NetChannelLocation loc)
        throws JCSPNetworkException
    {
        return factory.one2net(loc);
    }

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
    public static <T> NetChannelOutput<T> one2net(NetChannelLocation loc, int immunityLevel)
        throws JCSPNetworkException
    {
		return One2NetChannel.create (loc, immunityLevel, new ObjectNetworkMessageFilter.FilterTX());
    }

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
    public static <T> NetChannelOutput<T> one2net(NetChannelLocation loc, NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException
    {
        return factory.one2net(loc, filter);
    }

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
    public static <T> NetChannelOutput<T> one2net(NetChannelLocation loc, int immunityLevel,
            NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException
    {
        return factory.one2net(loc, immunityLevel, filter);
    }

    /**
     * Creates a new NetSharedChannelOutput connected to the input end at the given location
     * 
     * @param loc
     *            The location of the input end of the channel
     * @return A new NetSharedChannelInput
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static <T> NetSharedChannelOutput<T> any2net(NetChannelLocation loc)
        throws JCSPNetworkException
    {
        return factory.any2net(loc);
    }

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
    public static <T> NetSharedChannelOutput<T> any2net(NetChannelLocation loc, int immunityLevel)
        throws JCSPNetworkException
    {
        return factory.any2net(loc, immunityLevel);
    }

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
    public static <T> NetSharedChannelOutput<T> any2net(NetChannelLocation loc, NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException
    {
        return factory.any2net(loc, filter);
    }

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
    public static <T> NetSharedChannelOutput<T> any2net(NetChannelLocation loc, int immunityLevel,
            NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException
    {
        return factory.any2net(loc, immunityLevel, filter);
    }

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
    public static <T> NetChannelOutput<T> one2net(NodeID nodeID, int vcn)
        throws JCSPNetworkException
    {
        return factory.one2net(nodeID, vcn);
    }

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
    public static <T> NetChannelOutput<T> one2net(NodeID nodeID, int vcn, int immunityLevel)
        throws JCSPNetworkException
    {
        return factory.one2net(nodeID, vcn, immunityLevel);
    }

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
    public static <T> NetChannelOutput<T> one2net(NodeID nodeID, int vcn, NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException
    {
        return factory.one2net(nodeID, vcn, filter);
    }

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
    public static <T> NetChannelOutput<T> one2net(NodeID nodeID, int vcn, int immunityLevel,
            NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException
    {
        return factory.one2net(nodeID, vcn, immunityLevel, filter);
    }

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
    public static <T> NetSharedChannelOutput<T> any2net(NodeID nodeID, int vcn)
        throws JCSPNetworkException
    {
        return factory.any2net(nodeID, vcn);
    }

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
    public static <T> NetSharedChannelOutput<T> any2net(NodeID nodeID, int vcn, int immunityLevel)
        throws JCSPNetworkException
    {
        return factory.any2net(nodeID, vcn, immunityLevel);
    }

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
    public static <T> NetSharedChannelOutput<T> any2net(NodeID nodeID, int vcn, NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException
    {
        return factory.any2net(nodeID, vcn, filter);
    }

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
    public static <T> NetSharedChannelOutput<T> any2net(NodeID nodeID, int vcn, int immunityLevel,
            NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException
    {
        return factory.any2net(nodeID, vcn, immunityLevel, filter);
    }

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
    public static <T> NetChannelOutput<T> one2net(NodeAddress nodeAddr, int vcn)
        throws JCSPNetworkException
    {
        return factory.one2net(nodeAddr, vcn);
    }

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
    public static <T> NetChannelOutput<T> one2net(NodeAddress nodeAddr, int vcn, int immunityLevel)
        throws JCSPNetworkException
    {
        return factory.one2net(nodeAddr, vcn, immunityLevel);
    }

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
    public static <T> NetChannelOutput<T> one2net(NodeAddress nodeAddr, int vcn, NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException
    {
        return factory.one2net(nodeAddr, vcn, filter);
    }

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
    public static <T> NetChannelOutput<T> one2net(NodeAddress nodeAddr, int vcn, int immunityLevel,
            NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException
    {
        return factory.one2net(nodeAddr, vcn, immunityLevel, filter);
    }

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
    public static <T> NetSharedChannelOutput<T> any2net(NodeAddress nodeAddr, int vcn)
        throws JCSPNetworkException
    {
        return factory.any2net(nodeAddr, vcn);
    }

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
    public static <T> NetSharedChannelOutput<T> any2net(NodeAddress nodeAddr, int vcn, int immunityLevel)
        throws JCSPNetworkException
    {
        return factory.any2net(nodeAddr, vcn, immunityLevel);
    }

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
    public static <T> NetSharedChannelOutput<T> any2net(NodeAddress nodeAddr, int vcn, NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException
    {
        return factory.any2net(nodeAddr, vcn, filter);
    }

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
    public static <T> NetSharedChannelOutput<T> any2net(NodeAddress nodeAddr, int vcn, int immunityLevel,
            NetworkMessageFilter.FilterTx filter)
        throws JCSPNetworkException
    {
        return factory.any2net(nodeAddr, vcn, immunityLevel, filter);
    }

}
