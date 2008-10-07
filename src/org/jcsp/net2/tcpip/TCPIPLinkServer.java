package org.jcsp.net2.tcpip;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.jcsp.lang.ProcessManager;
import org.jcsp.net2.JCSPNetworkException;
import org.jcsp.net2.LinkServer;
import org.jcsp.net2.Node;
import org.jcsp.net2.NodeID;

/**
 * Concrete implementation of a LinkServer that listens on a TCP/IP based ServerSocket. For information on LinkServer,
 * see the relevant documentation.
 * <p>
 * It is possible for an advanced user to create this object themselves, although it is not recommended. For example:
 * </p>
 * <p>
 * <code>
 * TCPIPLinkServer serv = new TCPIPLinkServer(address);<br>
 * new ProcessManager(serv).start();
 * </code>
 * </p>
 * <p>
 * This is done automatically during Node initialisation. However, if the machine used has multiple interfaces, this can
 * be used to listen on another interface also.
 * </p>
 * 
 * @see LinkServer
 * @author Kevin Chalmers
 */
public final class TCPIPLinkServer
    extends LinkServer
{
    /**
     * The ServerSocket that this class wraps around. The process listens on this connection
     */
    private final ServerSocket serv;

    /**
     * The NodeAddress that this LinkServer is listening on. This should be the same as the Node's address.
     */
    final TCPIPNodeAddress listeningAddress;

    /**
     * Creates LinkServer by wrapping round an existing ServerSocket. Used internally by JCSP
     * 
     * @param serverSocket
     *            The ServerSocket to create the LinkServer with
     */
    TCPIPLinkServer(ServerSocket serverSocket)
    {
        // We need to set the NodeAddress. Create from ServerSocket address and port
        this.listeningAddress = new TCPIPNodeAddress(serverSocket.getInetAddress().getHostAddress(), serverSocket
                .getLocalPort());
        this.serv = serverSocket;
    }

    /**
     * Creates a new TCPIPLinkServer listening on the given address
     * 
     * @param address
     *            The address to listen on for new connections
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong during the creation of the ServerSocket
     */
    public TCPIPLinkServer(TCPIPNodeAddress address)
        throws JCSPNetworkException
    {
        try
        {
            // First check if we have an ip address in the string
            if (address.getIpAddress().equals(""))
            {
                // Get the local IP addresses
                InetAddress[] local = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
                InetAddress toUse = InetAddress.getLocalHost();

                // We basically have four types of addresses to worry about. Loopback (127), link local (169),
                // local (192) and (possibly) global. Grade each 1, 2, 3, 4 and use highest scoring address. In all
                // cases use first address of that score.
                int current = 0;

                // Loop until we have checked all the addresses
                for (int i = 0; i < local.length; i++)
                {
                    // Ensure we have an IPv4 address
                    if (local[i] instanceof Inet4Address)
                    {
                        // Get the first byte of the address
                        byte first = local[i].getAddress()[0];

                        // Now check the value
                        if (first == (byte)127 && current < 1)
                        {
                            // We have a Loopback address
                            current = 1;
                            // Set the address to use
                            toUse = local[i];
                        }
                        else if (first == (byte)169 && current < 2)
                        {
                            // We have a link local address
                            current = 2;
                            // Set the address to use
                            toUse = local[i];
                        }
                        else if (first == (byte)192 && current < 3)
                        {
                            // We have a local address
                            current = 3;
                            // Set the address to use
                            toUse = local[i];
                        }
                        else
                        {
                            // Assume the address is globally accessible and use by default.
                            toUse = local[i];
                            // Break from the loop
                            break;
                        }
                    }
                }

                // Now set the IP address of the address
                address.setIpAddress(toUse.getHostAddress());

                // Set the address part now, but it may change if we have to get a port number
                address.setAddress(address.getIpAddress() + ":" + address.getPort());
            }

            // Now check if the address has a port number
            if (address.getPort() == 0)
            {
                // No port number supplied. Get one as we create the ServerSocket
                InetAddress socketAddress = InetAddress.getByName(address.getIpAddress());

                // Create the server socket with a random port
                this.serv = new ServerSocket(0, 0, socketAddress);

                // Assign the port to the address
                address.setPort(this.serv.getLocalPort());

                // And set the address
                address.setAddress(address.getIpAddress() + ":" + address.getPort());

                // Set the listening address
                this.listeningAddress = address;
            }
            else
            {
                // Create an IP address from the NodeAddress
                InetAddress inetAddress = InetAddress.getByName(address.getIpAddress());
                // Now create the ServerSocket
                this.serv = new ServerSocket(address.getPort(), 10, inetAddress);
                // Set listeningAddress
                this.listeningAddress = address;
            }
        }
        catch (IOException ioe)
        {
            throw new JCSPNetworkException("Failed to create TCPIPLinkServer on: " + address.getAddress());
        }
    }

    /**
     * The run method for the TCPIPLinkServer process
     */
    public void run()
    {
        // Log start of Link Server
        Node.log.log(this.getClass(), "TCPIP Link Server started on " + this.listeningAddress.getAddress());
        try
        {
            // Now we loop until something goes wrong
            while (true)
            {
                // Receive incoming connection
                Socket incoming = this.serv.accept();
                // Log
                Node.log.log(this.getClass(), "Received new incoming connection");
                // Set TcpNoDelay
                incoming.setTcpNoDelay(true);

                // Now we want to receive the connecting Node's NodeID
                DataInputStream inStream = new DataInputStream(incoming.getInputStream());

                // Receive remote NodeID and parse
                String otherID = inStream.readUTF();
                NodeID remoteID = NodeID.parse(otherID);

                // First check we have a tcpip Node connection
                if (remoteID.getNodeAddress() instanceof TCPIPNodeAddress)
                {
                    // Create an output stream from the Socket
                    DataOutputStream outStream = new DataOutputStream(incoming.getOutputStream());

                    // Now Log that we have received a connection
                    Node.log.log(this.getClass(), "Received connection from: " + remoteID.toString());

                    // Check if already connected
                    if (requestLink(remoteID) == null)
                    {
                        // No existing connection to incoming Node exists. Keep connection

                        // Write OK to the connecting Node
                        outStream.writeUTF("OK");
                        outStream.flush();

                        // Send out our NodeID
                        outStream.writeUTF(Node.getInstance().getNodeID().toString());
                        outStream.flush();

                        // Create Link, register, and start.
                        TCPIPLink link = new TCPIPLink(incoming, remoteID);
                        registerLink(link);
                        new ProcessManager(link).start();
                    }
                    else
                    {
                        // We already have a connection to the incoming Node

                        // Log failed connection
                        Node.log.log(this.getClass(), "Connection to " + remoteID
                                                      + " already exists.  Informing remote Node.");

                        // Write EXISTS to the remote Node
                        outStream.writeUTF("EXISTS");
                        outStream.flush();

                        // Send out NodeID. We do this so the opposite Node can find its own connection
                        outStream.writeUTF(Node.getInstance().getNodeID().toString());
                        outStream.flush();

                        // Close socket
                        incoming.close();
                    }
                }

                // Address is not a TCPIP address. Close socket. This will cause an exception on the opposite Node
                else
                    incoming.close();
            }
        }
        catch (IOException ioe)
        {
            // We can't really recover from this. This may happen if the network connection was lost.
            // Log and fail
            Node.err.log(this.getClass(), "TCPIPLinkServer failed.  " + ioe.getMessage());
        }
    }

}
