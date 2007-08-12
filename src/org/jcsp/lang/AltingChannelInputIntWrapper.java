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

/**
 * This class wraps an ALTable int channel so that only the reading part is
 * available to the caller.  Writes are impossible unless you subclass
 * this (and use getChannel()) or keep a reference to the original
 * channel.  <p>
 *
 * Note that usually you do not need the absolute guarantee that this class
 * provides - you can usually just cast the channel to an AltingChannelInput,
 * which prevents you from <I>accidentally</I> writing to the channel.  This
 * class mainly exists for use by some of the jcsp.net classes, where the
 * absolute guarantee that you cannot write to it is important.
 *
 * This is adapted from AltingChannelInputWrapper.
 *
 * @see org.jcsp.lang.AltingChannelInput
 *
 * @author Quickstone Technologies Limited
 */
public class AltingChannelInputIntWrapper extends AltingChannelInputInt
{
    /**
     * Creates a new AltingChannelInputWrapper which wraps the specified
     * channel.
     */
    public AltingChannelInputIntWrapper(AltingChannelInputInt channel)
    {
        this.channel = channel;
    }

    /**
     * The real channel which this object wraps.
     */
    private final AltingChannelInputInt channel;

    /**
     * Get the real channel.
     *
     * @return The real channel.
     */
    protected AltingChannelInputInt getChannel()
    {
        return channel;
    }

    /**
     * Read an Object from the channel.
     *
     * @return the object read from the channel
     */
    public int read()
    {
        return channel.read();
    }
    
    public int startRead()
    {
      return channel.startRead();
    }
    
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
}
