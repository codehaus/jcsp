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
 * This extends {@link Guard} and {@link ChannelAccept}
 * to enable a process to choose between many CALL channel (and other) events.
 * <H2>Description</H2>
 * <TT>AltingChannelAccept</TT> extends {@link Guard} and {@link ChannelAccept}
 * to enable a process
 * to choose between many CALL channel (and other) events.  The methods inherited from
 * <TT>Guard</TT> are of no concern to users of this package.
 *
 * <H2>Example</H2>
 * See the explanations and examples documented in {@link One2OneCallChannel} and
 * {@link Any2OneCallChannel}.
 *
 * @see org.jcsp.lang.Alternative
 * @see org.jcsp.lang.One2OneCallChannel
 * @see org.jcsp.lang.Any2OneCallChannel
 *
 * @author P.H. Welch
 */

public abstract class AltingChannelAccept extends Guard implements ChannelAccept
{
  // nothing to add ...
}
