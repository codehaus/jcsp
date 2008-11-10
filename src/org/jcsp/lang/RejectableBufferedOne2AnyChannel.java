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

import org.jcsp.util.ChannelDataStore;

/**
 * <p>This implements a one-to-any object channel with user-definable buffering,
 * safe for use by a single writer and many readers. Refer to {@link One2AnyChannel} for a
 * description of this behaviour.</p>
 *
 * <p>Additionally, this channel supports a <code>reject</code> operation. One of the readers may call
 * the reject method to force any current writer to abort with a
 * <code>ChannelDataRejectedException</code> (unless there is already a read which will cause
 * completion of the write). Subsequent read and write attempts will immediately cause a
 * <code>ChannelDataRejectedException</code>.</p>
 *
 * @author Quickstone Technologies Limited
 * 
 * @deprecated This channel is superceded by the poison mechanisms, please see {@link PoisonException}
 */
public class RejectableBufferedOne2AnyChannel implements RejectableChannel
{
	BufferedOne2AnyChannel innerChannel;
	
    /**
     * Constructs a new <code>RejectableBufferedOne2AnyChannel</code>
     *
     * @param data the buffer implementation to use.
     */
    public RejectableBufferedOne2AnyChannel(ChannelDataStore data)
    {
        innerChannel = (BufferedOne2AnyChannel)Channel.one2any(data);
    }
    
	public RejectableChannelInput in() {
		return new RejectableChannelInputImpl(innerChannel,0);
	}

	public RejectableChannelOutput out() {
		return new RejectableChannelOutputImpl(innerChannel,0);
	}

}
