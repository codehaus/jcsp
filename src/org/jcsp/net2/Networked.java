package org.jcsp.net2;

/**
 * Defines a standard JCSP synchronization mechanism which is also networked. For concrete examples of this class, see
 * NetBarrier, and the networked channels
 * 
 * @see NetBarrier
 * @see NetChannelInput
 * @see NetChannelOutput
 * @author Kevin Chalmers (updated from Quickstone Technologies)
 */
public interface Networked
{
    /**
     * Gets the networked location of the Networked construct
     * 
     * @return The location of the construct
     */
    public NetLocation getLocation();

    /**
     * Destroys the Networked construct
     */
    public void destroy();
}
