    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2006 Peter Welch and Paul Austin.            //
    //                2001-2004 Quickstone Technologies Limited.        //
    //                                                                  //
    //  This library is free software; you can redistribute it and/or   //
    //  modify it under the terms of the GNU Lesser General Public      //
    //  License as published by the Free Software Foundation; either    //
    //  version 2.1 of the License, or (at your option) any later       //
    //  version.                                                        //
    //                                                                  //
    //  This library is distributed in the hope that it will be         //
    //  useful, but WITHOUT ANY WARRANTY; without even the implied      //
    //  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR         //
    //  PURPOSE. See the GNU Lesser General Public License for more     //
    //  details.                                                        //
    //                                                                  //
    //  You should have received a copy of the GNU Lesser General       //
    //  Public License along with this library; if not, write to the    //
    //  Free Software Foundation, Inc., 59 Temple Place, Suite 330,     //
    //  Boston, MA 02111-1307, USA.                                     //
    //                                                                  //
    //  Author contact: P.H.Welch@ukc.ac.uk                             //
    //                                                                  //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

package org.jcsp.lang;

import org.jcsp.util.*;

/**
 * <p>This implements a one-to-one object channel with user-definable buffering,
 * for use by a single writer and single reader. Refer to {@link One2OneChannel} for a
 * description of this behaviour.</p>
 *
 * <p>Additionally, this channel supports a <code>reject</code> operation. The reader may call
 * the reject method to force any current writer to abort with a
 * <code>ChannelDataRejectedException</code>. Subsequent read and write attempts will immediately cause a
 * <code>ChannelDataRejectedException</code>.</p>
 *
 * <p>Note that the <code>reject</code> operation cannot be called concurrently to a read.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class RejectableBufferedOne2OneChannel
        extends BufferedOne2OneChannel
        implements RejectableChannel
{
    /** Set to true once <code>reject</code> has been called. */
    private boolean rejected = false;

    /** True if a writer is blocked and must be woken up by a <code>reject</code>. */
    private boolean writing = false;

    /**
     * Constructs a new channel.
     *
     * @param buffer the buffer implementation to use.
     */
    public RejectableBufferedOne2OneChannel(ChannelDataStore buffer)
    {
        super(buffer);
    }

    /**
     * Writes an object to the channel. If the reader process calls <code>reject</code> rather than
     * accept the data an exception will be thrown.
     *
     * @param value the object to write.
     * @throws ChannelDataRejectedException if <code>reject</code> was called.
     */
    public void write(Object value)
    {
        synchronized (super.rwMonitor)
        {
            /*
             *
             * THERE IS A VERY GOOD REASON FOR THIS CODE.
             *
             * DO NOT CHANGE TO A SIMPLE VERSION USING SEPEARATE
             *
             * UNDERLYING CHANNELS AS THIS WILL DEADLOCK
             *
             */
            if (rejected)
                throw (new ChannelDataRejectedException());
            writing = true;
            super.write(value);
            super.rwMonitor.notify();
            writing = false;
            if (rejected)
                throw (new ChannelDataRejectedException());
        }
    }

    /**
     * Reads an object from the channel.
     *
     * @return the object read.
     */
    public Object read()
    {
        synchronized (super.rwMonitor)
        {
            Object o = super.read();

            //This code makes sure that the write terminates
            //before the read().
            //Otherwise read() or reject() could be called
            //and think that more data was pending. It could
            //then call read on the channel and block forever!

            try
            {
                if (writing)
                    super.rwMonitor.wait();
            }
            catch (InterruptedException e)
            {
                throw new ProcessInterruptedException
                        ("*** Thrown from RejectableOne2OneChannel.read (int)\n" +
                         e.toString());
            }
            return o;
        }
    }

    /**
     * Marks the channel as rejected. If there is a currently blocked writer it will be notified and
     * raise an exception. Any subsequent write attempts will immediately cause an exception to be
     * raised. Once this method has been called no more calls can be made to <code>read</code> or
     * <code>reject</code> by the reading process.
     */
    public void reject()
    {
        synchronized (super.rwMonitor)
        {
            rejected = true;
            if (writing)
            {
                super.read();
                //This code makes sure that the write terminates
                //before the read().
                //Otherwise read() or reject() could be called
                //and think that more data was pending. It could
                //then call read on the channel and block forever!
                try
                {
                    super.rwMonitor.wait();
                }
                catch (InterruptedException e)
                {
                    throw new ProcessInterruptedException
                            ("*** Thrown from RejectableOne2OneChannel.read (int)\n" +
                             e.toString());
                }
            }
        }
    }
}
