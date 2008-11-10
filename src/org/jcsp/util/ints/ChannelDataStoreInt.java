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

import org.jcsp.util.OverWriteOldestBuffer;
import org.jcsp.util.OverWritingBuffer;

/**
 * This is the interface for integer channel plug-ins that define their buffering
 * characteristics.
 * <H2>Description</H2>
 * <TT>ChannelDataStoreInt</TT> defines the interface to the logic used by
 * the integer channels defined in the <TT>org.jcsp.lang</TT> package to manage
 * the data being communicated.
 * <P>
 * This enables that logic to be varied by creating channels specifying
 * a particular implementation of this interface.  This reduces the number of
 * classes that would otherwise need to be defined.  The default channel
 * constructor (with no parameters) uses the <TT>ZeroBuffer</TT> implementation,
 * which gives the standard CSP semantics -- no buffering and full synchronisation
 * between reading and writing processes.
 * See the <tt>static</tt> construction methods of {@link org.jcsp.lang.Channel}
 * ({@link org.jcsp.lang.Channel#one2oneInt(org.jcsp.util.ints.ChannelDataStoreInt)} etc.).
 * <P>
 * <I>Note: instances of </I><TT>ChannelDataStoreInt</TT><I> implementations are
 * used by the various channel classes within </I><TT>org.jcsp.lang</TT><I>
 * in a thread-safe way.  They are not intended for any other purpose.  
 * Developers of new </I><TT>ChannelDataStoreInt</TT><I> implementations,
 * therefore, do not need to worry about thread safety (e.g. by making its
 * methods </I><TT>synchronized</TT><I>).  Also, developers can assume that
 * the documented pre-conditions for invoking the </I><TT>get</TT><I>
 * and </I><TT>put</TT><I> methods will be met.</I>
 *
 * @see org.jcsp.util.ints.ZeroBufferInt
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

public interface ChannelDataStoreInt extends Cloneable {

  /** Indicates that the <TT>ChannelDataStoreInt</TT> is empty
   * -- it can accept only a <TT>put</TT>.
   */
  public final static int EMPTY        = 0;

  /**
   * Indicates that the <TT>ChannelDataStoreInt</TT> is neither empty nor full
   * -- it can accept either a <TT>put</TT> or a <TT>get</TT> call.
   */
  public final static int NONEMPTYFULL = 1;

  /** Indicates that the <TT>ChannelDataStoreInt</TT> is full
   * -- it can accept only a <TT>get</TT>.
   */
  public final static int FULL         = 2;

  /**
   * Returns the current state of the <TT>ChannelDataStoreInt</TT>.
   *
   * @return the current state of the <TT>ChannelDataStoreInt</TT> (<TT>EMPTY</TT>,
   * <TT>NONEMPTYFULL</TT> or <TT>FULL</TT>)
   */
  public abstract int getState ();

  /**
   * Puts a new <TT>int</TT> into the <TT>ChannelDataStoreInt</TT>.
   * <P>
   * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>FULL</TT>.
   *
   * @param value the int to put into the ChannelDataStoreInt
   */
  public abstract void put (int value);

  /**
   * Returns an <TT>int</TT> from the <TT>ChannelDataStoreInt</TT>.
   * <P>
   * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>EMPTY</TT>.
   *
   * @return an <TT>int</TT> from the <TT>ChannelDataStoreInt</TT>
   */
  public abstract int get ();
  
  /**
   * Begins an extended read on the buffer, returning the data for the extended read
   * 
   * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>EMPTY</TT>.
   * 
   * The exact behaviour of this method depends on your buffer.  When a process performs an
   * extended rendezvous on a buffered channel, it will first call this method, then the
   * {@link #endGet} method.  
   * 
   * A FIFO buffer would implement this method as returning the value from the front of the buffer
   * and the next call would remove the value.  An overflowing buffer would do the same.
   * 
   * However, for an overwriting buffer it is more complex.  Refer to the documentation for
   * {@link OverWritingBuffer#startGet} and {@link OverWriteOldestBuffer#startGet}
   * for details  
   * 
   * @return The int to be read from the channel at the beginning of the extended rendezvous 
   *
   * @see #endGet
   */
  public abstract int startGet();
  
  /**
   * Ends an extended read on the buffer.
   * 
   * The channels guarantee that this method will be called exactly once after each beginExtRead call.
   * During the period between startGet and endGet, it is possible that {@link #put} will be called,
   * but not {@link #get}. 
   *
   * @see #startGet
   */
  public abstract void endGet();

  /**
   * Returns a new (and <TT>EMPTY</TT>) <TT>ChannelDataStoreInt</TT> with the same
   * creation parameters as this one.
   * <P>
   * <I>Note: Only the size and structure of the </I><TT>ChannelDataStoreInt</TT><I> should
   * be cloned, not any stored data.</I>
   *
   * @return the cloned instance of this <TT>ChannelDataStoreInt</TT>.
   */
  public abstract Object clone ();

  
  public abstract void removeAll();
}
