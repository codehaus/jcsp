package org.jcsp.net2;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.Channel;
import org.jcsp.lang.ChannelOutput;
import org.jcsp.lang.One2OneChannel;
import org.jcsp.util.InfiniteBuffer;

/**
 * Class for managing Links. Ensures that only one Link is only ever created for each individual Node that the hosting
 * Node may be connected to. This is an internal management class of JCSP. For information on how to create Links, see
 * LinkFactory.
 * 
 * @see Link
 * @see LinkFactory
 * @author Kevin Chalmers (updated from Quickstone Technologies)
 */
final class LinkManager
{
    /**
     * A table containing the links currently in operation within the Node. The key is a NodeID and the value is the
     * Link itself for that specific NodeID.
     */
    private static final Hashtable links = new Hashtable();

    /**
     * These event channels are used by the LinkManager to inform any process that may be interested in Link Lost
     * events.
     */
    private static ArrayList eventChans = new ArrayList();

    /**
     * Singleton instance of the LinkManager.
     */
    private static LinkManager instance = new LinkManager();

    /**
     * Private default constructor for singleton instance.
     */
    private LinkManager()
    {
        // Empty constructor
    }

    /**
     * Gets the singleton instance of the LinkManager
     * 
     * @return The singleton instance of the LinkManager
     */
    static LinkManager getInstance()
    {
        return instance;
    }

    /**
     * Handles a Link Lost event. This is done by sending messages over the event channels registered in the eventChans
     * ArrayList. This is possible in the current thread as each event channel is infinitely buffered, so no blocking
     * can occur.
     * 
     * @param link
     *            The Link that has been lost.
     */
    synchronized void lostLink(Link link)
    {
        // First remove the Link from the links table, using the Link's NodeID
        Link removed = (Link)links.remove(link.remoteID);

        // Now check if the Link was indeed removed. Unlikely to happened, but the Link may have been previously removed
        // meaning we have achieved nothing
        if (removed != null)
        {
            // Log the Link Lost
            Node.log.log(this.getClass(), "Link lost to: " + removed.remoteID);

            // Now inform any process listening on a Link Lost channel
            for (Iterator iter = eventChans.iterator(); iter.hasNext();)
                ((ChannelOutput)iter.next()).write(removed.remoteID);
        }
    }

    /**
     * Registers a new Link with the LinkManager.
     * 
     * @param link
     *            The Link to register.
     * @return True if a Link to the Node does not yet exist, false otherwise.
     */
    synchronized boolean registerLink(Link link)
    {
        // Log the registration attempt
        Node.log.log(this.getClass(), "Trying to register Link to: " + link.remoteID);

        // Retrieve the NodeID for the Link
        NodeID remoteID = link.remoteID;

        // Now check whether the key has been registered in the Links table
        if (links.containsKey(remoteID))
        {
            // Connection to the Node already exists. Log.
            Node.err.log(this.getClass(), "Failed to register Link to " + link.remoteID
                                          + ". Connection to Node already exists");

            // Return false.
            return false;
        }

        // Link registration successful. Log.
        Node.log.log(this.getClass(), "Link established to: " + link.remoteID);

        // Add the Link to the links table
        links.put(link.remoteID, link);

        // Return true
        return true;
    }

    /**
     * Returns the Link for the given NodeID
     * 
     * @param id
     *            The NodeID of the remote node
     * @return The Link for the given NodeID
     */
    synchronized Link requestLink(NodeID id)
    {
        return (Link)links.get(id);
    }

    /**
     * Gets a channel input end for receiving Link Lost events.
     * 
     * @return A input end for receiving Link Lost Events.
     */
    synchronized AltingChannelInput getLinkLostEventChannel()
    {
        // Create a new infinitely buffered one to one channel
        final One2OneChannel eventChan = Channel.one2one(new InfiniteBuffer());

        // Add the output end to the list of event channels
        eventChans.add(eventChan.out());

        // Return the input end
        return eventChan.in();
    }

}
