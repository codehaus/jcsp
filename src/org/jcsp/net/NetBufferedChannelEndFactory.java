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

import org.jcsp.util.ChannelDataStore;

/**
 * This interface defines methods for constructing buffered
 * Networked <code>ChannelInput</code> objects.
 *
 * @author Quickstone Technologies Limited
 */
public interface NetBufferedChannelEndFactory
{
   /**
    * Constructs a <code>NetAltingChannelInput</code> object.
    *
    * @param buffer  the <code>ChannelDataStore</code> to use as a buffer.
    * @return the constructed <code>NetAltingChannelInput</code> object.
    */
   public NetAltingChannelInput createNet2One(ChannelDataStore buffer);
   
   /**
    * Constructs a <code>NetSharedChannelInput</code> object.
    *
    * @param buffer  the <code>ChannelDataStore</code> to use as a buffer.
    * @return the constructed <code>NetSharedChannelInput</code> object.
    */
   public NetSharedChannelInput createNet2Any(ChannelDataStore buffer);
   
}