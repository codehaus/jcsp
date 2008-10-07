package org.jcsp.net2;

import org.jcsp.net.tcpip.TCPIPProtocolID;

/**
 * This abstract class must be defined in concrete protocol implementations. Its main usage is to allow installation and
 * correct parsing of relevant address strings into correct address objects. See TCPIPProtocolID for an example.
 * 
 * @see TCPIPProtocolID
 * @author Kevin Chalmers
 */
public abstract class ProtocolID
{
    /**
     * Parses an address string into an address object
     * 
     * @param addressString
     *            String representation of an address
     * @return A new NodeAddress object
     * @throws IllegalArgumentException
     *             Thrown if the string is in an incorrect form
     */
    protected abstract NodeAddress parse(String addressString)
        throws IllegalArgumentException;
}
