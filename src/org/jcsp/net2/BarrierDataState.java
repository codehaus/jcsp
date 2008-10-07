package org.jcsp.net2;

/**
 * Describes the possible states that a networked Barrier might be in.
 * 
 * @author Kevin Chalmers
 */
final class BarrierDataState
{
    /**
     * Empty private constructor. This is simply a protocol.
     */
    private BarrierDataState()
    {
        // Empty constructor
    }

    /**
     * Barrier is inactive. It has not been initialised yet.
     */
    static final byte INACTIVE = 0;

    /**
     * Barrier is in OK state, and is a server end. Has been initialised.
     */
    static final byte OK_SERVER = 1;

    /**
     * Barrier is in OK state, and is a client end. Has been initialised
     */
    static final byte OK_CLIENT = 2;

    /**
     * Barrier is broken.
     */
    static final byte BROKEN = 3;

    /**
     * Barrier has been destroyed
     */
    static final byte DESTROYED = 4;

    /**
     * Barrier has resigned from the server front end.
     */
    static final byte RESIGNED = 5;
}
