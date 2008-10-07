package org.jcsp.net2;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

import org.jcsp.lang.AltingChannelInput;
import org.jcsp.net2.bns.BNS;
import org.jcsp.net2.cns.CNS;

/**
 * @author Kevin Chalmers
 */
public final class Node
{
    /**
     * 
     */
    private NodeID nodeID;

    /**
     * 
     */
    private boolean initialized = false;

    /**
     * 
     */
    private NodeKey nk;

    /**
     * 
     */
    private static Node instance = new Node();

    /**
     * 
     */
    public static Logger log = new Logger();

    /**
     * 
     */
    public static Logger err = new Logger();

    /**
     * @return The singleton instance of the Node
     */
    public static Node getInstance()
    {
        return instance;
    }

    /**
     * @return The NodeID of this Node
     */
    public NodeID getNodeID()
    {
        return this.nodeID;
    }

    /**
     * @param aNodeID
     */
    void setNodeID(NodeID aNodeID)
    {
        this.nodeID = aNodeID;
    }

    /**
     * 
     */
    private Node()
    {
        // Empty constructor
    }

    /**
     * @param addr
     * @return NodeKey for this Node
     * @throws JCSPNetworkException
     */
    public NodeKey init(NodeAddress addr)
        throws JCSPNetworkException
    {
        return this.init("", addr);
    }

    /**
     * @param name
     * @param addr
     * @return NodeKey for this Node
     * @throws JCSPNetworkException
     */
    public NodeKey init(String name, NodeAddress addr)
        throws JCSPNetworkException
    {
        Node.log.log(this.getClass(), "Node initialisation begun");
        if (this.initialized)
            throw new JCSPNetworkException("Node already initialised");
        this.initialized = true;
        LinkServer.start(addr);
        this.nodeID = new NodeID(name, addr);
        this.nk = new NodeKey();
        NodeAddress.installProtocol(addr.getProtocol(), addr.getProtocolID());
        Node.log.log(this.getClass(), "Node initialisation complete");
        return this.nk;
    }

    /**
     * @param factory
     * @return NodeKey for this Node
     * @throws JCSPNetworkException
     */
    public NodeKey init(NodeFactory factory)
        throws JCSPNetworkException
    {
        Node.log.log(this.getClass(), "Node initialisation begun");
        if (this.initialized)
            throw new JCSPNetworkException("Node already initialised");
        NodeAddress localAddr = factory.initNode(this);
        this.nodeID = new NodeID("", localAddr);
        this.initialized = true;
        this.nk = new NodeKey();
        Link toServer = LinkFactory.getLink(factory.cnsAddress);
        CNS.initialise(toServer.remoteID);
        BNS.initialise(toServer.remoteID);
        return this.nk;
    }

    /**
     * @return A channel to receive disconnect events on
     */
    public AltingChannelInput getLinkLostEventChannel()
    {
        return LinkManager.getInstance().getLinkLostEventChannel();
    }

    /**
     * @param stream
     */
    public void setLog(OutputStream stream)
    {
        log = new Logger(stream);
    }

    /**
     * @param stream
     */
    public void setErr(OutputStream stream)
    {
        err = new Logger(stream);
    }

    /**
     * @author Kevin Chalmers
     */
    public static class Logger
    {
        /**
         * 
         */
        private final PrintWriter logger;

        /**
         * 
         */
        Logger()
        {
            this.logger = null;
        }

        /**
         * @param stream
         */
        Logger(OutputStream stream)
        {
            this.logger = new PrintWriter(stream);
        }

        /**
         * @param clazz
         * @param message
         */
        public synchronized void log(Class clazz, String message)
        {
            if (this.logger == null)
                return;
            Date date = new Date(System.currentTimeMillis());
            try
            {
                this.logger.println("(" + date.toString() + ")-" + clazz.getName() + ":");
                this.logger.println("\t\"" + message + "\"");
                this.logger.flush();
            }
            catch (Exception e)
            {
                // Do nothing
            }
        }
    }
}
