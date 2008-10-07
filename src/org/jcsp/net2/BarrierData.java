package org.jcsp.net2;

import org.jcsp.lang.ChannelOutput;

/**
 * Contains the data that relates to a networked Barrier. This is an external data structure within JCSP networking, and
 * is held by both the NetBarrier and the BarrierManager. For information on the operation of the NetBarrier, see the
 * relevant documentation.
 * 
 * @see org.jcsp.net2.NetBarrier
 * @author Kevin Chalmers
 */
final class BarrierData
{
    /**
     * The virtual Barrier number that uniquely identifies the Barrier within the Node
     */
    int vbn = -1;

    /**
     * The current state of the Barrier
     */
    byte state = BarrierDataState.INACTIVE;

    /**
     * The connection to the Barrier for connecting to the NetBarrier object from the Link
     */
    ChannelOutput toBarrier = null;

}
