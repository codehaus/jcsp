package org.jcsp.net2.cns;

/**
 * This class defines the message types that can be sent to and from the CNS. This is internal to JCSP
 * 
 * @author Kevin Chalmers
 */
final class CNSMessageProtocol
{
    /**
     * Empty, private constructor. We do not create instances of this class.
     */
    private CNSMessageProtocol()
    {
        // Empty constructor
    }

    /**
     * A message sent from a CNSService to a CNS allowing logging on
     */
    static final byte LOGON_MESSAGE = 1;

    /**
     * The reply from a Logon
     */
    static final byte LOGON_REPLY_MESSAGE = 2;

    /**
     * Register a name with the CNS
     */
    static final byte REGISTER_REQUEST = 3;

    /**
     * Resolve a location from the CNS
     */
    static final byte RESOLVE_REQUEST = 4;

    /**
     * *** Not currently used ***
     */
    static final byte LEASE_REQUEST = 5;

    /**
     * *** Not currently used ***
     */
    static final byte DEREGISTER_REQUEST = 6;

    /**
     * A reply from a registration request
     */
    static final byte REGISTER_REPLY = 7;

    /**
     * A reply from a resolve request
     */
    static final byte RESOLVE_REPLY = 8;

    /**
     * *** Not currently used ***
     */
    static final byte LEASE_REPLY = 9;

    /**
     * *** Not currently used ***
     */
    static final byte DEREGISTER_REPLY = 10;
}
