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

package org.jcsp.lang;

/**
 * This class wraps an ALTable channel so that only the reading part is
 * available to the caller.  Writes are impossible unless you subclass
 * this (and use getChannel()) or keep a reference to the original
 * channel.  <p>
 *
 * @deprecated There is no longer any need to use this class, after the 1.1 class reorganisation.
 *
 * Note that usually you do not need the absolute guarantee that this class
 * provides - you can usually just cast the channel to an AltingChannelInput,
 * which prevents you from <I>accidentally</I> writing to the channel.  This
 * class mainly exists for use by some of the org.jcsp.net classes, where the
 * absolute guarantee that you cannot write to it is important.
 *
 * @see org.jcsp.lang.AltingChannelInput
 *
 * @author Quickstone Technologies Limited
 */
public class AltingChannelInputWrapper<T> extends AltingChannelInput<T>
{
    /**
     * Creates a new AltingChannelInputWrapper which wraps the specified
     * channel.
     */
    public AltingChannelInputWrapper(AltingChannelInput<T> channel)
    {
        this.channel = channel;
    }

    /**
     * This constructor does not wrap a channel.
     * The underlying channel can be set by calling
     * <code>setChannel(AltingChannelInput)</code>.
     *
     */
    protected AltingChannelInputWrapper()
    {
        this.channel = null;
    }

    /**
     * The real channel which this object wraps.
     *
     * This used to be a final field but this caused problems
     * when sub-classes wanted to be serializable. Added a
     * protected mutator.
     */
    private AltingChannelInput<T> channel;

    /**
     * Get the real channel.
     *
     * @return The real channel.
     */
    protected AltingChannelInput getChannel()
    {
        return channel;
    }

    /**
     * Sets the real channel to be used.
     *
     * @param chan the real channel to be used.
     */
    protected void setChannel(AltingChannelInput<T> chan)
    {
        this.channel = chan;
    }

    /**
     * Read an Object from the channel.
     *
     * @return the object read from the channel
     */
    public T read()
    {
        return channel.read();
    }
    
    /**
     * Begins an extended rendezvous
     * 
     * @see ChannelInput.startRead
     * @return The object read from the channel
     */
    public T startRead()
    {
    	return channel.startRead();
    }
    
    /**
     * Ends an extended rendezvous
     * 
     * @see ChannelInput.endRead
     */
    public void endRead()
    {
    	channel.endRead();
    }
    
    

    /**
     * Returns whether there is data pending on this channel.
     * <P>
     * <I>Note: if there is, it won't go away until you read it.  But if there
     * isn't, there may be some by the time you check the result of this method.</I>
     *
     * @return state of the channel.
     */
    public boolean pending()
    {
        return channel.pending();
    }

    /**
     * Returns true if the event is ready.  Otherwise, this enables the guard
     * for selection and returns false.
     * <P>
     * <I>Note: this method should only be called by the Alternative class</I>
     *
     * @param alt the Alternative class that is controlling the selection
     * @return true if and only if the event is ready
     */
    boolean enable(Alternative alt)
    {
        return channel.enable(alt);
    }

    /**
     * Disables the guard for selection. Returns true if the event was ready.
     * <P>
     * <I>Note: this method should only be called by the Alternative class</I>
     *
     * @return true if and only if the event was ready
     */
    boolean disable()
    {
        return channel.disable();
    }

	public void poison(int strength) 
	{
		channel.poison(strength);	
	}
    
    
}
