package org.jcsp.net2.cns;

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
import org.jcsp.net2.NetChannel;
import org.jcsp.net2.NetChannelLocation;
import org.jcsp.net2.NetChannelOutput;
import org.jcsp.net2.NetSharedChannelInput;
import org.jcsp.net2.NetSharedChannelOutput;
import org.jcsp.net2.NetworkMessageFilter;
import org.jcsp.net2.Node;
import org.jcsp.net2.NodeAddress;
import org.jcsp.net2.NodeID;

/**
 * <p>
 * This class is the Channel Name Server's main server process class.
 * </p>
 * <p>
 * This class should only be instantiated at Nodes wishing to run a server process. Although this class does not need to
 * be used by clients wishing to interact with a server, it does provide some convenient static methods for client code
 * to use. There are static versions of the methods provided in <code>CNSService</code> and there are also static
 * factory methods for constructing CNS registered channel objects.
 * </p>
 * <p>
 * <h3>Server Installation</h3>
 * </p>
 * <p>
 * Channel Name Servers may be run either on a dedicated Node or else on the same Node as one of the user Nodes. The
 * former approach is recommended for most situations but for smaller scale use, the latter approach may suffice.
 * </p>
 * <p>
 * The following example initialises a Node and installs a Channel Name Server. It then proceeds to install a CNS client
 * service and creates and resolves a channel. The example does not proceed to do anything else but could be used as the
 * framework for an application wishing to host its own Channel Name Server.
 * </p>
 * <p>
 * <code>
 * import org.jcsp.lang.*; <br>
 * import org.jcsp.net2.*; <br>
 * import org.jcsp.net2.cns.*; <br>
 * import org.jcsp.net2.tcpip.*; <br>
 * import java.io.IOException; <br>
 * public class CNSInSameJVM implements CSProcess { <br>
 * //main method for running example <br>
 * public static void main(String[] args) { <br>
 *  CNSInSameJVM proc = new CNSInSameJVM(); <br>
 *  proc.run(); <br>
 * } <br>
 * public void run() { <br>
 *  NodeKey key = null; <br>
 *  NodeID localNodeID = null; <br>
 *  try { <br>
 *      //Initialize a Node that does not have a CNS client <br>
 *      key = Node.getInstance().init(new TCPIPNodeAddress(7890)); <br>
 *      new ProcessManager(CNS.getInstance()).start(); <br>
 *      //Dedicated server code could stop here <br>
 *      //Initialise the CNS client <br>
 *      //use the local NodeID to connect to the CNS <br>
 *      localNodeID = Node.getInstance().getNodeID(); <br>
 *      CNS.init(localNodeID); <br>
 *      // creating Channel named &quot;in&quot; <br>
 *      NetChannelInput in = CNS.net2one(&quot;in&quot;); <br>
 *      //resolve the channel <br>
 *      NetChannelOutput out = CNS.one2net(&quot;in&quot;); <br>
 *      //could now use these channels for something!! <br>
 *      //but this is only a test so will terminate <br>
 *  } catch (NodeInitFailedException e) { <br>
 *      e.printStackTrace(); <br>
 *  } catch (IOException e) { <br>
 *      e.printStackTrace(); <br>
 *  } <br>
 *  Node.log.log(this, &quot;Done.&quot;); } } <br>
 * </code>
 * </p>
 * <p>
 * <h3>Channel Factory Methods</h3>
 * </p>
 * <p>
 * In order to construct a <code>ChannelInput</code> object which can be resolved by other users of a channel name
 * server, a client simply needs to to do this:
 * </p>
 * <code>
 * NetChannelInput in = CNS.net2one(&quot;Fred&quot;);
 * </code>
 * <p>
 * Another process using the same channel name server can create a <code>ChannelOutput</code> that will send objects
 * to this channel by do this:
 * </p>
 * <code>
 * NetChannelOutput out = CNS.one2net(&quot;Fred&quot;);
 * </code>
 * <p>
 * When these factory methods are called, various resources are used within the JCSP infrastructure. A channel name will
 * be registered and held in the channel name server. These resources are taken for the duration of the JCSP Node's
 * runtime.
 * </p>
 * <p>
 * This is an example "Hello World" program which contains two inner classes with main methods, each of which can be run
 * in separate JVMs.
 * </p>
 * 
 * <pre>
 * import org.jcsp.lang.*;
 * import org.jcsp.net2.*;
 * import org.jcsp.net2.cns.*;
 * 
 * public class TestCNS
 * {
 *     public static class Rx
 *     {
 *         public static void main(String[] args)
 *         {
 *             try
 *             {
 *                 Node.getInstance().init(new TCPIPNodeAddress(7890));
 *                 NetChannelInput in = CNS.net2one(&quot;rx.in&quot;);
 *                 System.out.println(in.read());
 *             }
 *             catch (Exception e)
 *             {
 *                 e.printStackTrace();
 *             }
 *         }
 *     }
 * 
 *     public static class Tx
 *     {
 *         public static void main(String[] args)
 *         {
 *             try
 *             {
 *                 Node.getInstance().init(new TCPIPNodeAddress(7890));
 *                 NetChannelOutput out = CNS.one2net(&quot;rx.in&quot;);
 *                 out.write(&quot;Hello World&quot;);
 *             }
 *             catch (Exception e)
 *             {
 *                 e.printStackTrace();
 *             }
 *         }
 *     }
 * }
 * </pre>
 * 
 * <p>
 * This code can be compiled and then the following run at two command prompts:
 * </p>
 * <p>
 * java TestCNS$Rx
 * </p>
 * <p>
 * java TestCNS$Tx
 * </p>
 * <p>
 * The programs will connect to a default channel name server. The Rx program will create a <code>NetChannelInput</code>
 * and wait for a message on the channel. Once it has received the message, it prints it, then terminates. The Tx
 * program creates a <code>NetChannelOutput</code> that will send to the Rx program's input channel. It sends a "Hello
 * World" message. Once this has been accepted by the Rx process, it terminates.
 * </p>
 * </p>
 * 
 * @see CNSService
 * @see Node
 * @author Quickstone Technologies Limited
 * @author Kevin Chalmers (updates for new architecture)
 */
