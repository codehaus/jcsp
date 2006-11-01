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

import org.jcsp.util.ChannelDataStore;

/**
 * <p>This implements a one-to-any object channel with user-definable buffering,
 * safe for use by a single writer and many readers. Refer to {@link One2AnyChannel} for a
 * description of this behaviour.</p>
 *
 * <p>Additionally, this channel supports a <code>reject</code> operation. One of the readers may call
 * the reject method to force any current writer to abort with a
 * <code>ChannelDataRejectedException</code> (unless there is already a read which will cause
 * completion of the write). Subsequent read and write attempts will immediately cause a
 * <code>ChannelDataRejectedException</code>.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class RejectableBufferedOne2AnyChannel extends BufferedOne2AnyChannel implements RejectableChannel,SharedChannelOutput
{
    /** Constant to represent the state of the writer. */
    private static int WRITE_NOT_CALLED = 0;
    /** Constant to represent the state of the writer. */
    private static int WRITE_STARTED = 1;
    /** Constant to represent the state of the writer. */
    private static int WRITE_COMPLETED = 2;

    /** Constant to represent the state of the reader. */
    private static int READ_NOT_CALLED = 0;
    /** Constant to represent the state of the reader. */
    private static int READ_STARTED = 1;
    /** Constant to represent the state of the reader. */
    private static int READ_COMPLETED = 2;

    /** Constant to represent the state of the readers and writer. */
    private static int EITHER_STARTED = 1;

    /** Set to true if <code>reject</code> has been called. */
    private boolean rejected = false;

    /**
     * <p>Indicates the current state of the writer, taking the value WRITE_NOT_CALLED, WRITE_STARTED, or
     * WRITE_COMPLETED.</p>
     *
     * <p>WRITE_NOT_CALLED is before the <code>write</code> method gets called.</p>
     *
     * <p>WRITE_STARTED is when the <code>write</code> method has been called and the underlying
     * channel's </code>write</code> method has not yet returned.</p>
     *
     * <p>WRITE_COMPLETED is when the underlying channel's <code>write</code> method has returned but
     * this <code>write</code> method has not fully completed.</p>
     */
    private int writing = WRITE_NOT_CALLED;

    /**
     * <p>Indicates the current state of the writer, taking the value WRITE_NOT_CALLED, WRITE_STARTED, or
     * WRITE_COMPLETED.</p>
     *
     * <p>WRITE_NOT_CALLED is before the <code>write</code> method gets called.</p>
     *
     * <p>WRITE_STARTED is when the <code>write</code> method has been called and the underlying
     * channel's </code>write</code> method has not yet returned.</p>
     *
     * <p>WRITE_COMPLETED is when the underlying channel's <code>write</code> method has returned but
     * this <code>write</code> method has not fully completed.</p>
     */
    private int reading = WRITE_NOT_CALLED;

    /**
     * Constructs a new <code>RejectableBufferedOne2AnyChannel</code>
     *
     * @param data the buffer implementation to use.
     */
    public RejectableBufferedOne2AnyChannel(ChannelDataStore data)
    {
        super(data);
    }

    /**
     * <p>This method will reject any input from the channel.
     * Any writer waiting to output data will have a
     * <code>ChannelDataRejectedException</code> thrown.</p>
     *
     * <p>The reject can be called by any thread.</p>
     *
     * @see org.jcsp.lang.RejectableChannelInput#reject()
     */
    public void reject()
    {
        synchronized (super.rwMonitor)
        {
            if (rejected)
                return;
            //no more readers or writers can start but
            //there might be existing processes reading
            //and writing
            if (reading + writing == EITHER_STARTED)
                //Either read or write has started.
                //Either the reading or writing process must
                //have called wait() - BUT ONLY ONE!!
                //Need a notify to wake up process.
                //don't care which process is waiting
                super.rwMonitor.notify();

                //if both reading and writing are true, we
                //don't need a notify as the write process
                //notifies the read process and the read
                //process notifies the write process.
            rejected = true;
        }
    }

    /**
     * Reads an object over the channel. This method will throw an exception if another thread calls
     * <code>reject</code> before any data is available.
     *
     * @throws ChannelDataRejectedException if <code>reject</code> was called.
     * @return the object read.
     */
    public Object read()
    {
        synchronized (super.readMonitor)
        {
            synchronized (super.rwMonitor)
            {
                if (rejected)
                    throw (new ChannelDataRejectedException());
                reading = READ_STARTED;
                Object toReturn = super.read();

                //either been woken up by a write process or
                // a reject process.
                if (writing == WRITE_NOT_CALLED)
                    throw (new ChannelDataRejectedException());
                //write has not been called and never will be
                //successfully so throw an Exception to notify the
                //user.

                //A write must have occurred and we must have
                //the data. If write occurs and then a Read, read
                //will not wait but will notify the write process
                //and then finish.
                //Don't need to throw an exception even if
                //reject has been called.
                //Can deliver the data.

                //don't want to reset state if rejected as writer
                //needs to know that we have read
                //Read can never be called again anyway
                //if(!rejected) reading = 2;
                reading = READ_COMPLETED;

                if (writing == WRITE_COMPLETED)
                {
                    //write finished before read
                    reading = READ_NOT_CALLED;
                    writing = WRITE_NOT_CALLED;
                }
                return toReturn;
            }
        }
    }

    /**
     * Writes an object over the channel. This method will throw an exception if
     * <code>reject()</code> is called to reject the data before a process is ready to accept the
     * data.
     *
     * @param data the object to write.
     * @throws ChannelDataRejectedException if <code>reject</code> was called.
     */
    public void write(Object data)
    {
        synchronized (super.rwMonitor)
        {
            if (rejected)
                throw (new ChannelDataRejectedException());
            writing = WRITE_STARTED;
            super.write(data);

            //write process could be scheduled before second half of
            //read process - if rejected need to leave writing
            //equal to true.
            writing = WRITE_COMPLETED;

            //if reading then it doesn't matter that data has been rejected
            //as read will have succeeded.
            if (reading == READ_NOT_CALLED && rejected)
                throw (new ChannelDataRejectedException());
            if (reading == READ_COMPLETED)
            {
                //read finished before write
                reading = READ_NOT_CALLED;
                writing = WRITE_NOT_CALLED;
            }
        }
    }
}
