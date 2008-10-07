package org.jcsp.net2;

/**
 * This is the general exception thrown when something bad happens in the underlying architecture. Currently this is
 * generalised for the sake of simplicity. However, a number of different errors may occur internally, and therefore
 * this exception may be specialised into particular exception types in the future.
 * 
 * @author Kevin Chalmers
 */
public final class JCSPNetworkException
    extends RuntimeException
{
    /**
     * Default serial ID. Given for the sake of completeness.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for a new JCSPNetworkException
     * 
     * @param message
     *            The message for the exception
     */
    public JCSPNetworkException(String message)
    {
        super(message);
    }
}
