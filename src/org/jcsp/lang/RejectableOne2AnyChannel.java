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

/**
 * <p>This implements a one-to-any object channel,
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
 * 
 * @deprecated This channel is superceded by the poison mechanisms, please see {@link PoisonException}
 */
public class RejectableOne2AnyChannel
        extends One2AnyChannelImpl
        implements RejectableChannel, SharedChannelOutput
{
    
    /**
     * Constructs a new channel.
     */
    public RejectableOne2AnyChannel()
    {
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
        poisonIn(new ChannelDataRejectedException());
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
        return super.read();
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
        super.write(data);
    }
}
