    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2001 Peter Welch and Paul Austin.            //
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
    //                  mailbox@quickstone.com                          //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

package org.jcsp.util.ints;

/**
 * This is thrown if an attempt is made to create some variety of buffered channel
 * with a zero or negative sized buffer.
 *
 * <H2>Description</H2>
 * Buffered channels must have (usually non-zero) positive sized buffers.
 * The following constructions will all throw this {@link java.lang.Error}:
 * <pre>
 *   One2OneChannelInt c = ChannelInt.createOne2One
 *                     (new BufferInt (-42));
 *                     // zero allowed
 *   One2OneChannelInt c = ChannelInt.createOne2One
 *                     (new OverFlowingBufferInt (-42));
 *                     // zero not allowed
 *   One2OneChannelInt c = ChannelInt.createOne2One
 *                     (new OverWriteOldestBufferInt (-42));
 *                     // zero not allowed
 *   One2OneChannelInt c = ChannelInt.createOne2One
 *                     (new OverWritingBufferInt (-42));
 *                     // zero not allowed
 *   One2OneChannelInt c = ChannelInt.createOne2One
 *                     (new InfiniteBufferInt (-42));
 *                     // zero not allowed
 * </pre>
 * Zero-buffered non-overwriting channels are, of course, the default channel semantics.
 * The following constructions are all legal and equivalent:
 * <pre>
 *   One2OneChannelInt c = ChannelInt.createOne2One ();
 *   One2OneChannelInt c = ChannelInt.createOne2One (new ZeroBufferInt ());
 *   One2OneChannelInt c = ChannelInt.createOne2One (new BufferInt (0));
 *   // legal but less efficient
 * </pre>
 * No action should be taken to catch <TT>BufferSizeError</TT>.
 * Application code generating it is in error and needs correcting.
 *
 * @author P.H.Welch
 */

public class BufferIntSizeError extends Error
{
    /**
     * Constructs a new <code>BufferIntSizeError</code> with the specified detail message.
     *
     * @param s the detail message.
     */
    public BufferIntSizeError(String s)
    {
        super(s);
    }
}