public class CNS
    implements CSProcess
{
    /**
     * The internal service. This is used by the factory methods
     */
    private static CNSService service;

    /**
     * Flag used to denote whether the CNS has been initialised
     */
    private static boolean initialised = false;

    /**
     * Singleton instance of a CNS. Only one may be created on a Node
     */
    private static final CNS instance = new CNS();

    /**
     * The map of registered channels, name->location
     */
    private final HashMap registeredChannels = new HashMap();

    /**
     * The map of channels registered to a Node; NodeID-><list of channels>
     */
    private final HashMap channelRegister = new HashMap();

    /**
     * The map of currently waiting resolves; name->reply-location
     */
    private final HashMap waitingResolves = new HashMap();

    /**
     * The map of currently logged clients; NodeID->reply-channel
     */
    private final HashMap loggedClients = new HashMap();

    /**
     * A channel used to receive incoming link lost notifications
     */
    private final AltingChannelInput lostLink = Node.getInstance().getLinkLostEventChannel();

    /**
     * Private empty constructor
     */
    private CNS()
    {
        // Empty constructor
    }

    /**
     * Gets the singleton instance of the CNS
     * 
     * @return The singleton instance of the CNS
     */
    public static CNS getInstance()
    {
        return instance;
    }

    /**
     * Initialises the factory methods to allow creation of channels with the CNS
     * 
     * @param cnsNode
     *            The Node that the CNS is located on
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static void initialise(NodeID cnsNode)
        throws JCSPNetworkException
    {
        // First check that we are not already initialised
        if (CNS.initialised)
            throw new JCSPNetworkException("The CNS is already initialised");

        // We are not initialised. Attempt to do so.
        // First, we need to create the CNSService
        CNS.service = new CNSService(cnsNode);

        // Now set initialised to true
        CNS.initialised = true;

        // We are now connected
    }

    /**
     * Initialises the factory methods to allow creation of channels with the CNS
     * 
     * @param cnsNode
     *            The Node that the CNS is located on
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static void initialise(NodeAddress cnsNode)
        throws JCSPNetworkException
    {
        // First check that we are not already initialised
        if (CNS.initialised)
            throw new JCSPNetworkException("The CNS is already initialised");

        // We are not initialised. Attempt to do so.
        // First, we need to get the NodeID
        Link link = LinkFactory.getLink(cnsNode);

        // Now create the CNSService
        CNS.service = new CNSService(link.getRemoteNodeID());

        // Now set initialised to true
        CNS.initialised = true;

        // We are now connected
    }

    /**
     * The run method for the CNS process
     */
    public void run()
    {
        // Create the channel to receive incoming messages on. The index is 1.
        NetAltingChannelInput in = NetChannel.numberedNet2One(1, new CNSNetworkMessageFilter.FilterRX());

        // Now we wish to alternate upon this channel, and the link lost channel
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

                    // First remove the logged client
                    this.loggedClients.remove(lostNode);

                    // Next get the ArrayList of any channels registered by that Node
                    ArrayList registeredChans = (ArrayList)this.channelRegister.get(lostNode);

                    // If this ArrayList is null, we have no registrations.
                    if (registeredChans != null)
                    {
                        // There are registered channels

                        // Remove the list from the HashMap
                        this.channelRegister.remove(lostNode);

                        // Now remove all the channels registered by that Node
                        for (Iterator iter = registeredChans.iterator(); iter.hasNext();)
                        {
                            String toRemove = (String)iter.next();
                            this.registeredChannels.remove(toRemove);
                            Node.log.log(this.getClass(), toRemove + " deregistered");
                        }
                    }
                    break;

                // We have received a new incoming message
                case 1:
                {
                    // Read in the message
                    CNSMessage message = (CNSMessage)in.read();

                    // Now behave based on the type of the message
                    switch (message.type)
                    {
                        // We have received a LOGON_MESSAGE
                        case CNSMessageProtocol.LOGON_MESSAGE:
                        {
                            // Log the logon attempt
                            Node.log.log(this.getClass(), "Logon received from: "
                                                          + message.location1.getNodeID().toString());

                            // try-catch loop. We don't want the CNS to fail
                            try
                            {
                                // Check if the Node is already logged on
                                NetChannelOutput out = (NetChannelOutput)this.loggedClients.get(message.location1
                                        .getNodeID());

                                // If out is null, no previous log on received
                                if (out != null)
                                {
                                    // This Node is already logged on. Send fail message
                                    // Log failed attempt
                                    Node.err.log(this.getClass(), message.location1.getNodeID().toString()
                                                                  + " already logged on.  Rejecting");

                                    // Create reply channel to the Node.
                                    NetChannelOutput toNewRegister = NetChannel.one2net(message.location1,
                                            new CNSNetworkMessageFilter.FilterTX());

                                    // Create the reply message
                                    CNSMessage reply = new CNSMessage();
                                    reply.type = CNSMessageProtocol.LOGON_REPLY_MESSAGE;
                                    reply.success = false;

                                    // Asynchronously write to Node. We don't want the CNS to block
                                    toNewRegister.asyncWrite(reply);
                                    // Destroy the temporary channel
                                    toNewRegister.destroy();
                                }
                                else
                                {
                                    // Node hasn't previously registered
                                    // Log registration
                                    Node.log.log(this.getClass(), message.location1.getNodeID().toString()
                                                                  + " successfully logged on");

                                    // Create the reply channel
                                    NetChannelOutput toNewRegister = NetChannel.one2net(message.location1,
                                            new CNSNetworkMessageFilter.FilterTX());

                                    // Add the Node and the reply channel to the logged clients table
                                    this.loggedClients.put(message.location1.getNodeID(), toNewRegister);

                                    // Create reply message
                                    CNSMessage reply = new CNSMessage();
                                    reply.type = CNSMessageProtocol.LOGON_REPLY_MESSAGE;
                                    reply.success = true;

                                    // Write reply to the logging on Node asynchronously
                                    toNewRegister.asyncWrite(reply);
                                }
                            }
                            catch (JCSPNetworkException jne)
                            {
                                // We do nothing. We have caught this to ensure nothing goes wrong during the I/O
                            }
                            break;
                        }

                            // A Node is attempting to register a channel
                        case CNSMessageProtocol.REGISTER_REQUEST:
                        {
                            // Log registration
                            Node.log.log(this.getClass(), "Registration for " + message.name + " received");

                            // Catch any JCSPNetworkException
                            try
                            {
                                // Get the reply channel from our logged clients map
                                NetChannelOutput out = (NetChannelOutput)this.loggedClients.get(message.location1
                                        .getNodeID());

                                // Check if the Node has logged on with us
                                if (out == null)
                                {
                                    // The Node is not logged on. Send failure message
                                    Node.err.log(this.getClass(), "Registration failed. "
                                                                  + message.location1.getNodeID() + " not logged on");

                                    // Create the channel to reply to
                                    out = NetChannel.one2net(message.location1, new CNSNetworkMessageFilter.FilterTX());

                                    // Create the reply message
                                    CNSMessage reply = new CNSMessage();
                                    reply.type = CNSMessageProtocol.REGISTER_REPLY;
                                    reply.success = false;

                                    // Write the reply asynchronously. Do not block the CNS
                                    out.asyncWrite(reply);

                                    // Destroy the temporary channel
                                    out.destroy();
                                }

                                // The Node is registered, now check if the name is
                                else if (this.registeredChannels.containsKey(message.name))
                                {
                                    // The name is already registered. Inform the register
                                    // Log the failed registration
                                    Node.err.log(this.getClass(), "Registration failed. " + message.name
                                                                  + " already registered");

                                    // Create reply message
                                    CNSMessage reply = new CNSMessage();
                                    reply.type = CNSMessageProtocol.REGISTER_REPLY;
                                    reply.success = false;

                                    // Write the reply asynchronously. Do not block the CNS
                                    out.asyncWrite(reply);
                                }
                                else
                                {
                                    // Name is not already registered.
                                    // Log successful registration
                                    Node.log.log(this.getClass(), "Registration of " + message.name + " succeded");

                                    // Now check if any client end is waiting for this name
                                    ArrayList pending = (ArrayList)this.waitingResolves.get(message.name);

                                    if (pending != null)
                                    {
                                        // We have waiting resolves. Complete
                                        for (Iterator iter = pending.iterator(); iter.hasNext();)
                                        {
                                            NetChannelOutput toPending = null;

                                            // We now catch internally any JCSPNetworkExceptions
                                            try
                                            {
                                                // Get the next waiting message
                                                CNSMessage msg = (CNSMessage)iter.next();

                                                // Log resolve completion
                                                Node.log.log(this.getClass(), "Queued resolve of " + message.name
                                                                              + " by " + msg.location1.getNodeID()
                                                                              + " completed");

                                                // Create the channel to the resolver
                                                toPending = NetChannel.one2net(msg.location1,
                                                        new CNSNetworkMessageFilter.FilterTX());

                                                // Create the reply message
                                                CNSMessage reply = new CNSMessage();
                                                reply.type = CNSMessageProtocol.RESOLVE_REPLY;
                                                reply.location1 = message.location2;
                                                reply.success = true;

                                                // Write the reply asynchronously to the waiting resolver
                                                toPending.asyncWrite(reply);
                                            }
                                            catch (JCSPNetworkException jne)
                                            {
                                                // Something went wrong as we tried to send the resolution completion.
                                                // Do nothing
                                            }
                                            finally
                                            {
                                                // Destroy the temporary channel if necessary
                                                if (toPending != null)
                                                    toPending.destroy();
                                            }
                                        }

                                        // Remove the name from the pending resolves
                                        this.waitingResolves.remove(message.name);
                                    }

                                    // We have completed any pending resolves on this channel. Now we register the
                                    // channel
                                    this.registeredChannels.put(message.name, message.location2);

                                    // Now we add the registered channel to the channels registered by this Node
                                    ArrayList registered = (ArrayList)this.channelRegister.get(message.location1
                                            .getNodeID());

                                    // If the ArrayList is null, we have no previous registrations
                                    if (registered == null)
                                    {
                                        // Create a new ArrayList to store the registered names with
                                        registered = new ArrayList();
                                        // Add it to the channel register
                                        this.channelRegister.put(message.location1.getNodeID(), registered);
                                    }

                                    // Add the name to the ArrayList
                                    registered.add(message.name);

                                    // Log the successful registration
                                    Node.log.log(this.getClass(), message.name + " registered to " + message.location2);

                                    // Create the reply message
                                    CNSMessage reply = new CNSMessage();
                                    reply.type = CNSMessageProtocol.REGISTER_REPLY;
                                    reply.success = true;

                                    // Write it asynchronously to the registering Node
                                    out.asyncWrite(reply);
                                }
                            }
                            catch (JCSPNetworkException jne)
                            {
                                // Something went wrong during the I/O operations. Ignore
                            }
                        }
                            break;

                        // We have received a resolve request
                        case CNSMessageProtocol.RESOLVE_REQUEST:
                        {
                            // Log resolve request
                            Node.log.log(this.getClass(), "Resolve request for " + message.name + " received");

                            // Catch any JCSP Network Exception
                            try
                            {
                                // Check if the resolving Node is logged on
                                NetChannelOutput out = (NetChannelOutput)this.loggedClients.get(message.location1
                                        .getNodeID());

                                // If the channel is null, then the Node has yet to log on with us
                                if (out == null)
                                {
                                    // Node is not logged on
                                    // Log failed resolution
                                    Node.err.log(this.getClass(), "Resolve failed. " + message.location1.getNodeID()
                                                                  + " not logged on");

                                    // Create connection to the resolver
                                    out = NetChannel.one2net(message.location1, new CNSNetworkMessageFilter.FilterTX());

                                    // Create the reply message
                                    CNSMessage reply = new CNSMessage();
                                    reply.type = CNSMessageProtocol.RESOLVE_REPLY;
                                    reply.success = false;

                                    // Write message asynchronously to the Node
                                    out.asyncWrite(reply);

                                    // Destroy temporary channel
                                    out.destroy();
                                }
                                else
                                {
                                    // Node is logged on. Now we check if the name is already registered
                                    NetChannelLocation loc = (NetChannelLocation)this.registeredChannels
                                            .get(message.name);

                                    // If the location is null, then the name has yet to be registered.
                                    if (loc == null)
                                    {
                                        // The name is not registered. We need to queue the resolve until it does
                                        // Log the queueing of the resolve
                                        Node.log.log(this.getClass(), message.name
                                                                      + " not registered. Queueing resolve by "
                                                                      + message.location1.getNodeID().toString());

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
                                        // Log successful resolution
                                        Node.log.log(this.getClass(), "Resolve request completed. " + message.name
                                                                      + " location being sent to "
                                                                      + message.location1.getNodeID());

                                        // Create channel to the resolver
                                        NetChannelOutput toPending = NetChannel.one2net(message.location1,
                                                new CNSNetworkMessageFilter.FilterTX());

                                        // Create the reply message
                                        CNSMessage reply = new CNSMessage();
                                        reply.type = CNSMessageProtocol.RESOLVE_REPLY;
                                        reply.location1 = loc;
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
                                // Something went wrong during the I/O. Ignore. Do not bring down the CNS
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a new NetAltingChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @deprecated Use net2one instead
     * @return A new NetAltingChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static NetAltingChannelInput createNet2One(String name)
        throws IllegalStateException, IllegalArgumentException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetAltingChannelInput toReturn = NetChannel.net2one();

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetSharedChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @deprecated Use net2any instead
     * @return A new NetSharedChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static NetSharedChannelInput createNet2Any(String name)
        throws IllegalStateException, IllegalArgumentException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetSharedChannelInput toReturn = NetChannel.net2any();

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetChannelOutput connected to the input channel registered with the given name
     * 
     * @param name
     *            The name to resolve
     * @deprecated Use one2net instead
     * @return A new NetChannelOutput connected to the input with the registered name
     * @throws IllegalStateException
     *             Thrown if the connection to the CNS is not initialised
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static NetChannelOutput createOne2Net(String name)
        throws IllegalStateException, JCSPNetworkException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Resolve the location of the channel
        NetChannelLocation loc = CNS.service.resolve(name);

        // Create and return a new channel
        return NetChannel.one2net(loc);
    }

    /**
     * Creates a new NetSharedChannelOutput connected to the input channel registered with the given name
     * 
     * @param name
     *            The name to resolve
     * @deprecated Use one2net instead
     * @return A new NetChannelOutput connected to the input with the registered name
     * @throws IllegalStateException
     *             Thrown if the connection to the CNS is not initialised
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static NetSharedChannelOutput createAny2Net(String name)
        throws JCSPNetworkException, IllegalStateException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Resolve the location of the channel
        NetChannelLocation loc = CNS.service.resolve(name);

        // Create and return a new channel
        return NetChannel.any2net(loc);
    }

    /**
     * Creates a new NetAltingChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @return A new NetAltingChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetAltingChannelInput<T> net2one(String name)
        throws IllegalStateException, IllegalArgumentException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetAltingChannelInput<T> toReturn = NetChannel.net2one();

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetAltingChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @param immunityLevel
     *            The immunity to poison the channel has
     * @return A new NetAltingChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetAltingChannelInput<T> net2one(String name, int immunityLevel)
        throws IllegalArgumentException, IllegalStateException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetAltingChannelInput<T> toReturn = NetChannel.net2one(immunityLevel);

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetAltingChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetAltingChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetAltingChannelInput<T> net2one(String name, NetworkMessageFilter.FilterRx filter)
        throws IllegalArgumentException, IllegalStateException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetAltingChannelInput<T> toReturn = NetChannel.net2one(filter);

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetAltingChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @param immunityLevel
     *            The immunity level to poison that the channel has
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetAltingChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetAltingChannelInput<T> net2one(String name, int immunityLevel, NetworkMessageFilter.FilterRx filter)
        throws IllegalArgumentException, IllegalStateException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetAltingChannelInput<T> toReturn = NetChannel.net2one(immunityLevel, filter);

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetSharedChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @return A new NetSharedChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetSharedChannelInput<T> net2any(String name)
        throws IllegalArgumentException, IllegalStateException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetSharedChannelInput<T> toReturn = NetChannel.net2any();

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetSharedChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @param immunityLevel
     *            The immunity level to poison that the channel has
     * @return A new NetSharedChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetSharedChannelInput<T> net2any(String name, int immunityLevel)
        throws IllegalArgumentException, IllegalStateException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetSharedChannelInput<T> toReturn = NetChannel.net2any(immunityLevel);

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }
    /**
     * Creates a new NetSharedChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetSharedChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetSharedChannelInput<T> net2any(String name, NetworkMessageFilter.FilterRx filter)
        throws IllegalArgumentException, IllegalStateException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetSharedChannelInput<T> toReturn = NetChannel.net2any(filter);

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetSharedChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @param immunityLevel
     *            The immunity to poison that this channel has
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetSharedChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetSharedChannelInput<T> net2any(String name, int immunityLevel, NetworkMessageFilter.FilterRx filter)
        throws IllegalArgumentException, IllegalStateException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetSharedChannelInput<T> toReturn = NetChannel.net2any(immunityLevel, filter);

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetAltingChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @param index
     *            The index to create the channel with
     * @return A new NetAltingChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetAltingChannelInput<T> numberedNet2One(String name, int index)
        throws IllegalStateException, IllegalArgumentException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetAltingChannelInput<T> toReturn = NetChannel.numberedNet2One(index);

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetAltingChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @param index
     *            The index to create the channel with
     * @param immunityLevel
     *            The immunity level to poison that the channel has
     * @return A new NetAltingChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetAltingChannelInput<T> numberedNet2One(String name, int index, int immunityLevel)
        throws IllegalStateException, IllegalArgumentException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetAltingChannelInput<T> toReturn = NetChannel.numberedNet2One(index, immunityLevel);

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetAltingChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @param index
     *            The index to create the channel with
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetAltingChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetAltingChannelInput<T> numberedNet2One(String name, int index, NetworkMessageFilter.FilterRx filter)
        throws IllegalStateException, IllegalArgumentException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetAltingChannelInput<T> toReturn = NetChannel.numberedNet2One(index, filter);

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetAltingChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @param index
     *            The index to create the channel with
     * @param immunityLevel
     *            The immunity level to poison that the channel has
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetAltingChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetAltingChannelInput<T> numberedNet2One(String name, int index, int immunityLevel,
            NetworkMessageFilter.FilterRx filter)
        throws IllegalStateException, IllegalArgumentException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetAltingChannelInput<T> toReturn = NetChannel.numberedNet2One(index, immunityLevel, filter);

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetSharedChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @param index
     *            The index to create the channel with
     * @return A new NetSharedChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetSharedChannelInput<T> numberedNet2Any(String name, int index)
        throws IllegalStateException, IllegalArgumentException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetSharedChannelInput<T> toReturn = NetChannel.numberedNet2Any(index);

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetSharedChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @param index
     *            The index to create the channel with
     * @param immunityLevel
     *            The immunity to poison that this channel has
     * @return A new NetSharedChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetSharedChannelInput<T> numberedNet2Any(String name, int index, int immunityLevel)
        throws IllegalStateException, IllegalArgumentException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetSharedChannelInput<T> toReturn = NetChannel.numberedNet2Any(index, immunityLevel);

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetSharedChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @param index
     *            The index to create the channel with
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetSharedChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetSharedChannelInput<T> numberedNet2Any(String name, int index, NetworkMessageFilter.FilterRx filter)
        throws IllegalStateException, IllegalArgumentException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetSharedChannelInput<T> toReturn = NetChannel.numberedNet2Any(index, filter);

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetSharedChannelInput registered with the given name
     * 
     * @param name
     *            The name to register with the CNS
     * @param index
     *            The index to create the channel with
     * @param immunityLevel
     *            The immunity to poison that this channel has
     * @param filter
     *            The filter used to decode incoming messages
     * @return A new NetSharedChannelInput registered with the given name
     * @throws IllegalStateException
     *             Thrown if the CNS has not been initialised
     * @throws IllegalArgumentException
     *             Thrown if the channel name is already registered
     */
    public static <T> NetSharedChannelInput<T> numberedNet2Any(String name, int index, int immunityLevel,
            NetworkMessageFilter.FilterRx filter)
        throws IllegalStateException, IllegalArgumentException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Create a new channel
        NetSharedChannelInput<T> toReturn = NetChannel.numberedNet2Any(index, immunityLevel, filter);

        // Attempt to register
        if (CNS.service.register(name, toReturn))
            return toReturn;

        // Failed to register channel. Destroy the channel and throw exception
        toReturn.destroy();
        throw new IllegalArgumentException("Failed to register " + name + " with the CNS");
    }

    /**
     * Creates a new NetChannelOutput connected to the input channel registered with the given name
     * 
     * @param name
     *            The name to resolve
     * @return A new NetChannelOutput connected to the input with the registered name
     * @throws IllegalStateException
     *             Thrown if the connection to the CNS is not initialised
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static <T> NetChannelOutput<T> one2net(String name)
        throws IllegalStateException, JCSPNetworkException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Resolve the location of the channel
        NetChannelLocation loc = CNS.service.resolve(name);

        // Create and return a new channel
        return NetChannel.one2net(loc);
    }

    /**
     * Creates a new NetChannelOutput connected to the input channel registered with the given name
     * 
     * @param name
     *            The name to resolve
     * @param immunityLevel
     *            The immunity to poison that this channel has
     * @return A new NetChannelOutput connected to the input with the registered name
     * @throws IllegalStateException
     *             Thrown if the connection to the CNS is not initialised
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static <T> NetChannelOutput<T> one2net(String name, int immunityLevel)
        throws IllegalStateException, JCSPNetworkException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Resolve the location of the channel
        NetChannelLocation loc = CNS.service.resolve(name);

        // Create and return a new channel
        return NetChannel.one2net(loc, immunityLevel);
    }

    /**
     * Creates a new NetChannelOutput connected to the input channel registered with the given name
     * 
     * @param name
     *            The name to resolve
     * @param filter
     *            The filter used to encode outgoing messages
     * @return A new NetChannelOutput connected to the input with the registered name
     * @throws IllegalStateException
     *             Thrown if the connection to the CNS is not initialised
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static <T> NetChannelOutput<T> one2net(String name, NetworkMessageFilter.FilterTx filter)
        throws IllegalStateException, JCSPNetworkException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Resolve the location of the channel
        NetChannelLocation loc = CNS.service.resolve(name);

        // Create and return a new channel
        return NetChannel.one2net(loc, filter);
    }

    /**
     * Creates a new NetChannelOutput connected to the input channel registered with the given name
     * 
     * @param name
     *            The name to resolve
     * @param immunityLevel
     *            The immunity to poison that this channel has
     * @param filter
     *            The filter used to encode outgoing messages
     * @return A new NetChannelOutput connected to the input with the registered name
     * @throws IllegalStateException
     *             Thrown if the connection to the CNS is not initialised
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static <T> NetChannelOutput<T> one2net(String name, int immunityLevel, NetworkMessageFilter.FilterTx filter)
        throws IllegalStateException, JCSPNetworkException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Resolve the location of the channel
        NetChannelLocation loc = CNS.service.resolve(name);

        // Create and return a new channel
        return NetChannel.one2net(loc, immunityLevel, filter);
    }

    /**
     * Creates a new NetSharedChannelOutput connected to the input channel registered with the given name
     * 
     * @param name
     *            The name to resolve
     * @return A new NetSharedChannelOutput connected to the input with the registered name
     * @throws IllegalStateException
     *             Thrown if the connection to the CNS is not initialised
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static <T> NetSharedChannelOutput<T> any2net(String name)
        throws IllegalStateException, JCSPNetworkException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Resolve the location of the channel
        NetChannelLocation loc = CNS.service.resolve(name);

        // Create and return a new channel
        return NetChannel.any2net(loc);
    }

    /**
     * Creates a new NetSharedChannelOutput connected to the input channel registered with the given name
     * 
     * @param name
     *            The name to resolve
     * @param immunityLevel
     *            The immunity to poison that this channel has
     * @return A new NetSharedChannelOutput connected to the input with the registered name
     * @throws IllegalStateException
     *             Thrown if the connection to the CNS is not initialised
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static <T> NetSharedChannelOutput<T> any2net(String name, int immunityLevel)
        throws IllegalStateException, JCSPNetworkException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Resolve the location of the channel
        NetChannelLocation loc = CNS.service.resolve(name);

        // Create and return a new channel
        return NetChannel.any2net(loc, immunityLevel);
    }

    /**
     * Creates a new NetSharedChannelOutput connected to the input channel registered with the given name
     * 
     * @param name
     *            The name to resolve
     * @param filter
     *            The filter used to encode outgoing messages
     * @return A new NetSharedChannelOutput connected to the input with the registered name
     * @throws IllegalStateException
     *             Thrown if the connection to the CNS is not initialised
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static <T> NetSharedChannelOutput<T> any2net(String name, NetworkMessageFilter.FilterTx filter)
        throws IllegalStateException, JCSPNetworkException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Resolve the location of the channel
        NetChannelLocation loc = CNS.service.resolve(name);

        // Create and return a new channel
        return NetChannel.any2net(loc, filter);
    }

    /**
     * Creates a new NetSharedChannelOutput connected to the input channel registered with the given name
     * 
     * @param name
     *            The name to resolve
     * @param immunityLevel
     *            The immunity level to poison that this channel has
     * @param filter
     *            The filter used to encode outgoing messages
     * @return A new NetSharedChannelOutput connected to the input with the registered name
     * @throws IllegalStateException
     *             Thrown if the connection to the CNS is not initialised
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public static <T> NetSharedChannelOutput<T> any2net(String name, int immunityLevel, NetworkMessageFilter.FilterTx filter)
        throws IllegalStateException, JCSPNetworkException
    {
        // Check if the CNS connection is initialised
        if (!CNS.initialised)
            throw new IllegalStateException("The connection to the CNS has not been initialised");

        // Resolve the location of the channel
        NetChannelLocation loc = CNS.service.resolve(name);

        // Create and return a new channel
        return NetChannel.any2net(loc, filter);
    }

}
