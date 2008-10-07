package org.jcsp.net2;

import java.util.Hashtable;

/**
 * A class used to manage the networked channels on the Node. This is an internal object to JCSP networking. For a
 * description of networked channels, see the relevant documentation.
 * 
 * @see org.jcsp.net2.NetChannelInput
 * @see org.jcsp.net2.NetChannelOutput
 * @author Kevin Chalmers
 */
final class ChannelManager
{
    /**
     * The index for the next channel to be created. We start at 50 as it allows us to have up to 50 default channels,
     * for example for use to connect to a Channel Name Server.
     */
    private static int index = 50;

    /**
     * The table containing the channels. An Integer (object wrapped int) is used as the key, and the ChannelData as the
     * value.
     */
    private final Hashtable channels = new Hashtable();

    /**
     * Singleton instance of the ChannelManager
     */
    private static ChannelManager instance = new ChannelManager();

    /**
     * Private default constructor. Used for the singleton instance.
     */
    private ChannelManager()
    {
        // Empty constructor
    }

    /**
     * Allows getting of the singleton instance.
     * 
     * @return The singleton instance of the ChannelManager
     */
    static ChannelManager getInstance()
    {
        return instance;
    }

    /**
     * Allocates a new number to the channel, and stores it in the table.
     * 
     * @param cd
     *            The ChannelData for the channel
     */
    synchronized void create(ChannelData cd)
    {
        // First allocate a new number for the channel
        Integer objIndex = new Integer(index);
        while (this.channels.get(objIndex) != null)
            objIndex = new Integer(++index);

        // Set the index of the ChannelData
        cd.vcn = index;

        // Now put the channel in the channel Hashtable
        this.channels.put(objIndex, cd);

        // Finally increment the index for the next channel to be created
        index++;
    }

    /**
     * Stores a channel in the given index in the table.
     * 
     * @param idx
     *            The index to use for the channel
     * @param cd
     *            The ChannelData for the channel
     * @throws IllegalArgumentException
     *             If a channel of the given index already exists.
     */
    synchronized void create(int idx, ChannelData cd)
        throws IllegalArgumentException
    {
        // First check that a channel of the given index does not exist. If it does, throw an exception
        Integer objIndex = new Integer(idx);
        if (this.channels.get(objIndex) != null)
            throw new IllegalArgumentException("Channel of given number already exists.");

        // Set the index of the channel data
        cd.vcn = idx;

        // Now add the channel to the channels table
        this.channels.put(objIndex, cd);

        // Update the index if necessary
        if (idx == ChannelManager.index)
            ChannelManager.index++;
    }

    /**
     * Retrieves a channel from the table
     * 
     * @param idx
     *            Index in the table to retrieve the channel from.
     * @return The ChannelData object for the channel.
     */
    ChannelData getChannel(int idx)
    {
        Integer objIndex = new Integer(idx);
        return (ChannelData)this.channels.get(objIndex);
    }

    /**
     * Removes a channel from the table.
     * 
     * @param data
     *            ChannelData for channel to remove
     */
    void removeChannel(ChannelData data)
    {
        Integer objIndex = new Integer(data.vcn);
        this.channels.remove(objIndex);
    }

}
