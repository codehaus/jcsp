package org.jcsp.net2;

import java.io.Serializable;

/**
 * This class is a data structure representing the location of a NetBarrier in a network. The NetBarrierLocation
 * consists of the NodeID of the Node on which the NetBarrier resides, and its Virtual Barrier Number, which is the
 * number uniquely identifying the NetBarrier on said node.
 * <p>
 * To acquire the NetBarrierLocation of a NetBarrier, use the getLocation method:
 * </p>
 * <p>
 * <code>
 * NetBarrierLocation location = (NetBarrierLocation)bar.getLocation();
 * </code>
 * </p>
 * <p>
 * The location returned depends on whether the NetBarrier is a client or a server end. A server end of a NetBarrier
 * will return its own location. A client end of a NetBarrier will return the location of the server end it is connected
 * to. This is because we consider the NetBarrier to be a single, virtual construct, with only one location. That
 * location is where the server end of the NetBarrier is located.
 * </p>
 * 
 * @see NetBarrier
 * @see NetLocation
 * @author Kevin Chalmers
 */
public final class NetBarrierLocation
    extends NetLocation
    implements Serializable
{
    /**
     * The SUID of this object. Required for Serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The NodeID portion of the location structure
     */
    private final NodeID nodeID;

    /**
     * The index portion of the location structure
     */
    private final int vbn;

    /**
     * Constructor to create a new NetBarrierLocation
     * 
     * @param aNodeID
     *            The NodeID portion of the NetBarrierLocation
     * @param aVBN
     *            The index part of the NetBarrierLocation
     */
    public NetBarrierLocation(NodeID aNodeID, int aVBN)
    {
        this.nodeID = aNodeID;
        this.vbn = aVBN;
    }

    /**
     * Gets the NodeID part of the location
     * 
     * @return The NodeID part of the NetBarrierLocation
     */
    public NodeID getNodeID()
    {
        return this.nodeID;
    }

    /**
     * Gets the NodeAddress part of the location
     * 
     * @return The NodeAddress part of the NetBarrierLocation
     */
    public NodeAddress getNodeAddress()
    {
        return this.nodeID.getNodeAddress();
    }

    /**
     * Gets the index part of the location
     * 
     * @return The VBN part of the NetBarrierLocation
     */
    public int getVBN()
    {
        return this.vbn;
    }

    /**
     * Returns the string representation of the NetBarrierLocation. This takes the form of nbl://[<I>NodeID</I>]/[<I>VBN</I>].
     * This string representation has been created to allow other frameworks to interpret the NetBarrierLocation.
     * 
     * @return String representation of the NetBarrierLocation
     */
    public String toString()
    {
        return "nbl://" + this.nodeID.toString() + "/" + this.vbn;
    }

    /**
     * Takes the string representation of a NetBarrierLocation and converts it back into an object for usage by JCSP.
     * 
     * @param str
     *            The string representation of the NetBarrierLocation
     * @return A NetBarrierLocation produced from a String representation
     * @throws IllegalArgumentException
     *             Thrown if a non NetBarrierLocation is attempted to be parsed
     */
    public static NetBarrierLocation parse(String str)
        throws IllegalArgumentException
    {
        if (str.equalsIgnoreCase("null"))
            return null;
        // Check that the string starts with nbl://
        if (str.startsWith("nbl://"))
        {
            // Take the off the starting part of the string
            String toParse = str.substring(6);
            // Split the string in two
            int index = toParse.indexOf("/");
            // Parse the NodeID portion
            NodeID nodeID = NodeID.parse(toParse.substring(0, index));
            // Parse the VBN portion
            int vcn = Integer.parseInt(toParse.substring(index + 1));
            // Return a new NetBarrierLocation created from the two parts
            return new NetBarrierLocation(nodeID, vcn);
        }
        // We don't have a NetBarrierLocation
        throw new IllegalArgumentException("String is not a string form of a NetBarrierLocation");
    }
}
