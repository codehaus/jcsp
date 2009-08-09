package org.jcsp.net2;

import org.jcsp.lang.ChannelOutput;

/**
 * An interface defining a ChannelOutput that is networked. For information on how to an object of this type, see
 * ChannelOutput. For information on how to create a NetChannelOutput, see the the relevant factory.
 * <p>
 * The only method that this interface defines is asyncSend. This is considered a dangerous method to use, and careful
 * consideration must be taken. The inclusion of asyncSend is to provide the impression of a simple infinitely buffered
 * networked channel, without having to create extra buffers beyond what the channel uses.
 * </p>
 * 
 * @see ChannelOutput
 * @see Networked
 * @see NetChannel
 * @author Kevin Chalmers (updated from Quickstone Technologies)
 */
public interface NetChannelOutput<T>
    extends ChannelOutput<T>, Networked
{
    /**
     * Sends a message to the input end of the channel asynchronously (no blocking)
     * 
     * @param obj
     *            The object to send to the input end
     * @throws JCSPNetworkException
     *             Thrown if something goes wrong in the underlying architecture
     * @throws NetworkPoisonException
     *             Thrown if the channel is poisoned
     */
    public void asyncWrite(T obj)
        throws JCSPNetworkException, NetworkPoisonException;

    /**
     * Sets the underlying encoder for the channel
     * 
     * @param encoder
     *            The encoder to use for the channel.
     */
    public void setEncoder(NetworkMessageFilter.FilterTx encoder);
}
