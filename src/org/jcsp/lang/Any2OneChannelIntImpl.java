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

import java.io.*;
import org.jcsp.util.ints.*;

/**
 * This implements an any-to-one integer channel,
 * safe for use by many writers and one reader.Refer to the {@link Any2OneChannelInt} interface for
 * a fuller description.
 *
 * @see org.jcsp.lang.One2OneChannelIntImpl
 * @see org.jcsp.lang.One2AnyChannelIntImpl
 * @see org.jcsp.lang.Any2AnyChannelIntImpl
 * @see org.jcsp.util.ints.ChannelDataStoreInt
 *
 * @author P.D.Austin and P.H.Welch
 */

class Any2OneChannelIntImpl extends AltingChannelInputInt implements SharedChannelOutputInt, Any2OneChannelInt, Serializable
{
    /** The monitor synchronising reader and writer on this channel */
    protected Object rwMonitor = new Object();

    /** The (invisible-to-users) buffer used to store the data for the channel */
    private int hold;

    /** The synchronisation flag */
    private boolean empty = true;

    /** The Alternative class that controls the selection */
    protected Alternative alt;

    /** The monitor on which writers must synchronize */
    protected final Object writeMonitor = new Object();


    /*************Methods from Any2OneChannelInt******************************/

    /**
     * Returns the <code>AltingChannelInputInt</code> object to use for this
     * channel. As <code>Any2OneChannelIntImpl</code> implements
     * <code>AltingChannelInputInt</code> itself, this method simply returns
     * a reference to the object that it is called on.
     *
     * @return the <code>AltingChannelInputInt</code> object to use for this
     *          channel.
     */
    public AltingChannelInputInt in()
    {
        return this;
    }

    /**
     * Returns the <code>SharedChannelOutputInt</code> object to use for this
     * channel. As <code>Any2OneChannelIntImpl</code> implements
     * <code>SharedChannelOutputInt</code> itself, this method simply returns
     * a reference to the object that it is called on.
     *
     * @return the <code>SharedChannelOutputInt</code> object to use for this
     *          channel.
     */
    public SharedChannelOutputInt out()
    {
        return this;
    }

    /**********************************************************************/


    /**
     * Reads an <TT>int</TT> from the channel.
     *
     * @return the integer read from the channel.
     */
    public int read()
    {
        synchronized (rwMonitor)
        {
            if (empty)
            {
                empty = false;
                try
                {
                    rwMonitor.wait();
                }
                catch (InterruptedException e)
                {
                    throw new ProcessInterruptedError
                            ("*** Thrown from Any2OneChannelIntImpl.read ()\n"
                            + e.toString());
                }
            }
            else
                empty = true;
            rwMonitor.notify();
            return hold;
        }
    }

    /**
     * Writes an <TT>int</TT> to the Channel. This method also ensures only one
     * of the writers can actually be writing at any time. All other writers
     * are blocked until it completes the write.
     *
     * @param value The integer to write to the Channel.
     */
    public void write(int value)
    {
        synchronized (writeMonitor)
        {
            synchronized (rwMonitor)
            {
                hold = value;
                if (empty)
                {
                    empty = false;
                    if (alt != null)
                        alt.schedule();
                }
                else
                {
                    empty = true;
                    rwMonitor.notify();
                }
                try
                {
                    rwMonitor.wait();
                }
                catch (InterruptedException e)
                {
                    throw new ProcessInterruptedError
                            ("*** Thrown from Any2OneChannelIntImpl.write (int)\n"
                            + e.toString());
                }
            }
        }
    }

    /**
     * turns on Alternative selection for the channel. Returns true if the
     * channel has data that can be read immediately.
     * <P>
     * <I>Note: this method should only be called by the Alternative class</I>
     *
     * @param alt the Alternative class which will control the selection
     * @return true if the channel has data that can be read, else false
     */
    boolean enable(Alternative alt)
    {
        synchronized (rwMonitor)
        {
            if (empty)
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
     * <I>Note: this method should only be called by the Alternative class</I>
     *
     * @return true if the channel has data that can be read, else false
     */
    boolean disable()
    {
        synchronized (rwMonitor)
        {
            alt = null;
            return !empty;
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
     *     int x = c.read ();
     *     ...  do something with x
     *   } else (
     *     ...  do something else
     *   }
     * </PRE>
     * is equivalent to:
     * <PRE>
     *   if (c_pending.priSelect () == 0) {
     *     int x = c.read ();
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
            return !empty;
        }
    }

    /**
     * Creates an array of Any2OneChannelIntImpl.
     *
     * @param n the number of channels to create in the array
     * @return the array of Any2OneChannelIntImpl
     */
    public static Any2OneChannelIntImpl[] create(int n)
    {
        Any2OneChannelIntImpl[] channels = new Any2OneChannelIntImpl[n];
        for (int i = 0; i < n; i++)
            channels[i] = new Any2OneChannelIntImpl();
        return channels;
    }

    /**
     * Creates a Any2OneChannelIntImpl using the specified ChannelDataStoreInt.
     *
     * @return the Any2OneChannelIntImpl
     */
    public static Any2OneChannelIntImpl create(ChannelDataStoreInt store)
    {
        return new BufferedAny2OneChannelIntImpl(store);
    }

    /**
     * Creates an array of Any2OneChannelIntImpl using the specified ChannelDataStoreInt.
     *
     * @param n the number of channels to create in the array
     * @return the array of Any2OneChannelIntImpl
     */
    public static Any2OneChannelIntImpl[] create(int n, ChannelDataStoreInt store)
    {
        Any2OneChannelIntImpl[] channels = new Any2OneChannelIntImpl[n];
        for (int i = 0; i < n; i++)
            channels[i] = new BufferedAny2OneChannelIntImpl(store);
        return channels;
    }
}
