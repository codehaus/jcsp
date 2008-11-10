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

import org.jcsp.util.*;

/**
 * <p>This implements a one-to-one object channel with user-definable buffering,
 * for use by a single writer and single reader. Refer to {@link One2OneChannel} for a
 * description of this behaviour.</p>
 *
 * <p>Additionally, this channel supports a <code>reject</code> operation. The reader may call
 * the reject method to force any current writer to abort with a
 * <code>ChannelDataRejectedException</code>. Subsequent read and write attempts will immediately cause a
 * <code>ChannelDataRejectedException</code>.</p>
 *
 * <p>Note that the <code>reject</code> operation cannot be called concurrently to a read.</p>
 *
 * @author Quickstone Technologies Limited
 * 
 * @deprecated This channel is superceded by the poison mechanisms, please see {@link PoisonException}
 */
public class RejectableBufferedOne2OneChannel        
        implements RejectableChannel
{    
	BufferedOne2OneChannel innerChannel;	
	
    /**
     * Constructs a new channel.
     *
     * @param buffer the buffer implementation to use.
     */
    public RejectableBufferedOne2OneChannel(ChannelDataStore buffer)
    {
        innerChannel = (BufferedOne2OneChannel)Channel.one2one(buffer);
    }

	public RejectableAltingChannelInput inAlt() {
		return new RejectableAltingChannelInputImpl(innerChannel,0);
	}
	
	public RejectableChannelInput in() {
		return new RejectableChannelInputImpl(innerChannel,0);
	}

	public RejectableChannelOutput out() {
		return new RejectableChannelOutputImpl(innerChannel,0);
	}

}
