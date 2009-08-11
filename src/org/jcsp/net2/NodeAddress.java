package org.jcsp.net2;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * This abstract class defines encapsulates the address of a Node within a JCSP networked system. Specific protocols
 * must provide concrete implementations of this class to allow Node initialisation and connection. One concrete example
 * is provided in the org.jcsp.net2.tcpip package.
 * 
 * @see Node
 * @author Kevin Chalmers (updated from Quickstone Technologies)
 */
public abstract class NodeAddress
    implements Serializable, Comparable
{
    /**
     * String representing the protocol in used
     */
    protected String protocol;

    /**
     * String representation of the address
     */
    protected String address;

    /**
     * The table of installed protocols on this Node
     */
    private static Hashtable installedProtocols = new Hashtable();

    /**
     * Gets the string representing the protocol
     * 
     * @return The String representation of the protocol part of the NodeAddress
     */
    public String getProtocol()
    {
        return this.protocol;
    }

    /**
     * Gets a string representing the address
     * 
     * @return The String representation of the address part of the NodeAddress
     */
    public String getAddress()
    {
        return this.address;
    }

    /**
     * Converts the NodeAddress into a String. The form is [protocol]\\[address]
     * 
     * @return A String representation of this NodeAddress
     */
    public String toString()
    {
        return this.protocol + "\\\\" + this.address;
    }

    /**
     * Gets the hash code of this object
     * 
     * @return Hashcode for this NodeAddress
     */
    public int hashCode()
    {
        return this.address.hashCode();
    }

    /**
     * Checks if this NodeAddress is equal to another
     * 
     * @param obj
     *            The NodeAddress to compare to
     * @return True if object is equal to this NodeAddress, false otherwise
     */
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof NodeAddress))
            return false;
        NodeAddress other = (NodeAddress)obj;
        return (this.protocol.equals(other.protocol) && this.address.equals(other.address));
    }

    /**
     * Compares this NodeAddress to another
     * 
     * @param arg0
     *            The NodeAddress to compare to
     * @return 1 if object is greater than this one, 0 if they are equal, -1 otherwise
     */
    public int compareTo(final Object arg0)
    {
        if (!(arg0 instanceof NodeAddress))
            return -1;
        NodeAddress other = (NodeAddress)arg0;
        if (!this.protocol.equals(other.protocol))
            return this.protocol.compareTo(other.protocol);
        return this.address.compareTo(other.address);
    }

    /**
     * Creates a Link connected to this address
     * 
     * @return A new Link connected to this address
     * @throws JCSPNetworkException
     *             If something goes wrong during the creation of the Link
     */
    protected abstract Link createLink()
        throws JCSPNetworkException;

    /**
     * Creates a LinkServer listening on this address
     * 
     * @return A new LinkServer listening on this address
     * @throws JCSPNetworkException
     *             If something goes wrong during the creation of the LinkServer
     */
    protected abstract LinkServer createLinkServer()
        throws JCSPNetworkException;

    /**
     * Retrieves the correct protocol handler for the implemented address type. This is used during Node initialisation
     * 
     * @return the ProtocolID for this address type
     */
    protected abstract ProtocolID getProtocolID();

    /**
     * Parses a string representation of a NodeAddress back to its object form
     * 
     * @param str
     *            The string to parse
     * @return A new NodeAddress created from a String form
     * @throws IllegalArgumentException
     *             Thrown if the string is not for a recognised protocol.
     */
    public static NodeAddress parse(String str)
        throws IllegalArgumentException
    {
        int index = str.indexOf("\\\\");
        ProtocolID protocol = (ProtocolID)NodeAddress.installedProtocols.get(str.substring(0, index));
        if (protocol != null)
        {
            // return protocol.parse(str.substring(index+4)); <-- This kills the IP address :-(
			// ProtocolID's parse expects the string, including slashes (e.g. \\127.0.0.1:1234)
			return protocol.parse (str.substring (index));
        }
        throw new IllegalArgumentException("Unknown protocol used for parsing NodeAddress");
    }

    /**
     * Installs a new Protocol on the Node
     * 
     * @param name
     *            Name of the protocol to install
     * @param protocol
     *            ProtocolID installed
     */
    public synchronized static void installProtocol(String name, ProtocolID protocol)
    {
        if (!NodeAddress.installedProtocols.containsKey(name))
            NodeAddress.installedProtocols.put(name, protocol);
        // If the protocol is already installed, we do nothing.
    }
}
