package org.jcsp.net2;

/**
 * Used to initialise a Node by connecting to the CNS. This approach is now deprecated. To initialise a Node now:
 * <p>
 * <code>
 * NodeAddress localAddress = ...;<br>
 * NodeAddress nodeServerAddr = ...;<br>
 * Node.getInstance().init(localAddress);<br>
 * CNS.init(nodeServerAddr);<br>
 * BNS.init(nodeServerAddr);<br>
 * </code>
 * </p>
 * 
 * @see Node
 * @author Kevin Chalmers
 */
public abstract class NodeFactory
{
    /**
     * The NodeAddress where the CNS / BNS is located
     */
    protected NodeAddress cnsAddress;

    /**
     * Initialises a Node
     * 
     * @param node
     *            The Node to initialise
     * @return A new NodeAddress for the Node
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong during the initialisation
     */
    protected abstract NodeAddress initNode(Node node)
        throws JCSPNetworkException;
}
