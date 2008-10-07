package org.jcsp.net2.bns;

import org.jcsp.net2.NetBarrierLocation;
import org.jcsp.net2.NetChannelLocation;

/**
 * A message sent between a BNS and a BNSService. This is an internal structure to JCSP
 * 
 * @author Kevin Chalmers
 */
final class BNSMessage
{

    /**
     * The message type. See BNSMessageProtocol
     */
    byte type = 0;

    /**
     * Whether the previous message was successful
     */
    boolean success = false;

    /**
     * The location that the BNS must reply to
     */
    NetChannelLocation serviceLocation = null;

    /**
     * The location of a resolves or registered barrier
     */
    NetBarrierLocation location = null;

    /**
     * The name to register or resolve
     */
    String name = "";
}
