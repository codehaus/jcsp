package org.jcsp.net2;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.ProcessManager;

/**
 * Abstract class defining the LinkServer.
 * 
 * @author Kevin Chalmers
 */
public abstract class LinkServer
    implements CSProcess
{
    /**
     * @param address
     * @throws IllegalArgumentException
     * @throws JCSPNetworkException
     */
    public static final void start(NodeAddress address)
        throws IllegalArgumentException, JCSPNetworkException
    {
        Node.log.log(LinkServer.class, "Attempting to start Link Server on " + address);
        LinkServer linkServer = address.createLinkServer();
        ProcessManager linkServProc = new ProcessManager(linkServer);
        linkServProc.setPriority(Link.LINK_PRIORITY);
        linkServProc.start();
        Node.log.log(LinkServer.class, "Link Server started on " + address);
    }

    /**
     * @param nodeID
     * @return The Link connected to the Node with the corresponding NodeID, or null if no such Node exists
     */
    protected final Link requestLink(NodeID nodeID)
    {
        return LinkManager.getInstance().requestLink(nodeID);
    }

    /**
     * @param link
     * @return True if the Link to the Node was successfully registered, false otherwise
     */
    protected final boolean registerLink(Link link)
    {
        return LinkManager.getInstance().registerLink(link);
    }
}
