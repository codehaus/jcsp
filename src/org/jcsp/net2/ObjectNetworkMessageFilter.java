package org.jcsp.net2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class is the standard encoding and decoding filter for networked JCSP channels. It uses standard Java
 * serialization to operate.
 * 
 * @author Kevin Chalmers
 */
public final class ObjectNetworkMessageFilter
{
    /*
     * Size of the internal buffer of the memory stream
     */
    public static int BUFFER_SIZE = 8192;

    /**
     * The receiving (decoding) filter for Objects
     * 
     * @author Kevin Chalmers
     */
    public static final class FilterRX
        implements NetworkMessageFilter.FilterRx
    {
        /**
         * These four bytes represent the normal header expected in Java for object streams
         */
        static final byte[] objectStreamHeader = { -84, -19, 0, 5 };

        /**
         * The byte array stream used to connect to the ObjectInputStream
         */
        private final ResettableByteArrayInputStream bais;

        /**
         * The ObjectInputStream used to read the objects from.
         */
        private final ObjectInputStream ois;

        /**
         * Creates a new incoming object filter
         */
        public FilterRX()
        {
            try
            {
                // We need to put the header down the stream first to create the ObjectInputStream
                this.bais = new ResettableByteArrayInputStream(ObjectNetworkMessageFilter.FilterRX.objectStreamHeader);

                // Now hook the ObjectInputStream to the byte array stream. Should work fine.
                this.ois = new ObjectInputStream(this.bais);
            }
            catch (IOException ioe)
            {
                // Should never really happen, however...
                throw new RuntimeException(
                        "Failed to create the required streams for ObjectNetwrokMessageFilter.FilterRX");
            }
        }

        /**
         * Decodes an incoming byte array, converting it back into an Object
         * 
         * @param bytes
         *            The byte representation of the object
         * @return The recreated Object
         * @throws IOException
         *             Thrown of something goes wrong during the decoding
         */
        public Object filterRX(byte[] bytes)
            throws IOException
        {
            try
            {
                // Reset the byte array stream with the incoming bytes
                this.bais.reset(bytes);
                // Return the object read from the input stream
                return this.ois.readObject();
            }
            catch (ClassNotFoundException cnfe)
            {
                // Not an exception thrown by other filters, so we convert into an IOException
                throw new IOException("Class not found");
            }
        }

    }

    /**
     * The sending (encoding) filter for Object channels
     * 
     * @author Kevin Chalmers
     */
    public static final class FilterTX
        implements NetworkMessageFilter.FilterTx
    {
        /**
         * The output stream to get the bytes from
         */
        private final ResettableByteArrayOutputStream baos;

        /**
         * The ObjectOutputStream connected to the byte stream to allow the serialization of objects
         */
        private final ObjectOutputStream oos;

        /**
         * Creates a new encoding object filter
         */
        public FilterTX()
        {
            try
            {
                // We use an 8Kb buffer to serialize into as default, although this could can adjusted
                this.baos = new ResettableByteArrayOutputStream(ObjectNetworkMessageFilter.BUFFER_SIZE);
                this.oos = new ObjectOutputStream(this.baos);
            }
            catch (IOException ioe)
            {
                throw new RuntimeException(
                        "Failed to create the required streams for ObjectNetworkMessageFilter.FilterTX");
            }
        }

        /**
         * Encodes an object into bytes by using Object serialization
         * 
         * @param obj
         *            The Object to serialize
         * @return The byte array equivalent of the object
         * @throws IOException
         *             Thrown if something goes wrong during the serialization
         */
        public byte[] filterTX(Object obj)
            throws IOException
        {
            // First we reset the byte buffer to the buffer size, just in case a previous message caused it to grow
            this.baos.reset(ObjectNetworkMessageFilter.BUFFER_SIZE);
            // Now reset the object stream. This clears any remembered messages
            this.oos.reset();
            // Write the object to the stream
            this.oos.writeObject(obj);
            // Get the bytes
            return this.baos.toByteArray();
        }

    }

}
