package org.jcsp.net2;

/**
 * This class is the factory class for creating NetBarrier objects. For information, see NetBarrier.
 * 
 * @see NetBarrier
 * @author Kevin Chalmers
 */
public final class NetBarrierEnd
{
    /**
     * 
     */
    private NetBarrierEnd()
    {
        // Empty constructor
    }

    /**
     * Creates a new server end of a NetBarrier
     * 
     * @param localEnrolled
     *            The number of locally enrolled processes
     * @param netEnrolled
     *            The number of net enrolled processes to expect
     * @return A new NetBarrier server end with the number of enrolled processes
     * @throws IllegalArgumentException
     *             Thrown if the parameters are outside the defined ranges
     */
    public static NetBarrier netBarrier(int localEnrolled, int netEnrolled)
        throws IllegalArgumentException
    {
        return NetBarrier.create(localEnrolled, netEnrolled);
    }

    /**
     * Creates a new server end of a NetBarrier with a given index
     * 
     * @param index
     *            The index to create the NetBarrier with
     * @param localEnrolled
     *            The number of locally enrolled processes
     * @param netEnrolled
     *            The number of remote enrollments to wait for
     * @return A new NetBarrier
     * @throws IllegalArgumentException
     *             Thrown if the parameters are outside the defined ranges
     */
    public static NetBarrier numberedNetBarrier(int index, int localEnrolled, int netEnrolled)
        throws IllegalArgumentException
    {
        return NetBarrier.create(localEnrolled, netEnrolled, index);
    }

    /**
     * Creates a new client end of a NetBarrier
     * 
     * @param loc
     *            The location of the server end of the NetBarrier
     * @param enrolled
     *            The number of locally enrolled processes
     * @return A new NetBarrier client end with the number of enrolled processes
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     * @throws IllegalArgumentException
     *             Thrown if the number of of local enrolled is outside the defined range
     */
    public static NetBarrier netBarrier(NetBarrierLocation loc, int enrolled)
        throws JCSPNetworkException, IllegalArgumentException
    {
        return NetBarrier.create(loc, enrolled);
    }

    /**
     * Creates a new client end of a NetBarrier connected to the barrier with the given index on the given Node
     * 
     * @param nodeID
     *            The NodeID of the Node to connect to
     * @param vbn
     *            The index of the barrier on the remote Node
     * @param enrolled
     *            The number of locally enrolled processes
     * @return A new client end of a NetBarrier
     * @throws JCSPNetworkException
     *             Thrown is something goes wrong in the underlying architecture
     * @throws IllegalArgumentException
     *             Thrown if the number of enrolled is outside the defined range
     */
    public static NetBarrier netBarrier(NodeID nodeID, int vbn, int enrolled)
        throws JCSPNetworkException, IllegalArgumentException
    {
        return NetBarrier.create(new NetBarrierLocation(nodeID, vbn), enrolled);
    }

    /**
     * Creates a new client end of a NetBarrier connected to the barrier with the given index on the given Node
     * 
     * @param addr
     *            NodeAddres of the Node that the barrier is located
     * @param vbn
     *            Index of the barrier to connect to
     * @param enrolled
     *            The number of locally enrolled processes
     * @return A new client end of a NetBarrier
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     * @throws IllegalArgumentException
     *             Thrown if the number of enrolled processes is outside the defined range.
     */
    public static NetBarrier netBarrier(NodeAddress addr, int vbn, int enrolled)
        throws JCSPNetworkException, IllegalArgumentException
    {
        // Get the Link with the given address
        Link link = LinkFactory.getLink(addr);

        // Create a new NetBarrier
        return NetBarrier.create(new NetBarrierLocation(link.remoteID, vbn), enrolled);
    }
}
