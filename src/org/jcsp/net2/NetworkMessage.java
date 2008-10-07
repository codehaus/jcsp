package org.jcsp.net2;

import org.jcsp.lang.ChannelOutput;

/**
 * A message received or to be sent via a Link. This is an internal structure to JCSP, and is an object encapsulation of
 * the messages sent between nodes
 * 
 * @author Kevin Chalmers
 */
final class NetworkMessage
{
    /**
     * The message type, as described in NetworkProtocol.
     */
    byte type = -1;

    /**
     * The first attribute of the message.
     */
    int attr1 = -1;

    /**
     * The second attribute of the message
     */
    int attr2 = -1;

    /**
     * Data sent in the message if relevant.
     */
    byte[] data = null;

    /**
     * ChannelOutput to the Link so that acknowledgements can be sent.
     */
    ChannelOutput toLink = null;

}
