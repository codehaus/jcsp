package org.jcsp.net2.bns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jcsp.lang.Alternative;
import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Guard;
import org.jcsp.net2.JCSPNetworkException;
import org.jcsp.net2.Link;
import org.jcsp.net2.LinkFactory;
import org.jcsp.net2.NetAltingChannelInput;
import org.jcsp.net2.NetBarrier;
import org.jcsp.net2.NetBarrierEnd;
import org.jcsp.net2.NetBarrierLocation;
import org.jcsp.net2.NetChannel;
import org.jcsp.net2.NetChannelOutput;
import org.jcsp.net2.Node;
import org.jcsp.net2.NodeAddress;
import org.jcsp.net2.NodeID;

/**
 * This is the main process for the Barrier Name Server. For a more in depth discussion of name servers, see CNS.
 * 
 * @see org.jcsp.net2.cns.CNS
 * @see BNSService
 * @see Node
 * @author Kevin Chalmers
 */
public class BNS
    implements CSProcess
{
    /**
     * The internal service. This is used by the factory methods.
     */
    private static BNSService service;

    /**
     * Flag used to denote if the connection to the BNS has been initialised
     */
    private static boolean initialised = false;

    /**
     * Singleton instance of the BNS. Only one may be created on a Node
     */
    private static final BNS instance = new BNS();

    /**
     * Map of registered barriers; name->location
     */
    private final HashMap registeredBarriers = new HashMap();

    /**
     * Map of barriers registered to a Node; NodeID-><list of barriers>
     */
    private final HashMap barrierRegister = new HashMap();

    /**
     * Map of currently waiting resolves; name->reply-location
     */
    private final HashMap waitingResolves = new HashMap();

    /**
     * Map of currently logged clients; NodeID->reply-channel
     */
    private final HashMap loggedClients = new HashMap();

    /**
     * A channel used to receive incoming link lost notifications
     */
    private final AltingChannelInput lostLink = Node.getInstance().getLinkLostEventChannel();

    /**
     * Private empty constructor
     */
    private BNS()
    {
        // Empty constructor
    }

    /**
     * Gets the singleton instance of the BNS
     * 
     * @return The singleton instance of the BNS
     */
    public static BNS getInstance()
    {
        return instance;
    }

    /**
     * Initialises the connection to the BNS
     * 
     * @param bnsNode
     *            The NodeID of the BNS Node
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static void initialise(NodeID bnsNode)
        throws JCSPNetworkException
    {
        // First check that we are not already initialised
        if (BNS.initialised)
            throw new JCSPNetworkException("The BNS is already initialised");

        // We are initialised. Attempt to do so.
        // First, we need to create the BNSService
        BNS.service = new BNSService(bnsNode);

        // Now set initialised to true
        BNS.initialised = true;

        // We are now connected
    }

    /**
     * Initialises the connection to the BNS
     * 
     * @param bnsNode
     *            The NodeAddress of the BNS Node
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static void initialise(NodeAddress bnsNode)
        throws JCSPNetworkException
    {
        // First check that we are not already initialised
        if (BNS.initialised)
            throw new JCSPNetworkException("The BNS is already initialised");

        // We are initialised. Attempt to do so.
        // First, we need to connect to the BNS Node
        Link link = LinkFactory.getLink(bnsNode);

        // Now create the BNSService
        BNS.service = new BNSService(link.getRemoteNodeID());

        // Now set initialised to true
        BNS.initialised = true;

        // We are now connected
    }

    /**
     * The run method for the BNS process
     */
    public void run()
    {
        // Create the channel to receive incoming messages on. The index is 2.
        NetAltingChannelInput in = NetChannel.numberedNet2One(2, new BNSNetworkMessageFilter.FilterRX());

        // We now wish to alternate upon this channel and the link lost channel
        Alternative alt = new Alternative(new Guard[] { this.lostLink, in });

        // Loop forever
        while (true)
        {
            // Select next available Guard. Give priority to link failure
            switch (alt.priSelect())
            {
                // We have lost the connection to a Node
                case 0:
                    // Read in the NodeID of the lost Node
                    NodeID lostNode = (NodeID)this.lostLink.read();

                    // Log loss of connection
                    Node.log.log(this.getClass(), "Lost Link to: " + lostNode.toString());

                    // Remove the logged client
                    this.loggedClients.remove(lostNode);

                    // Next get the ArrayList of any barriers registered by that Node
                    ArrayList registeredBars = (ArrayList)this.barrierRegister.get(lostNode);

                    // If this ArrayList is null, we have no registrations
                    if (registeredBars != null)
                    {
                        // There are registered barriers

                        // Remove the list from the Hashmap
                        this.barrierRegister.remove(lostNode);

                        // Now remove all the barriers registered by the Node
                        for (Iterator iter = registeredBars.iterator(); iter.hasNext();)
                        {
                            String toRemove = (String)iter.next();
                            this.registeredBarriers.remove(toRemove);
                            Node.log.log(this.getClass(), toRemove + " deregistered");
                        }
                    }
                    break;

                // We have received a new incoming message
                case 1:
                {
                    // Read in the message
                    BNSMessage message = (BNSMessage)in.read();

                    // Now behave based on the type of the message
                    switch (message.type)
                    {
                        // We have received a logon message
                        case BNSMessageProtocol.LOGON_MESSAGE:
                        {
                            // Log the logon attempt
                            Node.log.log(this.getClass(), "Logon received from: "
                                                          + message.serviceLocation.getNodeID().toString());

                            // try-catch loop. We don't want the BNS to fail
                            try
                            {
                                // Check if the node is already logged on
                                NetChannelOutput out = (NetChannelOutput)this.loggedClients.get(message.serviceLocation
                                        .getNodeID());

                                // If out is null, no previous logon received
                                if (out != null)
                                {
                                    // This Node is already logged on. Send fail message
                                    // Log failed attempt
                                    Node.err.log(this.getClass(), message.serviceLocation.getNodeID().toString()
                                                                  + " already logged on.  Rejecting");

                                    // Create reply channel to the Node
                                    NetChannelOutput toNewRegister = NetChannel.one2net(message.serviceLocation,
                                            new BNSNetworkMessageFilter.FilterTX());

                                    // Create the reply message
                                    BNSMessage reply = new BNSMessage();
                                    reply.type = BNSMessageProtocol.LOGON_REPLY_MESSAGE;
                                    reply.success = false;

                                    // Asynchronously write to Node. We don't want the BNS to block
                                    toNewRegister.asyncWrite(reply);
                                    // Destroy the temporary channel
                                    toNewRegister.destroy();
                                }
                                else
                                {
                                    // Node hasn't previously registered
                                    // Log registration
                                    Node.log.log(this.getClass(), message.serviceLocation.getNodeID().toString()
                                                                  + " successfully logged on");

                                    // Create the reply channel
                                    NetChannelOutput toNewRegister = NetChannel.one2net(message.serviceLocation,
                                            new BNSNetworkMessageFilter.FilterTX());

                                    // Add the Node and reply channel to the logged clients map
                                    this.loggedClients.put(message.serviceLocation.getNodeID(), toNewRegister);

                                    // Create a reply message
                                    BNSMessage reply = new BNSMessage();
                                    reply.type = BNSMessageProtocol.LOGON_REPLY_MESSAGE;
                                    reply.success = true;

                                    // Write reply asynchronously to the logging on Node
                                    toNewRegister.asyncWrite(reply);
                                }
                            }
                            catch (JCSPNetworkException jne)
                            {
                                // Catch any JCSPNetworkException. We don't let the BNS go down
                            }
                            break;
                        }

                            // A Node is attempting to register a Barrier
                        case BNSMessageProtocol.REGISTER_REQUEST:
                        {
                            // Log registration attempt
                            Node.log.log(this.getClass(), "Registeration for " + message.name + " received");

                            // Catch any JCSPNetworkException
                            try
                            {
                                // Get the reply channel from our logged clients map
                                NetChannelOutput out = (NetChannelOutput)this.loggedClients.get(message.serviceLocation
                                        .getNodeID());

                                // Check if the Node has logged on with us
                                if (out == null)
                                {
                                    // The Node is not logged on. Send failure message
                                    Node.err.log(this.getClass(), "Registration failed. "
                                                                  + message.serviceLocation.getNodeID()
                                                                  + " not logged on");

                                    // Create the channel to reply to
                                    out = NetChannel.one2net(message.serviceLocation,
                                            new BNSNetworkMessageFilter.FilterTX());

                                    // Create the reply message
                                    BNSMessage reply = new BNSMessage();
                                    reply.type = BNSMessageProtocol.REGISTER_REPLY;
                                    reply.success = false;

                                    // Write message asynchronously to the Node
                                    out.asyncWrite(reply);

                                    // Destroy the temporary channel
                                    out.destroy();
                                }

                                // The Node is registered. Now check if the name is
                                else if (this.registeredBarriers.containsKey(message.name))
                                {
                                    // The name is already registered. Inform the register
                                    // Log the failed registration
                                    Node.err.log(this.getClass(), "Registration failed. " + message.name
                                                                  + " already registered");

                                    // Create reply message
                                    BNSMessage reply = new BNSMessage();
                                    reply.type = BNSMessageProtocol.RESOLVE_REPLY;
                                    reply.success = false;

                                    // Write the reply asynchronously. Do not block the BNS
                                    out.asyncWrite(reply);
                                }
                                else
                                {
                                    // The name is not registered
                                    // Log successful registration
                                    Node.log.log(this.getClass(), "Registration of " + message.name + " succeeded.");

                                    // First check if any client end is waiting for this name
                                    ArrayList pending = (ArrayList)this.waitingResolves.get(message.name);

                                    if (pending != null)
                                    {
                                        // We have waiting resolves. Complete
                                        for (Iterator iter = pending.iterator(); iter.hasNext();)
                                        {
                                            NetChannelOutput toPending = null;

                                            // We now catch internally any JCSPNetworkException
                                            try
                                            {
                                                // Get the next waiting message
                                                BNSMessage msg = (BNSMessage)iter.next();

                                                // Log resolve completion
                                                Node.log.log(this.getClass(), "Queued resolve of " + message.name
                                                                              + " by "
                                                                              + msg.serviceLocation.getNodeID()
                                                                              + " completed");

                                                // Create channel to the resolver
                                                toPending = NetChannel.one2net(msg.serviceLocation,
                                                        new BNSNetworkMessageFilter.FilterTX());

                                                // Create the reply message
                                                BNSMessage reply = new BNSMessage();
                                                reply.type = BNSMessageProtocol.RESOLVE_REPLY;
                                                reply.location = message.location;
                                                reply.success = true;

                                                // Write the reply asynchronously to the waiting resolver
                                                toPending.asyncWrite(reply);
                                            }
                                            catch (JCSPNetworkException jne)
                                            {
                                                // Something went wrong as we tried to send the resolution complete
                                                // message
                                                // Do nothing
                                            }
                                            finally
                                            {
                                                // Check if we need to destroy the temporary channel
                                                if (toPending != null)
                                                    toPending.destroy();
                                            }
                                        }

                                        // Remove the name from the pending resolves
                                        this.waitingResolves.remove(message.name);
                                    }

                                    // We have completed any pending resolves. Now register the barrier
                                    this.registeredBarriers.put(message.name, message.location);

                                    // Now add the registered barrier to the barriers registered by this Node
                                    ArrayList registered = (ArrayList)this.barrierRegister.get(message.serviceLocation
                                            .getNodeID());

                                    // If the ArrayList is null, we have no previous registrations
                                    if (registered == null)
                                    {
                                        // Create a new ArrayList to store the registered names
                                        registered = new ArrayList();
                                        // Add it to the barrier register
                                        this.barrierRegister.put(message.location.getNodeID(), registered);
                                    }

                                    // Add the name to the ArrayList
                                    registered.add(message.name);

                                    // Log the successful registration
                                    Node.log.log(this.getClass(), message.name + " registered to " + message.location);

                                    // Create the reply message
                                    BNSMessage reply = new BNSMessage();
                                    reply.type = BNSMessageProtocol.REGISTER_REPLY;
                                    reply.success = true;

                                    // Write the reply asynchronously to the Node
                                    out.asyncWrite(reply);
                                }
                            }
                            catch (JCSPNetworkException jne)
                            {
                                // Do nothing. Do not let the BNS break
                            }
                            break;
                        }

                            // We have received a resolve request
                        case BNSMessageProtocol.RESOLVE_REQUEST:
                        {
                            // Log resolve request
                            Node.log.log(this.getClass(), "Resolve request for " + message.name + " received");

                            // Catch any JCSPNetworkException
                            try
                            {
                                // Check if the resolving Node is logged on
                                NetChannelOutput out = (NetChannelOutput)this.loggedClients.get(message.serviceLocation
                                        .getNodeID());

                                // If the channel is null, then the Node has yet to log on with us
                                if (out == null)
                                {
                                    // Node is not logged on
                                    // Log failed resolution
                                    Node.err.log(this.getClass(), "Resolve failed. "
                                                                  + message.serviceLocation.getNodeID()
                                                                  + " not logged on");

                                    // Create connection to the receiver
                                    out = NetChannel.one2net(message.serviceLocation,
                                            new BNSNetworkMessageFilter.FilterTX());

                                    // Create the reply message
                                    BNSMessage reply = new BNSMessage();
                                    reply.type = BNSMessageProtocol.RESOLVE_REPLY;
                                    reply.success = false;

                                    // Write message asynchronously to the Node
                                    out.asyncWrite(reply);

                                    // Destroy the temporary channel
                                    out.destroy();
                                }
                                else
                                {
                                    // Node is logged on. Now check if the name is already registered
                                    NetBarrierLocation loc = (NetBarrierLocation)this.registeredBarriers
                                            .get(message.name);

                                    // If the location is null, then the name has not yet been registered
                                    if (loc == null)
                                    {
                                        // The name is not registered. We need to queue the resolve until it is
                                        // Log the queueing of the resolve
                                        Node.log.log(this.getClass(), message.name
                                                                      + " not registered. Queueing resolve by "
                                                                      + message.serviceLocation.getNodeID().toString());

                                        // Check if any other resolvers are waiting for the channel
                                        ArrayList pending = (ArrayList)this.waitingResolves.get(message.name);

                                        // If the ArrayList is null, no one else is waiting
                                        if (pending == null)
                                        {
                                            // No one else is waiting. Create a new list and add it to the waiting
                                            // resolves
                                            pending = new ArrayList();
                                            this.waitingResolves.put(message.name, pending);
                                        }

                                        // Add this resolve message to the list of waiting resolvers
                                        pending.add(message);
                                    }
                                    else
                                    {
                                        // The location is not null. Send it to the resolver
                                        Node.log.log(this.getClass(), "Resolve request completed. " + message.name
                                                                      + " location being sent to "
                                                                      + message.serviceLocation.getNodeID());

                                        // Create channel to the resolver
                                        NetChannelOutput toPending = NetChannel.one2net(message.serviceLocation,
                                                new BNSNetworkMessageFilter.FilterTX());

                                        // Create the reply message
                                        BNSMessage reply = new BNSMessage();
                                        reply.type = BNSMessageProtocol.RESOLVE_REPLY;
                                        reply.location = loc;
                                        reply.success = true;

                                        // Write the reply to the resolver asynchronously
                                        toPending.asyncWrite(reply);

                                        // Destroy the temporary channel
                                        toPending.destroy();
                                    }
                                }
                            }
                            catch (JCSPNetworkException jne)
                            {
                                // Something went wrong during the resolution. Ignore
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * Creates a new server end of a NetBarrier with the given name
     * 
     * @param name
     *            The name to register with the BNS
     * @param localEnrolled
     *            The number of locally enrolled processes
     * @param netEnrolled
     *            The number of net enrolled processes to expect
     * @return A new NetBarrier server end with the number of enrolled processes
     * @throws IllegalArgumentException
     *             Thrown if the parameters are outside the defined ranges
     * @throws IllegalStateException
     *             Thrown if the BNS connection has not been initialised
     */
    public static NetBarrier netBarrier(String name, int localEnrolled, int netEnrolled)
        throws IllegalArgumentException, IllegalStateException
    {
        // Check if the BNS connection is initialised
        if (!BNS.initialised)
            throw new IllegalStateException("The connection to the BNS has not been initialised");

        // Create a new NetBarrier
        NetBarrier toReturn = NetBarrierEnd.netBarrier(localEnrolled, netEnrolled);

        // Attempt to register
        if (BNS.service.register(name, toReturn))
        {
            return toReturn;
        }

        // Registration failed. Destroy and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the BNS");
    }

    /**
     * Creates a new server end of a NetBarrier with a given index and name
     * 
     * @param name
     *            Name to register with the BNS
     * @param index
     *            The index to create the NetBarrier with
     * @param localEnrolled
     *            The number of locally enrolled processes
     * @param netEnrolled
     *            The number of remote enrollments to wait for
     * @return A new NetBarrier
     * @throws IllegalArgumentException
     *             Thrown if the parameters are outside the defined ranges
     * @throws IllegalStateException
     *             Thrown if the connection to the BNS has not been initialised
     */
    public static NetBarrier numberedNetBarrier(String name, int index, int localEnrolled, int netEnrolled)
        throws IllegalArgumentException, IllegalStateException
    {
        // Check if the BNS connection is initialised
        if (!BNS.initialised)
            throw new IllegalStateException("The connection to the BNS has not been initialised");

        // Create a new NetBarrier
        NetBarrier toReturn = NetBarrierEnd.numberedNetBarrier(index, localEnrolled, netEnrolled);

        // Attempt to register
        if (BNS.service.register(name, toReturn))
        {
            return toReturn;
        }

        // Registration failed. Destroy and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the BNS");
    }

    /**
     * Creates a new client end of a NetBarrier
     * 
     * @param name
     *            The name to resolve with the BNS
     * @param enrolled
     *            The number of locally enrolled processes
     * @return A new NetBarrier client end with the number of enrolled processes
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     * @throws IllegalArgumentException
     *             Thrown if the number of of local enrolled is outside the defined range
     * @throws IllegalStateException
     *             Thrown if the connection to the BNS has not been initialised
     */
    public static NetBarrier netBarrier(String name, int enrolled)
        throws IllegalArgumentException, IllegalStateException, JCSPNetworkException
    {
        // Check if the BNS connection is initialised
        if (!BNS.initialised)
            throw new IllegalStateException("The connection to the BNS has not been initialised");

        // Resolve the location of the barrier
        NetBarrierLocation loc = BNS.service.resolve(name);

        // Return a new NetBarrier
        return NetBarrierEnd.netBarrier(loc, enrolled);
    }

}
