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
 * <p>
 * This factory constructs buffered <code>NetChannelInput</code>
 * objects which have labelled VCN's.
 * </p>
 * <p>
 * See {@link NetLabelledChannelEndFactory} for an explanation of
 * labelled VCN's.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public interface NetLabelledBufferedChannelEndFactory
{
   /**
    * Constructs a buffered <code>NetAltingChannelInput</code> which
    * has a VCN assigned with the specified label.
    *
    * @param label the label to apply to the channel's VCN.
    * @param buffer the <code>ChannelDataStore</code> to use.
    *
    * @return the constructed <code>NetAltingChannelInput</code>
    *          object.
    */
   public NetAltingChannelInput createNet2One(String label, ChannelDataStore buffer);
   
   /**
    * Constructs a buffered <code>NetSharedChannelInput</code> which
    * has a VCN assigned with the specified label.
    *
    * @param label the label to apply to the channel's VCN.
    * @param buffer the <code>ChannelDataStore</code> to use.
    *
    * @return the constructed <code>NetSharedChannelInput</code>
    *          object.
    */
   public NetSharedChannelInput createNet2Any(String label, ChannelDataStore buffer);
}