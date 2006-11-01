    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2006 Peter Welch and Paul Austin.            //
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
    //                                                                  //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

package org.jcsp.lang;

import java.io.Serializable;
import org.jcsp.util.ChannelDataStore;

/**
 * This implements a one-to-any object channel,
 * safe for use by one writer and many readers.
 * <H2>Description</H2>
 * <TT>One2AnyChannel</TT> is an implementation of a channel which is safe
 * for use by many reading processes but only one writer.  Reading processes
 * compete with each other to use the channel.  Only one reader and the writer will
 * actually be using the channel at any one time.  This is taken care of by
 * <TT>One2AnyChannel</TT> -- user processes just read from or write to it.
 * <P>
 * <I>Please note that this is a safely shared channel and not
 * a broadcaster.  Currently, broadcasting has to be managed by
 * writing active processes (see {@link org.jcsp.plugNplay.DynamicDelta}
 * for an example).</I>
 * <P>
 * All reading processes and the writing process commit to the channel
 * (i.e. may not back off).  This means that the reading processes
 * <I>may not</I> {@link Alternative <TT>ALT</TT>} on this channel.
 * <P>
 * The default semantics of the channel is that of CSP -- i.e. it is
 * zero-buffered and fully synchronised.  A reading process must wait
 * for the matching writer and vice-versa.
 * <P>
 * A factory pattern is used to create channel instances. The <tt>create</tt> methods of {@link Channel}
 * allow creation of channels, arrays of channels and channels with varying semantics such as
 * buffering with a user-defined capacity or overwriting with various policies.
 * Standard examples are given in the <TT>org.jcsp.util</TT> package, but
 * <I>careful users</I> may write their own.
 *
 * <H3><A NAME="Caution">Implementation Note and Caution</H3>
 * <I>Fair</I> servicing of readers to this channel depends on the <I>fair</I>
 * servicing of requests to enter a <TT>synchronized</TT> block (or method) by
 * the underlying Java Virtual Machine (JVM).  Java does not specify how threads
 * waiting to synchronize should be handled.  Currently, Sun's standard JDKs queue
 * these requests - which is <I>fair</I>.  However, there is at least one JVM
 * that puts such competing requests on a stack - which is legal but <I>unfair</I>
 * and can lead to infinite starvation.  This is a problem for <I>any</I> Java system
 * relying on good behaviour from <TT>synchronized</TT>, not just for these
 * <I>1-any</I> channels.
 *
 * @see org.jcsp.lang.One2OneChannel
 * @see org.jcsp.lang.Any2OneChannel
 * @see org.jcsp.lang.Any2AnyChannel
 * @see org.jcsp.util.ChannelDataStore
 *
 * @author P.D.Austin and P.H.Welch
 */

class One2AnyChannelImpl implements ChannelOutput, SharedChannelInput, One2AnyChannel, Serializable
{
    /**
     * WARNING:
     * RejectableOne2AnyChannel extends this class and depends
     * heavily on the algorithms used.
     * Do not modify without checking that class first.
     * 
     * NCCB: I have changed this algorithm with the spurious wake-up code from Peter.  
     * From what I can make out in the RejectableOne2AnyChannel (the concept of which
     * looks a lot like one-off poison), this shouldn't break it.
     */

	/** The monitor synchronising reader and writer on this channel */
	  protected Object rwMonitor = new Object ();

	  /** The (invisible-to-users) buffer used to store the data for the channel */
	  private Object hold;

	  /** The synchronisation flag */
	  private boolean empty = true;

	  /** The monitor on which readers must synchronize */
	  protected final Object readMonitor = new Object ();

	  /** Flag to deal with a spurious wakeup during a write */
	  private boolean spuriousWakeUp = true;

    /*************Methods from One2AnyChannel******************************/

	  /**
     * Returns the <code>SharedChannelInput</code> to use for this channel.
     * As <code>One2AnyChannelImpl</code> implements
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
     * Returns the <code>ChannelOutput</code> object to use for this channel.
     * As <code>One2AnyChannelImpl</code> implements
     * <code>ChannelOutput</code> itself, this method simply returns
     * a reference to the object that it is called on.
     *
     * @return the <code>ChannelOutput</code> object to use for this
     *          channel.
     */
    public ChannelOutput out()
    {
        return this;
    }

    /*************Methods from ChannelOutput*******************************/

    /**
	   * Reads an <TT>Object</TT> from the channel.
	   *
	   * @return the object read from the channel.
	   */
	  public Object read () {
	    synchronized (readMonitor) {
	      synchronized (rwMonitor) {
	        if (empty) {
	          empty = false;
	          try {
	            rwMonitor.wait ();
		    while (!empty) {
		      if (Spurious.logging) {
		        SpuriousLog.record (SpuriousLog.One2AnyChannelRead);
		      }
		      rwMonitor.wait ();
		    }
	          }
	          catch (InterruptedException e) {
	            throw new ProcessInterruptedException (
	              "*** Thrown from One2AnyChannel.read ()\n" + e.toString ()
	            );
	          }
	        } else {
	          empty = true;
	        }
	        spuriousWakeUp = false;
	        rwMonitor.notify ();
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
	  public void write (Object value) {
	    synchronized (rwMonitor) {
	      hold = value;
	      if (empty) {
	        empty = false;
	      } else {
	        empty = true;
	        rwMonitor.notify ();
	      }
	      try {
	        rwMonitor.wait ();
		while (spuriousWakeUp) {
		  if (Spurious.logging) {
		    SpuriousLog.record (SpuriousLog.One2AnyChannelWrite);
		  }
		  rwMonitor.wait ();
		}
		spuriousWakeUp = true;
	      }
	      catch (InterruptedException e) {
	        throw new ProcessInterruptedException (
	          "*** Thrown from One2AnyChannel.write (Object)\n" + e.toString ()
	        );
	      }
	    }
	  }

    /**
     * Creates an array of One2AnyChannel.
     *
     * @param n the number of channels to create in the array
     * @return the array of One2AnyChannel
     */
    public static One2AnyChannel[] create(int n)
    {
        One2AnyChannel[] channels = new One2AnyChannel[n];
        for (int i = 0; i < n; i++)
            channels[i] = new One2AnyChannelImpl();
        return channels;
    }

    /**
     * Creates a One2AnyChannel using the specified ChannelDataStore.
     *
     * @return the One2AnyChannel
     */
    public static One2AnyChannel create(ChannelDataStore store)
    {
        return new BufferedOne2AnyChannel(store);
    }

    /**
     * Creates an array of One2AnyChannel using the specified ChannelDataStore.
     *
     * @param n the number of channels to create in the array
     * @deprecated use methods in Channel class instead.
     * {@link org.jcsp.lang.Channel#createOne2Any(ChannelDataStore, int)}
     * @return the array of One2AnyChannel
     */
    public static One2AnyChannel[] create(int n, ChannelDataStore store)
    {
        One2AnyChannel[] channels = new One2AnyChannel[n];
        for (int i = 0; i < n; i++)
            channels[i] = new BufferedOne2AnyChannel(store);
        return channels;
    }
}
