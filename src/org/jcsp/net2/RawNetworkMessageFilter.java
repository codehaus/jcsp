package org.jcsp.net2;

import java.io.IOException;

/**
 * A NetworkMessageFilter used to send and receive raw byte data.
 * 
 * @see NetworkMessageFilter
 * @author Kevin Chalmers
 */
public final class RawNetworkMessageFilter
{
    /**
     * The receiving filter
     * 
     * @author Kevin Chalmers
     */
    public static final class FilterRX
        implements NetworkMessageFilter.FilterRx
    {
        /**
         * Creates a new RawNetworkMessageFilter.FilterRX
         */
        public FilterRX()
        {
            // Nothing to do
        }

        /**
         * Decodes an incoming byte array. Does nothing
         * 
         * @param bytes
         *            The bytes received in an incoming message
         * @return The same bytes as is passed in
         */
        public Object filterRX(byte[] bytes)
        {
            return bytes;
        }

    }

    /**
     * The sending Filter
     * 
     * @author Kevin Chalmers
     */
    public static final class FilterTX
        implements NetworkMessageFilter.FilterTx
    {
        /**
         * Creates a new output filter
         */
        public FilterTX()
        {
            // Nothing to do
        }

        /**
         * Will send a byte array as raw bytes
         * 
         * @param obj
         *            The object to send. This must be a byte array
         * @return The same byte array as sent in
         * @throws IOException
         *             Thrown if the sent object is not a byte array
         */
        public byte[] filterTX(Object obj)
            throws IOException
        {
            if (!(obj instanceof byte[]))
                throw new IOException("Raw data filter received an object that was not a byte[]");
            return (byte[])obj;
        }
    }
}
