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

package org.jcsp.net;

import org.jcsp.lang.*;

/**
 * <p>
 * An abstract class that is sub-classed by classes whose instances
 * should be networked channel ends that can be used as an
 * <code>{@link AltingChannelInput}</code> objects.
 * </p>
 * <p>
 * This class does not need to be sub-classed by JCSP users.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public abstract class NetAltingChannelInput extends AltingChannelInputWrapper implements NetChannelInput
{
   /**
    * <p>
    * Constructs a channel end and takes the actual channel to use
    * to deliver the data.
    * </p>
    *
    * @param channel the actual channel used to deliver data to the user.
    */
   protected NetAltingChannelInput(AltingChannelInput channel)
   {
      super(channel);
   }
   
   /**
    * <p>
    * Constructs a channel end without supplying the actual channel to
    * use. <code>setChannel(AltingChannelInput)</code> should be called
    * before the channel end is actually used.
    * </p>
    */
   protected NetAltingChannelInput()
   {
   }
}