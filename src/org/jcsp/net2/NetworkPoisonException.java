package org.jcsp.net2;

import org.jcsp.lang.PoisonException;

/**
 * An exception thrown when a networked channel is poisoned. See the poison exception in the core package for more
 * information
 * 
 * @see PoisonException
 * @author Kevin Chalmers
 */
public final class NetworkPoisonException
    extends PoisonException
{
    /**
     * The SUID of the class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new NetworkPoisonException
     * 
     * @param strength
     *            The strength of the poison
     */
    protected NetworkPoisonException(int strength)
    {
        super(strength);
    }
}
