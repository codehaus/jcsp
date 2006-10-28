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

package org.jcsp.util.ints;

import java.io.Serializable;

/**
 * This is used to create a buffered integer channel that always accepts input,
 * overwriting its oldest data if full.
 * <H2>Description</H2>
 * <TT>OverWriteOldestBufferInt</TT> is an implementation of <TT>ChannelDataStoreInt</TT> that yields
 * a <I>FIFO</I> buffered semantics for a channel.  When empty, the channel blocks readers.
 * When full, a writer will overwrite the <I>oldest</I> item left unread in the channel.
 * See the static
 * {@link org.jcsp.lang.ChannelInt#createOne2One(org.jcsp.util.ints.ChannelDataStoreInt)
 * <TT>create</TT>} methods of {@link org.jcsp.lang.ChannelInt} etc.
 * <P>
 * The <TT>getState</TT> method returns <TT>EMPTY</TT> or <TT>NONEMPTYFULL</TT>, but
 * never <TT>FULL</TT>.
 *
 * @see org.jcsp.util.ints.ZeroBufferInt
 * @see org.jcsp.util.ints.BufferInt
 * @see org.jcsp.util.ints.OverWritingBufferInt
 * @see org.jcsp.util.ints.OverFlowingBufferInt
 * @see org.jcsp.util.ints.InfiniteBufferInt
 * @see org.jcsp.lang.ChannelInt
 *
 * @author P.D.Austin
 */
//}}}

public class OverWriteOldestBufferInt implements ChannelDataStoreInt, Serializable
{
    /** The storage for the buffered ints */
    private final int[] buffer;

    /** The number of ints stored in the Buffer */
    private int counter = 0;

    /** The index of the oldest element (when counter > 0) */
    private int firstIndex = 0;

    /** The index of the next free element (when counter < buffer.length) */
    private int lastIndex = 0;

    /**
     * Construct a new <TT>OverWriteOldestBufferInt</TT> with the specified size.
     *
     * @param size the number of <TT>int</TT>s the <TT>OverWriteOldestBufferInt</TT> can store.
     * @throws BufferIntSizeError if <TT>size</TT> is zero or negative.  Note: no action
     * should be taken to <TT>try</TT>/<TT>catch</TT> this exception
     * - application code generating it is in error and needs correcting.
     */
    public OverWriteOldestBufferInt(int size)
    {
        if (size <= 0)
            throw new BufferIntSizeError
                    ("\n*** Attempt to create an overwriting buffered channel with negative or zero capacity");
        buffer = new int[size];
    }

    /**
     * Returns the oldest <TT>int</TT> from the <TT>OverWriteOldestBufferInt</TT> and removes it.
     * <P>
     * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>EMPTY</TT>.
     *
     * @return the oldest <TT>int</TT> from the <TT>OverWriteOldestBufferInt</TT>
     */
    public int get()
    {
        int value = buffer[firstIndex];
        firstIndex = (firstIndex + 1) % buffer.length;
        counter--;
        return value;
    }

    /**
     * Puts a new <TT>int</TT> into the <TT>OverWriteOldestBufferInt</TT>.
     * <P>
     * If <TT>OverWriteOldestBufferInt</TT> is full, the <I>oldest</I> item
     * left unread in the buffer will be overwritten.
     *
     * @param value the <TT>int</TT> to put into the <TT>OverWriteOldestBufferInt</TT>
     */
    public void put(int value)
    {
        if (counter == buffer.length)
            firstIndex = (firstIndex + 1) % buffer.length;
        else
            counter++;
        buffer[lastIndex] = value;
        lastIndex = (lastIndex + 1) % buffer.length;
    }

    /**
     * Returns the current state of the <TT>OverWriteOldestBufferInt</TT>.
     *
     * @return the current state of the <TT>OverWriteOldestBufferInt</TT> (<TT>EMPTY</TT> or
     * <TT>NONEMPTYFULL</TT>)
     */
    public int getState()
    {
        if (counter == 0)
            return EMPTY;
        else
            return NONEMPTYFULL;
    }

    /**
     * Returns a new (and <TT>EMPTY</TT>) <TT>OverWriteOldestBufferInt</TT> with the same
     * creation parameters as this one.
     * <P>
     * <I>Note: Only the size and structure of the </I><TT>OverWriteOldestBufferInt</TT><I> is
     * cloned, not any stored data.</I>
     *
     * @return the cloned instance of this <TT>OverWriteOldestBufferInt</TT>.
     */
    public Object clone()
    {
        return new OverWriteOldestBufferInt(buffer.length);
    }
}
