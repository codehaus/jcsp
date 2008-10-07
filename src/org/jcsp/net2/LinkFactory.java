package org.jcsp.net2;

import org.jcsp.lang.ProcessManager;

/**
 * This class is used to create a new Link from a given NodeID or NodeAddress.
 * <p>
 * It is perfectly reasonable for a user to create a Link to another Node without utilising the normal connection
 * methods. This class is therefore made public to allow such an occurrence.
 * </p>
 * <p>
 * Example, using TCP/IP
 * </p>
 * <p>
 * <code>
 * TCPIPNodeAddress address = new TCPIPNodeAddress("192.168.1.100", 5000);<br>
 * Link link = LinkFactory.getLink(address);<br>
 * </code>
 * </p>
 * <p>
 * The getLink method will either return an existing Link if one exists, or it shall create a new one if necessary.
 * </p>
 * <p>
 * Using this method allows quick creation of channels / barrier on a remote Node without going through the normal name
 * servers. For example:
 * </p>
 * <p>
 * <code>
 * NetChannelLocation loc = new NetChannelLocation(link.getRemoteNodeID(), 100);<br>
 * NetChannelOutput out = NetChannelEnd.one2net(loc);<br>
 * </code>
 * </p>
 * <p>
 * This method is generally considered faster than using the CNS, or creating channels just using the address and VCN.
 * It does require the user to know the address and channel number that is to be connected too.
 * </p>
 * 
 * @see Link
 * @see NodeAddress
 * @see NodeID
 * @author Kevin Chalmers (updated from Quickstone Technologies)
 */
public final class LinkFactory
{

    /**
     * Default private constructor. Not used.
     */
    private LinkFactory()
    {
        // Empty constructor
    }

    /**
     * Creates a new Link or gets an existing one from the the given NodeID.
     * 
     * @param nodeID
     *            NodeID of the Node to connect to.
     * @return A Link connected to the remote Node of given NodeID
     * @throws JCSPNetworkException
     *             Thrown if there is a problem connecting to the Node
     * @throws IllegalArgumentException
     *             Thrown if an attempt is made to connect to the local Node
     */
    public static Link getLink(NodeID nodeID)
        throws JCSPNetworkException, IllegalArgumentException
    {
        if (nodeID.equals(Node.getInstance().getNodeID()))
            throw new IllegalArgumentException("Attempted to connect to the local Node");

        // Log start of Link creation
        Node.log.log(LinkFactory.class, "Trying to get link to " + nodeID.toString());

        // First check if Link to NodeID exists. Request the Link from the Link Manager
        Link toReturn = LinkManager.getInstance().requestLink(nodeID);
        if (toReturn != null)
        {
            // A Link already exists to given NodeID. Log and return existing Link
            Node.log.log(LinkFactory.class, "Link to " + nodeID.toString()
                                            + " already exists.  Returning existing Link");
            return toReturn;
        }

        // An existing Link does not exist within the Link Manager. Log, and then create Link via the address
        Node.log.log(LinkFactory.class, "Link does not exist to " + nodeID.toString() + ".  Creating new Link");
        toReturn = nodeID.getNodeAddress().createLink();

        // Now attempt to connect the Link. If connect fails, then the opposite node already has a connection to us.
        // This may occur during connection if the opposite end registered its Link prior to us doing so. In such
        // a circumstance, the Link should eventually appear in the LinkManager.
        if (!toReturn.connect())
        {
            // Connect failed. Get NodeID of remote Node (this will have been set during the connect operation)
            NodeID remoteID = toReturn.getRemoteNodeID();

            // Log failed connect
            Node.log.log(LinkFactory.class, "Failed to connect to " + remoteID.toString());

            // Set the Link to return to null
            toReturn = null;

            // Loop until the Link is connected. This is a possible weakness. Although it is believed that the
            // opposite end will eventually connect, we may have a problem if this continually loops. For information,
            // log every attempt.
            while (toReturn == null)
            {
                try
                {
                    // Go to sleep for a bit, and give the Link a chance to register
                    Thread.sleep(100);

                    // Log start of attempt
                    Node.log.log(LinkFactory.class, "Attempting to retrieve Link to " + remoteID.toString());

                    // Retrieve Link from LinkManager
                    toReturn = LinkManager.getInstance().requestLink(remoteID);
                }
                catch (InterruptedException ie)
                {
                    // Ignore. Should never really happen
                }
            }
            // Return the Link retrieved from the LinkManager
            return toReturn;
        }

        // Connection succeeded. Register Link with the LinkManager
        toReturn.registerLink();

        // Now start the Link
        ProcessManager proc = new ProcessManager(toReturn);
        proc.setPriority(toReturn.priority);
        proc.start();

        // Return the Link
        return toReturn;
    }

    /**
     * Creates a new Link, or retrieves an existing one, from a NodeAddress
     * 
     * @param addr
     *            The NodeAddress of the Node to connect to
     * @return A new Link connected to the node at the given NodeAddress
     * @throws JCSPNetworkException
     *             Thrown if anything goes wrong during the connection
     */
    public static Link getLink(NodeAddress addr)
        throws JCSPNetworkException
    {
        // Log start of creation
        Node.log.log(LinkFactory.class, "Trying to get link to " + addr.toString());

        // Create Link from address
        Link toReturn = addr.createLink();

        // Now attempt to connect the Link. If connect fails, then the opposite node already has a connection to us.
        // This may occur during connection if the opposite end registered its Link prior to us doing so. In such
        // a circumstance, the Link should eventually appear in the LinkManager.
        if (!toReturn.connect())
        {
            // Connect failed. Get NodeID of remote Node (this will have been set during the connect operation)
            NodeID remoteID = toReturn.getRemoteNodeID();

            // Log failed connect
            Node.log.log(LinkFactory.class, "Failed to connect to " + remoteID.toString());

            // Set the Link to return to null
            toReturn = null;

            // Loop until the Link is connected. This is a possible weakness. Although it is believed that the
            // opposite end will eventually connect, we may have a problem if this continually loops. For information,
            // log every attempt.
            while (toReturn == null)
            {
                try
                {
                    // Go to sleep for a bit, and give the Link a chance to register
                    Thread.sleep(100);

                    // Log start of attempt
                    Node.log.log(LinkFactory.class, "Attempting to retrieve Link to " + remoteID.toString());

                    // Retrieve Link from LinkManager
                    toReturn = LinkManager.getInstance().requestLink(remoteID);
                }
                catch (InterruptedException ie)
                {
                    // Ignore. Should never really happen
                }
            }
            // Return the Link retrieved from the LinkManager
            return toReturn;
        }

        // Connection succeeded. Register Link with the LinkManager
        toReturn.registerLink();

        // Now start the Link
        new ProcessManager(toReturn).start();

        // Return the Link
        return toReturn;
    }
}
