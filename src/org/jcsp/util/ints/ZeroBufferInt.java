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

package org.jcsp.util.ints;

import java.io.Serializable;

/**
 * This is used to create a zero-buffered integer channel that never loses data.
 * <H2>Description</H2>
 * <TT>ZeroBufferInt</TT> is an implementation of <TT>ChannelDataStoreInt</TT> that yields
 * the standard <I><B>CSP</B></I> semantics for a channel -- that is zero buffered with
 * direct synchronisation between reader and writer.  Unless specified otherwise,
 * this is the default behaviour for channels.
 * See the <tt>static</tt> construction methods of {@link org.jcsp.lang.Channel}
 * ({@link org.jcsp.lang.Channel#one2oneInt(org.jcsp.util.ints.ChannelDataStoreInt)} etc.).
 * <P>
 * The <TT>getState</TT> method will return <TT>FULL</TT> if there is an output
 * waiting on the channel and <TT>EMPTY</TT> if there is not.
 *
 * @see org.jcsp.util.ints.BufferInt
 * @see org.jcsp.util.ints.OverWriteOldestBufferInt
 * @see org.jcsp.util.ints.OverWritingBufferInt
 * @see org.jcsp.util.ints.OverFlowingBufferInt
 * @see org.jcsp.util.ints.InfiniteBufferInt
 * @see org.jcsp.lang.ChannelInt
 *
 * @author P.D. Austin
 */
//}}}

public class ZeroBufferInt implements ChannelDataStoreInt, Serializable
{
    /** The current state */
    private int state = EMPTY;

    /** The int */
    private int value;

    /**
     * Returns the <TT>int</TT> from the <TT>ZeroBufferInt</TT>.
     * <P>
     * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>EMPTY</TT>.
     *
     * @return the <TT>int</TT> from the <TT>ZeroBufferInt</TT>
     */
    public int get()
    {
        state = EMPTY;
        int o = value;
        return o;
    }
    
    /**
     * Begins an extended rendezvous - simply returns the next integer in the buffer.  
     * This function does not remove the integer.
     * 
     * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>EMPTY</TT>.
     * 
     * @return The integer in the buffer. 
     */
    public int startGet() {
      return value;     
    }
    
    /**
     * Ends the extended rendezvous by clearing the buffer.
     */
    public void endGet() {      
      state = EMPTY;      
    }

    /**
     * Puts a new <TT>int</TT> into the <TT>ZeroBufferInt</TT>.
     * <P>
     * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>FULL</TT>.
     *
     * @param value the int to put into the ZeroBufferInt
     */
    public void put(int value)
    {
        state = FULL;
        this.value = value;
    }

    /**
     * Returns the current state of the <TT>ZeroBufferInt</TT>.
     *
     * @return the current state of the <TT>ZeroBufferInt</TT> (<TT>EMPTY</TT>
     * or <TT>FULL</TT>)
     */
    public int getState()
    {
        return state;
    }

    /**
     * Returns a new (and <TT>EMPTY</TT>) <TT>ZeroBufferInt</TT> with the same
     * creation parameters as this one.
     * <P>
     * <I>Note: Only the size and structure of the </I><TT>ZeroBufferInt</TT><I> is
     * cloned, not any stored data.</I>
     *
     * @return the cloned instance of this <TT>ZeroBufferInt</TT>.
     */
    public Object clone()
    {
        return new ZeroBufferInt();
    }
    
    public void removeAll()
    {
    	state = EMPTY;
    }    
}
