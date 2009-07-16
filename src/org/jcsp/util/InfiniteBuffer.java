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
 * This is used to create a buffered object channel that always accepts and
 * never loses any input.
 * <H2>Description</H2>
 * <TT>InfiniteBuffer</TT> is an implementation of <TT>ChannelDataStore</TT> that yields
 * a <I>FIFO</I> buffered semantics for a channel.  When empty, the channel blocks readers.
 * However, its capacity is <I>infinite</I> (expanding to whatever is needed so far as
 * the underlying memory system will permit).  So, it <I>never</I> gets full and blocks
 * a writer.
 * See the <tt>static</tt> construction methods of {@link org.jcsp.lang.Channel}
 * ({@link org.jcsp.lang.Channel#one2one(org.jcsp.util.ChannelDataStore)} etc.).
 * <P>
 * The <TT>getState</TT> method returns <TT>EMPTY</TT> or <TT>NONEMPTYFULL</TT>, but
 * never <TT>FULL</TT>.
 * <P>
 * An initial size for the buffer can be specified during construction.
 *
 * @see org.jcsp.util.ZeroBuffer
 * @see org.jcsp.util.Buffer
 * @see org.jcsp.util.OverWriteOldestBuffer
 * @see org.jcsp.util.OverWritingBuffer
 * @see org.jcsp.util.OverFlowingBuffer
 * @see org.jcsp.lang.Channel
 *
 * @author P.D. Austin
 */

public class InfiniteBuffer<T> implements ChannelDataStore<T>, Serializable
{
    /** The default size of the buffer */
    private static final int DEFAULT_SIZE = 8;

    /** The initial size of the buffer */
    private int initialSize;

    /** The storage for the buffered Objects */
    private T[] buffer;

    /** The number of Objects stored in the InfiniteBuffer */
    private int counter = 0;

    /** The index of the oldest element (when counter > 0) */
    private int firstIndex = 0;

    /** The index of the next free element (when counter < buffer.length) */
    private int lastIndex = 0;

    /**
     * Construct a new <TT>InfiniteBuffer</TT> with the default size (of 8).
     */
    public InfiniteBuffer()
    {
        this(DEFAULT_SIZE);
    }

    /**
     * Construct a new <TT>InfiniteBuffer</TT> with the specified initial size.
     *
     * @param initialSize the number of Objects
     * the <TT>InfiniteBuffer</TT> can initially store.
     * @throws BufferSizeError if <TT>size</TT> is zero or negative.  Note: no action
     * should be taken to <TT>try</TT>/<TT>catch</TT> this exception
     * - application code generating it is in error and needs correcting.
     */
    public InfiniteBuffer(int initialSize)
    {
        if (initialSize <= 0)
            throw new BufferSizeError
                    ("\n*** Attempt to create a buffered channel with an initially negative or zero capacity");
        this.initialSize = initialSize;
        buffer = (T[]) new Object[initialSize];
    }

    /**
     * Returns the oldest <TT>Object</TT> from the <TT>InfiniteBuffer</TT> and removes it.
     * <P>
     * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>EMPTY</TT>.
     *
     * @return the oldest <TT>Object</TT> from the <TT>InfiniteBuffer</TT>
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
     * Puts a new <TT>Object</TT> into the <TT>InfiniteBuffer</TT>.
     * <P>
     * <I>Implementation note:</I> if <TT>InfiniteBuffer</TT> is full, a new internal
     * buffer with double the capacity is constructed and the old data copied across.
     *
     * @param value the Object to put into the InfiniteBuffer
     */
    public void put(T value)
    {
        if (counter == buffer.length)
        {
            T[] temp = buffer;
            buffer = (T[]) new Object[buffer.length * 2];
            System.arraycopy(temp, firstIndex, buffer, 0, temp.length - firstIndex);
            System.arraycopy(temp, 0, buffer, temp.length - firstIndex, firstIndex);
            firstIndex = 0;
            lastIndex = temp.length;
        }
        buffer[lastIndex] = value;
        lastIndex = (lastIndex + 1) % buffer.length;
        counter++;
    }

    /**
     * Returns the current state of the <TT>InfiniteBuffer</TT>.
     *
     * @return the current state of the <TT>InfiniteBuffer</TT> (<TT>EMPTY</TT> or
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
     * Returns a new (and <TT>EMPTY</TT>) <TT>InfiniteBuffer</TT> with the same
     * creation parameters as this one.
     * <P>
     * <I>Note: Only the initial size and structure of the </I><TT>InfiniteBuffer</TT><I>
     * is cloned, not any stored data.</I>
     *
     * @return the cloned instance of this <TT>InfiniteBuffer</TT>.
     */
    public Object clone()
    {
        return new InfiniteBuffer(initialSize);
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
