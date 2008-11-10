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

package org.jcsp.net.dynamic;

import java.io.Serializable;
import org.jcsp.net.*;
import org.jcsp.util.filter.FilteredChannelInput;

/**
 * A migratable input channel end that can be used as a guard within an <code>Alternative</code>.
 *
 * @author Quickstone Technologies Limited
 */
public abstract class MigratableAltingChannelInput 
extends NetAltingChannelInput 
implements MigratableChannelInput, FilteredChannelInput, Serializable
{
   /**
    * Constructs a new <code>MigratableAltingChannelInput</code> for the underlying channel.
    *
    * @param channel the underlying channel.
    */
   protected MigratableAltingChannelInput(NetAltingChannelInput channel)
   {
      super(channel);
   }
   
   /**
    * Constructs a new <code>MigratableAltingChannelInput</code>.
    */
   protected MigratableAltingChannelInput()
   {
      super();
   }
}