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

//{{{  javadoc
/**
 * This is used to create a zero-buffered object channel that never loses data.
 * <H2>Description</H2>
 * <TT>ZeroBuffer</TT> is an implementation of <TT>ChannelDataStore</TT> that yields
 * the standard <I><B>CSP</B></I> semantics for a channel -- that is zero buffered with
 * direct synchronisation between reader and writer.  Unless specified otherwise,
 * this is the default behaviour for channels.
 * See the <tt>static</tt> construction methods of {@link org.jcsp.lang.Channel}
 * ({@link org.jcsp.lang.Channel#one2one(org.jcsp.util.ChannelDataStore)} etc.).
 * <P>
 * The <TT>getState</TT> method will return <TT>FULL</TT> if there is an output
 * waiting on the channel and <TT>EMPTY</TT> if there is not.
 *
 * @see org.jcsp.util.Buffer
 * @see org.jcsp.util.OverWriteOldestBuffer
 * @see org.jcsp.util.OverWritingBuffer
 * @see org.jcsp.util.OverFlowingBuffer
 * @see org.jcsp.util.InfiniteBuffer
 * @see org.jcsp.lang.One2OneChannelImpl
 * @see org.jcsp.lang.Any2OneChannelImpl
 * @see org.jcsp.lang.One2AnyChannelImpl
 * @see org.jcsp.lang.Any2AnyChannelImpl
 *
 * @author P.D. Austin
 */
//}}}

public class ZeroBuffer<T> implements ChannelDataStore<T>, Serializable
{
    /** The current state */
    private int state = EMPTY;

    /** The Object */
    private T value;

    /**
     * Returns the <TT>Object</TT> from the <TT>ZeroBuffer</TT>.
     * <P>
     * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>EMPTY</TT>.
     *
     * @return the <TT>Object</TT> from the <TT>ZeroBuffer</TT>
     */
    public T get()
    {
        state = EMPTY;
        T o = value;
        value = null;
        return o;
    }
    
    /**
     * Begins an extended rendezvous - simply returns the next object in the buffer.  
     * This function does not remove the object.
     * 
     * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>EMPTY</TT>.
     * 
     * @return The object in the buffer. 
     */
    public T startGet() {
      return value;     
    }
    
    /**
     * Ends the extended rendezvous by clearing the buffer.
     */
    public void endGet() {
      value = null;
      state = EMPTY;      
    }

    /**
     * Puts a new <TT>Object</TT> into the <TT>ZeroBuffer</TT>.
     * <P>
     * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>FULL</TT>.
     *
     * @param value the Object to put into the ZeroBuffer
     */
    public void put(T value)
    {
        state = FULL;
        this.value = value;
    }

    /**
     * Returns the current state of the <TT>ZeroBuffer</TT>.
     *
     * @return the current state of the <TT>ZeroBuffer</TT> (<TT>EMPTY</TT>
     * or <TT>FULL</TT>)
     */
    public int getState()
    {
        return state;
    }

    /**
     * Returns a new (and <TT>EMPTY</TT>) <TT>ZeroBuffer</TT> with the same
     * creation parameters as this one.
     * <P>
     * <I>Note: Only the size and structure of the </I><TT>ZeroBuffer</TT><I> is
     * cloned, not any stored data.</I>
     *
     * @return the cloned instance of this <TT>ZeroBuffer</TT>.
     */
    public Object clone()
    {
        return new ZeroBuffer();
    }
    
    public void removeAll()
    {
    	state = EMPTY;
    	value = null;
    }
}
