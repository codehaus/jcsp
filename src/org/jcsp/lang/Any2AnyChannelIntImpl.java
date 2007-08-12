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

import org.jcsp.util.ints.ChannelDataStoreInt;
import java.io.Serializable;

/**
 * This implements an any-to-any integer channel,
 * safe for use by many writers and many readers. Refer to the {@link Any2AnyChannelInt} interface
 * for more details.
 *
 * @see org.jcsp.lang.One2OneChannelImpl
 * @see org.jcsp.lang.Any2OneChannelImpl
 * @see org.jcsp.lang.One2AnyChannelImpl
 *
 * @author P.D.Austin and P.H.Welch
 */

class Any2AnyChannelIntImpl implements SharedChannelInputInt, SharedChannelOutputInt, Any2AnyChannelInt, Serializable
{
	  /** The monitor synchronising reader and writer on this channel */
	  private final Object rwMonitor = new Object ();

	  /** The (invisible-to-users) buffer used to store the data for the channel */
	  private int hold;

	  /** The synchronisation flag */
	  private boolean empty = true;

	  /** The monitor on which readers must synchronize */
	  private final Mutex readMutex = new Mutex ();

	  /** The monitor on which writers must synchronize */
	  private final Object writeMonitor = new Object ();

	  /** Flag to deal with a spurious wakeup during a write */
	  private boolean spuriousWakeUp = true;
      
      
	  
	  /*************Methods from Any2AnyChannel******************************/

	  /**
     * Returns the <code>SharedChannelInputInt</code> object to use for this
     * channel. As <code>Any2AnyChannelIntImpl</code> implements
     * <code>SharedChannelInputInt</code> itself, this method simply returns
     * a reference to the object that it is called on.
     *
     * @return the <code>SharedChannelInputInt</code> object to use for this
     *          channel.
     */
    public SharedChannelInputInt in()
    {
        return this;
    }

    /**
     * Returns the <code>SharedChannelOutputInt</code> object to use for this
     * channel. As <code>Any2AnyChannelIntImpl</code> implements
     * <code>SharedChannelOutputInt</code> itself, this method simply returns
     * a reference to the object that it is called on.
     *
     * @return the <code>SharedChannelOutputInt</code> object to use for this
     *          channel.
     */
    public SharedChannelOutputInt out()
    {
        return this;
    }

    /**********************************************************************/


    /**
	   * Reads an <TT>int</TT> from the channel.
	   *
	   * @return the integer read from the channel.
	   */
	  public int read () {
        int retValue;
	    readMutex.claim();
	      synchronized (rwMonitor) {
	        if (empty) {
	          empty = false;
	          try {
	            rwMonitor.wait ();
		    while (!empty) {
		      if (Spurious.logging) {
		        SpuriousLog.record (SpuriousLog.Any2AnyChannelIntRead);
		      }
		      rwMonitor.wait ();
		    }
	          }
	          catch (InterruptedException e) {
	            throw new ProcessInterruptedException (
	              "*** Thrown from Any2AnyChannelInt.read ()\n" + e.toString ()
	            );
	          }
	        } else {
	          empty = true;
	        }
	        spuriousWakeUp = false;
	        rwMonitor.notify ();
	        retValue = hold;
	      }
	    readMutex.release();
        return retValue;
	  }
      
      public int startRead () {        
          readMutex.claim();
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
              
              return hold;
            }        
          //We don't release the readMutex until endExtRead  
        }
        
        public void endRead() {
          synchronized (rwMonitor) {
            spuriousWakeUp = false;
            rwMonitor.notify ();
            readMutex.release();
          }
        }

	  /**
	   * Writes an <TT>int</TT> to the Channel. This method also ensures only one
	   * of the writers can actually be writing at any time. All other writers
	   * are blocked until it completes the write.
	   *
	   * @param value The integer to write to the Channel.
	   */
	  public void write (int value) {
	    synchronized (writeMonitor) {
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
		      SpuriousLog.record (SpuriousLog.Any2AnyChannelIntWrite);
		    }
		    rwMonitor.wait ();
		  }
		  spuriousWakeUp = true;
	        }
	        catch (InterruptedException e) {
	          throw new ProcessInterruptedException (
	            "*** Thrown from Any2AnyChannelInt.write (int)\n" + e.toString ()
	          );
	        }
	      }
	    }
	  }

    /**
     * Creates an array of Any2AnyChannelIntImpl.
     *
     * @param n the number of channels to create in the array
     * @return the array of Any2AnyChannelIntImpl
     */
    public static Any2AnyChannelInt[] create(int n)
    {
        Any2AnyChannelIntImpl[] channels = new Any2AnyChannelIntImpl[n];
        for (int i = 0; i < n; i++)
            channels[i] = new Any2AnyChannelIntImpl();
        return channels;
    }

    /**
     * Creates a Any2AnyChannelIntImpl using the specified ChannelDataStoreInt.
     *
     * @return the Any2AnyChannelIntImpl
     */
    public static Any2AnyChannelInt create(ChannelDataStoreInt store)
    {
        return new BufferedAny2AnyChannelIntImpl(store);
    }

    /**
     * Creates an array of Any2AnyChannelIntImpl using the specified ChannelDataStoreInt.
     *
     * @param n the number of channels to create in the array
     * @return the array of Any2AnyChannelIntImpl
     */
    public static Any2AnyChannelInt[] create(int n, ChannelDataStoreInt store)
    {
        Any2AnyChannelInt[] channels = new Any2AnyChannelIntImpl[n];
        for (int i = 0; i < n; i++)
            channels[i] = new BufferedAny2AnyChannelIntImpl(store);
        return channels;
    }

}
