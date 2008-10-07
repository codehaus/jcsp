package org.jcsp.net2.bns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.jcsp.net2.NetBarrierLocation;
import org.jcsp.net2.NetChannelLocation;
import org.jcsp.net2.NetworkMessageFilter;

/**
 * This filter is used by the BNS and BNSService to transmit messages between one another in a manner that is platform
 * independent. This is an internal class to JCSP and is created automatically by the BNS and BNSService. For more
 * information, see the relevant documentation.
 * 
 * @see BNS
 * @see BNSService
 * @see NetworkMessageFilter
 * @author Kevin Chalmers
 */
final class BNSNetworkMessageFilter
{
    /**
     * The encoding filter used to convert a BNSMessage into bytes
     * 
     * @author Kevin Chalmers
     */
    static final class FilterTX
        implements NetworkMessageFilter.FilterTx
    {

        /**
         * The byte stream we will use to retrieve the byte message from
         */
        private final ByteArrayOutputStream baos;

        /**
         * the data stream used to write the parts of the BNSMessage to
         */
        private final DataOutputStream dos;

        /**
         * Creates a new BNSMessage encoding filter
         */
        FilterTX()
        {
            this.baos = new ByteArrayOutputStream(8192);
            this.dos = new DataOutputStream(this.baos);
        }

        /**
         * Converts a BNSMessage into bytes
         * 
         * @param obj
         *            A BNSMessage to convert
         * @return the byte equivalent of the BNSMessage
         * @throws IOException
         *             Thrown if something goes wrong during the conversion
         */
        public byte[] filterTX(Object obj)
            throws IOException
        {
            // First ensure we have a BNSMessage
            if (!(obj instanceof BNSMessage))
                throw new IOException("Attempted to send a non BNSMessage on a BNSMessage channel");

            BNSMessage msg = (BNSMessage)obj;

            // Now reset the byte stream
            this.baos.reset();
            // Write the parts of the BNSMessage to the stream
            this.dos.writeByte(msg.type);
            this.dos.writeBoolean(msg.success);
            if (msg.serviceLocation != null)
                this.dos.writeUTF(msg.serviceLocation.toString());
            else
                this.dos.writeUTF("null");
            if (msg.location != null)
                this.dos.writeUTF(msg.location.toString());
            else
                this.dos.writeUTF("null");
            this.dos.writeUTF(msg.name);
            // flush the stream
            this.dos.flush();
            // Get the bytes
            return this.baos.toByteArray();
        }

    }

    /**
     * The filter used to convert an array of bytes back into a BNSMessage
     * 
     * @author Kevin Chalmers
     */
    static final class FilterRX
        implements NetworkMessageFilter.FilterRx
    {
        /**
         * The input end of the pipe to read the message back
         */
        private ByteArrayInputStream byteIn;

        /**
         * The data input stream used to read in parts of the message
         */
        private DataInputStream dis;

        /**
         * Creates a new decoding BNSMessageFilter
         */
        FilterRX()
        {
            // Nothing to do.
        }

        /**
         * Decodes a byte array back into a BNSMessage
         * 
         * @param bytes
         *            The bytes to convert back into a BNSMessage
         * @return The recreated BNSMessage
         * @throws IOException
         *             Thrown if something goes wrong during the recreation
         */
        public Object filterRX(byte[] bytes)
            throws IOException
        {
            this.byteIn = new ByteArrayInputStream(bytes);
            this.dis = new DataInputStream(byteIn);

            // Recreate the message
            BNSMessage msg = new BNSMessage();
            msg.type = this.dis.readByte();
            msg.success = this.dis.readBoolean();
            msg.serviceLocation = NetChannelLocation.parse(this.dis.readUTF());
            msg.location = NetBarrierLocation.parse(this.dis.readUTF());
            msg.name = this.dis.readUTF();
            return msg;
        }

    }
}
