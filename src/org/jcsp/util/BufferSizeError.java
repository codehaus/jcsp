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

package org.jcsp.util;

/**
 * This is thrown if an attempt is made to create some variety of buffered channel
 * with a zero or negative sized buffer.
 *
 * <H2>Description</H2>
 * Buffered channels must have (usually non-zero) positive sized buffers.
 * The following constructions will all throw this {@link java.lang.Error}:
 * <pre>
 *   One2OneChannel c = Channel.one2one (new Buffer (-42));                 // must be &gt;= 0
 *   One2OneChannel c = Channel.one2one (new OverFlowingBuffer (-42));      // must be &gt; 0
 *   One2OneChannel c = Channel.one2one (new OverWriteOldestBuffer (-42));  // must be &gt; 0
 *   One2OneChannel c = Channel.one2one (new OverWritingBuffer (-42));      // must be &gt; 0
 *   One2OneChannel c = Channel.one2one (new InfiniteBuffer (-42));         // must be &gt; 0
 * </pre>
 * Zero-buffered non-overwriting channels are, of course, the default channel semantics.
 * The following constructions are all legal and equivalent:
 * <pre>
 *   One2OneChannel c = Channel.one2one ();
 *   One2OneChannel c = Channel.one2one (new ZeroBuffer ());    // less efficient
 *   One2OneChannel c = Channel.one2one (new Buffer (0));       // less efficient
 * </pre>
 * No action should be taken to catch <TT>BufferSizeError</TT>.
 * Application code generating it is in error and needs correcting.
 *
 * @author P.H. Welch
 */

public class BufferSizeError extends Error
{
    public BufferSizeError (String s)
    {
        super (s);
    }
}
