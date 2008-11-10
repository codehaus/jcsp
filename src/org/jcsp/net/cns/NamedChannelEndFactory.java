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

package org.jcsp.net.cns;

import org.jcsp.net.*;

/**
 * <p>
 * Classes implementing this interface act as factories for constructing
 * <code>NetChannelInput</code> and <code>NetChannelOutput</code> objects.
 * </p>
 * <p>
 * <code>NetChannelInput</code> objects are constructed and have their
 * location registered with a channel naming service.
 * </p>
 * <p>
 * <code>NetChannelOutput</code> objects are constructed and connected
 * to <code>NetChannelInput</code> objects whose location is resolved from
 * a channel naming service.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public interface NamedChannelEndFactory
{
   /**
    * Constructs a <code>NetAltingChannelInput</code> object and
    * registers its location with the supplied name in the global namespace
    * of a channel naming service.
    *
    * @param name the name against which to register the channel.
    *
    * @return the constructed <code>NetAltingChannelInput</code> object.
    */
   public NetAltingChannelInput createNet2One(String name);
   
   /**
    * Constructs a <code>NetAltingChannelInput</code> object and
    * registers its location with the supplied name in specified
    * namespace of a channel naming service.
    *
    * @param name the name against which to register the channel.
    * @param nameAccessLevel the namespace in which to register the name.
    *
    * @return the constructed <code>NetAltingChannelInput</code> object.
    */
   public NetAltingChannelInput createNet2One(String name, NameAccessLevel nameAccessLevel);
   
   /**
    * Constructs a <code>NetSharedChannelInput</code> object and
    * registers its location with the supplied name in the global namespace
    * of a channel naming service.
    *
    * @param name the name against which to register the channel.
    *
    * @return the constructed <code>NetSharedChannelInput</code> object.
    */
   public NetSharedChannelInput createNet2Any(String name);
   
   /**
    * Constructs a <code>NetSharedChannelInput</code> object and
    * registers its location with the supplied name in specified
    * namespace of a channel naming service.
    *
    * @param name the name against which to register the channel.
    * @param nameAccessLevel the namespace in which to register the name.
    *
    * @return the constructed <code>NetSharedChannelInput</code> object.
    */
   public NetSharedChannelInput createNet2Any(String name, NameAccessLevel nameAccessLevel);
   
   /**
    * Constructs a <code>NetChannelOutput</code> object connected
    * to a <code>NetChannelInput</code> located at a location
    * resolved from the specified channel name.
    *
    * @param name the name of the channel from which to resolve the location.
    *
    * @return the constructed <code>NetChannelOutput</code> object.
    */
   public NetChannelOutput createOne2Net(String name);
   
   /**
    * Constructs a <code>NetChannelOutput</code> object connected
    * to a <code>NetChannelInput</code> located at a location
    * resolved from the specified channel name that exists in the supplied
    * namespace.
    *
    * @param name the name of the channel from which to resolve the location.
    * @param accessLevel the namespace in which the channel name exists.
    *
    * @return the constructed <code>NetChannelOutput</code> object.
    */
   public NetChannelOutput createOne2Net(String name, NameAccessLevel accessLevel);
   
   
   /**
    * Constructs a <code>NetSharedChannelOutput</code> object connected
    * to a <code>NetChannelInput</code> located at a location
    * resolved from the specified channel name.
    *
    * @param name the name of the channel from which to resolve the location.
    *
    * @return the constructed <code>NetChannelOutput</code> object.
    */
   public NetSharedChannelOutput createAny2Net(String name);
   
   /**
    * Constructs a <code>NetSharedChannelOutput</code> object connected
    * to a <code>NetChannelInput</code> located at a location
    * resolved from the specified channel name that exists in the supplied
    * namespace.
    *
    * @param name the name of the channel from which to resolve the location.
    * @param accessLevel the namespace in which the channel name exists.
    *
    * @return the constructed <code>NetChannelOutput</code> object.
    */
   public NetSharedChannelOutput createAny2Net(String name, NameAccessLevel accessLevel);
}