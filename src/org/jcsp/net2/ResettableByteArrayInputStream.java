package org.jcsp.net2;

import java.io.ByteArrayInputStream;

/**
 * This class is used by the ObjectNetworkMessageFilter. It acts as a ByteArrayInputStream, but allows the internal byte
 * array to be replaced by another. This stops unnecessary object creation
 * 
 * @author Kevin Chalmers
 */
final class ResettableByteArrayInputStream
    extends ByteArrayInputStream
{
    /**
     * Creates a new ResettableByteArrayInputStream
     * 
     * @param bytes
     *            The byte array to read data from
     */
    ResettableByteArrayInputStream(byte[] bytes)
    {
        super(bytes);
    }

    /**
     * Replaces the internal byte array
     * 
     * @param bytes
     *            The byte array to replace the existing internal one
     */
    void reset(byte[] bytes)
    {
        this.buf = bytes;
        this.count = bytes.length;
        this.pos = 0;
    }
}
