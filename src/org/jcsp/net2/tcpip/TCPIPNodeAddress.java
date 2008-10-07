package org.jcsp.net2.tcpip;

import org.jcsp.net2.JCSPNetworkException;
import org.jcsp.net2.Link;
import org.jcsp.net2.LinkServer;
import org.jcsp.net2.NodeAddress;
import org.jcsp.net2.ProtocolID;

/**
 * A concrete implementation of a NodeAddress that is designed for TCP/IP connections.
 * 
 * @see NodeAddress
 * @author Kevin Chalmers
 */
public final class TCPIPNodeAddress
    extends NodeAddress
{
    /**
     * The SUID for this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * The IP address part of the address
     */
    private String ip;

    /**
     * The port part of the address
     */
    private int port;

    /**
     * Creates a new TCPIPNodeAddress from an IP address and port
     * 
     * @param ipAddress
     *            The IP address part of the NodeAddress
     * @param portNumber
     *            The port number part of the NodeAddress
     */
    public TCPIPNodeAddress(String ipAddress, int portNumber)
    {
        this.ip = ipAddress;
        this.port = portNumber;
        this.protocol = "tcpip";
        this.address = ipAddress + ":" + portNumber;
    }

    /**
     * Creates a new TCPIPNodeAddress using the local IP address and a given port number. Allows a
     * 
     * @param portNumber
     *            The port number to use
     */
    public TCPIPNodeAddress(int portNumber)
    {
        this.port = portNumber;
        this.ip = "";
        this.protocol = "tcpip";
    }

    /**
     * Creates a new TCPIPNodeAddress
     */
    public TCPIPNodeAddress()
    {
        this.port = 0;
        this.ip = "";
        this.protocol = "tcpip";
    }

    /**
     * Gets the port number part of this address
     * 
     * @return The port number part of the address
     */
    public final int getPort()
    {
        return this.port;
    }

    /**
     * Sets the port part of the address. Used internally in JCSP
     * 
     * @param portNumber
     *            The port number to use
     */
    void setPort(int portNumber)
    {
        this.port = portNumber;
    }

    /**
     * Gets the IP address part of the address
     * 
     * @return The IP Address part of the address
     */
    public final String getIpAddress()
    {
        return this.ip;
    }

    /**
     * Sets the IP address part of the NodeAddress. Used internally in JCSP
     * 
     * @param ipAddr
     *            The IP address to use
     */
    void setIpAddress(String ipAddr)
    {
        this.ip = ipAddr;
    }

    /**
     * Sets the address String. Used internally within JCSP
     * 
     * @param str
     *            The String to set as the address
     */
    void setAddress(String str)
    {
        this.address = str;
    }

    /**
     * Creates a new TCPIPLink connected to a Node with this address
     * 
     * @return A new TCPIPLink connected to this address
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong during the creation of the Link
     */
    protected Link createLink()
        throws JCSPNetworkException
    {
        return new TCPIPLink(this);
    }

    /**
     * Creates a new TCPIPLinkServer listening on this address
     * 
     * @return A new TCPIPLinkServer listening on this address
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong during the creation of the LinkServer
     */
    protected LinkServer createLinkServer()
        throws JCSPNetworkException
    {
        return new TCPIPLinkServer(this);
    }

    /**
     * Returns the TCPIPProtocolID
     * 
     * @return TCPIPProtocolID
     */
    protected ProtocolID getProtocolID()
    {
        return TCPIPProtocolID.getInstance();
    }

}
