package org.jcsp.net2;

import java.util.LinkedList;

import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.Any2OneChannel;
import org.jcsp.lang.Barrier;
import org.jcsp.lang.Channel;
import org.jcsp.lang.ChannelOutput;
import org.jcsp.lang.ProcessInterruptedException;
import org.jcsp.util.InfiniteBuffer;

/**
 * This class is a networked implementation of the standard JCSP Barrier.
 * <p>
 * The NetBarrier is a networked version of the JCSP Barrier, a synchronization primitive similar to the standard event
 * in CSP. The networked implementation follows the standard interface for a local Barrier, with the addition of the
 * interface defining a networked construct. Internally, the two constructs behave differently due to the distributed
 * nature of the NetBarrier.
 * </p>
 * <H3>Client and Server Ends</H3>
 * <p>
 * Unlike a normal Barrier, a NetBarrier has two types, based on whether the Barrier is the hosting end or an attached,
 * synchronizing end. These are differentiated between as server and client ends. The server end, like the input end of
 * a networked channel, will be declared first. The location of this server end can then be used to connect a number of
 * client ends to. The server end can declare an initial number of expected client ends, which it waits for enrolls from
 * before beginning any sync operations. This value can be set to 0 if need be. Each end of a barrier must also declare
 * the number of local syncing processes, creating a two tier construct:
 * </p>
 * <p>
 * Process ---> NetBarrier (client) ---> NetBarrier (server)
 * </p>
 * <H3>Creating NetBarriers</H3>
 * <p>
 * To create a NetBarrier, a similar method is used as a networked channel. A Barrier Name Server is provided for
 * declaring named barriers, or the NetBarrierEnd factory can be used. First, creation of a sever end:
 * </p>
 * <p>
 * <code>
 * int locallyEnrolled = 5;<br>
 * int remoteEnrolled = 1;<br>
 * NetBarrier bar = NetBarrierEnd.netBarrier(locallyEnrolled, remoteEnrolled);<br>
 * </code>
 * </p>
 * <p>
 * A client end requires the location of this barrier to allow creation:
 * </p>
 * <p>
 * <code>
 * NetBarrierLocation loc;<br>
 * int locallyEnrolled = 5;<br>
 * NetBarrier bar = NetBarrierEnd.netBarrier(loc, locallyEnrolled);<br>
 * </code>
 * </p>
 * <p>
 * These barriers can then be used as normal.
 * </p>
 * <H3><B>IMPLMENTATION NOTE</B></H3>
 * <p>
 * To save on resources, a NetBarrier does not have an internal process controlling it (although other implementations
 * may decide to do this). Because of this, the declaring (server) end of the barrier must always have at least one
 * process enrolled with it to ensure that the SYNC operation occurs. If there is a danger that the enrolled processes
 * on the server node will become 0, it is safer to define a process that is only responsible for SYNCing with the
 * barrier. This minor overhead in certain circumstances is seen as a better approach than all NetBarriers being a
 * process within JCSP, where processes are expensive in resources.
 * </p>
 * <p>
 * <code>
 * public void run() { <br>
 *  while (true) { <br>
 *      bar.sync(); }} <br>
 * </code>
 * </p>
 * 
 * @see Barrier
 * @author Kevin Chalmers (networked part)
 * @author P.H. Welch (Barrier)
 */
