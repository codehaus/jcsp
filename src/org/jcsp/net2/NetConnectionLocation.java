/**
 * 
 */
package org.jcsp.net2;

import java.io.Serializable;

/**
 * @author Kevin
 */
public final class NetConnectionLocation
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
     * The vconnn portion of the location
     */
    private final int vconnn;

    /**
     * Creates a new NetConnectionLocation
     * 
     * @param aNodeID
     *            The NodeID part of the location
     * @param aVConnN
     *            The vconnn part of the location
     */
    public NetConnectionLocation(NodeID aNodeID, int aVConnN)
    {
        this.nodeID = aNodeID;
        this.vconnn = aVConnN;
    }

    /**
     * Gets the NodeID part of the location
     * 
     * @return The NodeID part of the NetConnectionLocation
     */
    public NodeID getNodeID()
    {
        return this.nodeID;
    }

    /**
     * Gets the NodeAddress part of the location
     * 
     * @return The NodeAddress part of the NetConnectionLocation
     */
    public NodeAddress getNodeAddress()
    {
        return this.nodeID.getNodeAddress();
    }

    /**
     * Gets the vconnn part of the location
     * 
     * @return The VConnN part of the NetConnectionLocation
     */
    public int getVConnN()
    {
        return this.vconnn;
    }

    /**
     * Converts the NetConnectionLocation object into a string representation of the form nconnl://[NodeID]/[VConnN]
     * 
     * @return The String form of the NetConnectionLocation
     */
    public String toString()
    {
        return "nconnl://" + this.nodeID.toString() + "/" + this.vconnn;
    }

    /**
     * Converts the string form of a NetConnectionLocation back into its object form
     * 
     * @param str
     *            The string representation of a NetConnectionLocation
     * @return A new NetConnectionLocation created from the String representation
     */
    public static NetConnectionLocation parse(String str)
    {
        if (str.equalsIgnoreCase("null"))
            return null;
        if (str.startsWith("nconnl://"))
        {
            String toParse = str.substring(6);
            String[] addressBits = toParse.split("/");
            NodeID nodeID = NodeID.parse(addressBits[0]);
            int vcn = Integer.parseInt(addressBits[1]);
            return new NetConnectionLocation(nodeID, vcn);
        }
        throw new IllegalArgumentException("String is not a string form of a NetConnectionLocation");
    }
}
