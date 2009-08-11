package org.jcsp.net2;

import java.io.Serializable;

/**
 * This class is used to uniquely identify a Node within the entire JCSP network of Nodes in operation. This is to allow
 * ease to identify individual Nodes when IDs come in, and to quickly find them within tables of other Links to allow
 * usage of existing connections. This is different to a NodeAddress, which is a symbolic name representing a Node, and
 * which therefore may be repeated. The hope here is that by using enough pieces of data the Node should have a unique
 * identification. This is done by gathering the information on the current system time in milliseconds, the current
 * free memory of the JVM, the hash code of a newly created object, the name of the Node, if there is one, and the
 * address of the Node itself. Having this much information should provide us with a unique ID. Other implementations of
 * the protocol can use other means of identifying a Node uniquely, but they must use the same amount of data, e.g.
 * string(number 64 bits) - string(number 64 bits) - string (number 32 bits) - string - address string, when
 * communicating with another JCSP Node for the sake of compatibility.
 * 
 * @author Kevin Chalmers
 */
public final class NodeID
    implements Comparable, Serializable
{
    /**
     * The SUID of this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Current time in milliseconds
     */
    private final long time;

    /**
     * Current amount of free memory to the JVM
     */
    private final long mem;

    /**
     * Hash code of a object
     */
    private final int hashCode;

    /**
     * Name of the Node
     */
    private final String name;

    /**
     * Address of the Node
     */
    private final NodeAddress address;

    /**
     * Constructor taking the name and the address of the Node
     * 
     * @param nodeName
     *            Symbolic name of the Node
     * @param nodeAddress
     *            Symbolic address of the Node
     */
    NodeID(String nodeName, NodeAddress nodeAddress)
    {
        this.time = System.currentTimeMillis();
        this.mem = Runtime.getRuntime().freeMemory();
        this.hashCode = new Object().hashCode();
        this.name = nodeName;
        this.address = nodeAddress;
    }

    /**
     * Constructor taking the full details for a remote Node connection
     * 
     * @param long1
     *            The time component of the remote Node
     * @param long2
     *            The memory component of the remote Node
     * @param int1
     *            The hashCode component of the remote Node
     * @param nodeName
     *            The name component of the remote Node
     * @param nodeAddress
     *            The NodeAddress component of the remote Node
     */
    public NodeID(long long1, long long2, int int1, String nodeName, NodeAddress nodeAddress)
    {
        this.time = long1;
        this.mem = long2;
        this.hashCode = int1;
        this.name = nodeName;
        this.address = nodeAddress;
    }

    /**
     * Compares this NodeID with another NodeID.
     * 
     * @param arg0
     * @return -1, 0 or 1 if less than, equal, or greater than the other NodeID
     */
    public int compareTo(final Object arg0)
    {
        // Check if other object is a NodeID. If not throw exception
        if (!(arg0 instanceof NodeID))
            throw new IllegalArgumentException("Attempting to compare NodeID to an object that is not a NodeID");

        NodeID other = (NodeID)arg0;

        // Compare to other NodeID values
        if (other.time < this.time)
            return 1;
        else if (other.time > this.time)
            return -1;
        else
        {
            // Time part is equal
            if (other.mem < this.mem)
                return 1;
            else if (other.mem > this.mem)
                return -1;
            else
            {
                // Memory part is equal
                if (other.hashCode < this.hashCode)
                    return 1;
                else if (other.hashCode > this.hashCode)
                    return -1;
                else
                {
                    // Hashcode part is equal
                    if (!(other.name.equals(this.name)))
                        return this.name.compareTo(other.name);
                    return this.address.compareTo(other.address);
                }
            }
        }
    }

    /**
     * Checks if the given object is equal to this NodeID
     * 
     * @param arg0
     * @return True if equal, false otherwise
     */
    public boolean equals(Object arg0)
    {
        return this.compareTo(arg0) == 0;
    }

    /**
     * Returns the hashCode for this object
     * 
     * @return Hashcode for the NodeID
     */
    public int hashCode()
    {
        return this.hashCode;
    }

    /**
     * Converts the NodeID into a string for communication with other implementations, or for display purposes.
     * 
     * @return String representation of the NodeID
     */
    public String toString()
    {
        return this.time + "-" + this.mem + "-" + this.hashCode + "-" + this.name + "-" + this.address.toString();
    }

    /**
     * Gets the NodeAddress part of the NodeID
     * 
     * @return The NodeAddress part of the NodeID
     */
    public NodeAddress getNodeAddress()
    {
        return this.address;
    }

    /**
     * Converts a string representation of a NodeID back to a NodeID object
     * 
     * @param str
     *            The string version of a NodeID
     * @return A new NodeID created from the String representation
     */
    public static NodeID parse(String str)
    {
        // Split the string into its separate parts
        String[] pieces = new String[5];
        int index = 0;
        int last = 0;
        for (int i = 0; i < 5; i++)
        {
            index = str.indexOf("-", index + 1);
            if (index == -1)
                index = str.length();
            pieces[i] = str.substring(last, index);
            last = index + 1;
        }

        // Get the relevant parts
        long time = Long.parseLong(pieces[0]);
        long mem = Long.parseLong(pieces[1]);
        int hashCode = Integer.parseInt(pieces[2]);
        String name = pieces[3];

        // Parse the address
        NodeAddress addr = NodeAddress.parse(pieces[4]);

        // Return the NodeID
        return new NodeID(time, mem, hashCode, name, addr);
    }
}
