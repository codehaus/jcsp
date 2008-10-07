package org.jcsp.net2;

import org.jcsp.lang.ChannelInput;
import org.jcsp.lang.ChannelOutput;

/**
 * A class that is used to keep track of the state of a network channel. For a description of networked channels, see
 * the relevant documentation.
 * 
 * @see org.jcsp.net2.NetChannelInput
 * @see org.jcsp.net2.NetChannelOutput
 * @author Kevin Chalmers
 */
final class ChannelData
{
    /**
     * The virtual channel number. A unique number on the Node for identifying a channel.
     */
    int vcn = -1;

    /**
     * The current state of the channel.
     */
    byte state = ChannelDataState.INACTIVE;

    /**
     * The channel output used to connect to the network channel object. For a networked input end this is used to
     * communicate input messages from connected output ends. For output channels this is usually used for
     * acknowledgement, but may also be used for passing link lost and poison messages.
     */
    ChannelOutput toChannel = null;

    /**
     * Indicates the level of poison that has been placed on the channel, if relevant.
     */
    int poisonLevel = -1;

    /**
     * Indicates the immunity level to poison this channel has
     */
    int immunityLevel = Integer.MAX_VALUE;

    /**
     * The other end of the toChannel. This will be set whenever a channel is used in a mobile manner. Any received
     * messages when the channel is moved are written to the normal channel, and they can then be accessed via this end.
     */
    ChannelInput fromChannel = null;
}
