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
 * This implements an any-to-any object channel,
 * safe for use by many writers and many readers. Refer to the {@link Any2AnyChannel} interface
 * for more details.
 *
 * @see org.jcsp.lang.One2OneChannelImpl
 * @see org.jcsp.lang.Any2OneChannelImpl
 * @see org.jcsp.lang.One2AnyChannelImpl
 *
 * @author P.D.Austin and P.H.Welch
 */

class Any2AnyChannelImpl implements SharedChannelInput, SharedChannelOutput, Any2AnyChannel, Serializable
{
    /** The monitor synchronising reader and writer on this channel */
    protected Object rwMonitor = new Object();

    /** The (invisible-to-users) buffer used to store the data for the channel */
    private Object hold;

    /** The synchronisation flag */
    private boolean empty = true;

    /** The monitor on which readers must synchronize */
    protected final Object readMonitor = new Object();

    /** The monitor on which writers must synchronize */
    protected final Object writeMonitor = new Object();

    /*************Methods from Any2AnyChannel******************************/

    /**
     * Returns the <code>SharedChannelInput</code> object to use for this
     * channel. As <code>Any2AnyChannelImpl</code> implements
     * <code>SharedChannelInput</code> itself, this method simply returns
     * a reference to the object that it is called on.
     *
     * @return the <code>SharedChannelInput</code> object to use for this
     *          channel.
     */
    public SharedChannelInput in()
    {
        return this;
    }

    /**
     * Returns the <code>SharedChannelOutput</code> object to use for this
     * channel. As <code>Any2AnyChannelImpl</code> implements
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
        synchronized (readMonitor)
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
                                ("*** Thrown from Any2AnyChannelImpl.read ()\n" +
                                e.toString());
                    }
                }
                else
                    empty = true;
                rwMonitor.notify();
                return hold;
            }
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
                    empty = false;
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
                            ("*** Thrown from Any2AnyChannelImpl.write (Object)\n" +
                            e.toString());
                }
            }
        }
    }

    /**
     * Creates an array of Any2AnyChannelImpl.
     *
     * @deprecated Should now use methods in Channel class
     * @param n the number of channels to create in the array
     * @return the array of Any2AnyChannelImpl
     */
    public static Any2AnyChannelImpl[] create(int n)
    {
        Any2AnyChannelImpl[] channels = new Any2AnyChannelImpl[n];
        for (int i = 0; i < n; i++)
            channels[i] = new Any2AnyChannelImpl();
        return channels;
    }

    /**
     * Creates a Any2AnyChannelImpl using the specified ChannelDataStore.
     *
     * @return the Any2AnyChannelImpl
     */
    public static Any2AnyChannelImpl create(ChannelDataStore store)
    {
        return new BufferedAny2AnyChannel(store);
    }

    /**
     * Creates an array of Any2AnyChannelImpl using the specified ChannelDataStore.
     *
     * @deprecated Should now use methods in Channel class
     * @param n the number of channels to create in the array
     * @return the array of Any2AnyChannelImpl
     */
    public static Any2AnyChannelImpl[] create(int n, ChannelDataStore store)
    {
        Any2AnyChannelImpl[] channels = new Any2AnyChannelImpl[n];
        for (int i = 0; i < n; i++)
            channels[i] = new BufferedAny2AnyChannel(store);
        return channels;
    }
}
