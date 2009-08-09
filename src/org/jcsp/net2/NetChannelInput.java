package org.jcsp.net2;

import org.jcsp.lang.ChannelInput;

/**
 * This interface defines a ChannelInput that is also networked. For information on ChannelInput see the relevant class.
 * For information on how to create a NetChannelInput, see the relevant factory class
 * 
 * @see ChannelInput
 * @see Networked
 * @see NetChannel
 * @author Kevin Chalmers (updated from Quickstone Technologies)
 */
public interface NetChannelInput<T>
    extends ChannelInput<T>, Networked
{
    /**
     * Sets the underlying decoder for the channel
     * 
     * @param decoder
     *            The new decoder to use.
     */
    public void setDecoder(NetworkMessageFilter.FilterRx decoder);
}
