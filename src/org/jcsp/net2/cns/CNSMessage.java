package org.jcsp.net2.cns;

import org.jcsp.net2.NetChannelLocation;

/**
 * A message sent between a CNS and a CNSService. This is an internal structure to JCSP.
 * 
 * @author Kevin Chalmers
 */
final class CNSMessage
{
    /**
     * The message type. See CNSMessageProtocol
     */
    byte type = 0;

    /**
     * Whether the previous message was successful
     */
    boolean success = false;

    /**
     * Location parameter. Usually the location to register
     */
    NetChannelLocation location1 = null;

    /**
     * Location parameter. Usually the reply location
     */
    NetChannelLocation location2 = null;

    /**
     * Name to register or resolve
     */
    String name = "";

}
