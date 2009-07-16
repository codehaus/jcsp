    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2008 Peter Welch and Paul Austin.            //
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
    //  Author contact: P.H.Welch@kent.ac.uk                             //
    //                                                                  //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

package org.jcsp.util;

import java.io.Serializable;

/**
 * This is used to create a buffered object channel that never loses data.
 * <H2>Description</H2>
 * <TT>Buffer</TT> is an implementation of <TT>ChannelDataStore</TT> that yields
 * a blocking <I>FIFO</I> buffered semantics for a channel.
 * See the <tt>static</tt> construction methods of {@link org.jcsp.lang.Channel}
 * ({@link org.jcsp.lang.Channel#one2one(org.jcsp.util.ChannelDataStore)} etc.).
 * <P>
 * The <TT>getState</TT> method returns <TT>EMPTY</TT>, <TT>NONEMPTYFULL</TT> or
 * <TT>FULL</TT> according to the state of the buffer.
 *
 * @see org.jcsp.util.ZeroBuffer
 * @see org.jcsp.util.OverWriteOldestBuffer
 * @see org.jcsp.util.OverWritingBuffer
 * @see org.jcsp.util.OverFlowingBuffer
 * @see org.jcsp.util.InfiniteBuffer
 * @see org.jcsp.lang.Channel
 *
 * @author P.D. Austin
 */

public class Buffer<T> implements ChannelDataStore<T>, Serializable
{
    /** The storage for the buffered Objects */
    private final T[] buffer;

    /** The number of Objects stored in the Buffer */
    private int counter = 0;

    /** The index of the oldest element (when counter > 0) */
    private int firstIndex = 0;

    /** The index of the next free element (when counter < buffer.length) */
    private int lastIndex = 0;

    /**
     * Construct a new <TT>Buffer</TT> with the specified size.
     *
     * @param size the number of Objects the Buffer can store.
     * @throws BufferSizeError if <TT>size</TT> is negative.  Note: no action
     * should be taken to <TT>try</TT>/<TT>catch</TT> this exception
     * - application code generating it is in error and needs correcting.
     */
    public Buffer(int size)
    {
        if (size < 0)
            throw new BufferSizeError("\n*** Attempt to create a buffered channel with negative capacity");
        buffer = (T[]) new Object[size + 1]; // the extra one is a subtlety needed by
        // the current channel algorithms.

		// NOTE the (T[]) cast here is required - java's generics don't allow
		// generic arrays to be created at run-time. This'll cause some build
		// warnings with Xlint:unchecked... no real way to fix this without
		// swapping out the array for a Collection of Objects which would
		// probably be slower than the array...
    }

    /**
     * Returns the oldest <TT>Object</TT> from the <TT>Buffer</TT> and removes it.
     * <P>
     * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>EMPTY</TT>.
     *
     * @return the oldest <TT>Object</TT> from the <TT>Buffer</TT>
     */
    public T get()
    {
        T value = buffer[firstIndex];
        buffer[firstIndex] = null;
        firstIndex = (firstIndex + 1) % buffer.length;
        counter--;
        return value;
    }
    
    /**
     * Returns the oldest object from the buffer but does not remove it.
     * 
     * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>EMPTY</TT>.
     *
     * @return the oldest <TT>Object</TT> from the <TT>Buffer</TT>
     */
    public T startGet()
    {
      return buffer[firstIndex];
    }
    
    /**
     * Removes the oldest object from the buffer.     
     */
    public void endGet()
    {
      buffer[firstIndex] = null;
      firstIndex = (firstIndex + 1) % buffer.length;
      counter--;
    }

    /**
     * Puts a new <TT>Object</TT> into the <TT>Buffer</TT>.
     * <P>
     * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>FULL</TT>.
     *
     * @param value the Object to put into the Buffer
     */
    public void put(T value)
    {
        buffer[lastIndex] = value;
        lastIndex = (lastIndex + 1) % buffer.length;
        counter++;
    }

    /**
     * Returns the current state of the <TT>Buffer</TT>.
     *
     * @return the current state of the <TT>Buffer</TT> (<TT>EMPTY</TT>,
     * <TT>NONEMPTYFULL</TT> or <TT>FULL</TT>)
     */
    public int getState()
    {
        if (counter == 0)
            return EMPTY;
        else if (counter == buffer.length)
            return FULL;
        else
            return NONEMPTYFULL;
    }

    /**
     * Returns a new (and <TT>EMPTY</TT>) <TT>Buffer</TT> with the same
     * creation parameters as this one.
     * <P>
     * <I>Note: Only the size and structure of the </I><TT>Buffer</TT><I> is
     * cloned, not any stored data.</I>
     *
     * @return the cloned instance of this <TT>Buffer</TT>
     */
    public Object clone()
    {
        return new Buffer<T>(buffer.length - 1);
    }
    
    public void removeAll()
    {
        counter = 0;
        firstIndex = 0;
        lastIndex = 0;
        
        for (int i = 0;i < buffer.length;i++) {
        	//Null the objects so they can be garbage collected:
        	buffer[i] = null;
        }
    }
}
