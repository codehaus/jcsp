package org.jcsp.net2;

import java.io.Serializable;

/**
 * This class is a data structure representing the location of a NetChannelInput in a network. The NetChannelLocation
 * consists of the NodeID of the Node on which the NetChannelInput resides, and its Virtual Channel Number, which is the
 * number uniquely identifying the NetChannelInput on said Node.
 * <p>
 * To acquire the NetChannelLocation of a NetBarrier, use the getLocation method:
 * </p>
 * <p>
 * <code>
 * NetChannelLocation location = (NetChannelLocation)chan.getLocation();
 * </code>
 * </p>
 * <p>
 * The location returned depends on whether the channel is a NetChannelInput or a NetChannelOutput end. An input end
 * will return its own location. An output end will return the location of the input end it is connected to. This is
 * because we consider a networked channel to be a single, virtual construct, with only one location. That location is
 * where the input end is located.
 * </p>
 * 
 * @see NetChannelInput
 * @see NetChannelOutput
 * @see NetLocation
 * @author Kevin Chalmers
 */
public final class NetChannelLocation
    extends NetLocation
    implements Serializable
{
    /**
     * The SUID representing this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * The NodeID portion of the location
     */
    private final NodeID nodeID;

    /**
     * The vcn portion of the location
     */
    private final int vcn;

    /**
     * Creates a new NetChannelLocation
     * 
     * @param aNodeID
     *            The NodeID part of the location
     * @param aVCN
     *            The vcn part of the location
     */
    public NetChannelLocation(NodeID aNodeID, int aVCN)
    {
        this.nodeID = aNodeID;
        this.vcn = aVCN;
    }

    /**
     * Gets the NodeID part of the location
     * 
     * @return The NodeID part of the NetChannelLocation
     */
    public NodeID getNodeID()
    {
        return this.nodeID;
    }

    /**
     * Gets the NodeAddress part of the location
     * 
     * @return The NodeAddress part of the NetChannelLocation
     */
    public NodeAddress getNodeAddress()
    {
        return this.nodeID.getNodeAddress();
    }

    /**
     * Gets the vcn part of the location
     * 
     * @return The VCN part of the NetChannelLocation
     */
    public int getVCN()
    {
        return this.vcn;
    }

    /**
     * Converts the NetChannelLocation object into a string representation of the form ncl://[NodeID]/[VCN]
     * 
     * @return The String form of the NetChannelLocation
     */
    public String toString()
    {
        return "ncl://" + this.nodeID.toString() + "/" + this.vcn;
    }

    /**
     * Converts the string form of a NetChannelLocation back into its object form
     * 
     * @param str
     *            The string representation of a NetChannelLocation
     * @return A new NetChannelLocation created from the String representation
     */
    public static NetChannelLocation parse(String str)
    {
        if (str.equalsIgnoreCase("null"))
            return null;
        if (str.startsWith("ncl://"))
        {
            String toParse = str.substring(6);
            int index = toParse.indexOf("/");
            NodeID nodeID = NodeID.parse(toParse.substring(0, index));
            int vcn = Integer.parseInt(toParse.substring(index + 1));
            return new NetChannelLocation(nodeID, vcn);
        }
        throw new IllegalArgumentException("String is not a string form of a NetChannelLocation");
    }
}
