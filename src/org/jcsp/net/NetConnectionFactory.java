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
 * This class should be implemented by classes wishing to act
 * as factories for creating Networked <code>ConnectionServer</code>
 * and <code>ConnectionClient</code> objects.
 * </p>
 * <p>
 * An implementation is provided, see
 * <code>{@link StandardNetConnectionFactory}</code>.
 * </p>
 * @author Quickstone Technologies Limited
 */
public interface NetConnectionFactory
{
   /**
    * <p>
    * Constructs a <code>NetAltingConnectionServer</code> object.
    * </p>
    *
    * @return the constructed <code>NetAltingConnectionServer</code> object.
    */
   public NetAltingConnectionServer createNet2One();
   
   /**
    * <p>
    * Constructs a <code>NetSharedConnectionServer</code> object.
    * </p>
    *
    * @return the constructed <code>NetSharedConnectionServer</code> object.
    */
   public NetSharedConnectionServer createNet2Any();
   
   /**
    * <p>
    * Constructs a <code>NetAltingConnectionClient</code> object.
    * </p>
    *
    * @return the constructed <code>NetAltingConnectionClient</code> object.
    */
   public NetAltingConnectionClient createOne2Net(NetChannelLocation serverLoc);
   
   /**
    * <p>
    * Constructs a <code>NetSharedAltingConnectionClient</code> object.
    * </p>
    *
    * @return the constructed <code>NetSharedAltingConnectionClient</code> object.
    */
   public NetSharedAltingConnectionClient createAny2Net(NetChannelLocation serverLoc);
}