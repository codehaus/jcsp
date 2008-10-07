/**
 * 
 */
package org.jcsp.net2;

/**
 * @author Kevin Chalmers
 */
final class ConnectionDataState
{
    static final byte INACTIVE = 0;

    static final byte CLIENT_STATE_CLOSED = 1;

    static final byte CLIENT_STATE_OPEN = 2;

    static final byte CLIENT_STATE_MADE_REQ = 3;

    static final byte SERVER_STATE_CLOSED = 4;

    static final byte SERVER_STATE_OPEN = 5;

    static final byte SERVER_STATE_RECEIVED = 6;

    static final byte DESTROYED = 7;

    static final byte BROKEN = 8;
}
