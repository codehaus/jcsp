package org.jcsp.net2.tcpip;

import org.jcsp.net2.NodeAddress;
import org.jcsp.net2.ProtocolID;

/**
 * Concrete implementation of a ProtocolID used to parse a string representation of a TCPIPNodeAddress into a
 * TCPIPNodeAddress object.
 * 
 * @author Kevin Chalmers
 */
public final class TCPIPProtocolID
    extends ProtocolID
{
    /**
     * Singleton instance of this class
     */
    private static TCPIPProtocolID instance = new TCPIPProtocolID();

    /**
     * Gets the singleton instance of this class
     * 
     * @return A new singleton instance of this class
     */
    public static TCPIPProtocolID getInstance()
    {
        return instance;
    }

    /**
     * Default private constructor
     */
    private TCPIPProtocolID()
    {
        // Empty constructor
    }

    /**
     * Parses a string to recreate a TCPIPNodeAddress object
     * 
     * @param addressString
     *            String representing the address
     * @return A new TCPIPNodeAddress object
     * @throws IllegalArgumentException
     *             Thrown if the address is not in a correct form
     */
    protected NodeAddress parse(String addressString)
        throws IllegalArgumentException
    {
        // Split address into IP and port
        int index = addressString.indexOf("\\\\");
        String temp = addressString.substring(index + 2);
        index = temp.indexOf(":");
        String address = temp.substring(0, index);
        int port = Integer.parseInt(temp.substring(index + 1, temp.length()));

        return new TCPIPNodeAddress(address, port);
    }

}
