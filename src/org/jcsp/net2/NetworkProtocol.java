package org.jcsp.net2;

/**
 * This class defines the constants used within the Link interactions. This is the network protocol for JCSP. This is an
 * internal class to JCSP, used specifically between Links and the Links and the networked constructs.
 * 
 * @author Kevin Chalmers
 */
final class NetworkProtocol
{
    /**
     * Empty constructor. This is a static set of values
     */
    private NetworkProtocol()
    {
        // Empty constructor
    }

    /**
     * A SEND message from an output end to an input end
     */
    final static byte SEND = 1;

    /**
     * An ACKnowledgment that releases an output end after a write
     */
    final static byte ACK = 2;

    /**
     * An ENROLLment from a client end of a NetBarrier to a server end
     */
    final static byte ENROLL = 3;

    /**
     * A RESIGNation of a client end of a NetBarrier from a server end
     */
    final static byte RESIGN = 4;

    /**
     * A SYNChronization message sent from a client end of a NetBarrier to a server end when the client's local
     * processes have all synchronised
     */
    final static byte SYNC = 5;

    /**
     * RELEASEs a waiting client end of a NetBarrier when the server end has completely been synced with
     */
    final static byte RELEASE = 6;

    /**
     * Rejects a message sent from a NetBarrier.
     */
    final static byte REJECT_BARRIER = 7;

    /**
     * Rejects a message sent from a NetChannelOutput
     */
    final static byte REJECT_CHANNEL = 8;

    /**
     * Signifies that a Link has been lost
     */
    final static byte LINK_LOST = 9;

    /**
     * Mobility message. Still to be defined
     */
    final static byte MOVED = 10;

    /**
     * Mobility message. Still to be defined
     */
    final static byte ARRIVED = 11;

    /**
     * A POISON message sent to poison a channel end
     */
    final static byte POISON = 12;

    /**
     * An Asynchronous send operation
     */
    final static byte ASYNC_SEND = 13;

    /**
     * The initial message sent from a client connection end to a server end
     */
    final static byte OPEN = 14;

    /**
     * The subsequent communications from a client connection before closing
     */
    final static byte REQUEST = 15;

    /**
     * The reply from the server end of a connection
     */
    final static byte REPLY = 16;

    /**
     * A reply from the server end of a connection which also closes the connection
     */
    final static byte REPLY_AND_CLOSE = 17;

    /**
     * An asynchronous open message
     */
    final static byte ASYNC_OPEN = 18;

    /**
     * An asynchronous request to a connection server
     */
    final static byte ASYNC_REQUEST = 19;

    /**
     * An asynchronous reply from the server
     */
    final static byte ASYNC_REPLY = 20;

    /**
     * An asynchronous reply and close
     */
    final static byte ASYNC_REPLY_AND_CLOSE = 21;

    /**
     * An acknowledgement of the initial OPEN or REQUEST by a client connection end
     */
    final static byte REQUEST_ACK = 22;

    /**
     * An acknowledgement of a connection server REPLY
     */
    final static byte REPLY_ACK = 23;

    /**
     * Rejects a message from a networked connection
     */
    final static byte REJECT_CONNECTION = 24;
}
