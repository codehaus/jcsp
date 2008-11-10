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

/**
 * <p>
 * This factory constructs <code>NetChannelInput</code> objects
 * which have labelled VCN's.
 * </p>
 * <p>
 * JCSP.NET network channel addresses (signified by
 * <code>{@link NetChannelLocation}</code> objects) have a
 * Virtual Channel Number (VCN). This number is not exposed
 * to JCSP users but is an integral part of channel addressing.
 * </p>
 * <p>
 * If two Nodes have no means of communication, there is no
 * way that one can pass a <code>{@link NetChannelLocation}</code>
 * object to the other. This means that a channel cannot be
 * established using convential means. JCSP.NET solves this
 * problem by allowing VCN's to be labelled by the user.
 * If the address of a Node hosting a <code>ChannelInput</code>
 * with a known labelled VCN is known by a process in another Node,
 * then a <code>ChannelOutput</code> can be established by
 * constructing with a <code>NetChannelLocation</code> object constrcuted
 * using the <code>{@link
 * NetChannelLocation#NetChannelLocation(NodeAddressID, String)
 * }</code> constructor.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public interface NetLabelledChannelEndFactory
{
   /**
    * Constructs a <code>NetAltingChannelInput</code> which
    * has a VCN assigned with the specified label.
    *
    * @param label the label to apply to the channel's VCN.
    *
    * @return the constructed <code>NetAltingChannelInput</code>
    *          object.
    */
   public NetAltingChannelInput createNet2One(String label);
   
   /**
    * Constructs a <code>NetSharedChannelInput</code> which
    * has a VCN assigned with the specified label.
    *
    * @param label the label to apply to the channel's VCN.
    *
    * @return the constructed <code>NetSharedChannelInput</code>
    *          object.
    */
   public NetSharedChannelInput createNet2Any(String label);
}