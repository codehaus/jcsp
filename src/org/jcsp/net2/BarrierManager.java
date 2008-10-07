package org.jcsp.net2;

import java.util.Hashtable;

/**
 * Manages the networked Barriers in the system. This object wraps a Hashtable containing the NetBarrier data objects,
 * and manages the allocation and removal of NetBarrier front ends within the JCSP networking architecture. For
 * information on the NetBarrier, see the appropriate documentation.
 * 
 * @see org.jcsp.net2.NetBarrier
 * @author Kevin Chalmers
 */
final class BarrierManager
{
    /**
     * The index for the next Barrier to be created. We start at 50 as it allows us to have up to 50 default Barriers
     * with set numbers.
     */
    private static int index = 50;

    /**
     * The table containing the Barriers. An Integer (object wrapped int) is used as the key, and the BarrierData as the
     * value.
     */
    private final Hashtable barriers = new Hashtable();

    /**
     * Singleton instance of the BarrierManager
     */
    private static BarrierManager instance = new BarrierManager();

    /**
     * Private default constructor. Used for the singleton instance.
     */
    private BarrierManager()
    {
        // Empty constructor
    }

    /**
     * Allows getting of the singleton instance.
     * 
     * @return The singleton instance of the BarrierManager
     */
    static BarrierManager getInstance()
    {
        return instance;
    }

    /**
     * Allocates a new number to the Barrier, and stores it in the table.
     * 
     * @param bd
     *            The BarrierData for the Barrier
     */
    synchronized void create(BarrierData bd)
    {
        // First allocate the next available number for the Barrier index (VBN).
        Integer objIndex = new Integer(index);
        while (this.barriers.get(objIndex) != null)
            objIndex = new Integer(++index);

        // Now set the index of the BarrierData to the required index
        bd.vbn = index;

        // And add the BarrierData at the given index in the Hashtable
        this.barriers.put(objIndex, bd);

        // Increment the index for the next allocation
        index++;
    }

    /**
     * Stores a barrier with the given index in the table.
     * 
     * @param idx
     *            The index to use for the barrier
     * @param bd
     *            The BarrierData representing the barrier
     * @throws IllegalArgumentException
     *             If a barrier of the given index already exists.
     */
    synchronized void create(int idx, BarrierData bd)
        throws IllegalArgumentException
    {
        Integer objIndex = new Integer(idx);

        // First, ensure that no barrier of the given index already exists. If it does, throw an exception
        if (this.barriers.get(objIndex) != null)
            throw new IllegalArgumentException("Barrier of given number already exists.");

        // Now allocate the index to the BarrierData object
        bd.vbn = idx;

        // And put the new barrier into the list of barriers, and increment the next index if necessary
        this.barriers.put(objIndex, bd);
        if (idx == BarrierManager.index)
            BarrierManager.index++;
    }

    /**
     * Retrieves a barrier from the table
     * 
     * @param idx
     *            Index in the table to retrieve the barrier from.
     * @return The BarrierData object for the barrier.
     */
    BarrierData getBarrier(int idx)
    {
        Integer objIndex = new Integer(idx);
        return (BarrierData)this.barriers.get(objIndex);
    }

    /**
     * Removes the given barrier from the table of barriers.
     * 
     * @param data
     *            The BarrierData object of the barrier to be removed
     */
    void removeBarrier(BarrierData data)
    {
        Integer objIndex = new Integer(data.vbn);
        this.barriers.remove(objIndex);
    }
}