public final class NetBarrier
    extends Barrier
    implements Networked
{
    /**
     * The SUID for this object. Shouldn't really need it. Barrier should not be serializable.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The data structure representing this NetBarrier object
     */
    private final BarrierData data;

    /**
     * The location that this NetBarrier is connected to
     */
    private final NetBarrierLocation remoteLocation;

    /**
     * The local location of this NetBarrier
     */
    private final NetBarrierLocation localLocation;

    /**
     * The number of locally connected processes
     */
    private int localEnrolled;

    /**
     * The number of local processes still to SYNC
     */
    private int localCountDown;

    /**
     * The number of remote connected processes
     */
    private int netEnrolled;

    /**
     * The number of networked processes still to SYNC
     */
    private int netCountDown;

    /**
     * Flag used to determine if the NetBarrier is connected to a server end on the same Node
     */
    private boolean locallyConnected = false;

    /**
     * A queue of waiting network ends waiting for a SYNC message
     */
    private final LinkedList waitingEnds = new LinkedList();

    /**
     * The number of initial network enrolls that this barrier must wait for.
     */
    private int initialNetEnrollCountdown;

    /**
     * The connection to the Link that the client end communicates with
     */
    private ChannelOutput toLinkTX;

    /**
     * Used by a locally connected barrier to allow it to check the state prior to sending the SYNC.
     */
    private BarrierData localBar = null;

    /**
     * The input channel into this NetBarrier from the Links
     */
    private final AltingChannelInput in;

    /**
     * The exclusive access lock for syncing, etc.
     */
    private final Object lock = new Object();

    /**
     * A flag used to signify that a waking process should perform a network sync when released
     */
    private boolean performNetSync = false;

    /**
     * The constructor for a NetBarrier
     * 
     * @param barData
     *            The data structure defining the Barrier
     * @param numToEnroll
     *            The number of local processes to enroll
     * @param netNumToEnroll
     *            The number of network processes that will enroll
     * @param serverLocation
     *            The location of the server end of the NetBarrier
     * @param inToBar
     *            The channel into the NetBarrier from the Link
     * @param toLink
     *            The channel connecting the client end of a NetBarrierer to its Link
     * @throws IllegalArgumentException
     *             Thrown if the number of local enrolled processes is less than 1, or remote enrolled is less than 0
     */
    private NetBarrier(BarrierData barData, int numToEnroll, int netNumToEnroll, NetBarrierLocation serverLocation,
            AltingChannelInput inToBar, ChannelOutput toLink)
        throws IllegalArgumentException
    {
        // First do some sanity checks
        if (numToEnroll < 1)
            throw new IllegalArgumentException("*** Attempt to set an enrollment of less than 1 on a NetBarrier *** \n");
        if (netNumToEnroll < 0)
            throw new IllegalArgumentException("*** Attempt to create a NetBarrier with negative remote enrolls *** \n");

        // Now set the standard parameters
        this.localEnrolled = numToEnroll;
        this.localCountDown = numToEnroll;
        this.data = barData;
        this.localLocation = new NetBarrierLocation(Node.getInstance().getNodeID(), this.data.vbn);
        this.in = inToBar;

        // Now check if we are a server or client end.
        if (this.data.state == BarrierDataState.OK_SERVER)
        {
            // We are a server end. There is no remote location, and we must set the networked enrolls
            this.remoteLocation = null;
            this.initialNetEnrollCountdown = netNumToEnroll;
            this.netEnrolled = netNumToEnroll;
            this.netCountDown = netNumToEnroll;
        }
        else
        {
            // We are a client end. Set the remote location
            this.remoteLocation = serverLocation;

            // Now, are we a locally connected barrier, or remote connected barrier
            if (serverLocation.getNodeID().equals(Node.getInstance().getNodeID()))
            {
                this.localBar = BarrierManager.getInstance().getBarrier(serverLocation.getVBN());
                // We are remotely connected. Get the channel connected to the server end
                this.toLinkTX = this.localBar.toBarrier;
                // Now we need to check if we can still enroll with it
                synchronized (this.localBar)
                {
                    if (this.localBar.state != BarrierDataState.OK_SERVER)
                        throw new JCSPNetworkException(
                                "Attempted to enroll with a NetBarrier that is not a server end.");
                    // Set the locally connected flag to true.
                    this.locallyConnected = true;
                    // Send an enroll message
                    NetworkMessage msg = new NetworkMessage();
                    msg.type = NetworkProtocol.ENROLL;
                    // Destination is the VBN of the location, although this isn't used as we are locally connected
                    msg.attr1 = serverLocation.getVBN();
                    // Attrubute 2 is not used
                    msg.attr2 = -1;
                    // Write the enroll to the server end
                    this.toLinkTX.write(msg);
                    // We do not need to register with a Link, as we do not go down to that layer.
                }
            }
            else
            {
                // Otherwise we are remotely connected. Set the link connection channel to the given one.
                this.toLinkTX = toLink;
            }
        }
    }

    /**
     * Static factory method used to create a server end of a NetBarrier
     * 
     * @param localEnroll
     *            The number of locally enrolled processes
     * @param remoteEnroll
     *            The number of remote processes to wait for enrolls from
     * @return A new NetBarrier
     * @throws IllegalArgumentException
     *             Thrown if the number of enrolled processes is outside the defined ranges
     */
    static NetBarrier create(int localEnroll, int remoteEnroll)
        throws IllegalArgumentException
    {
        // First, the sanity checks
        if (localEnroll < 1)
            throw new IllegalArgumentException(
                    "Tried to create a NetBarrier with fewer than one locally enrolled process");
        if (remoteEnroll < 0)
            throw new IllegalArgumentException("Tried to create a NetBarrier with negative remote enrollments");

        // Now create the BarrierData structure
        BarrierData data = new BarrierData();
        // Set state to OK_SERVER
        data.state = BarrierDataState.OK_SERVER;
        // Create the connecting channel
        Any2OneChannel chan = Channel.any2one(new InfiniteBuffer());
        // Add the output end to the structure
        data.toBarrier = chan.out();
        // Initialise the structure with the BarrierManager
        BarrierManager.getInstance().create(data);
        // Return a new NetBarrier
        return new NetBarrier(data, localEnroll, remoteEnroll, null, chan.in(), null);
    }

    /**
     * Static factory method for creating a new NetBarrier with a given index
     * 
     * @param localEnroll
     *            The number of locally enrolled processes
     * @param remoteEnroll
     *            The number of remote processes to wait for enrolls from
     * @param barrierIndex
     *            The index to create the barrier with
     * @return A new NetBarrier
     * @throws IllegalArgumentException
     *             Thrown if the any of the arguments are outside the desired ranges.
     */
    static NetBarrier create(int localEnroll, int remoteEnroll, int barrierIndex)
        throws IllegalArgumentException
    {
        // First, the sanity checks.
        if (localEnroll < 1)
            throw new IllegalArgumentException(
                    "Tried to create a NetBarrier with fewer than one locally enrolled process");
        if (remoteEnroll < 0)
            throw new IllegalArgumentException("Tried to create a NetBarrier with negative remote enrollments");

        // Now create the BarrierData structure
        BarrierData data = new BarrierData();
        // Set state to OK_SERVER
        data.state = BarrierDataState.OK_SERVER;
        // Create the connecting channel
        Any2OneChannel chan = Channel.any2one(new InfiniteBuffer());
        // Add the output end to the structure
        data.toBarrier = chan.out();
        // Initialise the structure with the BarrierManager, using the given index
        BarrierManager.getInstance().create(barrierIndex, data);
        // Return a new NetBarrier
        return new NetBarrier(data, localEnroll, remoteEnroll, null, chan.in(), null);
    }

    /**
     * Static factory method for creating a client end of a NetBarrier
     * 
     * @param loc
     *            The location of the server end of the connection
     * @param localEnroll
     *            The number of locally enrolled processes
     * @return A new NetBarrier client end
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     * @throws IllegalArgumentException
     *             Thrown if local enrolled is less than 1
     */
    static NetBarrier create(NetBarrierLocation loc, int localEnroll)
        throws JCSPNetworkException, IllegalArgumentException
    {
        // First, the sanity check.
        if (localEnroll < 1)
            throw new IllegalArgumentException(
                    "Tried to create a NetBarrier with fewer than one locally enrolled process");

        // Next, create the BarrierData structure
        BarrierData data = new BarrierData();
        // Set the state to OK_CLIENT
        data.state = BarrierDataState.OK_CLIENT;
        // Create the connecting channel between this object and the Links
        Any2OneChannel chan = Channel.any2one(new InfiniteBuffer());
        // Attach the output end to the structure
        data.toBarrier = chan.out();
        // Initialise the barrier with the BarrierManager
        BarrierManager.getInstance().create(data);

        // Now check if this is a locally connected barrier
        if (loc.getNodeID().equals(Node.getInstance().getNodeID()))
        {
            // We are locally connected, so create a new NetBarrier. The constructor will connect to the Barrier server
            // end for us.
            return new NetBarrier(data, localEnroll, 0, loc, chan.in(), null);
        }

        // We are not locally connected. Continue.

        // This is the channel we will pass to the NetBarrier
        ChannelOutput toLink;

        // First, check if the LinkManager has a connection for us.
        Link link = LinkManager.getInstance().requestLink(loc.getNodeID());

        // The previous operation returns null if no connection exists.
        if (link == null)
        {
            // No connection to the Link exists. Use the factory to get one.
            link = LinkFactory.getLink(loc.getNodeID());

            // The LinkFactory will have created and started the Link for us, if it could connect. We can continue
        }

        // Retrieve the channel connecting to the TX process
        toLink = link.getTxChannel();

        // We now need to enroll with the server end. Send the enroll message
        NetworkMessage msg = new NetworkMessage();
        msg.type = NetworkProtocol.ENROLL;
        // Destination is the VBN of the location
        msg.attr1 = loc.getVBN();
        // Attribute 2 is not used
        msg.attr2 = -1;
        // Write the message to the Link
        toLink.write(msg);
        // Register with the Link
        link.registerBarrier(data);
        // Return a new NetBarrier
        return new NetBarrier(data, localEnroll, 0, loc, chan.in(), toLink);
    }

    /**
     * Resets the number of locally enrolled processes. A dangerous operation.
     * 
     * @param numToEnroll
     *            The number of processes to reset the enrolled to.
     */
    public void reset(int numToEnroll)
    {
        if (numToEnroll < 1)
        {
            throw new IllegalArgumentException("*** Attempt to set an enrollment of less than 1 on a NetBarrier ***\n");
        }
        synchronized (this.lock)
        {
            this.localEnrolled = numToEnroll;
            this.localCountDown = numToEnroll;
        }
    }

    /**
     * Performs a SYNC operation with the Barrier throws JCSPNetworkException Thrown if something goes wrong in the
     * underlying architecture
     * 
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public void sync()
        throws JCSPNetworkException
    {
        // First check our state
        if (this.data.state == BarrierDataState.BROKEN)
            throw new JCSPNetworkException("The Barrier has broken");
        if (this.data.state == BarrierDataState.DESTROYED)
            throw new JCSPNetworkException("The Barrier has been destroyed");
        if (this.data.state == BarrierDataState.RESIGNED)
            throw new JCSPNetworkException("The Barrier has been completely resigned from");

        // Now we must ensure we are the only process interacting with the Barrier
        synchronized (this.lock)
        {
            // First we check for any incoming messages
            while (this.in.pending())
            {
                // We have a waiting message. Read it in.
                NetworkMessage msg = (NetworkMessage)this.in.read();

                // Now behave based on the type of message
                switch (msg.type)
                {
                    // Client end enrollment
                    case NetworkProtocol.ENROLL:
                        // We should be a server (the Link checked)

                        // Check the number of waiting enrolls, and decrement if required
                        if (this.initialNetEnrollCountdown > 0)
                            this.initialNetEnrollCountdown--;
                        else
                        {
                            // Otherwise we increment the netCountdown and netEnrolled
                            this.netCountDown++;
                            this.netEnrolled++;
                        }
                        break;

                    // We have lost connection, either to a client end or to the server end
                    case NetworkProtocol.LINK_LOST:
                        // Now we must determine how to behave based on whether we are a server or a client end.
                        if (this.data.state == BarrierDataState.OK_CLIENT)
                        {
                            // Link to server end has gone down. Break Barrier as we are no longer doing a network SYNC.
                            // Set state to broken
                            synchronized (this.data)
                            {
                                this.data.state = BarrierDataState.BROKEN;
                            }
                            // Release any waiting processes
                            this.lock.notifyAll();

                            // Throw exception
                            throw new JCSPNetworkException("Link to server end lost");
                        }

                        // We are a server end. Resign one front end, and then throw exception. The Barrier could still
                        // be used.
                        this.netCountDown--;
                        this.netEnrolled--;
                        throw new JCSPNetworkException("A connection to a Client end was lost");

                        // A client end is resigning
                    case NetworkProtocol.RESIGN:
                        // Resign an end from the server
                        this.netCountDown--;
                        this.netEnrolled--;

                        // We may have a problem with too many resigners. Check and throw exception if necessary
                        if (this.netEnrolled < 0)
                        {
                            // Set enrolled and countdown to 0
                            this.netEnrolled = 0;
                            this.netCountDown = 0;

                            // Throw exception. The syncing process can deal with if needed.
                            throw new JCSPNetworkException("Too many net resigns have occurred");
                        }
                        break;

                    case NetworkProtocol.SYNC:
                        // Decrement countdown
                        this.netCountDown--;

                        // Check we haven't received too many SYNC messages
                        if (this.netCountDown < 0)
                        {
                            // Set countdown to 0 and throw an exception
                            this.netCountDown = 0;
                            throw new JCSPNetworkException("Too many net syncs have occurred");
                        }

                        // Otherwise we can add the message to the waiting ends.
                        this.waitingEnds.add(msg);

                        break;
                }
            }

            // We have dealt with all incoming messages. Now deal with our own SYNC

            // First, are we the the final process to SYNC locally?
            if (this.localCountDown > 1)
            {
                // We are not the final process to SYNC locally. Decrement countdown.
                this.localCountDown--;

                // Wait on the lock
                try
                {
                    this.lock.wait();

                    // Now we check if we have had a problem
                    if (this.data.state != BarrierDataState.OK_CLIENT && this.data.state != BarrierDataState.OK_SERVER)
                    {
                        throw new JCSPNetworkException("The NetBarrier failed");
                    }

                    // Now check if we should be performing a netSYNC or not. If not we return. This occurs if a
                    // local process resigned from us, making the net sync occur.
                    if (!this.performNetSync)
                        // We are not performing the net sync. Continue
                        return;

                }
                catch (InterruptedException ie)
                {
                    // Should never happen, however
                    throw new ProcessInterruptedException("*** Thrown from NetBarrier.sync() ***\n" + ie.toString());
                }
            }

            // We are the final local process to sync, or we have been woken up. Now we perform the network SYNC
            // Are we a client?
            if (this.data.state == BarrierDataState.OK_CLIENT)
            {
                // We are a client end. We need to SYNC with the server end.

                // Send SYNC message to the server end
                NetworkMessage msg = new NetworkMessage();
                msg.type = NetworkProtocol.SYNC;
                // Destination taken from the remote location
                msg.attr1 = this.remoteLocation.getVBN();
                // Source is our own VBN
                msg.attr2 = this.data.vbn;

                // Now are we locally connected or not?
                if (this.locallyConnected)
                {
                    // We are locally connected. To ensure we get the SYNC back, attach our own input channel to the
                    // message
                    msg.toLink = this.data.toBarrier;

                    // Now check the state of the local barrier. We need to lock onto it.
                    synchronized (this.localBar)
                    {
                        if (this.localBar.state != BarrierDataState.OK_SERVER)
                            throw new JCSPNetworkException("The server end of the NetBarrier is down.");
                        this.toLinkTX.write(msg);
                    }
                }
                else
                {
                    // We are not locally connected. Send message to Link
                    this.toLinkTX.write(msg);
                }

                // Wait for incoming message
                NetworkMessage message = (NetworkMessage)this.in.read();

                // Now behave according to incoming message
                switch (message.type)
                {
                    case NetworkProtocol.RELEASE:
                        // Everything OK, release processes
                        this.lock.notifyAll();
                        this.localCountDown = this.localEnrolled;
                        break;

                    case NetworkProtocol.REJECT_BARRIER:
                    case NetworkProtocol.LINK_LOST:
                        // Our sync was rejected, or the Link to the server is down. Set state to broken, and throw
                        // exception
                        synchronized (this.data)
                        {
                            this.data.state = BarrierDataState.BROKEN;
                        }

                        this.lock.notifyAll();

                        if (message.type == NetworkProtocol.REJECT_BARRIER)
                            throw new JCSPNetworkException("SYNC to server end of NetBarrier was rejected");

                        throw new JCSPNetworkException("Link to server end of NetBarrier was lost");
                }
            }
            else
            {
                // We are a server end. Wait until all all client ends are ready
                while (this.netCountDown > 0)
                {
                    // Read in the next message
                    NetworkMessage message = (NetworkMessage)this.in.read();

                    // Deal with message
                    switch (message.type)
                    {
                        case NetworkProtocol.ENROLL:
                            // We've had another network end enrolling
                            // Check the number of waiting enrolls, and decrement if required
                            if (this.initialNetEnrollCountdown > 0)
                                this.initialNetEnrollCountdown--;
                            else
                            {
                                this.netCountDown++;
                                this.netEnrolled++;
                            }
                            break;

                        case NetworkProtocol.RESIGN:
                            // Resign a client end from the barrier
                            this.netEnrolled--;
                            this.netCountDown--;

                            // We don't need to do a check here, the while loop covers this. netCountdown can never be
                            // less than 0.
                            break;

                        case NetworkProtocol.LINK_LOST:
                            // A connection to a client end has gone down. We decrement the enrolled and countdown and
                            // throw an exception
                            this.netCountDown--;
                            this.netEnrolled--;

                            throw new JCSPNetworkException("Link to a client end of a NetBarrier was lost");

                        case NetworkProtocol.SYNC:
                            // A client end has synced with us. Decrement netCountdown and add the message to the queue
                            this.netCountDown--;
                            this.waitingEnds.add(message);

                            break;
                    }
                }

                // All local processes and client ends have synced. Release all.
                this.localCountDown = this.localEnrolled;
                this.netCountDown = this.netEnrolled;
                this.lock.notifyAll();

                // Iterate through the list of waiting ends and send them all a RELEASE message
                for (; !this.waitingEnds.isEmpty();)
                {
                    NetworkMessage waitingMessage = (NetworkMessage)this.waitingEnds.getFirst();
                    NetworkMessage reply = new NetworkMessage();
                    reply.type = NetworkProtocol.RELEASE;
                    reply.attr1 = waitingMessage.attr2;
                    waitingMessage.toLink.write(reply);
                }
            }
            // Set flag for performing net sync to false. All other released processes will now continue as normal.
            this.performNetSync = false;
        }
    }

    /**
     * Enrolls locally with the Barrier
     * 
     * @throws JCSPNetworkException
     *             Thrown if the barrier is not a state where it can be enrolled with
     */
    public void enroll()
        throws JCSPNetworkException
    {
        // First check our state
        if (this.data.state == BarrierDataState.BROKEN)
            throw new JCSPNetworkException("The Barrier has broken");
        if (this.data.state == BarrierDataState.DESTROYED)
            throw new JCSPNetworkException("The Barrier has been destroyed");

        // Get exclusive access to the Barrier
        synchronized (this.lock)
        {
            // Do we need to reenroll on the server?
            if (this.data.state == BarrierDataState.RESIGNED)
            {
                // We were previously resigned from the server end. First we need to change our state. Acquire
                // a lock on our state.
                synchronized (this.data)
                {
                    this.data.state = BarrierDataState.OK_CLIENT;
                }

                // Now re-enroll on the server
                NetworkMessage enroll = new NetworkMessage();
                enroll.type = NetworkProtocol.ENROLL;
                enroll.attr1 = this.remoteLocation.getVBN();
                this.toLinkTX.write(enroll);
            }
            // Increment countdown and enrolled
            this.localCountDown++;
            this.localEnrolled++;
        }
    }

    /**
     * Resigns an local process from the barrier
     * 
     * @throws JCSPNetworkException
     *             Thrown if something bad happens within the underlying architecture
     */
    public void resign()
        throws JCSPNetworkException
    {
        // First check our state
        if (this.data.state == BarrierDataState.BROKEN)
            throw new JCSPNetworkException("The Barrier has broken");
        if (this.data.state == BarrierDataState.DESTROYED)
            throw new JCSPNetworkException("The Barrier has been destroyed");
        if (this.data.state == BarrierDataState.RESIGNED)
            return;

        // Now acquire a lock on the barrier to assure exclusive access
        synchronized (this.lock)
        {
            // Now we must check if enrolled will become 0 or not
            if (this.localEnrolled == 1)
            {
                // Are we a server or client end?
                if (this.data.state == BarrierDataState.OK_CLIENT)
                {
                    // We are a server end, and we are the last enrolled one. We need to change our state.
                    // Lock our state object
                    synchronized (this.data)
                    {
                        // Change state to resigned
                        this.data.state = BarrierDataState.RESIGNED;
                    }

                    // We need to resign from the server end. Send resignment message
                    NetworkMessage resign = new NetworkMessage();
                    resign.type = NetworkProtocol.RESIGN;
                    resign.attr1 = this.remoteLocation.getVBN();
                    this.toLinkTX.write(resign);

                    // Now decrement the enrolled and countdown
                    this.localEnrolled--;
                    this.localCountDown--;
                }
                else
                {
                    // We must be a server end. We can't really have a server end with no local enrollments, as this
                    // would break the Barrier. Release all the waiting client ends with a REJECT and throw an exception

                    // Set state to broken
                    synchronized (this.data)
                    {
                        this.data.state = BarrierDataState.BROKEN;
                    }

                    // We now have to check for pending messages
                    while (this.in.pending())
                    {
                        // There is an incoming message. Handle it
                        NetworkMessage message = (NetworkMessage)this.in.read();

                        // The only message type we are interested in is SYNC calls. Enrollments and resignments will
                        // not effect the barrier going down.

                        if (message.type == NetworkProtocol.SYNC)
                        {
                            // Add the message to the queue of SYNCers
                            this.waitingEnds.add(message);
                        }
                    }

                    // Now we must inform all SYNCing ends that we are broken. Iterate through list of waiting ends
                    // and send the REJECT_BARRIER message
                    for (; !this.waitingEnds.isEmpty();)
                    {
                        NetworkMessage waitingMessage = (NetworkMessage)this.waitingEnds.getFirst();
                        NetworkMessage reply = new NetworkMessage();
                        reply.type = NetworkProtocol.REJECT_BARRIER;
                        reply.attr1 = waitingMessage.attr2;
                        waitingMessage.toLink.write(reply);
                    }

                    // Decrement local enrolled and countdown
                    this.localEnrolled--;
                    this.localCountDown--;

                    // Throw exception
                    throw new JCSPNetworkException(
                            "A server end of a NetBarrier has been completely resigned from locally.  No management of the NetBarrier can now occur");
                }

            }
            else
            {
                // We are not the final enrolled process.

                // First decrement enrolled
                this.localEnrolled--;

                // Next we check if countdown will become 0. If it does, the other processes need to be SYNCed
                if (this.localCountDown == 1)
                {
                    // The networked SYNC needs to occur. Set the flag accordingly and wake a waiting process to perform
                    // it.
                    this.performNetSync = true;

                    // *** IMPLEMENTATION NOTE: we are now going to wake one of the waiting processes to perform the
                    // networked part of the SYNC. We do this to avoid the resigning process from doing it, possibly
                    // leading to deadlock. Unfortunately this does mean we may have a problem when another process is
                    // attempting to enroll as we wake up a process. If synchronized is a fair queue in Java, then we
                    // will not have a problem.

                    // Wake a process
                    this.lock.notify();
                }
                else
                {
                    // We are not ready to SYNC, decrement the countdown
                    this.localCountDown--;
                }
            }
        }
    }

    /**
     * Destroys the Barrier
     * 
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     */
    public void destroy()
        throws JCSPNetworkException
    {
        // First check our state and change it accordingly
        if (this.data.state == BarrierDataState.BROKEN)
            return;
        if (this.data.state == BarrierDataState.DESTROYED)
            return;
        if (this.data.state == BarrierDataState.RESIGNED)
            this.data.state = BarrierDataState.DESTROYED;

        // Now acquire lock on the barrier
        synchronized (this.lock)
        {
            // Are we a client or server end?
            if (this.data.state == BarrierDataState.OK_CLIENT)
            {
                // Change our state to destroyed
                // We now lock on the barrier state to update it
                synchronized (this.data)
                {
                    this.data.state = BarrierDataState.DESTROYED;
                }

                // There isn't anything left to do but release the locally waiting processes
                this.lock.notifyAll();
            }
            else
            {
                // We are a server end. We need to reject the all waiting ends

                // Change our state to destroyed
                // We now lock on the barrier state to update it
                synchronized (this.data)
                {
                    this.data.state = BarrierDataState.DESTROYED;
                }

                // We now have to check for pending messages
                while (this.in.pending())
                {
                    // There is an incoming message. Handle it
                    NetworkMessage message = (NetworkMessage)this.in.read();

                    // The only message type we are interested in is SYNC calls. Enrollments and resignments will not
                    // effect the barrier going down. Whenever the enrolling end SYNCs, it will be rejected here or by
                    // the Link
                    if (message.type == NetworkProtocol.SYNC)
                    {
                        // Add the message to the queue of SYNCers
                        this.waitingEnds.add(message);
                    }
                }

                // Now iterate through all the waiting SYNCs and send them a REJECT_BARRIER message
                for (; !this.waitingEnds.isEmpty();)
                {
                    NetworkMessage waitingMessage = (NetworkMessage)this.waitingEnds.getFirst();
                    NetworkMessage reply = new NetworkMessage();
                    reply.type = NetworkProtocol.REJECT_BARRIER;
                    reply.attr1 = waitingMessage.attr2;
                    waitingMessage.toLink.write(reply);
                }
            }
        }
    }

    /**
     * Returns the location of this barrier
     * 
     * @return The location of this channel
     */
    public NetLocation getLocation()
    {
        if (this.data.state == BarrierDataState.OK_SERVER)
            return this.localLocation;
        return this.remoteLocation;
    }

    /**
     * Gets the local location of the barrier
     * 
     * @return The local location of the barrier
     */
    NetBarrierLocation getLocalLocation()
    {
        return this.localLocation;
    }
}
