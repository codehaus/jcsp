    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2001 Peter Welch and Paul Austin.            //
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
    //                  mailbox@quickstone.com                          //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

package org.jcsp.lang;

import org.jcsp.util.*;

/**
 * This implements an any-to-one object channel with user-definable buffering,
 * safe for use by many writers and one reader.
 * <H2>Description</H2>
 * <TT>BufferedAny2OneChannel</TT> implements an any-to-one object channel with
 * user-definable buffering.  It is safe for use by many writing processes
 * but only one reader.  Writing processes compete with each other to use
 * the channel.  Only the reader and one writer will
 * actually be using the channel at any one time.  This is taken care of by
 * <TT>BufferedAny2OneChannel</TT> -- user processes just read from or write to it.
 * <P>
 * The reading process may {@link Alternative <TT>ALT</TT>} on this channel.
 * The writing process is committed (i.e. it may not back off).
 * <P>
 * The constructor requires the user to provide
 * the channel with a <I>plug-in</I> driver conforming to the
 * {@link org.jcsp.util.ChannelDataStore <TT>ChannelDataStore</TT>}
 * interface.  This allows a variety of different channel semantics to be
 * introduced -- including buffered channels of user-defined capacity
 * (including infinite), overwriting channels (with various overwriting
 * policies) etc..
 * Standard examples are given in the <TT>org.jcsp.util</TT> package, but
 * <I>careful users</I> may write their own.
 *
 * <H3><A NAME="Caution">Implementation Note and Caution</H3>
 * <I>Fair</I> servicing of writers to this channel depends on the <I>fair</I>
 * servicing of requests to enter a <TT>synchronized</TT> block (or method) by
 * the underlying Java Virtual Machine (JVM).  Java does not specify how threads
 * waiting to synchronize should be handled.  Currently, Sun's standard JDKs queue
 * these requests - which is <I>fair</I>.  However, there is at least one JVM
 * that puts such competing requests on a stack - which is legal but <I>unfair</I>
 * and can lead to infinite starvation.  This is a problem for <I>any</I> Java system
 * relying on good behaviour from <TT>synchronized</TT>, not just for these
 * <I>any-1</I> channels.
 *
 * @see org.jcsp.lang.Alternative
 * @see org.jcsp.lang.BufferedOne2OneChannel
 * @see org.jcsp.lang.BufferedOne2AnyChannel
 * @see org.jcsp.lang.BufferedAny2AnyChannel
 * @see org.jcsp.util.ChannelDataStore
 *
 * @author P.D.Austin
 * @author P.H.Welch
 */

class BufferedAny2OneChannel extends Any2OneChannelImpl
{
    /** The ChannelDataStore used to store the data for the channel */
    private final ChannelDataStore data;

    /**
     * Constructs a new Any2OneChannelImpl with the specified ChannelDataStore.
     *
     * @param data The ChannelDataStore used to store the data for the channel
     */
    public BufferedAny2OneChannel(ChannelDataStore data)
    {
        if (data == null)
            throw new IllegalArgumentException
                    ("Null ChannelDataStore given to channel constructor ...\n");
        this.data = (ChannelDataStore) data.clone();
    }

    /**
     * Reads an <TT>Object</TT> from the channel.
     *
     * @return The object returned from the channel.
     */
    public Object read()
    {
        synchronized (rwMonitor)
        {
            if (data.getState() == ChannelDataStore.EMPTY)
                try
                {
                    rwMonitor.wait();
                }
                catch (InterruptedException e)
                {
                    throw new ProcessInterruptedError
                            ("*** Thrown from Any2OneChannelImpl.read ()\n" +
                            e.toString());
                }
            rwMonitor.notify();
            return data.get();
        }
    }

    /**
     * Writes an <TT>Object</TT> to the channel. This method also ensures only one
     * of the writers can actually be writing at any time. All other writers
     * are blocked until it completes the write.
     *
     * @param value The object to write to the channel.
     */
    public void write(Object value)
    {
        synchronized (writeMonitor)
        {
            synchronized (rwMonitor)
            {
                data.put(value);
                if (alt != null)
                    alt.schedule();
                else
                    rwMonitor.notify();
                if (data.getState() == ChannelDataStore.FULL)
                    try
                    {
                        rwMonitor.wait();
                    }
                    catch (InterruptedException e)
                    {
                        throw new ProcessInterruptedError
                                ("*** Thrown from Any2OneChannelImpl.write (Object)\n" +
                                e.toString());
                    }
            }
        }
    }

    /**
     * turns on Alternative selection for the channel. Returns true if the
     * channel has data that can be read immediately.
     * <P>
     * <I>NOTE: This method should only be called by the Alternative class</I>
     *
     * @param alt The Alternative class which will control the selection
     * @return true if the channel has data that can be read, else false
     */
    boolean enable(Alternative alt)
    {
        synchronized (rwMonitor)
        {
            if (data.getState() == ChannelDataStore.EMPTY)
            {
                this.alt = alt;
                return false;
            }
            else
                return true;
        }
    }

    /**
     * turns off Alternative selection for the channel. Returns true if the
     * channel contained data that can be read.
     * <P>
     * <I>NOTE: This method should only be called by the Alternative class</I>
     *
     * @return true if the channel has data that can be read false otherwise
     */
    boolean disable()
    {
        synchronized (rwMonitor)
        {
            alt = null;
            return data.getState() != ChannelDataStore.EMPTY;
        }
    }

    /**
     * Returns whether there is data pending on this channel.
     * <P>
     * <I>Note: if there is, it won't go away until you read it.  But if there
     * isn't, there may be some by the time you check the result of this method.</I>
     * <P>
     * This method is provided for convenience.  Its functionality can be provided
     * by <I>Pri Alting</I> the channel against a <TT>SKIP</TT> guard, although
     * at greater run-time and syntactic cost.  For example, the following code
     * fragment:
     * <PRE>
     *   if (c.pending ()) {
     *     Object x = c.read ();
     *     ...  do something with x
     *   } else (
     *     ...  do something else
     *   }
     * </PRE>
     * is equivalent to:
     * <PRE>
     *   if (c_pending.priSelect () == 0) {
     *     Object x = c.read ();
     *     ...  do something with x
     *   } else (
     *     ...  do something else
     * }
     * </PRE>
     * where earlier would have had to have been declared:
     * <PRE>
     * final Alternative c_pending =
     *   new Alternative (new Guard[] {c, new Skip ()});
     * </PRE>
     *
     * @return state of the channel.
     */
    public boolean pending()
    {
        synchronized (rwMonitor)
        {
            return (data.getState() != ChannelDataStore.EMPTY);
        }
    }
}
