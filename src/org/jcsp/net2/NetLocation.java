/**
 * 
 */
package org.jcsp.net2;

/**
 * This abstract class defines a data structure that is a location of a networked synchronization mechanism. Currently,
 * JCSP offers two location structures - NetChannelLocation and NetBarrierLocation. See the relevant documentation for
 * more information.
 * 
 * @see NetChannelLocation
 * @see NetBarrierLocation
 * @author Kevin Chalmers
 */
public abstract class NetLocation
{
    /**
     * Gets the NodeID part of the location structure
     * 
     * @return the NodeID part of the NetLocation
     */
    public abstract NodeID getNodeID();

    /**
     * Gets the NodeAddress part of the location structure
     * 
     * @return The NodeAddress part of the NetLocation
     */
    public abstract NodeAddress getNodeAddress();
}
