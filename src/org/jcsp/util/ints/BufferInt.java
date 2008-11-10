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
 * This is used to create a buffered integer channel that never loses data.
 * <H2>Description</H2>
 * <TT>BufferInt</TT> is an implementation of <TT>ChannelDataStoreInt</TT> that yields
 * a blocking <I>FIFO</I> buffered semantics for a channel.
 * See the <tt>static</tt> construction methods of {@link org.jcsp.lang.Channel}
 * ({@link org.jcsp.lang.Channel#one2oneInt(org.jcsp.util.ints.ChannelDataStoreInt)} etc.).
 * <P>
 * The <TT>getState</TT> method returns <TT>EMPTY</TT>, <TT>NONEMPTYFULL</TT> or
 * <TT>FULL</TT> according to the state of the buffer.
 *
 * @see org.jcsp.util.ints.ZeroBufferInt
 * @see org.jcsp.util.ints.OverWriteOldestBufferInt
 * @see org.jcsp.util.ints.OverWritingBufferInt
 * @see org.jcsp.util.ints.OverFlowingBufferInt
 * @see org.jcsp.util.ints.InfiniteBufferInt
 * @see org.jcsp.lang.ChannelInt
 * 
 * @author P.D. Austin
 */

public class BufferInt implements ChannelDataStoreInt, Serializable
{
  /** The storage for the buffered ints */
  private final int[] buffer;
  
  /** The number of ints stored in the BufferInt */
  private int counter = 0;
  
  /** The index of the oldest element (when counter > 0) */
  private int firstIndex = 0;
  
  /** The index of the next free element (when counter < buffer.length) */
  private int lastIndex = 0;
  
  /**
   * Construct a new <TT>BufferInt</TT> with the specified size.
   *
   * @param size the number of ints the BufferInt can store.
   * @throws BufferIntSizeError if <TT>size</TT> is negative.  Note: no action
   * should be taken to <TT>try</TT>/<TT>catch</TT> this exception
   * - application code generating it is in error and needs correcting.
   */
  public BufferInt (int size) {
    if (size < 0) {
      throw new BufferIntSizeError (
        "\n*** Attempt to create a buffered channel with negative capacity"
      );
    }
    buffer = new int[size + 1];  // the extra one is a subtlety needed by
                                 // the current channel algorithms.
  }
  
  /**
   * Returns the oldest <TT>int</TT> from the <TT>BufferInt</TT> and removes it.
   * <P>
   * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>EMPTY</TT>.
   *
   * @return the oldest <TT>int</TT> from the <TT>BufferInt</TT>
   */
  public int get () {
    int value = buffer[firstIndex];
    firstIndex = (firstIndex + 1) % buffer.length;  
    counter--;
    return value;
  }
  
  /**
   * Returns the oldest integer from the buffer but does not remove it.
   * 
   * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>EMPTY</TT>.
   *
   * @return the oldest <TT>int</TT> from the <TT>Buffer</TT>
   */
  public int startGet()
  {
    return buffer[firstIndex];
  }
  
  /**
   * Removes the oldest integer from the buffer.     
   */
  public void endGet()
  {      
    firstIndex = (firstIndex + 1) % buffer.length;
    counter--;
  }

  /**
   * Puts a new <TT>int</TT> into the <TT>BufferInt</TT>.
   * <P>
   * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>FULL</TT>.
   *
   * @param value the int to put into the BufferInt
   */
  public void put (int value) { 
    buffer[lastIndex] = value;
    lastIndex = (lastIndex + 1) % buffer.length;  
    counter++;
  }

  /**
   * Returns the current state of the <TT>BufferInt</TT>.
   *
   * @return the current state of the <TT>BufferInt</TT> (<TT>EMPTY</TT>,
   * <TT>NONEMPTYFULL</TT> or <TT>FULL</TT>)
   */
  public int getState () {
    if (counter == 0) {
      return EMPTY;
    }
    else if (counter == buffer.length) {
      return FULL;
    } else {
    return NONEMPTYFULL;
    }
  }
  
  /**
   * Returns a new (and <TT>EMPTY</TT>) <TT>BufferInt</TT> with the same
   * creation parameters as this one.
   * <P>
   * <I>Note: Only the size and structure of the </I><TT>BufferInt</TT><I> is
   * cloned, not any stored data.</I>
   *
   * @return the cloned instance of this <TT>BufferInt</TT>
   */
  public Object clone () {
    return new BufferInt (buffer.length - 1);
  }

  public void removeAll() {
	  counter = 0;
	  firstIndex = 0;
	  lastIndex = 0;
  }
}
