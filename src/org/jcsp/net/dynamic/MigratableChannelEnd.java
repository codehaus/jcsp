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

import org.jcsp.net.*;
import org.jcsp.net.cns.*;

/**
 * Static factory for creating migratable channel ends.
 *
 * @author Quickstone Technologies Limited
 */
public class MigratableChannelEnd
{
   /**
    * Standard factory for anonymous channel ends.
    */
   private static MigratableChannelEndFactory FACTORY = new MigratableChannelEndFactory();
   
   /**
    * Factory for named channel ends.
    */
   private static NamedMigratableChannelEndFactory NAMED_FACTORY = new NamedMigratableChannelEndFactoryImpl();
   
   /**
    * Creates a new <code>MigratableChannelEnd</code> object. This is private to prevent any
    * instances from being created. This class contains only static methods.
    */
   private MigratableChannelEnd()
   {
      super();
   }
   
   /**
    * Creates an anonymous migratable channel input.
    *
    * @return the created channel end.
    */
   public static MigratableAltingChannelInput createNet2One()
   {
      return (MigratableAltingChannelInput) FACTORY.createNet2One();
   }
   
   /**
    * Creates a migratable channel output to a given location.
    *
    * @param loc location of the input end of the channel.
    * @return the created channel end.
    */
   public static MigratableChannelOutput createOne2Net(NetChannelLocation loc)
   {
      return (MigratableChannelOutput) FACTORY.createOne2Net(loc);
   }
   
   /**
    * Creates a named migratable channel input using the default namespace.
    *
    * @param name the name of the channel to register with the CNS.
    * @return the created channel end.
    */
   public static MigratableAltingChannelInput createNet2One(String name)
   {
      return createNet2One(name, null);
   }
   
   /**
    * Creates a named migratable channel input within the given namespace.
    *
    * @param name the name of the channel to register with the CNS.
    * @param nameAccessLevel the namespace to register the name within.
    * @return the created channel end.
    */
   public static MigratableAltingChannelInput createNet2One(String name, NameAccessLevel nameAccessLevel)
   {
      return NAMED_FACTORY.createNet2One(name, nameAccessLevel);
   }
   
   /**
    * Creates a migratable channel output to a named channel within the default namespace.
    *
    * @param name the name of the channel as registered with the CNS.
    * @return the created channel end.
    */
   public static MigratableChannelOutput createOne2Net(String name)
   {
      return createOne2Net(name, null);
   }
   
   /**
    * Creates a migratable channel output to a named channel within a given namespace.
    *
    * @param name the name of the channel as registered with the CNS.
    * @param nameAccessLevel the namespace the name is registered within.
    * @return the created channel end.
    */
   public static MigratableChannelOutput createOne2Net(String name, NameAccessLevel nameAccessLevel)
   {
      return NAMED_FACTORY.createOne2Net(name, nameAccessLevel);
   }
}