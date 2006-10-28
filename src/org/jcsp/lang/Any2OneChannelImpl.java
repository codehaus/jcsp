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

import org.jcsp.util.ChannelDataStore;
import java.io.Serializable;

/**
 * This implements an any-to-one object channel,
 * safe for use by many writers and one reader. Refer to the {@link Any2OneChannel} interface for
 * more details.
 *
 * @see org.jcsp.lang.One2OneChannelImpl
 * @see org.jcsp.lang.One2AnyChannelImpl
 * @see org.jcsp.lang.Any2AnyChannelImpl
 *
 * @author P.D.Austin and P.H.Welch
 */

class Any2OneChannelImpl extends AltingChannelInput implements SharedChannelOutput, Any2OneChannel, Serializable
{
    /** The monitor synchronising reader and writer on this channel */
    protected Object rwMonitor = new Object();

    /** The (invisible-to-users) buffer used to store the data for the channel */
    private Object hold;

    /** The synchronisation flag */
    private boolean empty = true;

    /** The Alternative class that controls the selection */
    protected Alternative alt;

    /** The monitor on which writers must synchronize */
    protected final Object writeMonitor = new Object();

    /*************Methods from One2AnyChannel******************************/

    /**
     * Returns the <code>AltingChannelInput</code> object to use for this
     * channel. As <code>Any2OneChannelImpl</code> implements
     * <code>AltingChannelInput</code> itself, this method simply returns
     * a reference to the object that it is called on.
     *
     * @return the <code>AltingChannelInput</code> object to use for this
     *          channel.
     */
    public AltingChannelInput in()
    {
        return this;
    }

    /**
     * Returns the <code>SharedChannelOutput</code> object to use for this
     * channel. As <code>Any2OneChannelImpl</code> implements
     * <code>SharedChannelOutput</code> itself, this method simply returns
     * a reference to the object that it is called on.
     *
     * @return the <code>SharedChannelOutput</code> object to use for this
     *          channel.
     */
    public SharedChannelOutput out()
    {
        return this;
    }

    /**********************************************************************/
    /**
     * Reads an <TT>Object</TT> from the channel.
     *
     * @return the object read from the channel.
     */
    public Object read()
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
                            ("*** Thrown from Any2OneChannelImpl.read ()\n" +
                            e.toString());
                }
            }
            else
                empty = true;
            rwMonitor.notify();
            return hold;
        }
    }

    /**
     * Writes an <TT>Object</TT> to the Channel. This method also ensures only one
     * of the writers can actually be writing at any time. All other writers
     * are blocked until it completes the write.
     *
     * @param value The object to write to the Channel.
     */
    public void write(Object value)
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
            return!empty;
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
            return!empty;
        }
    }

    /**
     * Creates an array of Any2OneChannelImpl.
     *
     * @deprecated Should now use methods in Channel class
     * @param n the number of channels to create in the array
     * @return the array of Any2OneChannelImpl
     */
    public static Any2OneChannelImpl[] create(int n)
    {
        Any2OneChannelImpl[] channels = new Any2OneChannelImpl[n];
        for (int i = 0; i < n; i++)
            channels[i] = new Any2OneChannelImpl();
        return channels;
    }

    /**
     * Creates a Any2OneChannelImpl using the specified ChannelDataStore.
     *
     * @return the Any2OneChannelImpl
     */
    public static Any2OneChannelImpl create(ChannelDataStore store)
    {
        return new BufferedAny2OneChannel(store);
    }

    /**
     * Creates an array of Any2OneChannelImpl using the specified ChannelDataStore.
     *
     * @deprecated Should now use methods in Channel class
     * @param n the number of channels to create in the array
     * @return the array of Any2OneChannelImpl
     */
    public static Any2OneChannelImpl[] create(int n, ChannelDataStore store)
    {
        Any2OneChannelImpl[] channels = new Any2OneChannelImpl[n];
        for (int i = 0; i < n; i++)
            channels[i] = new BufferedAny2OneChannel(store);
        return channels;
    }
}
