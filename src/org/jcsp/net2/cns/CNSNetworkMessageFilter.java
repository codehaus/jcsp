package org.jcsp.net2.cns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.jcsp.net2.NetChannelLocation;
import org.jcsp.net2.NetworkMessageFilter;

/**
 * This filter is used by the CNS and CNSService to transmit messages between one another in a manner that is platform
 * independent. This is an internal class to JCSP, and is created automatically by the CNS and CNSService. For more
 * information, see the relevant documentation.
 * 
 * @see CNS
 * @see CNSService
 * @see NetworkMessageFilter
 * @author Kevin Chalmers
 */
final class CNSNetworkMessageFilter
{

    /**
     * The encoding filter used to convert a CNSMessage into bytes
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
         * The data stream, used to write the parts of the CNSMessage to
         */
        private final DataOutputStream dos;

        /**
         * Creates a new CNS encoding filter
         */
        FilterTX()
        {
            this.baos = new ByteArrayOutputStream(8192);
            this.dos = new DataOutputStream(this.baos);
        }

        /**
         * Converts an object (a CNSMessage) into bytes
         * 
         * @param obj
         *            The CNSMessage to convert
         * @return The byte equivalent of the CNSMessage
         * @throws IOException
         *             Thrown if something goes wrong during the conversion
         */
        public byte[] filterTX(Object obj)
            throws IOException
        {
            // First ensure we have a CNSMessage
            if (!(obj instanceof CNSMessage))
                throw new IOException("Attempted to send a non CNSMessage on a CNSMessage channel");
            CNSMessage msg = (CNSMessage)obj;

            // Now reset the byte stream
            this.baos.reset();
            // Write the parts of the CNSMessage to the stream
            this.dos.writeByte(msg.type);
            this.dos.writeBoolean(msg.success);
            if (msg.location1 != null)
                this.dos.writeUTF(msg.location1.toString());
            else
                this.dos.writeUTF("null");
            if (msg.location2 != null)
                this.dos.writeUTF(msg.location2.toString());
            else
                this.dos.writeUTF("null");
            this.dos.writeUTF(msg.name);
            // Flush the stream
            this.dos.flush();
            // Get the bytes
            return this.baos.toByteArray();
        }

    }

    /**
     * The filter used to convert a CNSMessage from its byte representation back into an object
     * 
     * @author Kevin Chalmers
     */
    static final class FilterRX
        implements NetworkMessageFilter.FilterRx
    {
        /**
         * The input end to read the message back from
         */
        private ByteArrayInputStream byteIn;

        /**
         * The data input stream used to read in the parts of the message
         */
        private DataInputStream dis;

        /**
         * Creates a new decoding CNSMessage filter
         */
        FilterRX()
        {
            // Nothing to do
        }

        /**
         * Decodes the byte equivalent of a CNSMessage
         * 
         * @param bytes
         *            The byte equivalent of a CNSMessage
         * @return The recreated CNSMessage
         * @throws IOException
         *             Thrown if something goes wrong during the recreation
         */
        public Object filterRX(byte[] bytes)
            throws IOException
        {
            this.byteIn = new ByteArrayInputStream(bytes);
            this.dis = new DataInputStream(byteIn);

            // Recreate the message
            CNSMessage msg = new CNSMessage();
            msg.type = this.dis.readByte();
            msg.success = this.dis.readBoolean();
            msg.location1 = NetChannelLocation.parse(this.dis.readUTF());
            msg.location2 = NetChannelLocation.parse(this.dis.readUTF());
            msg.name = this.dis.readUTF();
            return msg;
        }

    }
}
