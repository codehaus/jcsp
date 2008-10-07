package org.jcsp.net2;

import java.io.ByteArrayOutputStream;

/**
 * This class is used by the ObjectNetworkMessageFilter. It acts as a normal ByteArrayOutputStream, but allows the
 * internal buffer to be reset in size, thereby regaining some resources.
 * 
 * @author Kevin Chalmers
 */
final class ResettableByteArrayOutputStream
    extends ByteArrayOutputStream
{
    /**
     * Creates a new ResettableByteArrayOutputStream
     * 
     * @param size
     *            The size of the internal buffer
     */
    ResettableByteArrayOutputStream(int size)
    {
        super(size);
    }

    /**
     * Resets the internal buffer
     * 
     * @param size
     *            The size to reset the internal buffer to
     */
    void reset(int size)
    {
        this.reset();
        if (this.buf.length != size)
            this.buf = new byte[size];
    }
}
